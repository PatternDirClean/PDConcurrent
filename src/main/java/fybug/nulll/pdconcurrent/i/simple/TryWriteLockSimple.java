package fybug.nulll.pdconcurrent.i.simple;
import fybug.nulll.pdconcurrent.e.LockType;
import fybug.nulll.pdconcurrent.i.TryWriteLock;
import fybug.nulll.pdutilfunctionexpand.tryConsumer;
import fybug.nulll.pdutilfunctionexpand.tryFunction;
import jakarta.validation.constraints.NotNull;

/**
 * <h2>TryLock 写锁支持简易拓展.</h2>
 *
 * @author fybug
 * @version 0.0.1
 * @since simple 0.0.1
 */
@SuppressWarnings("unused")
public
interface TryWriteLockSimple extends TryWriteLock {
  /**
   * 尝试使用写锁执行指定回调
   * <p>
   * {@link #trylock(LockType, tryFunction)}指定写锁的变种
   */
  default
  <R, E extends Throwable> R trywrite(@NotNull tryFunction<Boolean, R, E> run) throws E
  { return trylock(LockType.WRITE, run); }

  /**
   * 尝试使用写锁执行指定回调
   * <p>
   * {@link #trylock(LockType, tryConsumer)}指定写锁的变种
   */
  default
  <E extends Throwable> void trywrite(@NotNull tryConsumer<Boolean, E> run) throws E
  { trylock(LockType.WRITE, run); }
}
