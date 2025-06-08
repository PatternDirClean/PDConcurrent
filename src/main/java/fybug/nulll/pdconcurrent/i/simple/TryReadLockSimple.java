package fybug.nulll.pdconcurrent.i.simple;
import fybug.nulll.pdconcurrent.e.LockType;
import fybug.nulll.pdconcurrent.i.TryReadLock;
import fybug.nulll.pdutilfunctionexpand.tryConsumer;
import fybug.nulll.pdutilfunctionexpand.tryFunction;
import jakarta.validation.constraints.NotNull;

/**
 * <h2>TryLock 读锁支持简易拓展.</h2>
 *
 * @author fybug
 * @version 0.0.1
 * @since simple 0.0.1
 */
@SuppressWarnings("unused")
public
interface TryReadLockSimple extends TryReadLock {
  /**
   * 尝试使用读锁执行指定回调
   * <p>
   * {@link #trylock(LockType, tryFunction)}指定读锁的变种
   */
  default
  <R, E extends Throwable> R tryread(@NotNull tryFunction<Boolean, R, E> run) throws E
  { return trylock(LockType.READ, run); }

  /**
   * 尝试使用读锁执行指定回调
   * <p>
   * {@link #trylock(LockType, tryConsumer)}指定读锁的变种
   */
  default
  <E extends Throwable> void tryread(@NotNull tryConsumer<Boolean, E> run) throws E
  { trylock(LockType.READ, run); }
}
