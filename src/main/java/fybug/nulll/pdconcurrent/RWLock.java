package fybug.nulll.pdconcurrent;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

import fybug.nulll.pdconcurrent.fun.trySupplier;
import lombok.Getter;

/**
 * 使用读写锁 {@link ReentrantReadWriteLock} 实现的并发管理
 * <pre>使用并发管理：
 *     public static
 *     void main(String[] args) {
 *         var lock = new RWLock();
 *         lock.read(() -> System.out.println("adsa"));
 *         lock.write(() -> System.out.println("adsa"));
 *     }</pre>
 * <pre>不使用：
 *     public static
 *     void main(String[] args) {
 *         var lock = new ReentrantReadWriteLock();
 *         lock.readLock().lock();
 *         try {
 *             System.out.println("adsa");
 *         } finally {
 *             lock.readLock().unlock();
 *         }
 *         lock.writeLock().lock();
 *         try {
 *             System.out.println("adsa");
 *         } finally {
 *             lock.writeLock().unlock();
 *         }
 *     }</pre>
 *
 * @author fybug
 * @version 0.0.1
 * @since PDConcurrent 0.0.1
 */
public
class RWLock implements SyLock {

    @Getter final private ReentrantReadWriteLock LOCK;
    @Getter final private ReentrantReadWriteLock.ReadLock Read_LOCK;
    @Getter final private ReentrantReadWriteLock.WriteLock Write_LOCK;

    public
    RWLock() {this(false);}

    /** 生成并发管理，并指定是否使用公平锁 */
    public
    RWLock(boolean fair) {
        LOCK = new ReentrantReadWriteLock(false);
        Read_LOCK = LOCK.readLock();
        Write_LOCK = LOCK.writeLock();
    }

    //----------------------------------------------------------------------------------------------

    @Override
    public
    <T> T read(@NotNull Supplier<T> run) {
        try {
            Read_LOCK.lock();
            return run.get();
        } finally {
            Read_LOCK.unlock();
        }
    }

    @Override
    public
    <T> T write(@NotNull Supplier<T> run) {
        try {
            Write_LOCK.lock();
            return run.get();
        } finally {
            Write_LOCK.unlock();
        }
    }

    //----------------------------------------------------------------------------------------------

    @Override
    public
    <T, E extends Exception> T tryread(@NotNull Class<E> ecla, @NotNull trySupplier<T, E> run)
    throws E
    {
        try {
            Read_LOCK.lock();
            return run.get();
        } finally {
            Read_LOCK.unlock();
        }
    }

    @Override
    public
    <T, E extends Exception> T trywrite(@NotNull Class<E> ecla, @NotNull trySupplier<T, E> run)
    throws E
    {
        try {
            Write_LOCK.lock();
            return run.get();
        } finally {
            Write_LOCK.unlock();
        }
    }

    //----------------------------------------------------------------------------------------------

    /** 获取 {@link Condition} */
    @NotNull
    public
    Condition newReadCondition() {return Read_LOCK.newCondition();}

    /** 获取 {@link Condition} */
    @NotNull
    public
    Condition newWriteCondition() {return Write_LOCK.newCondition();}
}