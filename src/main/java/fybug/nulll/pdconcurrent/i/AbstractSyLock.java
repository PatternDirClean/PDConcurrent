package fybug.nulll.pdconcurrent.i;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import fybug.nulll.pdconcurrent.SyLock;
import fybug.nulll.pdconcurrent.e.LockType;
import fybug.nulll.pdutilfunctionexpand.tryFunction;
import fybug.nulll.pdutilfunctionexpand.trySupplier;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * <h2>锁管理基本框架.</h2>
 * 提供了锁行为的实现整合<br/>
 * 支持通过主动设置当前线程的上下文参数{@link LockThreadContext}改变锁行为甚至当前线程使用的锁对象，默认使用{@link #defaultLockThreadContext}<br/>
 * 需要注意的是，设置为{@link #defaultLockThreadContext}的对象不应该被修改，否则可能会导致没有独立设置上下文的锁发生异常，更新{@link #defaultLockThreadContext}时也应该传入新的对象，至少不能是同一个对象
 *
 * @author fybug
 * @version 0.0.1
 * @see LockThreadContext
 * @since i 0.0.3
 */
@SuppressWarnings("unused")
public abstract
class AbstractSyLock<T> implements SyLock {
  /** 线程的锁上下文 */
  @NotNull protected final ThreadLocal<LockThreadContext<T>> lockThreadContext = new ThreadLocal<>();
  /** 默认锁上下文 */
  @NotNull protected LockThreadContext<T> defaultLockThreadContext;

  protected
  AbstractSyLock(@NotNull LockThreadContext<T> c) { this.defaultLockThreadContext = c; }

  /**
   * 锁运行具体实现
   * <p>
   * {@link #lock(LockType, trySupplier, Function)}与{@link #lock(LockType, trySupplier, Function, Function)}底层实现，要求同时覆盖两个接口的规范，同时应该支持上下文参数{@link LockThreadContext}<br/>
   * 通过{@code catchby}传入的值决定异常的处理
   *
   * @param lockType 锁类型
   * @param run      带返回的回调
   * @param catchby  进入catch块后的回调，传入当前异常，如果需要无视并不抛出异常则传入{@code null}
   * @param finaby   进入finally块后的回调，传入前一个回调的返回值，遇到异常传入{@code null}
   * @param <R>      要返回的数据类型
   * @param <E>      运行时异常类型
   *
   * @return 回调返回的内容，中途二次抛出异常时不返回
   *
   * @throws E1 二次抛出的异常类型
   * @implSpec {@code catchby}传入为{@code null}时应该无视并不抛出异常，同时给{@code finaby}的值为{@code null}
   */
  abstract protected
  <R, E extends Throwable, E1 extends Throwable> R lockimpl(@NotNull LockType lockType, @NotNull trySupplier<R, E> run,
                                                            @Nullable tryFunction<E, R, E1> catchby,
                                                            @Nullable Function<R, R> finaby) throws E1;

  /**
   * 尝试上锁运行具体实现
   * <p>
   * {@link #trylock(LockType, tryFunction, Function)}与{@link #trylock(LockType, tryFunction, Function, Function)}底层实现，要求同时覆盖两个接口的规范，同时应该支持上下文参数{@link LockThreadContext}<br/>
   * 通过{@code catchby}传入的值决定异常的处理
   *
   * @param lockType 锁类型
   * @param run      带返回的回调，传入参数是否获取到锁
   * @param catchby  进入catch块后的回调，传入当前异常，如果需要无视并不抛出异常则传入{@code null}
   * @param finaby   进入finally块后的回调，传入前一个回调的返回值，遇到异常传入{@code null}
   * @param <R>      要返回的数据类型
   * @param <E>      运行时异常类型
   *
   * @return 回调返回的内容，中途二次抛出异常时不返回
   *
   * @throws E1 二次抛出的异常类型
   * @implSpec {@code catchby}传入为{@code null}时应该无视并不抛出异常，同时给{@code finaby}的值为{@code null}
   */
  abstract protected
  <R, E extends Throwable, E1 extends Throwable> R trylockimpl(@NotNull LockType lockType,
                                                               @NotNull tryFunction<Boolean, R, E> run,
                                                               @Nullable tryFunction<E, R, E1> catchby,
                                                               @Nullable Function<R, R> finaby) throws E1;

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
   * @see #lockimpl(LockType, trySupplier, tryFunction, Function)
   */
  @Override
  public
  <R, E extends Throwable> R lock(@NotNull LockType lockType, @NotNull trySupplier<R, E> run,
                                  @Nullable Function<E, R> catchby, @Nullable Function<R, R> finaby)
  { return lockimpl(lockType, run, catchby == null ? null : catchby::apply, finaby); }

  /**
   * {@inheritDoc}
   *
   * @param lockType {@inheritDoc}
   * @param run      {@inheritDoc}
   * @param finaby   {@inheritDoc}
   * @param <R>      {@inheritDoc}
   * @param <E>      {@inheritDoc}
   *
   * @return {@inheritDoc}
   *
   * @throws E {@inheritDoc}
   * @see #lockimpl(LockType, trySupplier, tryFunction, Function)
   */
  @Override
  public
  <R, E extends Throwable> R lock(@NotNull LockType lockType, @NotNull trySupplier<R, E> run,
                                  @Nullable Function<R, R> finaby) throws E
  { return lockimpl(lockType, run, e -> { throw e; }, finaby); }

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
   * @see #trylockimpl(LockType, tryFunction, tryFunction, Function)
   */
  @Override
  public
  <R, E extends Throwable> R trylock(@NotNull LockType lockType, @NotNull tryFunction<Boolean, R, E> run,
                                     @Nullable Function<E, R> catchby, @Nullable Function<R, R> finaby)
  { return trylockimpl(lockType, run, catchby == null ? null : catchby::apply, finaby); }

  /**
   * {@inheritDoc}
   *
   * @param lockType {@inheritDoc}
   * @param run      {@inheritDoc}
   * @param finaby   {@inheritDoc}
   * @param <R>      {@inheritDoc}
   * @param <E>      {@inheritDoc}
   *
   * @return {@inheritDoc}
   *
   * @throws E {@inheritDoc}
   * @see #trylockimpl(LockType, tryFunction, tryFunction, Function)
   */
  @Override
  public
  <R, E extends Throwable> R trylock(@NotNull LockType lockType, @NotNull tryFunction<Boolean, R, E> run,
                                     @Nullable Function<R, R> finaby) throws E
  { return trylockimpl(lockType, run, e -> { throw e; }, finaby); }

  /**
   * 更新默认锁上下文参数
   * <p>
   * 该方法是非线程安全的，因为每次执行前都会先保存默认参数的引用，更新时应该传入一个新的参数对象，至少不能是同一个对象
   *
   * @param lockThreadContext 新的默认上下文参数
   */
  public
  void setDefaultLockThreadContext(@NotNull LockThreadContext<T> lockThreadContext) {
    this.defaultLockThreadContext = lockThreadContext;
  }

  /** 获取当前默认上下文参数 */
  @NotNull
  public
  LockThreadContext<T> defaultLockThreadContext() { return defaultLockThreadContext; }

  /**
   * 获取当前线程的上下文参数
   * <p>
   * 如果没有设置独立的上下文参数，则使用{@link #defaultLockThreadContext}
   */
  @NotNull
  public
  LockThreadContext<T> getLockThreadContext() {
    var c = lockThreadContext.get();
    if ( c == null )
      return defaultLockThreadContext;
    return c;
  }

  /**
   * 设置当前线程上下文参数
   * <p>
   * 要求不在锁内使用，否则可能会出现异常
   */
  public
  void setLockThreadContext(@NotNull LockThreadContext<T> c) { lockThreadContext.set(c); }

  /**
   * 移除当前上下文参数
   * <p>
   * 通常情况下执行结束后会自动移除，但如果手动设置后并没有执行锁的情况下才需要手动移除
   */
  public
  void removeLockThreadContext() { lockThreadContext.remove(); }

  /**
   * <h2>当前线程的锁上下文.</h2>
   * 根据该参数可以改变使用的锁对象，更改当前对象支持的上锁方式，与参数上锁的超时时间等<br/>
   * 参数上锁时间为{@code null}时为不使用带超时功能的参数上锁
   *
   * @param <T> 用于上锁的对象
   *
   * @author fybug
   * @version 0.0.1
   * @since AbstractSyLock 0.0.1
   */
  public static
  class LockThreadContext<T> {
    /** 本次锁对象 */
    @Setter @Getter @NotNull protected T lock;
    /**
     * 是否支持线程中断
     *
     * @see InterruptedException
     */
    @Setter @Getter protected boolean interruptiblyLock;
    /**
     * 尝试锁时间限制
     * <p>
     * 传入为{@code null}时为不使用超时功能
     *
     * @see InterruptedException
     */
    @Setter @Getter @Nullable protected Time tryTimeout;

    public
    LockThreadContext(@NotNull T lock, boolean interruptiblyLock, @Nullable Time tryTimeout) {
      this.lock = lock;
      this.interruptiblyLock = interruptiblyLock;
      this.tryTimeout = tryTimeout;
    }
  }

  /**
   * <h2>时间对象.</h2>
   * 用于记录时间，支持设置时间类型
   *
   * @author fybug
   * @version 0.0.1
   * @since AbstractSyLock 0.0.1
   */
  public static
  class Time {
    @Getter protected long timeout;
    @Getter @NotNull protected TimeUnit unit;

    public
    Time(long timeout, @NotNull TimeUnit unit) {
      this.timeout = timeout;
      this.unit = unit;
    }
  }
}
