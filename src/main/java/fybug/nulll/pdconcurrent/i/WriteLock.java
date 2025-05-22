package fybug.nulll.pdconcurrent.i;
import java.util.function.Consumer;
import java.util.function.Function;

import fybug.nulll.pdconcurrent.e.LockType;
import fybug.nulll.pdconcurrent.fun.tryRunnable;
import fybug.nulll.pdconcurrent.fun.trySupplier;
import fybug.nulll.pdconcurrent.i.simple.LockSimple;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

/**
 * <h2>并发管理写锁支持拓展.</h2>
 * {@link Lock}的拓展，增加 {@code write()} 类方法用于隐藏 {@link LockType} 参数
 *
 * @author fybug
 * @version 0.0.1
 * @see LockType#WRITE
 * @see Lock
 * @since i 0.0.2
 */
@SuppressWarnings("unused")
public
interface WriteLock extends LockSimple {
  /**
   * 使用写锁执行指定回调
   * <p>
   * {@link #lock(LockType, trySupplier, Function, Function)}指定写锁的变种
   */
  default
  <R> R write(@NotNull trySupplier<R> run, @Nullable Function<Exception, R> catchby, @Nullable Function<R, R> finaby)
  { return lock(LockType.WRITE, run, catchby, finaby); }

  /**
   * 使用写锁执行指定回调
   * <p>
   * {@link #lock(LockType, tryRunnable, Consumer, Runnable)}指定写锁的变种
   */
  default
  void write(@NotNull tryRunnable run, @Nullable Consumer<Exception> catchby, @Nullable Runnable finaby)
  { lock(LockType.WRITE, run, catchby, finaby); }

  /**
   * 使用写锁执行指定回调
   * <p>
   * {@link #lock(LockType, trySupplier, Function)}指定写锁的变种
   */
  default
  <R> R write(@NotNull trySupplier<R> run, @Nullable Function<R, R> finaby) throws Exception
  { return lock(LockType.WRITE, run, finaby); }

  /**
   * 使用写锁执行指定回调
   * <p>
   * {@link #lock(LockType, tryRunnable, Runnable)}指定写锁的变种
   */
  default
  void write(@NotNull tryRunnable run, @Nullable Runnable finaby) throws Exception
  { lock(LockType.WRITE, run, finaby); }
}
