package fybug.nulll.pdconcurrent.i.simple;
import java.util.function.Function;

import fybug.nulll.pdconcurrent.e.LockType;
import fybug.nulll.pdconcurrent.i.TryLock;
import fybug.nulll.pdutilfunctionexpand.tryConsumer;
import fybug.nulll.pdutilfunctionexpand.tryFunction;
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
  <R, E extends Throwable> R trylock(@NotNull LockType lockType, @NotNull tryFunction<Boolean, R, E> run) throws E
  { return trylock(lockType, run, null); }

  /**
   * 尝试使用锁执行指定回调
   * <p>
   * {@link #trylock(LockType, tryConsumer, Runnable)}的简易变体
   */
  default
  <E extends Throwable> void trylock(@NotNull LockType lockType, @NotNull tryConsumer<Boolean, E> run) throws E
  { trylock(lockType, run, null); }
}
