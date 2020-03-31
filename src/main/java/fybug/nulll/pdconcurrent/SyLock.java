package fybug.nulll.pdconcurrent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

import fybug.nulll.pdconcurrent.fun.tryRunnable;
import fybug.nulll.pdconcurrent.fun.trySupplier;

/**
 * <h2>并发管理.</h2>
 * 通过使用接口运行的方式隐藏内部的并发管理方法<br/>
 * 让开发人员无需管理并发的具体方式<br/>
 * {@code **read()} 方法用于申请读取方法，{@code **write()} 用于申请写入方法，只有在使用读写锁实现 {@link RWLock} 才有区别。其余实现两个之间无区别<br/>
 * {@code try**()} 类型的方法为可抛出异常的方法，可在传入的接口中抛出异常，但是需要指定异常的类型<br/>
 * 使用 {@code new**Lock()} 的方法获取不同并发管理的实例<br/>
 *
 * @author fybug
 * @version 0.0.1
 * @since PDConcurrent 0.0.1
 */
public
interface SyLock {
    /** 申请并运行于读锁 */
    default
    void read(@NotNull Runnable run) {
        read(() -> {
            run.run();
            return null;
        });
    }

    /** 申请并运行于写锁 */
    default
    void write(@NotNull Runnable run) {
        write(() -> {
            run.run();
            return null;
        });
    }

    //------------------------------------

    /**
     * 申请并运行于读锁
     *
     * @return 接口生成的数据
     */
    <T> T read(@NotNull Supplier<T> run);

    /**
     * 申请并运行于写锁
     *
     * @return 接口生成的数据
     */
    <T> T write(@NotNull Supplier<T> run);

    //----------------------------------------------------------------------------------------------

    /**
     * 尝试运行于读锁
     *
     * @param ecla 异常的类
     */
    default
    <E extends Exception> void tryread(@NotNull Class<E> ecla, @NotNull tryRunnable<E> run) throws E
    {
        tryread(ecla, () -> {
            run.run();
            return null;
        });
    }

    /**
     * 尝试运行于写锁
     *
     * @param ecla 异常的类
     */
    default
    <E extends Exception> void trywrite(@NotNull Class<E> ecla, @NotNull tryRunnable<E> run)
    throws E
    {
        trywrite(ecla, () -> {
            run.run();
            return null;
        });
    }

    //------------------------------------

    /**
     * 尝试运行于读锁
     *
     * @param ecla 异常的类
     *
     * @return 接口生成的数据
     */
    <T, E extends Exception> T tryread(@NotNull Class<E> ecla, @NotNull trySupplier<T, E> run)
    throws E;

    /**
     * 尝试运行于写锁
     *
     * @param ecla 异常的类
     *
     * @return 接口生成的数据
     */
    <T, E extends Exception> T trywrite(@NotNull Class<E> ecla, @NotNull trySupplier<T, E> run)
    throws E;

    /*--------------------------------------------------------------------------------------------*/

    /** 获取传统并发实现 */
    static @NotNull
    ObjLock newObjLock() {return new ObjLock();}

    /** 获取 Lock 实现 */
    static @NotNull
    ReLock newReLock() {return new ReLock();}

    /** 获取读写锁实现 */
    static @NotNull
    RWLock newRWLock() {return new RWLock();}
}
