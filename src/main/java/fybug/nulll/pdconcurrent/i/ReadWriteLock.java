package fybug.nulll.pdconcurrent.i;
import java.util.function.Consumer;
import java.util.function.Function;

import fybug.nulll.pdconcurrent.e.LockType;
import fybug.nulll.pdconcurrent.fun.tryRunnable;
import fybug.nulll.pdconcurrent.fun.trySupplier;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

/**
 * <h2>并发管理读写支持拓展.</h2>
 * {@link Lock}的拓展，增加{@code read()}、{@code write()}类方法用于隐藏{@link LockType}参数
 *
 * @author fybug
 * @version 0.0.1
 * @see LockType#READ
 * @see LockType#WRITE
 * @see Lock
 * @since i 0.0.1
 */
@SuppressWarnings("unused")
public
interface ReadWriteLock extends Lock {
  /**
   * 使用读锁执行指定回调
   * <p>
   * {@link #lock(LockType, trySupplier, Function, Function)}指定读锁的变种
   *
   * @param run     带返回的回调
   * @param catchby 进入catch块后的回调，传入当前异常
   * @param finaby  进入finally块后的回调，传入前两个回调的返回值
   * @param <R>     要返回的数据类型
   *
   * @return 回调返回的内容
   */
  default
  <R> R read(@NotNull trySupplier<R> run, @Nullable Function<Exception, R> catchby, @Nullable Function<R, R> finaby)
  { return lock(LockType.READ, run, catchby, finaby); }

  /**
   * 使用读锁执行指定回调
   * <p>
   * {@link #lock(LockType, tryRunnable, Consumer, Runnable)}指定读锁的变种
   *
   * @param run     执行的回调
   * @param catchby 进入catch块后的回调，传入当前异常
   * @param finaby  进入finally块后的回调
   */
  default
  void read(@NotNull tryRunnable run, @Nullable Consumer<Exception> catchby, @Nullable Runnable finaby)
  { lock(LockType.READ, run, catchby, finaby); }

  /**
   * 使用读锁执行指定回调
   * <p>
   * {@link #lock(LockType, trySupplier, Function)}指定读锁的变种
   *
   * @param run    带返回的回调
   * @param finaby 进入finally块后的回调，传入前一个回调的返回值，遇到异常传入{@code null}
   * @param <R>    要返回的数据类型
   *
   * @return 回调返回的内容，遇到异常不返回
   *
   * @throws Exception 异常类型根据实际运行时回调抛出决定
   */
  default
  <R> R read(@NotNull trySupplier<R> run, @Nullable Function<R, R> finaby) throws Exception
  { return lock(LockType.READ, run, finaby); }

  /**
   * 使用读锁执行指定回调
   * <p>
   * {@link #lock(LockType, trySupplier)}指定读锁的变种
   *
   * @param run 带返回的回调
   * @param <R> 要返回的数据类型
   *
   * @return 回调返回的内容
   *
   * @throws Exception 异常类型根据实际运行时回调抛出决定
   */
  default
  <R> R read(@NotNull trySupplier<R> run) throws Exception
  { return lock(LockType.READ, run); }

  /**
   * 使用读锁执行指定回调
   * <p>
   * {@link #lock(LockType, tryRunnable, Runnable)}指定读锁的变种
   *
   * @param run    执行的回调
   * @param finaby 进入finally块后的回调
   *
   * @throws Exception 异常类型根据实际运行时回调抛出决定
   */
  default
  void read(@NotNull tryRunnable run, @Nullable Runnable finaby) throws Exception
  { lock(LockType.READ, run, finaby); }

  /**
   * 使用读锁执行指定回调
   * <p>
   * {@link #lock(LockType, tryRunnable)}指定读锁的变种
   *
   * @param run 执行的回调
   *
   * @throws Exception 异常类型根据实际运行时回调抛出决定
   */
  default
  void read(@NotNull tryRunnable run) throws Exception
  { lock(LockType.READ, run); }


  /**
   * 使用写锁执行指定回调
   * <p>
   * {@link #lock(LockType, trySupplier, Function, Function)}指定写锁的变种
   *
   * @param run     带返回的回调
   * @param catchby 进入catch块后的回调，传入当前异常
   * @param finaby  进入finally块后的回调，传入前两个回调的返回值
   * @param <R>     要返回的数据类型
   *
   * @return 回调返回的内容
   */
  default
  <R> R write(@NotNull trySupplier<R> run, @Nullable Function<Exception, R> catchby, @Nullable Function<R, R> finaby)
  { return lock(LockType.WRITE, run, catchby, finaby); }

  /**
   * 使用写锁执行指定回调
   * <p>
   * {@link #lock(LockType, tryRunnable, Consumer, Runnable)}指定写锁的变种
   *
   * @param run     执行的回调
   * @param catchby 进入catch块后的回调，传入当前异常
   * @param finaby  进入finally块后的回调
   */
  default
  void write(@NotNull tryRunnable run, @Nullable Consumer<Exception> catchby, @Nullable Runnable finaby)
  { lock(LockType.WRITE, run, catchby, finaby); }

  /**
   * 使用写锁执行指定回调
   * <p>
   * {@link #lock(LockType, trySupplier, Function)}指定写锁的变种
   *
   * @param run    带返回的回调
   * @param finaby 进入finally块后的回调，传入前一个回调的返回值，遇到异常传入{@code null}
   * @param <R>    要返回的数据类型
   *
   * @return 回调返回的内容，遇到异常不返回
   *
   * @throws Exception 异常类型根据实际运行时回调抛出决定
   */
  default
  <R> R write(@NotNull trySupplier<R> run, @Nullable Function<R, R> finaby) throws Exception
  { return lock(LockType.WRITE, run, finaby); }

  /**
   * 使用写锁执行指定回调
   * <p>
   * {@link #lock(LockType, trySupplier)}指定写锁的变种
   *
   * @param run 带返回的回调
   * @param <R> 要返回的数据类型
   *
   * @return 回调返回的内容
   *
   * @throws Exception 异常类型根据实际运行时回调抛出决定
   */
  default
  <R> R write(@NotNull trySupplier<R> run) throws Exception
  { return lock(LockType.WRITE, run); }

  /**
   * 使用写锁执行指定回调
   * <p>
   * {@link #lock(LockType, tryRunnable, Runnable)}指定写锁的变种
   *
   * @param run    执行的回调
   * @param finaby 进入finally块后的回调
   *
   * @throws Exception 异常类型根据实际运行时回调抛出决定
   */
  default
  void write(@NotNull tryRunnable run, @Nullable Runnable finaby) throws Exception
  { lock(LockType.WRITE, run, finaby); }

  /**
   * 使用写锁执行指定回调
   * <p>
   * {@link #lock(LockType, tryRunnable)}指定写锁的变种
   *
   * @param run 执行的回调
   *
   * @throws Exception 异常类型根据实际运行时回调抛出决定
   */
  default
  void write(@NotNull tryRunnable run) throws Exception
  { lock(LockType.WRITE, run); }
}
