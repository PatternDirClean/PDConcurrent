package fybug.nulll.pdconcurrent.i;
import java.util.function.Function;

import fybug.nulll.pdconcurrent.SyLock;
import fybug.nulll.pdconcurrent.e.LockType;
import fybug.nulll.pdconcurrent.fun.tryFunction;
import fybug.nulll.pdconcurrent.fun.trySupplier;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

/**
 * <h2>读写锁管理基本框架.</h2>
 *
 * @author fybug
 * @version 0.0.1
 * @since i 0.0.1
 */
@SuppressWarnings("unused")
public abstract
class AbstractSyLock implements SyLock {
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
   * @implNote 使用 {@link #lock(LockType)} 与 {@link #unlock()} 方法实现上锁与解锁
   */
  @Override
  public
  <R> R lock(@NotNull LockType lockType, @NotNull trySupplier<R> run, @Nullable Function<Exception, R> catchby,
             @Nullable Function<R, R> finaby)
  {
    R o = null;
    // 防止finally内的回调抛异常
    try {
      try {
        // 上锁
        lock(lockType);
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
      unlock();
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
   * @implNote 使用 {@link #lock(LockType)} 与 {@link #unlock()} 方法实现上锁与解锁
   */
  @Override
  public
  <R> R lock(@NotNull LockType lockType, @NotNull trySupplier<R> run, @Nullable Function<R, R> finaby) throws Exception {
    R o = null;
    // 防止finally内的回调抛异常
    try {
      try {
        // 上锁
        lock(lockType);
        // 主要内容
        o = run.get();
      } finally {
        // 收尾
        if ( finaby != null )
          o = finaby.apply(o);
      }
    } finally {
      unlock();
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
   *
   * @return {@inheritDoc}
   *
   * @implNote 使用 {@link #trylock(LockType)} 与 {@link #unlock()} 方法实现上锁与解锁
   */
  @Override
  public
  <R> R trylock(@NotNull LockType lockType, @NotNull tryFunction<Boolean, R> run, @Nullable Function<Exception, R> catchby,
                @Nullable Function<R, R> finaby)
  {
    R o = null;
    // 防止finally内的回调抛异常
    try {
      try {
        // 上锁
        o = run.apply(trylock(lockType));
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
      unlock();
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
   * @throws Exception {@inheritDoc}
   * @implNote 使用 {@link #trylock(LockType)} 与 {@link #unlock()} 方法实现上锁与解锁
   */
  @Override
  public
  <R> R trylock(@NotNull LockType lockType, @NotNull tryFunction<Boolean, R> run, @Nullable Function<R, R> finaby)
  throws Exception
  {
    R o = null;
    // 防止finally内的回调抛异常
    try {
      try {
        // 上锁
        o = run.apply(trylock(lockType));
      } finally {
        // 收尾
        if ( finaby != null )
          o = finaby.apply(o);
      }
    } finally {
      unlock();
    }
    return o;
  }

  /**
   * 上锁函数
   *
   * @param lockType 锁类型
   *
   * @throws Exception 可能抛出的异常
   * @implSpec 在此处实现上锁功能
   */
  protected abstract
  void lock(@NotNull LockType lockType) throws Exception;

  /**
   * 尝试上锁函数
   *
   * @param lockType 锁类型
   *
   * @return 是否成功上锁
   *
   * @implSpec 在此处实现尝试上锁功能
   */
  protected abstract
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
