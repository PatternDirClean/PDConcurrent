package fybug.nulll.pdconcurrent.fun;
import java.util.function.Supplier;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

/**
 * @author fybug
 * @version 0.0.2
 * @see Supplier
 * @since fun 0.0.3
 */
@FunctionalInterface
public
interface trySupplier<R> {
	R get() throws Throwable;

	/** @since 0.0.2 */
	@NotNull
	default
	tryRunnable andThen(@Nullable tryConsumer<R> after) {
		if ( after != null )
			return () -> after.accept(get());
		return this::get;
	}

	/** @since 0.0.2 */
	@NotNull
	default
	<R1> trySupplier<R1> andThen(@Nullable tryFunction<R, R1> after) {
		if ( after != null )
			return () -> after.apply(get());
		return (trySupplier<R1>) this;
	}
}
