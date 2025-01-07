package fybug.nulll.pdconcurrent.e;

/**
 * <h2>锁类型.</h2>
 *
 * @author fybug
 * @version 0.0.1
 * @since PDConcurrent 0.1.0
 */
public
enum LockType {
	/** 读锁 */
	READ,
	/** 写锁 */
	WRITE,
	/** 不上锁 */
	NOLOCK
}
