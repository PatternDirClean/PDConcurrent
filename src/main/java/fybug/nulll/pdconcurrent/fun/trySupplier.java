package fybug.nulll.pdconcurrent.fun;
import java.util.function.Supplier;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

/**
 * @param <R> 返回的参数类型
 *
 * @author fybug
 * @version 0.0.2
 * @see Supplier
 * @since fun 0.0.4
 */
@SuppressWarnings("unused")
@FunctionalInterface
public
interface trySupplier<R> {
  /** @see Supplier#get() */
  R get() throws Exception;

  /** @since 0.0.2 */
  @SuppressWarnings("unused")
  @NotNull
  default
  tryRunnable andThen(@Nullable tryConsumer<R> after) {
    if ( after != null )
      return () -> after.accept(get());
    return this::get;
  }

  /** @since 0.0.2 */
  @SuppressWarnings("unused")
  @NotNull
  default
  <R1> trySupplier<R1> andThen(@Nullable tryFunction<R, R1> after) {
    if ( after != null )
      return () -> after.apply(get());
    //noinspection unchecked
    return (trySupplier<R1>) this;
  }
}
