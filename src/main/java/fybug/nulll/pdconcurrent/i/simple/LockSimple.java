package fybug.nulll.pdconcurrent.i.simple;
import java.util.function.Function;

import fybug.nulll.pdconcurrent.e.LockType;
import fybug.nulll.pdconcurrent.i.Lock;
import fybug.nulll.pdutilfunctionexpand.tryRunnable;
import fybug.nulll.pdutilfunctionexpand.trySupplier;
import jakarta.validation.constraints.NotNull;

/**
 * <h2>基础锁管理简易拓展.</h2>
 *
 * @author fybug
 * @version 0.0.1
 * @since simple 0.0.1
 */
public
interface LockSimple extends Lock {
  /**
   * 使用锁执行指定回调
   * <p>
   * {@link #lock(LockType, trySupplier, Function)}的简易变体
   */
  default
  <R, E extends Throwable> R lock(@NotNull LockType lockType, @NotNull trySupplier<R, E> run) throws E
  { return lock(lockType, run, null); }

  /**
   * 使用锁执行指定回调
   * <p>
   * {@link #lock(LockType, tryRunnable, Runnable)}的简易变体
   */
  default
  <E extends Throwable> void lock(@NotNull LockType lockType, @NotNull tryRunnable<E> run) throws E
  { lock(lockType, run, null); }
}
