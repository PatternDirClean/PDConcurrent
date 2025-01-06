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
 * 通过使用接口运行的方式隐藏内部的并发管理方法<br/>
 * 让开发人员无需管理并发的具体方式<br/>
 * {@code **read()} 方法用于申请读取方法，{@code **write()} 用于申请写入方法，只有在使用读写锁实现 {@link RWLock} 才有区别。其余实现两个之间无区别<br/>
 * {@code try**()} 类型的方法为可抛出异常的方法，可在传入的接口中抛出异常，但是需要指定异常的类型<br/>
 * 也可在该类方法中传入 catch 块和 finally 块的代码，随后将不会抛出异常。发生异常后返回将会变为 {@code null}
 * <br/><br/>
 * 使用 {@code new**Lock()} 的方法获取不同并发管理的实例<br/>
 *
 * @author fybug
 * @version 0.0.2
 * @since PDConcurrent 0.0.1
 */
public
interface SyLock {

	default
	c lock(@NotNull LockType lockType) { return new c(this, lockType); }

	default
	void lock(@NotNull LockType lockType, @NotNull tryRunnable run) { lock(lockType, run, null, null); }

	default
	void lock(@NotNull LockType lockType, @NotNull tryRunnable run, @Nullable Consumer<Throwable> catchby,
						@Nullable Runnable finaby)
	{
		lock(lockType, () -> {
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

	default
	<R> R lock(@NotNull LockType lockType, @NotNull trySupplier<R> run) { return lock(lockType, run, null, null); }

	<R> R lock(@NotNull LockType lockType, @NotNull trySupplier<R> run, @Nullable Function<Throwable, R> catchby,
						 @Nullable Function<R, R> finaby);

	//----------------------------------------------------------------------------------------------

	/**
	 * 申请并运行于读锁
	 *
	 * @param run 运行代码
	 */
	default
	void read(@NotNull tryRunnable run) { lock(LockType.READ, run); }

	/**
	 * 申请并运行于读锁
	 *
	 * @param run 带返回的运行代码
	 *
	 * @return 接口生成的数据
	 */
	default
	<R> R read(@NotNull trySupplier<R> run) { return lock(LockType.READ, run); }

	/**
	 * 申请并运行于写锁
	 *
	 * @param run 运行代码
	 */
	default
	void write(@NotNull tryRunnable run) { lock(LockType.WRITE, run); }

	/**
	 * 申请并运行于写锁
	 *
	 * @param run 带返回的运行代码
	 *
	 * @return 接口生成的数据
	 */
	default
	<R> R write(@NotNull trySupplier<R> run) { return lock(LockType.WRITE, run); }

	//----------------------------------------------------------------------------------------------

	default
	void read(@NotNull tryRunnable run, @Nullable Consumer<Throwable> catchby, @Nullable Runnable finaby)
	{ lock(LockType.READ, run, catchby, finaby); }

	default
	<R> R read(@NotNull trySupplier<R> run, @Nullable Function<Throwable, R> catchby, @Nullable Function<R, R> finaby)
	{ return lock(LockType.READ, run, catchby, finaby); }

	default
	void write(@NotNull tryRunnable run, @Nullable Consumer<Throwable> catchby, @Nullable Runnable finaby)
	{ lock(LockType.WRITE, run, catchby, finaby); }

	default
	<R> R write(@NotNull trySupplier<R> run, @Nullable Function<Throwable, R> catchby, @Nullable Function<R, R> finaby)
	{ return lock(LockType.WRITE, run, catchby, finaby); }

	/*--------------------------------------------------------------------------------------------*/

	/** 获取传统并发实现 */
	static @NotNull
	ObjLock newObjLock() { return new ObjLock(); }

	/** 获取 Lock 实现 */
	static @NotNull
	ReLock newReLock() { return new ReLock(); }

	/** 获取读写锁实现 */
	static @NotNull
	RWLock newRWLock() { return new RWLock(); }

	class c<R> {
		@NotNull SyLock self;
		@NotNull LockType lockType;
		@NotNull trySupplier<R> run = null;
		@Nullable Function<Throwable, R> catchby = null;
		@Nullable Function<R, R> finaby = null;

		c(@NotNull SyLock self, @NotNull LockType lockType) {
			this.self = self;
			this.lockType = lockType;
		}

		public
		c<R> run(@NotNull trySupplier<R> run) {
			this.run = run;
			return this;
		}

		public
		c<R> catchby(@Nullable Function<Throwable, R> catchby) {
			this.catchby = catchby;
			return this;
		}

		public
		c<R> finaby(@Nullable Function<R, R> finaby) {
			this.finaby = finaby;
			return this;
		}

		public
		R start() { return self.lock(lockType, run, catchby, finaby); }
	}
}
