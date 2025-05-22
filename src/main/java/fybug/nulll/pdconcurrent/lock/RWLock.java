package fybug.nulll.pdconcurrent.lock;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import fybug.nulll.pdconcurrent.e.LockType;
import fybug.nulll.pdconcurrent.i.AbstractSyLock;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

/**
 * <h2>使用{@link ReentrantReadWriteLock}实现的并发管理.</h2>
 * 使用{@link ReentrantReadWriteLock}实现并发域，读写锁均为标准实现，支持通过{@link #toread()}进行锁降级<br/>
 * 使用可中断的上锁操作{@link ReentrantReadWriteLock.ReadLock#lockInterruptibly()}和{@link ReentrantReadWriteLock.WriteLock#lockInterruptibly()}实现上锁<br/>
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
 * @version 0.1.1
 * @see ReentrantReadWriteLock
 * @see ReentrantReadWriteLock.ReadLock
 * @see ReentrantReadWriteLock.WriteLock
 * @since lock 0.0.1
 */
@SuppressWarnings("unused")
@Getter
public
class RWLock extends AbstractSyLock {
  /** 锁 */
  private final ReentrantReadWriteLock LOCK;
  /** 读锁 */
  private final ReentrantReadWriteLock.ReadLock Read_LOCK;
  /** 写锁 */
  private final ReentrantReadWriteLock.WriteLock Write_LOCK;
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
  private final AtomicLong READ_LOCK_COUNTER = new AtomicLong();
  /**
   * 写锁计数
   *
   * @since 0.1.1
   */
  private final AtomicLong WRITE_LOCK_COUNTER = new AtomicLong();

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
  RWLock(@NotNull ReentrantReadWriteLock lock) {
    LOCK = lock;
    Read_LOCK = LOCK.readLock();
    Write_LOCK = LOCK.writeLock();
  }

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
   * {@inheritDoc}
   *
   * @param lockType {@inheritDoc}
   *
   * @throws Exception {@inheritDoc}
   * @implNote 使用 {@link ReentrantReadWriteLock} 实现的并发域，使用了{@code lockInterruptibly()}进行可中断的上锁操作<br/>
   * 会记录本次锁类型并记录读写锁的计数
   * @since 0.1.1
   */
  @Override
  protected
  void lock(@NotNull LockType lockType) throws InterruptedException {
    // 获取记录列表
    var l = getCurrentThreadLockState();
    // 检查锁类型进行上锁，并更新对应锁计数
    if ( lockType != LockType.NOLOCK ) {
      if ( lockType == LockType.READ ) {
        Read_LOCK.lockInterruptibly();
        READ_LOCK_COUNTER.getAndIncrement();
      } else if ( lockType == LockType.WRITE ) {
        Write_LOCK.lockInterruptibly();
        WRITE_LOCK_COUNTER.getAndIncrement();
      }
      // 记录本次锁类型
      l.add(lockType);
    } else if ( l.isEmpty() ) {
      LOCK_STATE.remove();
    }
  }

  /**
   * {@inheritDoc}
   *
   * @param lockType {@inheritDoc}
   *
   * @return {@inheritDoc}
   *
   * @implNote 使用 {@link ReentrantReadWriteLock} 实现的并发域，使用了{@code trylock()}实现<br/>
   * 会记录本次锁类型并记录读写锁的计数
   * @since 0.1.1
   */
  @Override
  protected
  boolean trylock(@NotNull LockType lockType) {
    // 获取记录列表
    var l = getCurrentThreadLockState();
    // 是否成功
    boolean success = false;

    // 检查锁类型并上锁
    if ( lockType != LockType.NOLOCK ) {
      if ( lockType == LockType.READ )
        success = Read_LOCK.tryLock();
      else if ( lockType == LockType.WRITE )
        success = Write_LOCK.tryLock();
    }
    // 是否成功
    if ( success ) {
      // 记录本次锁类型
      l.add(lockType);
      // 更新对应锁计数
      if ( lockType == LockType.READ )
        READ_LOCK_COUNTER.getAndIncrement();
      else // noinspection ConstantValue
        if ( lockType == LockType.WRITE )
          WRITE_LOCK_COUNTER.getAndIncrement();
    } else if ( l.isEmpty() ) {
      LOCK_STATE.remove();
    }

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

    // 获取最后锁类型
    var lockType = l.removeLast();
    // 检查锁类型解锁，并更新对应锁计数
    if ( lockType == LockType.READ ) {
      Read_LOCK.unlock();
      READ_LOCK_COUNTER.getAndDecrement();
    } else if ( lockType == LockType.WRITE ) {
      Write_LOCK.unlock();
      WRITE_LOCK_COUNTER.getAndDecrement();
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
      Read_LOCK.lock();
      Write_LOCK.unlock();
      // 更新记录
      l.set(l.size() - 1, LockType.WRITE);
      // 更新计数
      WRITE_LOCK_COUNTER.getAndDecrement();
      READ_LOCK_COUNTER.getAndIncrement();
      return true;
    }
    return false;
  }

  /**
   * 检查锁是否被占用
   *
   * @return 是否被占用
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
  boolean isReadLocked() { return isWriteLocked() || READ_LOCK_COUNTER.get() > 0; }

  /**
   * 检查写锁是否被占用
   *
   * @return 是否被占用
   *
   * @since 0.1.0
   */
  public
  boolean isWriteLocked() { return WRITE_LOCK_COUNTER.get() > 0; }

  /**
   * 检查当前线程是否持有锁
   *
   * @return 当前线程是否拥有锁
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
  boolean isReadLockedCurrentThread() {
    // 获取锁队列
    var l = LOCK_STATE.get();
    // 没有记录
    if ( l == null )
      return false;
    // 没有上锁
    if ( l.isEmpty() ) {
      LOCK_STATE.remove();
      return false;
    }
    // 检查是否占用读锁
    return l.contains(LockType.WRITE) || l.contains(LockType.READ);
  }

  /**
   * 检查当前线程是否持有写锁
   *
   * @return 当前线程是否持有锁
   *
   * @since 0.1.1
   */
  public
  boolean isWriteLockedCurrentThread() {
    // 获取锁队列
    var l = LOCK_STATE.get();
    // 没有记录
    if ( l == null )
      return false;
    // 没有上锁
    if ( l.isEmpty() ) {
      LOCK_STATE.remove();
      return false;
    }
    // 检查是否占用读锁
    return l.contains(LockType.WRITE);
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
  Condition newReadCondition() { return Read_LOCK.newCondition(); }

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
  Condition newWriteCondition() { return Write_LOCK.newCondition(); }
}