package fybug.nulll.pdconcurrent;
import java.util.function.Consumer;
import java.util.function.Function;

import fybug.nulll.pdconcurrent.e.LockType;
import fybug.nulll.pdconcurrent.fun.tryRunnable;
import fybug.nulll.pdconcurrent.fun.trySupplier;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

/**
 * <h2>并发管理.</h2>
 * 通过传入回调的方式隐藏内部的并发管理方法，并支持复用内部的try块，通过传入的回调插入到catch，finally块中执行<br/>
 * {@code **lock()} 方法用于根据传入的{@link LockType}申请不同的锁类型进行执行<br/>
 * {@code **read()} 方法用于申请使用读锁，{@code **write()} 用于申请使用写锁，只有在使用读写锁实现 {@link RWLock} 才有区别。其余实现两个之间无区别<br/>
 * {@code try**()} 类型的方法为可抛出异常的方法，可在传入的接口中抛出异常
 * <br/><br/>
 * 使用 {@code new**Lock()} 的方法获取不同并发管理的实例<br/>
 *
 * @author fybug
 * @version 0.1.0
 * @apiNote 并发控制通用接口，规定并实现大部分通用控制功能
 * @since PDConcurrent 0.0.1
 */
public
interface SyLock {
	/**
	 * 使用锁执行指定回调
	 * <p>
	 * 可通过传入{@link LockType}指定锁的类型，运行时自带try-catch-finally块，通过三个回调参数插入不同的块中执行<br/>
	 * 所有回调均在并发域内执行
	 *
	 * @param lockType 锁类型
	 * @param run      带返回的回调
	 * @param catchby  进入catch块后的回调，传入当前异常
	 * @param finaby   进入finally块后的回调，传入前两个回调的返回值
	 * @param <R>      要返回的数据类型
	 *
	 * @return 回调返回的内容
	 *
	 * @implSpec 如果有传入 {@code finaby} 回调则返回值由{@code finaby}主导，传入{@code finaby}的值根据是否发生异常传入{@code run}的返回值或{@code catchby}的返回值<br/>
	 * 任意一个回调为空时直接穿透，使用上一个正确执行的值进行传递或者返回，返回应有默认值{@code null}用于应对{@code catchby}和{@code finaby}都为空但是发生了异常的情况
	 * @see trySupplier
	 * @see Function
	 * @see LockType
	 * @since 0.1.0
	 */
	<R> R lock(@NotNull LockType lockType, @NotNull trySupplier<R> run, @Nullable Function<Exception, R> catchby,
						 @Nullable Function<R, R> finaby);

	/**
	 * 使用锁执行指定回调
	 * <p>
	 * 可通过传入{@link LockType}指定锁的类型，运行时自带try-catch-finally块，通过三个回调参数插入不同的块中执行<br/>
	 * 所有回调均在并发域内执行
	 *
	 * @param lockType 锁类型
	 * @param run      执行的回调
	 * @param catchby  进入catch块后的回调，传入当前异常
	 * @param finaby   进入finally块后的回调
	 *
	 * @see tryRunnable
	 * @see Consumer
	 * @see Runnable
	 * @see LockType
	 * @see #lock(LockType, trySupplier, Function, Function)
	 * @since 0.1.0
	 */
	@SuppressWarnings("unused")
	default
	void lock(@NotNull LockType lockType, @NotNull tryRunnable run, @Nullable Consumer<Exception> catchby,
						@Nullable Runnable finaby)
	{
		lock(lockType, () -> {
			run.run();
			return null;
		}, catchby == null ? null : e -> {
			catchby.accept(e);
			return null;
		}, finaby == null ? null : r -> {
			finaby.run();
			return null;
		});
	}

	//-----------------------------------------------

	/**
	 * 使用锁执行指定回调
	 * <p>
	 * 可通过传入{@link LockType}指定锁的类型，运行时自带try-catch-finally块，遇到异常不处理返回{@code null}
	 *
	 * @param lockType 锁类型
	 * @param run      带返回的回调
	 * @param <R>      要返回的数据类型
	 *
	 * @return 回调返回的内容，遇到异常返回{@code null}
	 *
	 * @see trySupplier
	 * @see LockType
	 * @see #lock(LockType, trySupplier, Function, Function)
	 * @since 0.1.0
	 */
	default
	<R> R lock(@NotNull LockType lockType, @NotNull trySupplier<R> run) { return lock(lockType, run, null, null); }

	/**
	 * 使用锁执行指定回调
	 * <p>
	 * 可通过传入{@link LockType}指定锁的类型，运行时自带try-catch-finally块，遇到异常不处理
	 *
	 * @param lockType 锁类型
	 * @param run      执行的回调
	 *
	 * @see tryRunnable
	 * @see LockType
	 * @see #lock(LockType, trySupplier, Function, Function)
	 * @since 0.1.0
	 */
	default
	void lock(@NotNull LockType lockType, @NotNull tryRunnable run) {
		lock(lockType, () -> {
			run.run();
			return null;
		}, null, null);
	}

	//-----------------------------------------------

	/**
	 * 尝试使用锁执行指定回调
	 * <p>
	 * 可通过传入{@link LockType}指定锁的类型，运行时自带try-finally块，通过两个回调参数插入不同的块中执行，遇到异常会抛出<br/>
	 * 所有回调均在并发域内执行
	 *
	 * @param lockType 锁类型
	 * @param run      带返回的回调
	 * @param finaby   进入finally块后的回调，传入前一个回调的返回值，遇到异常传入{@code null}
	 * @param <R>      要返回的数据类型
	 *
	 * @return 回调返回的内容，遇到异常不返回
	 *
	 * @implSpec 如果有传入 {@code finaby} 回调则返回值由{@code finaby}主导，传入{@code finaby}的值根据是否发生异常传入{@code run}的返回值或{@code null}<br/>
	 * 任意一个回调为空时直接穿透，使用上一个正确执行的值进行传递或者返回，发生异常会执行{@code finaby}但是不会返回内容
	 * @see trySupplier
	 * @see Function
	 * @see LockType
	 * @since 0.1.0
	 */
	<R> R trylock(@NotNull LockType lockType, @NotNull trySupplier<R> run, @Nullable Function<R, R> finaby) throws Exception;

	/**
	 * 尝试使用锁执行指定回调
	 * <p>
	 * 可通过传入{@link LockType}指定锁的类型，运行时自带try-finally块，通过两个回调参数插入不同的块中执行，遇到异常会抛出<br/>
	 * 所有回调均在并发域内执行
	 *
	 * @param lockType 锁类型
	 * @param run      执行的回调
	 * @param finaby   进入finally块后的回调
	 *
	 * @see tryRunnable
	 * @see Runnable
	 * @see LockType
	 * @see #trylock(LockType, trySupplier, Function)
	 * @since 0.1.0
	 */
	@SuppressWarnings("unused")
	default
	void trylock(@NotNull LockType lockType, @NotNull tryRunnable run, @Nullable Runnable finaby) throws Exception {
		trylock(lockType, () -> {
			run.run();
			return null;
		}, finaby == null ? null : r -> {
			finaby.run();
			return null;
		});
	}

	//-----------------------------------------------

	/**
	 * 尝试使用锁执行指定回调
	 * <p>
	 * 可通过传入{@link LockType}指定锁的类型，运行时自带try-finally块，遇到异常会抛出
	 *
	 * @param lockType 锁类型
	 * @param run      带返回的回调
	 * @param <R>      要返回的数据类型
	 *
	 * @return 回调返回的内容，遇到异常不返回
	 *
	 * @see trySupplier
	 * @see LockType
	 * @see #trylock(LockType, trySupplier, Function)
	 * @since 0.1.0
	 */
	default
	<R> R trylock(@NotNull LockType lockType, @NotNull trySupplier<R> run) throws Exception
	{ return trylock(lockType, run, null); }

	/**
	 * 尝试使用锁执行指定回调
	 * <p>
	 * 可通过传入{@link LockType}指定锁的类型，运行时自带try-finally块，遇到异常会抛出
	 *
	 * @param lockType 锁类型
	 * @param run      执行的回调
	 *
	 * @see tryRunnable
	 * @see LockType
	 * @see #trylock(LockType, trySupplier, Function)
	 * @since 0.1.0
	 */
	default
	void trylock(@NotNull LockType lockType, @NotNull tryRunnable run) throws Exception {
		trylock(lockType, () -> {
			run.run();
			return null;
		}, null);
	}

	/*--------------------------------------------------------------------------------------------*/

	/**
	 * 使用读锁执行指定回调
	 * <p>
	 * 调用读锁执行，运行时自带try-catch-finally块，遇到异常不处理
	 *
	 * @param run 带返回的回调
	 * @param <R> 要返回的数据类型
	 *
	 * @return 回调返回的内容
	 *
	 * @see trySupplier
	 * @see LockType#READ
	 * @see #lock(LockType, trySupplier, Function, Function)
	 * @since 0.1.0
	 */
	default
	<R> R read(@NotNull trySupplier<R> run) { return lock(LockType.READ, run, null, null); }

	/**
	 * 使用读锁执行指定回调
	 * <p>
	 * 调用读锁执行，运行时自带try-catch-finally块，遇到异常不处理
	 *
	 * @param run 执行的回调
	 *
	 * @see tryRunnable
	 * @see LockType#READ
	 * @see #lock(LockType, trySupplier, Function, Function)
	 * @since 0.1.0
	 */
	default
	void read(@NotNull tryRunnable run) {
		lock(LockType.READ, () -> {
			run.run();
			return null;
		}, null, null);
	}

	/**
	 * 使用写锁执行指定回调
	 * <p>
	 * 调用写锁执行，运行时自带try-catch-finally块，遇到异常不处理
	 *
	 * @param run 带返回的回调
	 * @param <R> 要返回的数据类型
	 *
	 * @return 回调返回的内容
	 *
	 * @see trySupplier
	 * @see LockType#WRITE
	 * @see #lock(LockType, trySupplier, Function, Function)
	 * @since 0.1.0
	 */
	default
	<R> R write(@NotNull trySupplier<R> run) { return lock(LockType.WRITE, run, null, null); }

	/**
	 * 使用写锁执行指定回调
	 * <p>
	 * 调用写锁执行，运行时自带try-catch-finally块，遇到异常不处理
	 *
	 * @param run 执行的回调
	 *
	 * @see tryRunnable
	 * @see LockType#WRITE
	 * @see #lock(LockType, trySupplier, Function, Function)
	 * @since 0.1.0
	 */
	default
	void write(@NotNull tryRunnable run) {
		lock(LockType.WRITE, () -> {
			run.run();
			return null;
		}, null, null);
	}

	//-----------------------------------------------

	/**
	 * 使用读锁执行指定回调
	 * <p>
	 * 调用读锁执行，运行时自带try-catch-finally块，通过三个回调参数插入不同的块中执行<br/>
	 * 所有回调均在并发域内执行
	 *
	 * @param run     带返回的回调
	 * @param catchby 进入catch块后的回调，传入当前异常
	 * @param finaby  进入finally块后的回调，传入前两个回调的返回值
	 * @param <R>     要返回的数据类型
	 *
	 * @return 回调返回的内容
	 *
	 * @see trySupplier
	 * @see Function
	 * @see LockType#READ
	 * @see #lock(LockType, trySupplier, Function, Function)
	 * @since 0.1.0
	 */
	default
	<R> R read(@NotNull trySupplier<R> run, @Nullable Function<Exception, R> catchby, @Nullable Function<R, R> finaby)
	{ return lock(LockType.READ, run, catchby, finaby); }

	/**
	 * 使用读锁执行指定回调
	 * <p>
	 * 调用读锁执行，运行时自带try-catch-finally块，通过三个回调参数插入不同的块中执行<br/>
	 * 所有回调均在并发域内执行
	 *
	 * @param run     执行的回调
	 * @param catchby 进入catch块后的回调，传入当前异常
	 * @param finaby  进入finally块后的回调
	 *
	 * @see tryRunnable
	 * @see Consumer
	 * @see Runnable
	 * @see LockType#READ
	 * @see #lock(LockType, trySupplier, Function, Function)
	 * @since 0.1.0
	 */
	@SuppressWarnings("unused")
	default
	void read(@NotNull tryRunnable run, @Nullable Consumer<Exception> catchby, @Nullable Runnable finaby) {
		lock(LockType.READ, () -> {
			run.run();
			return null;
		}, catchby == null ? null : e -> {
			catchby.accept(e);
			return null;
		}, finaby == null ? null : r -> {
			finaby.run();
			return null;
		});
	}

	/**
	 * 使用写锁执行指定回调
	 * <p>
	 * 调用写锁执行，运行时自带try-catch-finally块，通过三个回调参数插入不同的块中执行<br/>
	 * 所有回调均在并发域内执行
	 *
	 * @param run     带返回的回调
	 * @param catchby 进入catch块后的回调，传入当前异常
	 * @param finaby  进入finally块后的回调，传入前两个回调的返回值
	 * @param <R>     要返回的数据类型
	 *
	 * @return 回调返回的内容
	 *
	 * @see trySupplier
	 * @see Function
	 * @see LockType#WRITE
	 * @see #lock(LockType, trySupplier, Function, Function)
	 * @since 0.1.0
	 */
	default
	<R> R write(@NotNull trySupplier<R> run, @Nullable Function<Exception, R> catchby, @Nullable Function<R, R> finaby)
	{ return lock(LockType.WRITE, run, catchby, finaby); }

	/**
	 * 使用写锁执行指定回调
	 * <p>
	 * 调用写锁执行，运行时自带try-catch-finally块，通过三个回调参数插入不同的块中执行<br/>
	 * 所有回调均在并发域内执行
	 *
	 * @param run     执行的回调
	 * @param catchby 进入catch块后的回调，传入当前异常
	 * @param finaby  进入finally块后的回调
	 *
	 * @see tryRunnable
	 * @see Consumer
	 * @see Runnable
	 * @see LockType#WRITE
	 * @see #lock(LockType, trySupplier, Function, Function)
	 * @since 0.1.0
	 */
	default
	void write(@NotNull tryRunnable run, @Nullable Consumer<Exception> catchby, @Nullable Runnable finaby) {
		lock(LockType.WRITE, () -> {
			run.run();
			return null;
		}, catchby == null ? null : e -> {
			catchby.accept(e);
			return null;
		}, finaby == null ? null : _ -> {
			finaby.run();
			return null;
		});
	}

	//-----------------------------------------------

	/**
	 * 尝试使用读锁执行指定回调
	 * <p>
	 * 调用读锁执行，运行时自带try-finally块，遇到异常会抛出
	 *
	 * @param run 带返回的回调
	 * @param <R> 要返回的数据类型
	 *
	 * @return 回调返回的内容，遇到异常不返回
	 *
	 * @see trySupplier
	 * @see LockType#READ
	 * @see #trylock(LockType, trySupplier, Function)
	 * @since 0.1.0
	 */
	default
	<R> R tryread(@NotNull trySupplier<R> run) throws Exception { return trylock(LockType.READ, run, null); }

	/**
	 * 尝试使用读锁执行指定回调
	 * <p>
	 * 调用读锁执行，运行时自带try-finally块，遇到异常会抛出
	 *
	 * @param run 执行的回调
	 *
	 * @see tryRunnable
	 * @see LockType#READ
	 * @see #trylock(LockType, trySupplier, Function)
	 * @since 0.1.0
	 */
	default
	void tryread(@NotNull tryRunnable run) throws Exception {
		trylock(LockType.READ, () -> {
			run.run();
			return null;
		}, null);
	}

	/**
	 * 尝试使用写锁执行指定回调
	 * <p>
	 * 调用写锁执行，运行时自带try-finally块，遇到异常会抛出
	 *
	 * @param run 带返回的回调
	 * @param <R> 要返回的数据类型
	 *
	 * @return 回调返回的内容，遇到异常不返回
	 *
	 * @see trySupplier
	 * @see LockType#WRITE
	 * @see #trylock(LockType, trySupplier, Function)
	 * @since 0.1.0
	 */
	default
	<R> R trywrite(@NotNull trySupplier<R> run) throws Exception { return trylock(LockType.WRITE, run, null); }

	/**
	 * 尝试使用写锁执行指定回调
	 * <p>
	 * 调用写锁执行，运行时自带try-finally块，遇到异常会抛出
	 *
	 * @param run 执行的回调
	 *
	 * @see tryRunnable
	 * @see LockType#WRITE
	 * @see #trylock(LockType, trySupplier, Function)
	 * @since 0.1.0
	 */
	default
	void trywrite(@NotNull tryRunnable run) throws Exception {
		trylock(LockType.WRITE, () -> {
			run.run();
			return null;
		}, null);
	}

	/*--------------------------------------------------------------------------------------------*/

	/**
	 * 获取传统并发实现
	 *
	 * @see ObjLock
	 */
	@NotNull
	static
	ObjLock newObjLock() { return new ObjLock(); }

	/**
	 * 获取可重入锁实现
	 *
	 * @see ReLock
	 */
	@NotNull
	static
	ReLock newReLock() { return new ReLock(); }

	/**
	 * 获取读写锁实现
	 *
	 * @see RWLock
	 */
	@NotNull
	static
	RWLock newRWLock() { return new RWLock(); }
}
