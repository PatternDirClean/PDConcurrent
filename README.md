<p align="center">
<img src="https://images.gitee.com/uploads/images/2020/1022/231243_f2ae30da_2071767.png" width="229" height="210" alt="Icon"/>

# PDConcurrent

![java library](https://img.shields.io/badge/type-Libary-gr.svg "type")
![JDK 14](https://img.shields.io/badge/JDK-14-green.svg "SDK")
![Gradle 6.4.1](https://img.shields.io/badge/Gradle-6.4.1-04303b.svg "tool")
![Apache 2](https://img.shields.io/badge/license-Apache%202-blue.svg "License")

-- [Java Doc](https://apidoc.gitee.com/PatternDirClean/PDConcurrent) --

-- [项目主页](https://fybug.gitee.io/projectsby/PDConcurrent.html) --

-------------------------------------------------------------------------------

## 简介

轻量级，高可靠，基于接口代码插入和 java 原生并发管理实现的 java 并发管理工具，可让开发人员省去处理并发的一些细节。

接口统一采用读写锁规范，可使用接口中不同的工厂方法，构造内部使用不同并发实现的管理工具。

使用接口将运行的代码传入并发域中，可通过接口抛出异常与返回数据，亦可将 try 块中的代码通过接口的方式插入并发域中的 try 块中。

## 使用方法
请导入其 `jar` 文件,文件在 **发行版** 或项目的 **jar** 文件夹下可以找到

**发行版中可以看到全部版本<br/>项目下的 jar 文件夹是当前最新的每夜版**

可通过 **WIKI** 或者 **doc文档** 查看示例

## 分支说明
**dev-master**：当前的开发分支，可以拿到最新的每夜版 jar

**releases**：当前发布分支，稳定版的源码

-------------------------------------------------------------------------------

### 提供bug反馈或建议

- [码云Gitee](https://gitee.com/PatternDirClean/PDConcurrent/issues)
- [Github](https://github.com/PatternDirClean/PDConcurrent/issues)