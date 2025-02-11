package fybug.nulll.pdconcurrent.fun;
import java.util.function.Consumer;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

/**
 * @param <T> 传入的参数类型
 *
 * @author fybug
 * @version 0.0.2
 * @see Consumer
 * @since fun 0.0.4
 */
public
interface tryConsumer<T> {
  /** @see Consumer#accept(Object) */
  void accept(T t) throws Exception;

  /** @since 0.0.2 */
  @NotNull
  default
  tryConsumer<T> andThen(@Nullable tryRunnable after) {
    if ( after != null ) {
      return t -> {
        accept(t);
        after.run();
      };
    }
    return this;
  }

  /** @since 0.0.2 */
  @NotNull
  default
  <R> tryFunction<T, R> andThen(@Nullable trySupplier<R> after) {
    if ( after != null ) {
      return t -> {
        accept(t);
        return after.get();
      };
    }
    return t -> {
      accept(t);
      return null;
    };
  }
}
