package fybug.nulll.pdconcurrent.i.simple;
import java.util.function.Function;

import fybug.nulll.pdconcurrent.e.LockType;
import fybug.nulll.pdconcurrent.fun.tryConsumer;
import fybug.nulll.pdconcurrent.fun.tryFunction;
import fybug.nulll.pdconcurrent.i.TryLock;
import jakarta.validation.constraints.NotNull;

/**
 * <h2>TryLock 简易拓展.</h2>
 *
 * @author fybug
 * @version 0.0.1
 * @since simple 0.0.1
 */
public
interface TryLockSimple extends TryLock {
  /**
   * 尝试使用锁执行指定回调
   * <p>
   * {@link #trylock(LockType, tryFunction, Function)}的简易变体
   */
  default
  <R> R trylock(@NotNull LockType lockType, @NotNull tryFunction<Boolean, R> run) throws Exception
  { return trylock(lockType, run, null); }

  /**
   * 尝试使用锁执行指定回调
   * <p>
   * {@link #trylock(LockType, tryConsumer, Runnable)}的简易变体
   */
  default
  void trylock(@NotNull LockType lockType, @NotNull tryConsumer<Boolean> run) throws Exception
  { trylock(lockType, run, null); }
}
