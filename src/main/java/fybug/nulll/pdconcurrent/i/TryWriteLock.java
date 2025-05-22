package fybug.nulll.pdconcurrent.i;
import java.util.function.Consumer;
import java.util.function.Function;

import fybug.nulll.pdconcurrent.e.LockType;
import fybug.nulll.pdconcurrent.fun.tryConsumer;
import fybug.nulll.pdconcurrent.fun.tryFunction;
import fybug.nulll.pdconcurrent.i.simple.TryLockSimple;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

/**
 * <h2>TryLock 写锁支持拓展.</h2>
 * {@link TryLock}的拓展，增加 {@code trywrite()} 类方法用于隐藏 {@link LockType} 参数
 *
 * @author fybug
 * @version 0.0.1
 * @see LockType#WRITE
 * @see TryLock
 * @since i 0.0.2
 */
@SuppressWarnings("unused")
public
interface TryWriteLock extends TryLockSimple {
  /**
   * 尝试使用写锁执行指定回调
   * <p>
   * {@link #trylock(LockType, tryFunction, Function, Function)}指定写锁的变种
   */
  default
  <R> R trywrite(@NotNull tryFunction<Boolean, R> run, @Nullable Function<Exception, R> catchby,
                 @Nullable Function<R, R> finaby)
  { return trylock(LockType.WRITE, run, catchby, finaby); }

  /**
   * 尝试使用写锁执行指定回调
   * <p>
   * {@link #trylock(LockType, tryConsumer, Consumer, Runnable)}指定写锁的变种
   */
  default
  void trywrite(@NotNull tryConsumer<Boolean> run, @Nullable Consumer<Exception> catchby, @Nullable Runnable finaby)
  { trylock(LockType.WRITE, run, catchby, finaby); }

  /**
   * 尝试使用写锁执行指定回调
   * <p>
   * {@link #trylock(LockType, tryFunction, Function)}指定写锁的变种
   */
  default
  <R> R trywrite(@NotNull tryFunction<Boolean, R> run, @Nullable Function<R, R> finaby) throws Exception
  { return trylock(LockType.WRITE, run, finaby); }

  /**
   * 尝试使用写锁执行指定回调
   * <p>
   * {@link #trylock(LockType, tryConsumer, Runnable)}指定写锁的变种
   */
  default
  void trywrite(@NotNull tryConsumer<Boolean> run, @Nullable Runnable finaby) throws Exception
  { trylock(LockType.WRITE, run, finaby); }
}
