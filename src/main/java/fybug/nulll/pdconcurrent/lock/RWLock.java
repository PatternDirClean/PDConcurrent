package fybug.nulll.pdconcurrent.lock;
import java.util.LinkedList;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import fybug.nulll.pdconcurrent.e.LockType;
import fybug.nulll.pdconcurrent.i.AbstractRWSyLock;
import jakarta.validation.constraints.NotNull;
import lombok.SneakyThrows;

/**
 * <h2>使用{@link ReentrantReadWriteLock}实现的并发管理.</h2>
 * 使用{@link ReentrantReadWriteLock}实现并发域，读写锁均为标准实现，支持通过{@link #toread()}进行锁降级<br/>
 * 支持使用{@link #newReadCondition()}{@link #newWriteCondition()}获取{@link Condition}
 * <br/><br/>
 * 使用并发管理：
 * {@snippet lang = java:
 * public final SyLock lock = new RWLock();
 * public static void main(String[] args) {
 *   // 使用读锁
 *   lock.read(() -> System.out.println("adsa"));
 *   // 使用写锁
 *   lock.write(() -> System.out.println("adsa"));
 * }}
 * 不使用：
 * {@snippet lang = java:
 * import java.util.concurrent.locks.ReentrantReadWriteLock;
 * public final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
 * public static void main(String[] args) {
 *   // 使用读锁
 *   try {
 *     lock.readLock().lock();
 *     System.out.println("adsa");
 *   } finally {
 *     lock.readLock().unlock();
 *   }
 *   // 使用写锁
 *   try {
 *     lock.writeLock().lock();
 *     System.out.println("adsa");
 *   } finally {
 *     lock.writeLock().unlock();
 *   }
 * }}
 *
 * @author fybug
 * @version 0.1.2
 * @see ReentrantReadWriteLock
 * @see ReentrantReadWriteLock.ReadLock
 * @see ReentrantReadWriteLock.WriteLock
 * @since lock 0.0.1
 */
@SuppressWarnings("unused")
public
class RWLock extends AbstractRWSyLock<ReentrantReadWriteLock> {
  /**
   * 每个线程的锁状态记录
   *
   * @see LockType
   */
  private final ThreadLocal<LinkedList<LockType>> LOCK_STATE = new ThreadLocal<>();
  /**
   * 读锁计数
   *
   * @since 0.1.1
   */
  private final WeakHashMap<ReentrantReadWriteLock, AtomicLong> READ_LOCK_COUNTER = new WeakHashMap<>();
  /**
   * 写锁计数
   *
   * @since 0.1.1
   */
  private final WeakHashMap<ReentrantReadWriteLock, AtomicLong> WRITE_LOCK_COUNTER = new WeakHashMap<>();

  /**
   * 构建并发管理
   * <p>
   * 使用非公平锁
   */
  public
  RWLock() { this(false); }

  /**
   * 构造并发处理
   *
   * @param fair 是否使用公平锁
   */
  public
  RWLock(boolean fair) { this(new ReentrantReadWriteLock(fair)); }

  /**
   * 构造并发处理
   *
   * @param lock 使用的锁
   *
   * @since 0.1.0
   */
  public
  RWLock(@NotNull ReentrantReadWriteLock lock) { super(new LockThreadContext<>(lock, true, null)); }

  /**
   * 获取当前线程锁记录
   *
   * @return 锁类型列表
   *
   * @since 0.1.1
   */
  @NotNull
  private
  LinkedList<LockType> getCurrentThreadLockState() {
    // 获取记录
    var l = LOCK_STATE.get();
    // 保证记录存在
    if ( l == null ) {
      l = new LinkedList<>();
      LOCK_STATE.set(l);
    }
    return l;
  }

  /**
   * 获取当前线程读锁计数
   *
   * @return 读锁计数对象
   *
   * @since 0.1.2
   */
  @NotNull
  private
  AtomicLong getReadLockCounter(@NotNull ReentrantReadWriteLock lock)
  { return READ_LOCK_COUNTER.computeIfAbsent(lock, k -> new AtomicLong()); }

  /**
   * 获取当前线程写锁计数
   *
   * @return 写锁计数对象
   *
   * @since 0.1.2
   */
  @NotNull
  private
  AtomicLong getWriteLockCounter(@NotNull ReentrantReadWriteLock lock)
  { return WRITE_LOCK_COUNTER.computeIfAbsent(lock, k -> new AtomicLong()); }

  /**
   * {@inheritDoc}
   *
   * @param lockType {@inheritDoc}
   *
   * @implNote 常规锁使用 {@code lock()}上锁，可中断锁使用{@code lockInterruptibly()}操作<br/>
   * 会记录本次锁类型并记录读写锁的计数
   * @since 0.1.1
   */
  @SneakyThrows
  @Override
  protected
  void lock(@NotNull LockType lockType) {
    var c = getLockThreadContext();
    // 检查锁类型进行上锁，并更新对应锁计数
    if ( lockType != LockType.NOLOCK ) {
      if ( lockType == LockType.READ ) {
        // 上锁
        if ( c.isInterruptiblyLock() )
          c.getLock().readLock().lockInterruptibly();
        else
          c.getLock().readLock().lock();
        getReadLockCounter(c.getLock()).getAndIncrement();
      } else if ( lockType == LockType.WRITE ) {
        // 上锁
        if ( c.isInterruptiblyLock() )
          c.getLock().writeLock().lockInterruptibly();
        else
          c.getLock().writeLock().lock();
        getWriteLockCounter(c.getLock()).getAndIncrement();
      }
      // 记录本次锁类型
      getCurrentThreadLockState().add(lockType);
    } else if ( getCurrentThreadLockState().isEmpty() )
      LOCK_STATE.remove();
  }

  /**
   * {@inheritDoc}
   *
   * @param lockType {@inheritDoc}
   *
   * @return {@inheritDoc}
   *
   * @implNote 使用 {@code trylock()} 实现，会记录本次锁类型并记录读写锁的计数
   * @since 0.1.1
   */
  @SuppressWarnings("ConstantValue")
  @SneakyThrows
  @Override
  protected
  boolean trylock(@NotNull LockType lockType) {
    // 是否成功
    boolean success = false;
    var c = getLockThreadContext();
    var t = c.getTryTimeout();

    // 检查锁类型并上锁
    if ( lockType != LockType.NOLOCK ) {
      if ( lockType == LockType.READ ) {
        if ( t == null )
          success = c.getLock().readLock().tryLock();
        else
          success = c.getLock().readLock().tryLock(t.getTimeout(), t.getUnit());
      } else if ( lockType == LockType.WRITE ) {
        if ( t == null )
          success = c.getLock().writeLock().tryLock();
        else
          success = c.getLock().writeLock().tryLock(t.getTimeout(), t.getUnit());
      }
    }

    // 是否成功
    if ( success ) {
      // 更新对应锁计数
      if ( lockType == LockType.READ )
        getReadLockCounter(c.getLock()).getAndIncrement();
      else if ( lockType == LockType.WRITE )
        getWriteLockCounter(c.getLock()).getAndIncrement();
      // 记录本次锁类型
      getCurrentThreadLockState().add(lockType);
    } else if ( getCurrentThreadLockState().isEmpty() )
      LOCK_STATE.remove();

    return success;
  }

  /**
   * {@inheritDoc}
   *
   * @implNote 通过 {@link #LOCK_STATE} 检查当前线程是否持有锁，持有时才会运行{@code unlock()}<br/>
   * 会更新读写锁的计数
   * @since 0.1.1
   */
  @Override
  public
  void unlock() {
    // 获取记录列表
    var l = LOCK_STATE.get();
    // 没有记录
    if ( l == null )
      return;
    // 没有上锁
    if ( l.isEmpty() ) {
      LOCK_STATE.remove();
      return;
    }
    var c = getLockThreadContext();
    // 获取最后锁类型
    var lockType = l.removeLast();
    // 检查锁类型解锁，并更新对应锁计数
    if ( lockType == LockType.READ ) {
      c.getLock().readLock().unlock();
      getReadLockCounter(c.getLock()).getAndDecrement();
    } else if ( lockType == LockType.WRITE ) {
      c.getLock().writeLock().unlock();
      getWriteLockCounter(c.getLock()).getAndDecrement();
    }

    // 记录清空
    if ( l.isEmpty() )
      LOCK_STATE.remove();
  }

  /**
   * 转为读锁
   * <p>
   * 如果当前状态为写锁则会降级为读锁，否则不进行操作
   *
   * @return 是否为读锁
   *
   * @since 0.1.0
   */
  public
  boolean toread() {
    // 获取记录列表
    var l = getCurrentThreadLockState();
    var c = getLockThreadContext();
    // 没有上锁
    if ( l.isEmpty() ) {
      LOCK_STATE.remove();
      return false;
    }

    // 获取最后锁类型
    var lockType = l.getLast();
    // 检查锁类型进行处理，并更新对应锁计数
    if ( lockType == LockType.READ )
      return true;
    else if ( lockType == LockType.WRITE ) {
      // 锁降级
      c.getLock().readLock().lock();
      c.getLock().writeLock().unlock();
      // 更新记录
      l.set(l.size() - 1, LockType.READ);
      // 更新计数
      getWriteLockCounter(c.getLock()).getAndDecrement();
      getReadLockCounter(c.getLock()).getAndIncrement();
      return true;
    }
    return false;
  }

  /**
   * /**
   * {@inheritDoc}
   *
   * @return {@inheritDoc}
   *
   * @since 0.1.0
   */
  @Override
  public
  boolean isLocked() { return isWriteLocked() || isReadLocked(); }

  /**
   * 检查读锁是否被占用
   *
   * @return 锁是否被占用，写锁被占用也会返回{@code true}
   *
   * @since 0.1.0
   */
  public
  boolean isReadLocked() { return isWriteLocked() || getReadLockCounter(getLockThreadContext().getLock()).get() > 0; }

  /**
   * 检查写锁是否被占用
   *
   * @return 是否被占用
   *
   * @since 0.1.0
   */
  public
  boolean isWriteLocked() { return getWriteLockCounter(getLockThreadContext().getLock()).get() > 0; }

  /**
   * /**
   * {@inheritDoc}
   *
   * @return {@inheritDoc}
   *
   * @since 0.1.1
   */
  @Override
  public
  boolean isLockedCurrentThread() { return isWriteLockedCurrentThread() || isReadLockedCurrentThread(); }

  /**
   * 检查当前线程是否持有读锁
   *
   * @return 当前线程是否持有锁，持有写锁也会返回{@code true}
   *
   * @since 0.1.1
   */
  public
  boolean isReadLockedCurrentThread() { return checkIsLockedCurrentThread(LOCK_STATE.get(), LockType.READ); }

  /**
   * 检查当前线程是否持有写锁
   *
   * @return 当前线程是否持有锁
   *
   * @since 0.1.1
   */
  public
  boolean isWriteLockedCurrentThread() { return checkIsLockedCurrentThread(LOCK_STATE.get(), LockType.WRITE); }

  /**
   * 检查是否有对应的锁在当前线程持有
   *
   * @param l        锁记录
   * @param lockType 要检查的锁类型，包含其中一个则返回true
   *
   * @since 0.1.2
   */
  private
  boolean checkIsLockedCurrentThread(@NotNull LinkedList<LockType> l, @NotNull LockType... lockType) {
    // 没有记录
    if ( l == null )
      return false;
    // 没有上锁
    if ( l.isEmpty() ) {
      LOCK_STATE.remove();
      return false;
    }
    for ( var t : lockType )
      if ( l.contains(t) )
        return true;
    return false;
  }

  /**
   * 获取读锁{@link Condition}
   *
   * @return {@link ReentrantReadWriteLock.ReadLock}的{@link Condition}
   *
   * @see ReentrantReadWriteLock.ReadLock#newCondition()
   * @since 0.1.0
   */
  @NotNull
  public
  Condition newReadCondition() { return getLockThreadContext().getLock().readLock().newCondition(); }

  /**
   * 获取写锁{@link Condition}
   *
   * @return {@link ReentrantReadWriteLock.WriteLock}的{@link Condition}
   *
   * @see ReentrantReadWriteLock.WriteLock#newCondition()
   * @since 0.1.0
   */
  @NotNull
  public
  Condition newWriteCondition() { return getLockThreadContext().getLock().writeLock().newCondition(); }
}