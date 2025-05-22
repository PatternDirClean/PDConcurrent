package fybug.nulll.pdconcurrent.i.simple;
import java.util.function.Function;

import fybug.nulll.pdconcurrent.e.LockType;
import fybug.nulll.pdconcurrent.fun.tryRunnable;
import fybug.nulll.pdconcurrent.fun.trySupplier;
import fybug.nulll.pdconcurrent.i.Lock;
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
  <R> R lock(@NotNull LockType lockType, @NotNull trySupplier<R> run) throws Exception
  { return lock(lockType, run, null); }

  /**
   * 使用锁执行指定回调
   * <p>
   * {@link #lock(LockType, tryRunnable, Runnable)}的简易变体
   */
  default
  void lock(@NotNull LockType lockType, @NotNull tryRunnable run) throws Exception
  { lock(lockType, run, null); }
}
