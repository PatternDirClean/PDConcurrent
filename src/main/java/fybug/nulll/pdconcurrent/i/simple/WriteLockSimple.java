package fybug.nulll.pdconcurrent.i.simple;
import fybug.nulll.pdconcurrent.e.LockType;
import fybug.nulll.pdconcurrent.fun.tryRunnable;
import fybug.nulll.pdconcurrent.fun.trySupplier;
import fybug.nulll.pdconcurrent.i.WriteLock;
import jakarta.validation.constraints.NotNull;

/**
 * <h2>并发写锁支持简易拓展.</h2>
 *
 * @author fybug
 * @version 0.0.1
 * @since simple 0.0.1
 */
@SuppressWarnings("unused")
public
interface WriteLockSimple extends WriteLock {
  /**
   * 使用写锁执行指定回调
   * <p>
   * {@link #lock(LockType, trySupplier)}指定写锁的变种
   */
  default
  <R> R write(@NotNull trySupplier<R> run) throws Exception
  { return lock(LockType.WRITE, run); }

  /**
   * 使用写锁执行指定回调
   * <p>
   * {@link #lock(LockType, tryRunnable)}指定写锁的变种
   */
  default
  void write(@NotNull tryRunnable run) throws Exception
  { lock(LockType.WRITE, run); }
}
