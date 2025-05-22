package fybug.nulll.pdconcurrent;
import fybug.nulll.pdconcurrent.e.LockType;
import fybug.nulll.pdconcurrent.i.simple.ReadLockSimple;
import fybug.nulll.pdconcurrent.i.simple.TryReadLockSimple;
import fybug.nulll.pdconcurrent.i.simple.TryWriteLockSimple;
import fybug.nulll.pdconcurrent.i.simple.WriteLockSimple;
import fybug.nulll.pdconcurrent.lock.ObjLock;
import fybug.nulll.pdconcurrent.lock.RWLock;
import fybug.nulll.pdconcurrent.lock.ReLock;
import jakarta.validation.constraints.NotNull;

/**
 * <h2>并发管理.</h2>
 * 通过传入回调的方式隐藏内部的并发管理方法，并支持复用内部的 try 块，通过传入的回调插入到 catch，finally 块中执行<br/>
 * {@code lock()}方法用于根据传入的{@link LockType}申请不同的锁类型进行执行<br/>
 * {@code read()}方法用于使用读锁执行，{@code write()}用于使用写锁执行，只有在使用读写锁实现{@link RWLock}才有区别。其余实现两个之间无区别<br/>
 * {@code trylock()}类型的方法为尝试获取锁的实现，这意味着并不是一定能获取到锁，但也意味着不需要等待<br/>
 * 上述方法均有可抛出异常的变种
 * <br/><br/>
 * 使用 {@code new**Lock()} 的方法获取不同并发管理的实例<br/>
 *
 * @author fybug
 * @version 0.1.3
 * @since PDConcurrent 0.0.1
 */
@SuppressWarnings("unused")
public
interface SyLock extends ReadLockSimple, WriteLockSimple, TryReadLockSimple, TryWriteLockSimple {
  /**
   * 获取传统并发实现
   *
   * @return 获取的并发控制对象
   *
   * @see ObjLock
   */
  @NotNull
  static
  ObjLock newObjLock() { return new ObjLock(); }

  /**
   * 获取可重入锁实现
   *
   * @return 获取的并发控制对象
   *
   * @see ReLock
   */
  @NotNull
  static
  ReLock newReLock() { return new ReLock(); }

  /**
   * 获取读写锁实现
   *
   * @return 获取的并发控制对象
   *
   * @see RWLock
   */
  @NotNull
  static
  RWLock newRWLock() { return new RWLock(); }
}
