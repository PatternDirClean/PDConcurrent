package fybug.nulll.pdconcurrent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import fybug.nulll.pdconcurrent.fun.tryRunnable;
import fybug.nulll.pdconcurrent.fun.trySupplier;

/**
 * <h2>并发管理.</h2>
 * 通过使用接口运行的方式隐藏内部的并发管理方法<br/>
 * 让开发人员无需管理并发的具体方式<br/>
 * {@code **read()} 方法用于申请读取方法，{@code **write()} 用于申请写入方法，只有在使用读写锁实现 {@link RWLock} 才有区别。其余实现两个之间无区别<br/>
 * {@code try**()} 类型的方法为可抛出异常的方法，可在传入的接口中抛出异常，但是需要指定异常的类型<br/>
 * 也可在该类方法中传入 catch 块和 finally 块的代码，随后将不会抛出异常。
 * <br/><br/>
 * 使用 {@code new**Lock()} 的方法获取不同并发管理的实例<br/>
 *
 * @author fybug
 * @version 0.0.2
 * @since PDConcurrent 0.0.1
 */
public
interface SyLock {
    /**
     * 申请并运行于读锁
     *
     * @param run 运行代码
     */
    default
    void read(@NotNull Runnable run) {
        read(() -> {
            run.run();
            return null;
        });
    }

    /**
     * 申请并运行于写锁
     *
     * @param run 运行代码
     */
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
     * @param run 带返回的运行代码
     *
     * @return 接口生成的数据
     */
    <T> T read(@NotNull Supplier<T> run);

    /**
     * 申请并运行于写锁
     *
     * @param run 带返回的运行代码
     *
     * @return 接口生成的数据
     */
    <T> T write(@NotNull Supplier<T> run);

    //----------------------------------------------------------------------------------------------

    /**
     * 尝试运行于读锁
     *
     * @param ecla 异常的类
     * @param run  运行代码
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
     * @param run  运行代码
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
     * @param run  带返回的运行代码
     *
     * @return 接口生成的数据
     */
    <T, E extends Exception> T tryread(@NotNull Class<E> ecla, @NotNull trySupplier<T, E> run)
    throws E;

    /**
     * 尝试运行于写锁
     *
     * @param ecla 异常的类
     * @param run  带返回的运行代码
     *
     * @return 接口生成的数据
     */
    <T, E extends Exception> T trywrite(@NotNull Class<E> ecla, @NotNull trySupplier<T, E> run)
    throws E;

    //----------------------------------------------------------------------------------------------

    /**
     * 尝试运行于读锁
     *
     * @param ecla 异常的类
     * @param run  运行代码
     * @param cate 异常处理代码
     *
     * @since SyLock 0.0.2
     */
    default
    <E extends Exception> void tryread(@NotNull Class<E> ecla, @NotNull tryRunnable<E> run,
                                       @NotNull Consumer<E> cate)
    {
        tryread(ecla, () -> {
            run.run();
            return null;
        }, (e) -> {
            cate.accept(e);
            return null;
        }, () -> {});
    }

    /**
     * 尝试运行于写锁
     *
     * @param ecla 异常的类
     * @param run  运行代码
     * @param cate 异常处理代码
     *
     * @since SyLock 0.0.2
     */
    default
    <E extends Exception> void trywrite(@NotNull Class<E> ecla, @NotNull tryRunnable<E> run,
                                        @NotNull Consumer<E> cate)
    {
        trywrite(ecla, () -> {
            run.run();
            return null;
        }, (e) -> {
            cate.accept(e);
            return null;
        }, () -> {});
    }

    //------------------------------------

    /**
     * 尝试运行于读锁
     *
     * @param ecla 异常的类
     * @param run  带返回的运行代码
     * @param cate 带返回的异常处理代码
     *
     * @return 接口生成的数据
     *
     * @since SyLock 0.0.2
     */
    default
    <T, E extends Exception> T tryread(@NotNull Class<E> ecla, @NotNull trySupplier<T, E> run,
                                       @NotNull Function<E, T> cate)
    { return tryread(ecla, run, cate, () -> {}); }

    /**
     * 尝试运行于写锁
     *
     * @param ecla 异常的类
     * @param run  带返回的运行代码
     * @param cate 带返回的异常处理代码
     *
     * @return 接口生成的数据
     *
     * @since SyLock 0.0.2
     */
    default
    <T, E extends Exception> T trywrite(@NotNull Class<E> ecla, @NotNull trySupplier<T, E> run,
                                        @NotNull Function<E, T> cate)
    { return trywrite(ecla, run, cate, () -> {}); }

    //----------------------------------------------------------------------------------------------

    /**
     * 尝试运行于读锁
     *
     * @param ecla 异常的类
     * @param run  运行代码
     * @param cate 异常处理代码
     *
     * @since SyLock 0.0.2
     */
    default
    <E extends Exception> void tryread(@NotNull Class<E> ecla, @NotNull tryRunnable<E> run,
                                       @NotNull Consumer<E> cate, @NotNull Runnable finall)
    {
        tryread(ecla, () -> {
            run.run();
            return null;
        }, (e) -> {
            cate.accept(e);
            return null;
        }, finall);
    }

    /**
     * 尝试运行于写锁
     *
     * @param ecla 异常的类
     * @param run  运行代码
     * @param cate 异常处理代码
     *
     * @since SyLock 0.0.2
     */
    default
    <E extends Exception> void trywrite(@NotNull Class<E> ecla, @NotNull tryRunnable<E> run,
                                        @NotNull Consumer<E> cate, @NotNull Runnable finall)
    {
        trywrite(ecla, () -> {
            run.run();
            return null;
        }, (e) -> {
            cate.accept(e);
            return null;
        }, finall);
    }

    //------------------------------------

    /**
     * 尝试运行于读锁
     *
     * @param ecla   异常的类
     * @param run    带返回的运行代码
     * @param cate   带返回的异常处理代码
     * @param finall finally 块处理代码
     *
     * @return 接口生成的数据
     *
     * @since SyLock 0.0.2
     */
    default
    <T, E extends Exception> T tryread(@NotNull Class<E> ecla, @NotNull trySupplier<T, E> run,
                                       @NotNull Function<E, T> cate, @NotNull Runnable finall)
    {
        try {
            return tryread(ecla, run);
        } catch ( Exception e ) {
            return cate.apply((E) e);
        } finally {
            finall.run();
        }
    }

    /**
     * 尝试运行于写锁
     *
     * @param ecla   异常的类
     * @param run    带返回的运行代码
     * @param cate   带返回的异常处理代码
     * @param finall finally 块处理代码
     *
     * @return 接口生成的数据
     *
     * @since SyLock 0.0.2
     */
    default
    <T, E extends Exception> T trywrite(@NotNull Class<E> ecla, @NotNull trySupplier<T, E> run,
                                        @NotNull Function<E, T> cate, @NotNull Runnable finall)
    {
        try {
            return trywrite(ecla, run);
        } catch ( Exception e ) {
            return cate.apply((E) e);
        } finally {
            finall.run();
        }
    }

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
