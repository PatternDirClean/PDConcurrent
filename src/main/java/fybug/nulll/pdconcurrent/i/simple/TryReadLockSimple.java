package fybug.nulll.pdconcurrent.i.simple;
import fybug.nulll.pdconcurrent.e.LockType;
import fybug.nulll.pdconcurrent.fun.tryConsumer;
import fybug.nulll.pdconcurrent.fun.tryFunction;
import fybug.nulll.pdconcurrent.i.TryReadLock;
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
  <R> R tryread(@NotNull tryFunction<Boolean, R> run) throws Exception
  { return trylock(LockType.READ, run); }

  /**
   * 尝试使用读锁执行指定回调
   * <p>
   * {@link #trylock(LockType, tryConsumer)}指定读锁的变种
   */
  default
  void tryread(@NotNull tryConsumer<Boolean> run) throws Exception
  { trylock(LockType.READ, run); }
}
