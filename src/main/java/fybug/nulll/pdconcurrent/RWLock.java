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
 * 使用读写锁 {@link ReentrantReadWriteLock} 实现的并发管理
 * <pre>使用并发管理：
 *     public static
 *     void main(String[] args) {
 *         var lock = new RWLock();
 *         lock.read(() -> System.out.println("adsa"));
 *         lock.write(() -> System.out.println("adsa"));
 *     }</pre>
 * <pre>不使用：
 *     public static
 *     void main(String[] args) {
 *         var lock = new ReentrantReadWriteLock();
 *         lock.readLock().lock();
 *         try {
 *             System.out.println("adsa");
 *         } finally {
 *             lock.readLock().unlock();
 *         }
 *         lock.writeLock().lock();
 *         try {
 *             System.out.println("adsa");
 *         } finally {
 *             lock.writeLock().unlock();
 *         }
 *     }</pre>
 *
 * @author fybug
 * @version 0.0.1
 * @since PDConcurrent 0.0.1
 */
@Getter
public
class RWLock implements SyLock {
	private final ReentrantReadWriteLock LOCK;
	private final ReentrantReadWriteLock.ReadLock Read_LOCK;
	private final ReentrantReadWriteLock.WriteLock Write_LOCK;
	private final ThreadLocal<Short> IS_LOCK = new ThreadLocal<>();

	public
	RWLock() { this(false); }

	/** 生成并发管理，并指定是否使用公平锁 */
	public
	RWLock(boolean fair) { this(new ReentrantReadWriteLock(fair)); }

	public
	RWLock(@NotNull ReentrantReadWriteLock lock) {
		LOCK = lock;
		Read_LOCK = LOCK.readLock();
		Write_LOCK = LOCK.writeLock();
	}

	//----------------------------------------------------------------------------------------------

	@Override
	public
	<R> R lock(@NotNull LockType lockType, trySupplier<R> run, @Nullable Function<Throwable, R> catchby,
						 @Nullable Function<R, R> finaby)
	{
		R o = null;
		// set null
		IS_LOCK.remove();
		try {
			if ( lockType != LockType.NOLOCK ) {
				if ( lockType == LockType.READ ) {
					Read_LOCK.lockInterruptibly();
					IS_LOCK.set((short) 1);
				} else {
					Write_LOCK.lockInterruptibly();
					IS_LOCK.set((short) 2);
				}
			}
			o = run.get();
		} catch ( Throwable e ) {
			if ( catchby != null )
				o = catchby.apply(e);
		} finally {
			if ( finaby != null )
				o = finaby.apply(o);
			// 根据实际状态解锁
			if ( IS_LOCK.get() == 1 ) {
				Read_LOCK.unlock();
			} else if ( IS_LOCK.get() == 2 ) {
				Write_LOCK.unlock();
			}
			IS_LOCK.remove();
		}
		return o;
	}

	//----------------------------------------------------------------------------------------------

	/** 获取 {@link Condition} */
	@NotNull
	public
	Condition newReadCondition() { return Read_LOCK.newCondition(); }

	/** 获取 {@link Condition} */
	@NotNull
	public
	Condition newWriteCondition() { return Write_LOCK.newCondition(); }

	public
	boolean isLocked() { return IS_LOCK.get() != null && IS_LOCK.get() > 0; }

	public
	boolean isReadLocked() { return IS_LOCK.get() == 1; }

	public
	boolean isWriteLocked() { return IS_LOCK.get() == 2; }

	public
	boolean toread() throws InterruptedException {
		// 转为读锁
		if ( IS_LOCK.get() == 2 ) {
			Read_LOCK.lockInterruptibly();
			Write_LOCK.unlock();
			IS_LOCK.set((short) 1);
			return true;
		}
		return false;
	}
}