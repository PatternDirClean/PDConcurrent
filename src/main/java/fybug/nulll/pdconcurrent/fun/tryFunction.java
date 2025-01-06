package fybug.nulll.pdconcurrent.fun;
import java.util.function.Function;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

/**
 * @author fybug
 * @version 0.0.2
 * @see Function
 * @since fun 0.0.3
 */
public
interface tryFunction<T, R> {
	R apply(T t) throws Throwable;

	/** @since 0.0.2 */
	@NotNull
	default
	tryConsumer<T> andThen(@Nullable tryConsumer<R> after) {
		if ( after != null )
			return t -> after.accept(apply(t));
		return this::apply;
	}

	/** @since 0.0.2 */
	@NotNull
	default
	<R1> tryFunction<T, R1> andThen(@Nullable tryFunction<R, R1> after) {
		if ( after != null )
			return t -> after.apply(apply(t));
		return (tryFunction<T, R1>) this;
	}
}
