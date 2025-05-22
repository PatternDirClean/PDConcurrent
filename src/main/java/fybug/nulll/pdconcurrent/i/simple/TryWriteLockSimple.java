package fybug.nulll.pdconcurrent.i.simple;
import fybug.nulll.pdconcurrent.e.LockType;
import fybug.nulll.pdconcurrent.fun.tryConsumer;
import fybug.nulll.pdconcurrent.fun.tryFunction;
import fybug.nulll.pdconcurrent.i.TryWriteLock;
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
  <R> R trywrite(@NotNull tryFunction<Boolean, R> run) throws Exception
  { return trylock(LockType.WRITE, run); }

  /**
   * 尝试使用写锁执行指定回调
   * <p>
   * {@link #trylock(LockType, tryConsumer)}指定写锁的变种
   */
  default
  void trywrite(@NotNull tryConsumer<Boolean> run) throws Exception
  { trylock(LockType.WRITE, run); }
}
