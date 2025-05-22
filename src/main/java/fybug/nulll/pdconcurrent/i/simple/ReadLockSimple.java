package fybug.nulll.pdconcurrent.i.simple;
import fybug.nulll.pdconcurrent.e.LockType;
import fybug.nulll.pdconcurrent.fun.tryRunnable;
import fybug.nulll.pdconcurrent.fun.trySupplier;
import fybug.nulll.pdconcurrent.i.ReadLock;
import jakarta.validation.constraints.NotNull;

/**
 * <h2>并发读锁支持简易拓展.</h2>
 *
 * @author fybug
 * @version 0.0.1
 * @since simple 0.0.1
 */
@SuppressWarnings("unused")
public
interface ReadLockSimple extends ReadLock {
  /**
   * 使用读锁执行指定回调
   * <p>
   * {@link #lock(LockType, trySupplier)}指定读锁的变种
   */
  default
  <R> R read(@NotNull trySupplier<R> run) throws Exception
  { return lock(LockType.READ, run); }

  /**
   * 使用读锁执行指定回调
   * <p>
   * {@link #lock(LockType, tryRunnable)}指定读锁的变种
   */
  default
  void read(@NotNull tryRunnable run) throws Exception
  { lock(LockType.READ, run); }
}
