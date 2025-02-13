package fybug.nulll.pdconcurrent.e;

/**
 * <h2>锁类型.</h2>
 *
 * @author fybug
 * @version 0.0.1
 * @since e 0.0.1
 */
public
enum LockType {
  /** 读锁 */
  READ,
  /** 写锁 */
  WRITE,
  /**
   * 不上锁
   * <p>
   * 该方式要求这次锁行为不会被记录与执行，故解锁时本次行为会被跳过转而解锁上一次的锁
   */
  NOLOCK
}
