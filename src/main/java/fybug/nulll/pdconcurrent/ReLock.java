package fybug.nulll.pdconcurrent;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

import fybug.nulll.pdconcurrent.e.LockType;
import fybug.nulll.pdconcurrent.fun.trySupplier;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

/**
 * 使用 {@link ReentrantLock} 实现的并发管理
 * <pre>使用并发管理：
 *     public static
 *     void main(String[] args) {
 *         var lock = new ReLock();
 *         lock.read(() -> System.out.println("asdas"));
 *     }</pre>
 * <pre>不使用：
 *     public static
 *     void main(String[] args) {
 *         var lock = new ReentrantLock();
 *         lock.lock();
 *         try {
 *             System.out.println("asdas");
 *         } finally {
 *             lock.unlock();
 *         }
 *     }</pre>
 *
 * @author fybug
 * @version 0.0.1
 * @since PDConcurrent 0.0.1
 */
@Getter
public
class ReLock implements SyLock {
	// 锁
	private final ReentrantLock LOCK;

	public
	ReLock() { this(false); }

	/** 构造并发处理，并决定使用公平锁还是非公平锁 */
	public
	ReLock(boolean fair) { this(new ReentrantLock(fair)); }

	public
	ReLock(@NotNull ReentrantLock LOCK) { this.LOCK = LOCK; }

	//----------------------------------------------------------------------------------------------

	@Override
	public
	<R> R lock(@NotNull LockType lockType, trySupplier<R> run, @Nullable Function<Throwable, R> catchby,
						 @Nullable Function<R, R> finaby)
	{
		R o = null;
		try {
			if ( lockType != LockType.NOLOCK )
				LOCK.lockInterruptibly();
			o = run.get();
		} catch ( Throwable e ) {
			if ( catchby != null )
				o = catchby.apply(e);
		} finally {
			if ( finaby != null )
				o = finaby.apply(o);
			if ( lockType != LockType.NOLOCK && LOCK.isLocked() )
				LOCK.unlock();
		}
		return o;
	}

	public
	boolean isLocked() { return LOCK.isLocked(); }

	/** 获取 {@link Condition} */
	@NotNull
	public
	Condition newCondition() { return LOCK.newCondition(); }
}
