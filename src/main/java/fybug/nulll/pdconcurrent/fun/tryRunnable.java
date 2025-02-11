package fybug.nulll.pdconcurrent.fun;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

/**
 * @author fybug
 * @version 0.0.2
 * @see Runnable
 * @since fun 0.0.4
 */
@FunctionalInterface
public
interface tryRunnable {
  /** @see Runnable#run() */
  void run() throws Exception;

  /** @since 0.0.2 */
  @NotNull
  default
  tryRunnable andThen(@Nullable tryRunnable after) {
    if ( after != null ) {
      return () -> {
        run();
        after.run();
      };
    }
    return this;
  }

  /** @since 0.0.2 */
  @NotNull
  default
  <R> trySupplier<R> andThen(@Nullable trySupplier<R> after) {
    if ( after != null ) {
      return () -> {
        run();
        return after.get();
      };
    }
    return () -> {
      run();
      return null;
    };
  }

  /** @since 0.0.2 */
  @NotNull
  default
  <T> tryRunnable andThen(@Nullable tryConsumer<T> after) {
    if ( after != null ) {
      return () -> {
        run();
        after.accept(null);
      };
    }
    return this;
  }

  /** @since 0.0.2 */
  @NotNull
  default
  <T, R> trySupplier<R> andThen(@Nullable tryFunction<T, R> after) {
    if ( after != null ) {
      return () -> {
        run();
        return after.apply(null);
      };
    }
    return () -> {
      run();
      return null;
    };
  }
}
