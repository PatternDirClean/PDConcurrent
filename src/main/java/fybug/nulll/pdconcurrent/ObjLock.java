package fybug.nulll.pdconcurrent;
import java.util.function.Function;

import fybug.nulll.pdconcurrent.e.LockType;
import fybug.nulll.pdconcurrent.fun.trySupplier;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

/**
 * <h2>使用传统并发管理的实现.</h2>
 * <pre>使用并发管理：
 *     public static
 *     void main(String[] args) {
 *         var lock = new ObjLock();
 *         lock.read(() -> System.out.println("asd"));
 *     }</pre>
 * <pre>不使用：
 *     public static
 *     void main(String[] args) {
 *         synchronized ( new Object() ){
 *             System.out.println("asd");
 *         }
 *     }</pre>
 *
 * @author fybug
 * @version 0.0.1
 * @since PDConcurrent 0.0.1
 */
@Getter
public
class ObjLock implements SyLock {
	/** 锁定的对象 */
	private final Object LOCK;

	public
	ObjLock() { this(new Object()); }

	/** 生成并发管理，并指定使用的并发对象锁 */
	public
	ObjLock(@NotNull Object lock) { LOCK = lock; }

	//----------------------------------------------------------------------------------------------

	public
	<R> R lock(@NotNull LockType lockType, @NotNull trySupplier<R> run, @Nullable Function<Throwable, R> catchby,
						 @Nullable Function<R, R> finaby)
	{
		R o = null;
		// 不上锁
		if ( lockType == LockType.NOLOCK ) {
			try {
				o = run.get();
			} catch ( Throwable e ) {
				if ( catchby != null )
					o = catchby.apply(e);
			} finally {
				if ( finaby != null )
					o = finaby.apply(o);
			}
		} else {
			synchronized ( LOCK ){
				try {
					o = run.get();
				} catch ( Throwable e ) {
					if ( catchby != null )
						o = catchby.apply(e);
				} finally {
					if ( finaby != null )
						o = finaby.apply(o);
				}
			}
		}
		return o;
	}
}
