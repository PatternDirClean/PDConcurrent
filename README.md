<img src="https://images.gitee.com/uploads/images/2020/1022/231243_f2ae30da_2071767.png" width="229" height="210" alt="Icon"/>

# PDConcurrent

![java library](https://img.shields.io/badge/type-Libary-gr.svg "type")
![JDK 23](https://img.shields.io/badge/JDK-23-green.svg "SDK")
![Gradle 8.10.2](https://img.shields.io/badge/Gradle-8.10.2-04303b.svg "tool")
![Apache 2](https://img.shields.io/badge/license-Apache%202-blue.svg "License")

-------------------------------------------------------------------------------

## 简介

轻量级，高可靠，易学习的并发管理工具。

## 核心用法

如果你已经熟悉 java 的多线程管理，尤其是 ReentrantReadWriteLock 读写锁的话你会更容易学会本工具。

无论内部实现如何，外部接口均提供 **read, **write 方法区分读锁并发域和写锁并发域

### 基础使用示例

```java
public final SyLock lock = SyLock.newObjLock();

public static
void main(String[] args) {
  lock.read(() -> {
    // 并发域代码内容
    return null; // 可选择是否返回
  });
}
```

以上即为核心用法，将需要并发处理的代码通过一个 `Runnable` 接口包起来。启用并发管理以及停止并发管理部分的的代码由本工具封装，直接通过上述代码的方式传入需要运行的内容即可

> SyLock 为该工具的标准接口，同时包含不同实现的基础工厂方法。

## 锁变形示例

可通过接口的工厂方法实例化内部采用不同实现的锁。

### synchronized 锁

```java
public final SyLock lock = SyLock.newObjLock();

public static
void main(String[] args) {
  // 使用
  lock.read(() -> {
    // 并发域代码内容
    return null;
  });

  // 不使用
  synchronized ( lock ){
    // 并发域代码内容
    return null;
  }
}

```

### ReentrantLock 锁

支持使用`newCondition()`获取`Condition`对象

```java
public static
void main(String[] args) {
  // 使用
  SyLock lock = SyLock.newReLock();
  lock.read(() -> {
    // 并发域代码内容
  });

  // 不使用
  ReentrantLock lock = new ReentrantLock();
  try {
    lock.lock();
    // 并发域代码内容
  } finally {
    lock.unlock();
  }
}
```

### ReadWriteLock 锁

支持通过`toread()`进行写锁 => 读锁降级

支持使用`newReadCondition()` `newWriteCondition()`获取`Condition`对象

支持通过`isLocked()` `isWriteLocked()` `isReadLocked()`检查是否被占用

```java
public static
void main(String[] args) {
  // 使用
  SyLock lock = SyLock.newRWLock();
  lock.read(() -> {
    // 并发域代码内容
  });

  // 不使用
  ReadWriteLock lock = new ReentrantReadWriteLock();
  try {
    lock.readLock().lock();
    // 并发域代码内容
  } finally {
    lock.readLock().unlock();
  }
}
```

## 使用方法

请导入其 `jar` 文件,文件在 **发行版** 或项目的 **jar** 文件夹下可以找到

> PDConcurrent.jar 为不包含依赖库的包
>
> PDConcurrent_bin.jar 为包含了依赖库的包
>
> PDConcurrent_all.jar 为包含了依赖库与源码的包
>
> PDConcurrent_sources.jar 为源码包

**发行版中可以看到全部版本<br/>项目下的 jar 文件夹是当前最新的每夜版**

依赖的同系列项目

- [PDUtilFunctionExpand](https://github.com/fybug/PDUtilFunctionExpand)

可通过 **WIKI** 或者 **doc文档** 深入学习本工具

## 分支说明

**dev-master**：当前的开发分支，可以拿到最新的每夜版 jar

**releases**：当前发布分支，稳定版的源码

-------------------------------------------------------------------------------

### 提供bug反馈或建议

- [码云Gitee](https://gitee.com/PatternDirClean/PDConcurrent/issues)
- [Github](https://github.com/PatternDirClean/PDConcurrent/issues)