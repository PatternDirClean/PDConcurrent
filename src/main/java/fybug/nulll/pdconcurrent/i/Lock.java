package fybug.nulll.pdconcurrent.i;
import java.util.function.Consumer;
import java.util.function.Function;

import fybug.nulll.pdconcurrent.e.LockType;
import fybug.nulll.pdconcurrent.fun.tryRunnable;
import fybug.nulll.pdconcurrent.fun.trySupplier;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

/**
 * <h2>基础锁管理.</h2>
 * 通过传入回调的方式隐藏内部的并发管理方法，并支持复用内部的try块，通过传入的回调插入到catch，finally块中执行<br/>
 * {@code lock()}方法用于根据传入的{@link LockType}申请不同的锁类型进行执行<br/>
 * 有可抛出异常的方法变体，可在传入的接口中抛出异常
 *
 * @author fybug
 * @version 0.0.1
 * @see LockType
 * @since i 0.0.1
 */
interface Lock {
  /**
   * 使用锁执行指定回调
   * <p>
   * 可通过传入{@link LockType}指定锁的类型，运行时自带try-catch-finally块，通过三个回调参数插入不同的块中执行<br/>
   * 所有回调均在并发域内执行
   *
   * @param lockType 锁类型
   * @param run      带返回的回调
   * @param catchby  进入catch块后的回调，传入当前异常
   * @param finaby   进入finally块后的回调，传入前两个回调的返回值
   * @param <R>      要返回的数据类型
   *
   * @return 回调返回的内容
   *
   * @implSpec 如果有传入 {@code finaby} 回调则返回值由{@code finaby}主导，传入{@code finaby}的值根据是否发生异常传入{@code run}的返回值或{@code catchby}的返回值<br/>
   * 任意一个回调为空时直接穿透，使用上一个正确执行的值进行传递或者返回，传递值应默认为{@code null}用于应对{@code catchby}和{@code finaby}都为空但是发生了异常的情况
   */
  <R> R lock(@NotNull LockType lockType, @NotNull trySupplier<R> run, @Nullable Function<Exception, R> catchby,
             @Nullable Function<R, R> finaby);

  /**
   * 使用锁执行指定回调
   * <p>
   * {@link #lock(LockType, trySupplier, Function, Function)}的无返回变体
   *
   * @param lockType 锁类型
   * @param run      执行的回调
   * @param catchby  进入catch块后的回调，传入当前异常
   * @param finaby   进入finally块后的回调
   */
  default
  void lock(@NotNull LockType lockType, @NotNull tryRunnable run, @Nullable Consumer<Exception> catchby,
            @Nullable Runnable finaby)
  {
    lock(lockType, () -> {
      run.run();
      return null;
    }, catchby == null ? null : e -> {
      catchby.accept(e);
      return null;
    }, finaby == null ? null : _ -> {
      finaby.run();
      return null;
    });
  }

  /**
   * 使用锁执行指定回调
   * <p>
   * {@link #lock(LockType, trySupplier, Function, Function)}的可抛异常变体<br/>
   * 运行时改为使用try-finally块，通过两个回调参数插入不同的块中执行，遇到异常会抛出
   *
   * @param lockType 锁类型
   * @param run      带返回的回调
   * @param finaby   进入finally块后的回调，传入前一个回调的返回值，遇到异常传入{@code null}
   * @param <R>      要返回的数据类型
   *
   * @return 回调返回的内容，遇到异常不返回
   *
   * @throws Exception 异常类型根据实际运行时回调抛出决定
   * @implSpec 如果有传入 {@code finaby} 回调则返回值由{@code finaby}主导，传入{@code finaby}的值根据是否发生异常传入{@code run}的返回值或{@code null}<br/>
   * 任意一个回调为空时直接穿透，使用上一个正确执行的值进行传递或者返回，发生异常会执行{@code finaby}但是不会返回内容
   */
  <R> R lock(@NotNull LockType lockType, @NotNull trySupplier<R> run, @Nullable Function<R, R> finaby) throws Exception;

  /**
   * 使用锁执行指定回调
   * <p>
   * {@link #lock(LockType, trySupplier, Function)}的简易变体
   *
   * @param lockType 锁类型
   * @param run      带返回的回调
   * @param <R>      要返回的数据类型
   *
   * @return 回调返回的内容，遇到异常不返回
   *
   * @throws Exception 异常类型根据实际运行时回调抛出决定
   */
  default
  <R> R lock(@NotNull LockType lockType, @NotNull trySupplier<R> run) throws Exception
  { return lock(lockType, run, null); }

  /**
   * 使用锁执行指定回调
   * <p>
   * {@link #lock(LockType, trySupplier, Function)}的无返回变体
   *
   * @param lockType 锁类型
   * @param run      执行的回调
   * @param finaby   进入finally块后的回调
   *
   * @throws Exception 异常类型根据实际运行时回调抛出决定
   */
  default
  void lock(@NotNull LockType lockType, @NotNull tryRunnable run, @Nullable Runnable finaby) throws Exception {
    lock(lockType, () -> {
      run.run();
      return null;
    }, finaby == null ? null : _ -> {
      finaby.run();
      return null;
    });
  }

  /**
   * 使用锁执行指定回调
   * <p>
   * {@link #lock(LockType, tryRunnable, Runnable)}的简易变体
   *
   * @param lockType 锁类型
   * @param run      执行的回调
   *
   * @throws Exception 异常类型根据实际运行时回调抛出决定
   */
  default
  void lock(@NotNull LockType lockType, @NotNull tryRunnable run) throws Exception
  { lock(lockType, run, null); }
}
