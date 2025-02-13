package fybug.nulll.pdconcurrent.i;
import java.util.function.Consumer;
import java.util.function.Function;

import fybug.nulll.pdconcurrent.e.LockType;
import fybug.nulll.pdconcurrent.fun.tryConsumer;
import fybug.nulll.pdconcurrent.fun.tryFunction;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

/**
 * <h2>TryLock 读写支持拓展.</h2>
 * {@link TryLock}的拓展，增加{@code tryread()}、{@code trywrite()}类方法用于隐藏{@link LockType}参数
 *
 * @author fybug
 * @version 0.0.1
 * @see LockType#READ
 * @see LockType#WRITE
 * @see TryLock
 * @since i 0.0.1
 */
@SuppressWarnings("unused")
public
interface TryReadWriteLock extends TryLock {
  /**
   * 尝试使用读锁执行指定回调
   * <p>
   * {@link #trylock(LockType, tryFunction, Function, Function)}指定读锁的变种
   *
   * @param run     带返回的回调，传入参数是否获取到锁
   * @param catchby 进入catch块后的回调，传入当前异常
   * @param finaby  进入finally块后的回调，传入前两个回调的返回值
   * @param <R>     要返回的数据类型
   *
   * @return 回调返回的内容
   */
  default
  <R> R tryread(@NotNull tryFunction<Boolean, R> run, @Nullable Function<Exception, R> catchby,
                @Nullable Function<R, R> finaby)
  { return trylock(LockType.READ, run, catchby, finaby); }

  /**
   * 尝试使用读锁执行指定回调
   * <p>
   * {@link #trylock(LockType, tryConsumer, Consumer, Runnable)}指定读锁的变种
   *
   * @param run     执行的回调，传入参数是否获取到锁
   * @param catchby 进入catch块后的回调，传入当前异常
   * @param finaby  进入finally块后的回调
   */
  default
  void tryread(@NotNull tryConsumer<Boolean> run, @Nullable Consumer<Exception> catchby, @Nullable Runnable finaby)
  { trylock(LockType.READ, run, catchby, finaby); }

  /**
   * 尝试使用读锁执行指定回调
   * <p>
   * {@link #trylock(LockType, tryFunction, Function)}指定读锁的变种
   *
   * @param run    带返回的回调，传入参数是否获取到锁
   * @param finaby 进入finally块后的回调，传入前两个回调的返回值，遇到异常传入{@code null}
   * @param <R>    要返回的数据类型
   *
   * @return 回调返回的内容，遇到异常不返回
   *
   * @throws Exception 异常类型根据实际运行时回调抛出决定
   */
  default
  <R> R tryread(@NotNull tryFunction<Boolean, R> run, @Nullable Function<R, R> finaby) throws Exception
  { return trylock(LockType.READ, run, finaby); }

  /**
   * 尝试使用读锁执行指定回调
   * <p>
   * {@link #trylock(LockType, tryFunction)}指定读锁的变种
   *
   * @param run 带返回的回调，传入参数是否获取到锁
   * @param <R> 要返回的数据类型
   *
   * @return 回调返回的内容，遇到异常不返回
   *
   * @throws Exception 异常类型根据实际运行时回调抛出决定
   */
  default
  <R> R tryread(@NotNull tryFunction<Boolean, R> run) throws Exception
  { return trylock(LockType.READ, run); }

  /**
   * 尝试使用读锁执行指定回调
   * <p>
   * {@link #trylock(LockType, tryConsumer, Runnable)}指定读锁的变种
   *
   * @param run    执行的回调，传入参数是否获取到锁
   * @param finaby 进入finally块后的回调
   *
   * @throws Exception 异常类型根据实际运行时回调抛出决定
   */
  default
  void tryread(@NotNull tryConsumer<Boolean> run, @Nullable Runnable finaby) throws Exception
  { trylock(LockType.READ, run, finaby); }

  /**
   * 尝试使用读锁执行指定回调
   * <p>
   * {@link #trylock(LockType, tryConsumer)}指定读锁的变种
   *
   * @param run 带返回的回调，传入参数是否获取到锁
   *
   * @throws Exception 异常类型根据实际运行时回调抛出决定
   */
  default
  void tryread(@NotNull tryConsumer<Boolean> run) throws Exception
  { trylock(LockType.READ, run); }

  /**
   * 尝试使用写锁执行指定回调
   * <p>
   * {@link #trylock(LockType, tryFunction, Function, Function)}指定写锁的变种
   *
   * @param run     带返回的回调，传入参数是否获取到锁
   * @param catchby 进入catch块后的回调，传入当前异常
   * @param finaby  进入finally块后的回调，传入前两个回调的返回值
   * @param <R>     要返回的数据类型
   *
   * @return 回调返回的内容
   */
  default
  <R> R trywrite(@NotNull tryFunction<Boolean, R> run, @Nullable Function<Exception, R> catchby,
                 @Nullable Function<R, R> finaby)
  { return trylock(LockType.WRITE, run, catchby, finaby); }

  /**
   * 尝试使用写锁执行指定回调
   * <p>
   * {@link #trylock(LockType, tryConsumer, Consumer, Runnable)}指定写锁的变种
   *
   * @param run     执行的回调，传入参数是否获取到锁
   * @param catchby 进入catch块后的回调，传入当前异常
   * @param finaby  进入finally块后的回调
   */
  default
  void trywrite(@NotNull tryConsumer<Boolean> run, @Nullable Consumer<Exception> catchby, @Nullable Runnable finaby)
  { trylock(LockType.WRITE, run, catchby, finaby); }

  /**
   * 尝试使用写锁执行指定回调
   * <p>
   * {@link #trylock(LockType, tryFunction, Function)}指定写锁的变种
   *
   * @param run    带返回的回调，传入参数是否获取到锁
   * @param finaby 进入finally块后的回调，传入前两个回调的返回值，遇到异常传入{@code null}
   * @param <R>    要返回的数据类型
   *
   * @return 回调返回的内容，遇到异常不返回
   *
   * @throws Exception 异常类型根据实际运行时回调抛出决定
   */
  default
  <R> R trywrite(@NotNull tryFunction<Boolean, R> run, @Nullable Function<R, R> finaby) throws Exception
  { return trylock(LockType.WRITE, run, finaby); }

  /**
   * 尝试使用写锁执行指定回调
   * <p>
   * {@link #trylock(LockType, tryFunction)}指定写锁的变种
   *
   * @param run 带返回的回调，传入参数是否获取到锁
   * @param <R> 要返回的数据类型
   *
   * @return 回调返回的内容，遇到异常不返回
   *
   * @throws Exception 异常类型根据实际运行时回调抛出决定
   */
  default
  <R> R trywrite(@NotNull tryFunction<Boolean, R> run) throws Exception
  { return trylock(LockType.WRITE, run); }

  /**
   * 尝试使用写锁执行指定回调
   * <p>
   * {@link #trylock(LockType, tryConsumer, Runnable)}指定写锁的变种
   *
   * @param run    执行的回调，传入参数是否获取到锁
   * @param finaby 进入finally块后的回调
   *
   * @throws Exception 异常类型根据实际运行时回调抛出决定
   */
  default
  void trywrite(@NotNull tryConsumer<Boolean> run, @Nullable Runnable finaby) throws Exception
  { trylock(LockType.WRITE, run, finaby); }

  /**
   * 尝试使用写锁执行指定回调
   * <p>
   * {@link #trylock(LockType, tryConsumer)}指定写锁的变种
   *
   * @param run 带返回的回调，传入参数是否获取到锁
   *
   * @throws Exception 异常类型根据实际运行时回调抛出决定
   */
  default
  void trywrite(@NotNull tryConsumer<Boolean> run) throws Exception
  { trylock(LockType.WRITE, run); }
}
