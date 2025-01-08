package fybug.nulll.pdconcurrent.fun;
import java.util.function.Function;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

/**
 * @param <T> 传入的参数类型
 * @param <R> 返回的参数类型
 *
 * @author fybug
 * @version 0.0.2
 * @see Function
 * @since fun 0.0.4
 */
public
interface tryFunction<T, R> {
	/** @see Function#apply(Object) */
	R apply(T t) throws Exception;

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
