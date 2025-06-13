package fybug.nulll.pdconcurrent.i;
import java.util.function.Function;

import fybug.nulll.pdconcurrent.e.LockType;
import fybug.nulll.pdutilfunctionexpand.tryFunction;
import fybug.nulll.pdutilfunctionexpand.trySupplier;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

/**
 * <h2>读写锁管理基本框架.</h2>
 * 提供了读写锁实现整合接口
 *
 * @author fybug
 * @version 0.0.1
 * @since i 0.0.3
 */
@SuppressWarnings("unused")
public abstract
class AbstractRWSyLock<T> extends AbstractSyLock<T> {
  protected
  AbstractRWSyLock(@NotNull LockThreadContext<T> c) { super(c); }

  /**
   * {@inheritDoc}
   *
   * @param lockType {@inheritDoc}
   * @param run      {@inheritDoc}
   * @param catchby  {@inheritDoc}
   * @param finaby   {@inheritDoc}
   * @param <R>      {@inheritDoc}
   * @param <E>      {@inheritDoc}
   *
   * @return {@inheritDoc}
   *
   * @throws E1 {@inheritDoc}
   * @implNote 使用 {@link #lock(LockType)}、{@link #unlock()} 进行上锁解锁，因此使用两层的异常捕获避免{@code finaby}中抛出异常
   */
  @SuppressWarnings("unchecked")
  @Override
  protected
  <R, E extends Throwable, E1 extends Throwable> R lockimpl(@NotNull LockType lockType, @NotNull trySupplier<R, E> run,
                                                            @Nullable tryFunction<E, R, E1> catchby,
                                                            @Nullable Function<R, R> finaby) throws E1
  {
    R o = null;
    // 防止finally内的回调抛异常
    try {
      try {
        // 上锁
        lock(lockType);
        // 主要内容
        o = run.get();
      } catch ( Throwable e ) {
        // 异常处理
        if ( catchby != null )
          o = catchby.apply((E) e);
      } finally {
        // 收尾
        if ( finaby != null )
          o = finaby.apply(o);
      }
    } finally {
      unlock();
      removeLockThreadContext();
    }
    return o;
  }

  /**
   * {@inheritDoc}
   *
   * @param lockType {@inheritDoc}
   * @param run      {@inheritDoc}
   * @param catchby  {@inheritDoc}
   * @param finaby   {@inheritDoc}
   * @param <R>      {@inheritDoc}
   * @param <E>      {@inheritDoc}
   *
   * @return {@inheritDoc}
   *
   * @throws E1 {@inheritDoc}
   * @implNote 使用 {@link #trylock(LockType)}、{@link #unlock()} 进行上锁解锁，因此使用两层的异常捕获避免{@code finaby}中抛出异常
   */
  @SuppressWarnings("unchecked")
  @Override
  protected
  <R, E extends Throwable, E1 extends Throwable> R trylockimpl(@NotNull LockType lockType,
                                                               @NotNull tryFunction<Boolean, R, E> run,
                                                               @Nullable tryFunction<E, R, E1> catchby,
                                                               @Nullable Function<R, R> finaby) throws E1
  {
    R o = null;
    // 防止finally内的回调抛异常
    try {
      try {
        // 上锁
        o = run.apply(trylock(lockType));
      } catch ( Throwable e ) {
        // 异常处理
        if ( catchby != null )
          o = catchby.apply((E) e);
      } finally {
        // 收尾
        if ( finaby != null )
          o = finaby.apply(o);
      }
    } finally {
      unlock();
      removeLockThreadContext();
    }
    return o;
  }

  /**
   * 上锁函数
   * <p>
   * 按照设置的上下文参数要求可能会因为线程被中断而抛出{@link InterruptedException}异常
   *
   * @param lockType 锁类型
   *
   * @implSpec 在此处实现上锁功能，实现时应该支持根据上下文选择是否使用可以被线程中断的上锁方式
   */
  public abstract
  void lock(@NotNull LockType lockType);

  /**
   * 尝试上锁函数
   *
   * @param lockType 锁类型
   *
   * @return 是否成功上锁
   *
   * @implSpec 在此处实现尝试上锁功能，实现时应该支持上下文参数
   */
  public abstract
  boolean trylock(@NotNull LockType lockType);

  /**
   * 解锁函数
   *
   * @implSpec 在此处实现解锁功能，解锁的类型按照最后上锁的类型处理
   */
  public abstract
  void unlock();

  /**
   * 检查锁是否被占用
   *
   * @return 是否被占用
   */
  public abstract
  boolean isLocked();

  /**
   * 检查当前线程是否持有锁
   *
   * @return 当前线程是否持有锁
   */
  public abstract
  boolean isLockedCurrentThread();
}
