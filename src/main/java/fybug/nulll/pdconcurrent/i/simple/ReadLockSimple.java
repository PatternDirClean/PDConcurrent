package fybug.nulll.pdconcurrent.i.simple;
import fybug.nulll.pdconcurrent.e.LockType;
import fybug.nulll.pdconcurrent.i.ReadLock;
import fybug.nulll.pdutilfunctionexpand.tryRunnable;
import fybug.nulll.pdutilfunctionexpand.trySupplier;
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
  <R, E extends Throwable> R read(@NotNull trySupplier<R, E> run) throws E
  { return lock(LockType.READ, run); }

  /**
   * 使用读锁执行指定回调
   * <p>
   * {@link #lock(LockType, tryRunnable)}指定读锁的变种
   */
  default
  <E extends Throwable> void read(@NotNull tryRunnable<E> run) throws E
  { lock(LockType.READ, run); }
}
