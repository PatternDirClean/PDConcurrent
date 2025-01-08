package fybug.nulll.pdconcurrent;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;

import fybug.nulll.pdconcurrent.e.LockType;
import fybug.nulll.pdconcurrent.fun.trySupplier;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

/**
 * <h2>使用{@link ReentrantReadWriteLock}实现的并发管理.</h2>
 * 使用{@link ReentrantReadWriteLock}实现并发域，读写锁均为标准实现，支持通过{@link #toread()}进行锁降级<br/>
 * 使用了可中断的上锁操作{@link ReentrantReadWriteLock.ReadLock#lockInterruptibly()}和{@link ReentrantReadWriteLock.WriteLock#lockInterruptibly()}<br/>
 * 支持使用{@link #newReadCondition()}{@link #newWriteCondition()}获取{@link Condition}，通过{@link #isLocked()}{@link #isWriteLocked()}{@link #isReadLocked()}检查是否被占用<br/>
 * <br/><br/>
 * 使用并发管理：
 * {@snippet lang = java:
 * public static void main(String[] args) {
 *   var lock = new RWLock();
 *   // 使用读锁
 *   lock.read(() -> System.out.println("adsa"));
 *   // 使用写锁
 *   lock.write(() -> System.out.println("adsa"));
 * }}
 * 不使用：
 * {@snippet lang = java:
 * import java.util.concurrent.locks.ReentrantReadWriteLock;
 * public static void main(String[] args) {
 *   var lock = new ReentrantReadWriteLock();
 *   // 使用读锁
 *   try {
 *     lock.readLock().lock();
 *     System.out.println("adsa");
 *   } finally {
 *   lock.readLock().unlock();
 *   }
 *   // 使用写锁
 *   try {
 *     lock.writeLock().lock();
 *     System.out.println("adsa");
 *   } finally {
 *     lock.writeLock().unlock();
 *   }
 * }}
 *
 * @author fybug
 * @version 0.1.0
 * @see SyLock
 * @see LockType
 * @see ReentrantReadWriteLock
 * @see ReentrantReadWriteLock.ReadLock
 * @see ReentrantReadWriteLock.WriteLock
 * @since PDConcurrent 0.0.1
 */
@Getter
public
class RWLock implements SyLock {
	/** 锁 */
	private final ReentrantReadWriteLock LOCK;
	/** 读锁 */
	private final ReentrantReadWriteLock.ReadLock Read_LOCK;
	/** 写锁 */
	private final ReentrantReadWriteLock.WriteLock Write_LOCK;
	/**
	 * 每个线程的锁状态记录
	 * <p>
	 * 读锁为{@code 1}<br/>
	 * 写锁为{@code 2}
	 */
	private final ThreadLocal<Short> LOCK_STATE = new ThreadLocal<>();

	/**
	 * 构建并发管理
	 * <p>
	 * 使用非公平锁
	 */
	public
	RWLock() { this(false); }

	/**
	 * 构造并发处理
	 *
	 * @param fair 是否使用公平锁
	 */
	public
	RWLock(boolean fair) { this(new ReentrantReadWriteLock(fair)); }

	/**
	 * 构造并发处理
	 *
	 * @param lock 使用的锁
	 *
	 * @since 0.1.0
	 */
	public
	RWLock(@NotNull ReentrantReadWriteLock lock) {
		LOCK = lock;
		Read_LOCK = LOCK.readLock();
		Write_LOCK = LOCK.writeLock();
	}

	//----------------------------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 *
	 * @param lockType {@inheritDoc}
	 * @param run      {@inheritDoc}
	 * @param catchby  {@inheritDoc}
	 * @param finaby   {@inheritDoc}
	 * @param <R>      {@inheritDoc}
	 *
	 * @return {@inheritDoc}
	 *
	 * @implNote 使用 {@link ReentrantReadWriteLock} 实现的并发域，上锁通过{@link #tolock(LockType)}进行
	 * @see SyLock#lock(LockType, trySupplier, Function, Function)
	 * @see #tolock(LockType)
	 * @see #tounlock()
	 * @since 0.1.0
	 */
	@Override
	public
	<R> R lock(@NotNull LockType lockType, @NotNull trySupplier<R> run, @Nullable Function<Exception, R> catchby,
						 @Nullable Function<R, R> finaby)
	{
		R o = null;
		// 清空当前线程的状态
		LOCK_STATE.remove();
		try {
			// 上锁
			tolock(lockType);
			// 主要内容
			o = run.get();
		} catch ( Exception e ) {
			// 异常处理
			if ( catchby != null )
				o = catchby.apply(e);
		} finally {
			// 防止错误
			try {
				// 收尾
				if ( finaby != null )
					o = finaby.apply(o);
			} finally {
				// 解锁
				tounlock();
			}
		}
		return o;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param lockType {@inheritDoc}
	 * @param run      {@inheritDoc}
	 * @param finaby   {@inheritDoc}
	 * @param <R>      {@inheritDoc}
	 *
	 * @return {@inheritDoc}
	 *
	 * @implNote 使用 {@link ReentrantReadWriteLock} 实现的并发域，上锁通过{@link #tolock(LockType)}进行
	 * @see SyLock#trylock(LockType, trySupplier, Function)
	 * @see #tolock(LockType)
	 * @see #tounlock()
	 * @since 0.1.0
	 */
	@Override
	public
	<R> R trylock(@NotNull LockType lockType, @NotNull trySupplier<R> run, @Nullable Function<R, R> finaby) throws Exception {
		R o = null;
		// 清空当前线程的状态
		LOCK_STATE.remove();
		try {
			// 上锁
			tolock(lockType);
			// 主要内容
			o = run.get();
		} finally {
			// 防止错误
			try {
				// 收尾
				if ( finaby != null )
					o = finaby.apply(o);
			} finally {
				// 解锁
				tounlock();
			}
		}
		return o;
	}

	/**
	 * 根据指定类型上锁
	 * <p>
	 * 使用了可中断的上锁操作{@link ReentrantReadWriteLock.ReadLock#lockInterruptibly()}和{@link ReentrantReadWriteLock.WriteLock#lockInterruptibly()}<br/>
	 * 同时更新{@link #LOCK_STATE}记录
	 *
	 * @param lockType 锁类型
	 *
	 * @throws InterruptedException 上锁过程中被中断
	 * @see #LOCK_STATE
	 * @see LockType
	 * @since 0.1.0
	 */
	private
	void tolock(@NotNull LockType lockType) throws InterruptedException {
		if ( lockType != LockType.NOLOCK ) {
			if ( lockType == LockType.READ ) {
				// 读锁
				Read_LOCK.lockInterruptibly();
				LOCK_STATE.set((short) 1);
			} else {
				// 写锁
				Write_LOCK.lockInterruptibly();
				LOCK_STATE.set((short) 2);
			}
		}
	}

	/**
	 * 根据状态解锁
	 * <p>
	 * 根据{@link #LOCK_STATE}的状态调用对应的解锁动作<br/>
	 * 成功后清空{@link #LOCK_STATE}内容
	 *
	 * @see #LOCK_STATE
	 * @since 0.1.0
	 */
	private
	void tounlock() {
		// 根据实际状态解锁
		if ( LOCK_STATE.get() == 1 )
			Read_LOCK.unlock();
		else if ( LOCK_STATE.get() == 2 )
			Write_LOCK.unlock();
		// 清除记录数据
		LOCK_STATE.remove();
	}

	/**
	 * 转为读锁
	 * <p>
	 * 如果当前状态为写锁则会降级为读锁，否则不进行操作
	 *
	 * @return 是否成功降级
	 *
	 * @see #LOCK_STATE
	 * @since 0.1.0
	 */
	public
	boolean toread() {
		// 转为读锁
		if ( LOCK_STATE.get() == 2 ) {
			Read_LOCK.lock();
			Write_LOCK.unlock();
			LOCK_STATE.set((short) 1);
			return true;
		}
		return false;
	}

	//----------------------------------------------------------------------------------------------

	/**
	 * 检查锁是否被占用
	 *
	 * @return 是否被占用
	 *
	 * @since 0.1.0
	 */
	public
	boolean isLocked() { return LOCK_STATE.get() != null && (LOCK_STATE.get() == 1 || LOCK_STATE.get() == 2); }

	/**
	 * 检查读锁是否被占用
	 *
	 * @return 是否被占用
	 *
	 * @since 0.1.0
	 */
	public
	boolean isReadLocked() { return LOCK_STATE.get() == 1; }

	/**
	 * 检查写锁是否被占用
	 *
	 * @return 是否被占用
	 *
	 * @since 0.1.0
	 */
	public
	boolean isWriteLocked() { return LOCK_STATE.get() == 2; }

	/**
	 * 获取读锁{@link Condition}
	 *
	 * @return {@link ReentrantReadWriteLock.ReadLock}的{@link Condition}
	 *
	 * @see ReentrantReadWriteLock.ReadLock#newCondition()
	 * @since 0.1.0
	 */
	@NotNull
	public
	Condition newReadCondition() { return Read_LOCK.newCondition(); }

	/**
	 * 获取写锁{@link Condition}
	 *
	 * @return {@link ReentrantReadWriteLock.WriteLock}的{@link Condition}
	 *
	 * @see ReentrantReadWriteLock.WriteLock#newCondition()
	 * @since 0.1.0
	 */
	@NotNull
	public
	Condition newWriteCondition() { return Write_LOCK.newCondition(); }
}