package fybug.nulll.pdconcurrent.i;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

import fybug.nulll.pdconcurrent.e.LockType;
import fybug.nulll.pdutilfunctionexpand.tryFunction;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

/**
 * <h2>允许尝试上锁的并发管理.</h2>
 * {@link Lock}的拓展，增加{@code trylock()}类方法用于适配需要立刻判断是否获取到锁并即刻往下执行的情况
 *
 * @author fybug
 * @version 0.0.2
 * @since i 0.0.1
 */
public
interface TryLock {
  /**
   * 尝试使用锁执行指定回调
   * <p>
   * 可通过传入{@link LockType}指定锁的类型，但需要注意该方法并非一定能成功获取到锁，可通过{@code run}回调参数中传入的{@code boolean}判断是否成功获取到了锁<br/>
   * 运行时自带try-catch-finally块，通过三个回调参数插入不同的块中执行<br/>
   * 若成功获取锁则所有回调均在并发域内执行
   *
   * @param lockType 锁类型
   * @param run      带返回的回调，传入参数是否获取到锁
   * @param catchby  进入catch块后的回调，传入当前异常
   * @param finaby   进入finally块后的回调，传入前两个回调的返回值
   * @param <R>      要返回的数据类型
   * @param <E>      运行时异常类型
   *
   * @return 回调返回的内容
   *
   * @implSpec 该方法实现时应尽量使用如 {@link ReentrantLock#tryLock()} 之类的方法获取锁，并将是否成功获取传入{@code run}回调中<br/>
   * 如果有传入 {@code finaby} 回调则返回值由{@code finaby}主导，传入{@code finaby}的值根据是否发生异常传入{@code run}的返回值或{@code catchby}的返回值<br/>
   * 任意一个回调为空时直接穿透，使用上一个正确执行的值进行传递或者返回，传递值应默认为{@code null}用于应对{@code catchby}和{@code finaby}都为空但是发生了异常的情况
   */
  <R, E extends Throwable> R trylock(@NotNull LockType lockType, @NotNull tryFunction<Boolean, R, E> run,
                                     @Nullable Function<E, R> catchby, @Nullable Function<R, R> finaby);

  /**
   * 尝试使用锁执行指定回调
   * <p>
   * {@link #trylock(LockType, tryFunction, Function, Function)}的可抛异常变体<br/>
   * 运行时改为使用try-finally块，通过两个回调参数插入不同的块中执行，遇到异常会抛出
   *
   * @param lockType 锁类型
   * @param run      带返回的回调，传入参数是否获取到锁
   * @param finaby   进入finally块后的回调，传入前两个回调的返回值，遇到异常传入{@code null}
   * @param <R>      要返回的数据类型
   * @param <E>      运行时异常类型
   *
   * @return 回调返回的内容，遇到异常不返回
   *
   * @throws E 异常类型根据实际运行时回调抛出决定
   * @implSpec 该方法实现时应尽量使用如 {@link ReentrantLock#tryLock()} 之类的方法获取锁，并将是否成功获取传入{@code run}回调中<br/>
   * 如果有传入 {@code finaby} 回调则返回值由{@code finaby}主导，传入{@code finaby}的值根据是否发生异常传入{@code run}的返回值或{@code catchby}的返回值<br/>
   * 任意一个回调为空时直接穿透，使用上一个正确执行的值进行传递或者返回，传递值应默认为{@code null}用于应对{@code catchby}和{@code finaby}都为空但是发生了异常的情况
   */
  <R, E extends Throwable> R trylock(@NotNull LockType lockType, @NotNull tryFunction<Boolean, R, E> run,
                                     @Nullable Function<R, R> finaby) throws E;
}
