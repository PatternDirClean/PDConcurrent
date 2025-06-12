package fybug.nulll.pdconcurrent.lock;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import fybug.nulll.pdconcurrent.e.LockType;
import fybug.nulll.pdconcurrent.i.AbstractRWSyLock;
import jakarta.validation.constraints.NotNull;
import lombok.SneakyThrows;

/**
 * <h2>使用{@link ReentrantLock}实现的并发管理.</h2>
 * 使用{@link ReentrantLock}实现并发域，读写锁均为同一个实现<br/>
 * 支持使用{@link #newCondition()}获取{@link Condition}
 * <br/><br/>
 * 使用并发管理：
 * {@snippet lang = java:
 * public final SyLock lock = new ReLock();
 * public static void main(String[] args) {
 *   lock.read(() -> System.out.println("asdas"));
 * }}
 * 不使用：
 * {@snippet lang = java:
 * import java.util.concurrent.locks.ReentrantLock;
 *
 * public final ReentrantLock lock = new ReentrantLock();
 * public static void main(String[] args) {
 *   try {
 *     lock.lock();
 *     System.out.println("asdas");
 *   } finally {
 *     lock.unlock();
 *   }
 * }}
 *
 * @author fybug
 * @version 0.1.3
 * @see ReentrantLock
 * @since lock 0.0.1
 */
@SuppressWarnings("unused")
public
class ReLock extends AbstractRWSyLock<ReentrantLock> {
  /**
   * 构建并发管理
   * <p>
   * 使用非公平锁
   */
  public
  ReLock() { this(false); }

  /**
   * 构造并发处理
   *
   * @param fair 是否使用公平锁
   */
  public
  ReLock(boolean fair) { this(new ReentrantLock(fair)); }

  /**
   * 构造并发处理
   *
   * @param LOCK 使用的锁
   *
   * @since 0.1.0
   */
  public
  ReLock(@NotNull ReentrantLock LOCK) { super(new LockThreadContext<>(LOCK, true, null)); }

  /**
   * {@inheritDoc}
   *
   * @param lockType {@inheritDoc}
   *
   * @implNote 常规锁使用 {@link ReentrantLock#lock()}，可中断锁使用{@link ReentrantLock#lockInterruptibly()}实现
   * @since 0.1.1
   */
  @SneakyThrows
  @Override
  protected
  void lock(@NotNull LockType lockType) {
    var c = getLockThreadContext();
    if ( lockType != LockType.NOLOCK ) {
      if ( c.isInterruptiblyLock() )
        c.getLock().lockInterruptibly();
      else
        c.getLock().lock();
    }
  }

  /**
   * {@inheritDoc}
   *
   * @param lockType {@inheritDoc}
   *
   * @return {@inheritDoc}
   *
   * @implNote 无参数时使用 {@link ReentrantLock#tryLock()} 实现，设置了超时时间时使用{@link ReentrantLock#tryLock(long, TimeUnit)}
   * @since 0.1.1
   */
  @SneakyThrows
  @Override
  protected
  boolean trylock(@NotNull LockType lockType) {
    var c = getLockThreadContext();
    var t = c.getTryTimeout();
    if ( lockType != LockType.NOLOCK ) {
      if ( t == null )
        return c.getLock().tryLock();
      else
        return c.getLock().tryLock(t.getTimeout(), t.getUnit());
    }
    return false;
  }

  /**
   * {@inheritDoc}
   *
   * @implNote 使用 {@link ReentrantLock#isHeldByCurrentThread()} 检查当前线程是否持有锁，持有时才会运行{@link ReentrantLock#unlock()}
   * @since 0.1.1
   */
  @Override
  public
  void unlock() {
    var lock = getLockThreadContext().getLock();
    if ( lock.isHeldByCurrentThread() )
      lock.unlock();
  }

  /**
   * 获取{@link Condition}
   *
   * @return {@link ReentrantLock}的{@link Condition}
   *
   * @see ReentrantLock#newCondition()
   */
  @NotNull
  public
  Condition newCondition() { return getLockThreadContext().getLock().newCondition(); }

  /**
   * {@inheritDoc}
   *
   * @return {@inheritDoc}
   */
  @Override
  public
  boolean isLocked() { return getLockThreadContext().getLock().isLocked(); }

  /**
   * {@inheritDoc}
   *
   * @return {@inheritDoc}
   *
   * @since 0.1.1
   */
  @Override
  public
  boolean isLockedCurrentThread() { return getLockThreadContext().getLock().isHeldByCurrentThread(); }
}
