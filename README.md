<p align="center">
<img src="https://images.gitee.com/uploads/images/2020/1022/231243_f2ae30da_2071767.png" width="229" height="210" alt="Icon"/>

# PDConcurrent

![java library](https://img.shields.io/badge/type-Libary-gr.svg "type")
![JDK 14](https://img.shields.io/badge/JDK-14-green.svg "SDK")
![Gradle 6.5](https://img.shields.io/badge/Gradle-6.5-04303b.svg "tool")
![Apache 2](https://img.shields.io/badge/license-Apache%202-blue.svg "License")

-- [Java Doc](https://apidoc.gitee.com/fybug/PDConcurrent) --

-- [项目主页](https://fybug.gitee.io/projectsby/PDConcurrent.html) --

-------------------------------------------------------------------------------

**专注于单个功能，无附加bug**

## 简介

轻量级，高可靠，易学习的并发管理工具。

## 核心用法

如果你已经熟悉 java 的多线程管理，尤其是 ReentrantReadWriteLock 读写锁的话你会更容易学会本工具。

本工具采用读写锁标准，无论内部实现如何，外部接口均提供 **read, **write 方法区分读锁并发域和写锁并发域

### \> > 基础使用示例
```java
// 尝试申请读锁
SyLock.newObjLock().read(() -> {
    // 并发域代码内容
    [return null;]? // 可选择是否返回
});

// 尝试申请写锁
SyLock.newObjLock().write(() -> {
    [return null;]?
});
```

以上即为核心用法，将需要并发处理的代码通过一个 `Runnable` 接口包起来。启用并发管理以及停止并发管理部分的的代码由本工具封装，直接通过上述代码的方式传入需要运行的内容即可

> SyLock 为该工具的标准接口，同时包含不同实现的基础工厂方法。

## 锁变形示例

可通过接口的工厂方法实例化内部采用不同实现的锁。

### \> > synchronized 锁
```java
// 使用
SyLock.newObjLock().read(() -> {
    // 并发域代码内容
});

// 不使用
synchronized ( new Object() ){
    // 并发域代码内容
}
```

### \> > ReentrantLock 锁
```java
// 使用
SyLock.newReLock().read(() -> {
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
```

### \> > ReadWriteLock 锁
```java
// 使用
SyLock.newRWLock().read(() -> {
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
```

## 使用方法
请导入其 `jar` 文件,文件在 **发行版** 或项目的 **jar** 文件夹下可以找到

其中 ` PDConcurrent_all.jar` 是包含了源码的包

**发行版中可以看到全部版本<br/>项目下的 jar 文件夹是当前最新的每夜版**

可通过 **WIKI** 或者 **doc文档** 深入学习本工具

## 分支说明
**dev-master**：当前的开发分支，可以拿到最新的每夜版 jar

**releases**：当前发布分支，稳定版的源码

-------------------------------------------------------------------------------

### 提供bug反馈或建议

- [码云Gitee](https://gitee.com/PatternDirClean/PDConcurrent/issues)
- [Github](https://github.com/PatternDirClean/PDConcurrent/issues)