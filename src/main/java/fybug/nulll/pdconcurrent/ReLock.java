package fybug.nulll.pdconcurrent;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

import fybug.nulll.pdconcurrent.e.LockType;
import fybug.nulll.pdconcurrent.fun.trySupplier;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

/**
 * <h2>使用{@link ReentrantLock}实现的并发管理.</h2>
 * 使用{@link ReentrantLock}实现并发域，读写锁均为同一个实现<br/>
 * 使用了可中断的上锁操作{@link ReentrantLock#lockInterruptibly()}<br/>
 * 支持使用{@link #newCondition()}获取{@link Condition}，通过{@link #isLocked()}检查是否被占用
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
 * @version 0.1.0
 * @see SyLock
 * @see ReentrantLock
 * @since PDConcurrent 0.0.1
 */
@Getter
public
class ReLock implements SyLock {
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
   * @param run      {@inheritDoc}
   * @param catchby  {@inheritDoc}
   * @param finaby   {@inheritDoc}
   * @param <R>      {@inheritDoc}
   *
   * @return {@inheritDoc}
   *
   * @implNote 使用 {@link ReentrantLock} 实现的并发域，使用了{@link ReentrantLock#lockInterruptibly()}进行可中断的上锁操作
   * @see SyLock#lock(LockType, trySupplier, Function, Function)
   * @since 0.1.0
   */
  @Override
  public
  <R> R lock(@NotNull LockType lockType, trySupplier<R> run, @Nullable Function<Exception, R> catchby,
             @Nullable Function<R, R> finaby)
  {
    R o = null;
    // 防止finally内的回调抛异常
    try {
      try {
        // 上锁
        if ( lockType != LockType.NOLOCK )
          LOCK.lockInterruptibly();
        // 主要内容
        o = run.get();
      } catch ( Exception e ) {
        // 异常处理
        if ( catchby != null )
          o = catchby.apply(e);
      } finally {
        // 收尾
        if ( finaby != null )
          o = finaby.apply(o);
      }
    } finally {
      // 解锁
      if ( lockType != LockType.NOLOCK && LOCK.isLocked() )
        LOCK.unlock();
    }
    return o;
  }

  /**
   * {@inheritDoc}
   *
   * @param lockType {@inheritDoc}
   * @param run      {@inheritDoc}
   * @param finaby   {@inheritDoc}
   * @param <R>      {@inheritDoc}
   *
   * @return {@inheritDoc}
   *
   * @implNote 使用 {@link ReentrantLock} 实现的并发域，使用了{@link ReentrantLock#lockInterruptibly()
   * @see SyLock#trylock(LockType, trySupplier, Function)
   * @since 0.1.0
   */
  @Override
  public
  <R> R trylock(@NotNull LockType lockType, @NotNull trySupplier<R> run, @Nullable Function<R, R> finaby) throws Exception {
    R o = null;
    // 防止finally内的回调抛异常
    try {
      try {
        // 上锁
        if ( lockType != LockType.NOLOCK )
          LOCK.lockInterruptibly();
        // 主要内容
        o = run.get();
      } finally {
        // 收尾
        if ( finaby != null )
          o = finaby.apply(o);
      }
    } finally {
      // 解锁
      if ( lockType != LockType.NOLOCK && LOCK.isLocked() )
        LOCK.unlock();
    }
    return o;
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
   * 检查锁是否被占用
   *
   * @return 是否被占用
   */
  public
  boolean isLocked() { return LOCK.isLocked(); }
}
