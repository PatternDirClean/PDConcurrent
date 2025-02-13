package fybug.nulll.pdconcurrent.lock;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import fybug.nulll.pdconcurrent.e.LockType;
import fybug.nulll.pdconcurrent.i.AbstractSyLock;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

/**
 * <h2>使用{@link ReentrantLock}实现的并发管理.</h2>
 * 使用{@link ReentrantLock}实现并发域，读写锁均为同一个实现<br/>
 * 使用了可中断的上锁操作{@link ReentrantLock#lockInterruptibly()}实现<br/>
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
 * @version 0.1.1
 * @see ReentrantLock
 * @since lock 0.0.1
 */
@SuppressWarnings("unused")
@Getter
public
class ReLock extends AbstractSyLock {
  /** 锁 */
  private final ReentrantLock LOCK;

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
  ReLock(@NotNull ReentrantLock LOCK) { this.LOCK = LOCK; }

  /**
   * {@inheritDoc}
   *
   * @param lockType {@inheritDoc}
   *
   * @throws InterruptedException 所在线程被中断
   * @implNote 使用 {@link ReentrantLock} 实现的并发域，使用了{@link ReentrantLock#lockInterruptibly()}进行可中断的上锁操作
   * @since 0.1.1
   */
  @Override
  protected
  void lock(@NotNull LockType lockType) throws InterruptedException {
    if ( lockType != LockType.NOLOCK )
      LOCK.lockInterruptibly();
  }

  /**
   * {@inheritDoc}
   *
   * @param lockType {@inheritDoc}
   *
   * @return {@inheritDoc}
   *
   * @implNote 使用 {@link ReentrantLock} 实现的并发域，使用了{@link ReentrantLock#tryLock()}实现
   * @since 0.1.1
   */
  @Override
  protected
  boolean trylock(@NotNull LockType lockType) {
    if ( lockType != LockType.NOLOCK )
      return LOCK.tryLock();
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
    if ( LOCK.isHeldByCurrentThread() )
      LOCK.unlock();
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
  Condition newCondition() { return LOCK.newCondition(); }

  /**
   * {@inheritDoc}
   *
   * @return {@inheritDoc}
   */
  @Override
  public
  boolean isLocked() { return LOCK.isLocked(); }

  /**
   * {@inheritDoc}
   *
   * @return {@inheritDoc}
   *
   * @since 0.1.1
   */
  @Override
  public
  boolean isLockedCurrentThread() { return LOCK.isHeldByCurrentThread(); }
}
