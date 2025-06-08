package fybug.nulll.pdconcurrent.lock;
import java.util.function.Function;

import fybug.nulll.pdconcurrent.SyLock;
import fybug.nulll.pdconcurrent.e.LockType;
import fybug.nulll.pdutilfunctionexpand.tryFunction;
import fybug.nulll.pdutilfunctionexpand.trySupplier;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

/**
 * <h2>使用传统并发管理的实现.</h2>
 * 使用{@code synchronized( Object )}实现并发域，读写锁均为同一个实现
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
 * @version 0.1.2
 * @since lock 0.0.1
 */
@SuppressWarnings("unused")
@Getter
public
class ObjLock implements SyLock {
  /** 作为锁的对象 */
  private final Object LOCK;

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
  ObjLock(@NotNull Object lock) { LOCK = lock; }

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
   * @implNote 使用 {@code synchronized( Object )} 实现的隐式并发域
   * @since 0.1.0
   */
  @SuppressWarnings("unchecked")
  @Override
  public
  <R, E extends Throwable> R lock(@NotNull LockType lockType, @NotNull trySupplier<R, E> run,
                                  @Nullable Function<E, R> catchby, @Nullable Function<R, R> finaby)
  {
    R o = null;
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
      synchronized ( LOCK ){
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
   * @implNote 使用 {@code synchronized( Object )} 实现的隐式并发域
   * @since 0.1.2
   */
  @Override
  public
  <R, E extends Throwable> R lock(@NotNull LockType lockType, @NotNull trySupplier<R, E> run,
                                  @Nullable Function<R, R> finaby) throws E
  {
    R o = null;
    // 不上锁
    if ( lockType == LockType.NOLOCK ) {
      try {
        // 主要内容
        o = run.get();
      } finally {
        // 收尾
        if ( finaby != null )
          o = finaby.apply(o);
      }
    } else {
      // 上锁
      synchronized ( LOCK ){
        try {
          // 主要内容
          o = run.get();
        } finally {
          // 收尾
          if ( finaby != null )
            o = finaby.apply(o);
        }
      }
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
   *
   * @return {@inheritDoc}
   *
   * @since 0.1.2
   */
  @Override
  public
  <R, E extends Throwable> R trylock(@NotNull LockType lockType, @NotNull tryFunction<Boolean, R, E> run,
                                     @Nullable Function<E, R> catchby, @Nullable Function<R, R> finaby)
  { return lock(lockType, () -> run.apply(true), catchby, finaby); }

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
   * @throws Exception {@inheritDoc}
   * @since 0.1.2
   */
  @Override
  public
  <R, E extends Throwable> R trylock(@NotNull LockType lockType, @NotNull tryFunction<Boolean, R, E> run,
                                     @Nullable Function<R, R> finaby) throws E
  { return lock(lockType, () -> run.apply(true), finaby); }
}
