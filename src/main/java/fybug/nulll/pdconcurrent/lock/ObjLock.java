package fybug.nulll.pdconcurrent.lock;
import java.util.function.Function;

import fybug.nulll.pdconcurrent.e.LockType;
import fybug.nulll.pdconcurrent.i.AbstractSyLock;
import fybug.nulll.pdutilfunctionexpand.tryFunction;
import fybug.nulll.pdutilfunctionexpand.trySupplier;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

/**
 * <h2>使用传统并发管理的实现.</h2>
 * 使用{@code synchronized( Object )}实现并发域，读写锁均为同一个实现<br/>
 * 因此也无法使用尝试上锁，其结果始终为{@code true}
 * <br/><br/>
 * 使用并发管理：
 * {@snippet lang = java:
 * import fybug.nulll.pdconcurrent.SyLock;
 * public final SyLock lock = new ObjLock();
 * public static void main(String[] args) {
 *   lock.read(() -> System.out.println("asd"));
 * }}
 * 不使用：
 * {@snippet lang = java:
 * public static void main(String[] args) {
 *   synchronized ( lock ){
 *     System.out.println("asd");
 *   }
 * }}
 *
 * @author fybug
 * @version 0.1.3
 * @see AbstractSyLock
 * @since lock 0.0.1
 */
@SuppressWarnings("unused")
public
class ObjLock extends AbstractSyLock<Object> {
  /**
   * 构建并发管理
   * <p>
   * 使用一个新的{@link Object}
   */
  public
  ObjLock() { this(new Object()); }

  /**
   * 构建并发管理
   *
   * @param lock 用作锁的对象
   *
   * @since 0.1.0
   */
  public
  ObjLock(@NotNull Object lock) { super(new LockThreadContext<>(lock, false, null)); }

  /**
   * {@inheritDoc}
   *
   * @param lockType {@inheritDoc}
   * @param run      {@inheritDoc}
   * @param catchby  {@inheritDoc}
   * @param finaby   {@inheritDoc}
   * @param <R>      {@inheritDoc}
   * @param <E>      {@inheritDoc}
   *
   * @return {@inheritDoc}
   *
   * @throws E1 {@inheritDoc}
   * @implNote 使用java自带的synchronized进行上锁，因此无需进行二层异常捕获与解锁
   * @since ObjLock 0.1.3
   */
  @SuppressWarnings("unchecked")
  protected
  <R, E extends Throwable, E1 extends Throwable> R lockimpl(@NotNull LockType lockType, @NotNull trySupplier<R, E> run,
                                                            @Nullable tryFunction<E, R, E1> catchby,
                                                            @Nullable Function<R, R> finaby) throws E1
  {
    R o = null;
    try {
      // 不上锁
      if ( lockType == LockType.NOLOCK ) {
        try {
          // 主要内容
          o = run.get();
        } catch ( Throwable e ) {
          // 异常处理
          if ( catchby != null )
            o = catchby.apply((E) e);
        } finally {
          // 收尾
          if ( finaby != null )
            o = finaby.apply(o);
        }
      } else {
        // 上锁
        synchronized ( getLockThreadContext().getLock() ){
          try {
            // 主要内容
            o = run.get();
          } catch ( Throwable e ) {
            // 异常处理
            if ( catchby != null )
              o = catchby.apply((E) e);
          } finally {
            // 收尾
            if ( finaby != null )
              o = finaby.apply(o);
          }
        }
      }
    } finally {
      removeLockThreadContext();
    }
    return o;
  }

  /**
   * {@inheritDoc}
   *
   * @param lockType {@inheritDoc}
   * @param run      {@inheritDoc}
   * @param catchby  {@inheritDoc}
   * @param finaby   {@inheritDoc}
   * @param <R>      {@inheritDoc}
   * @param <E>      {@inheritDoc}
   *
   * @return {@inheritDoc}
   *
   * @throws E1 {@inheritDoc}
   * @implNote 无法尝试上锁，直接使用{@link #lockimpl(LockType, trySupplier, tryFunction, Function)}实现
   * @since ObjLock 0.1.3
   */
  @Override
  protected
  <R, E extends Throwable, E1 extends Throwable> R trylockimpl(@NotNull LockType lockType,
                                                               @NotNull tryFunction<Boolean, R, E> run,
                                                               @Nullable tryFunction<E, R, E1> catchby,
                                                               @Nullable Function<R, R> finaby) throws E1
  { return lockimpl(lockType, () -> run.apply(true), catchby, finaby); }
}
