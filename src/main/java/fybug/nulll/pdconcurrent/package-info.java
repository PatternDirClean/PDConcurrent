/**
 * <h2>并发管理工具.</h2>
 * 用于并发管理的工具，使用回调在并发域中执行代码。<br/>
 * 根据实现的不同并发管理也不同，但都实现{@link fybug.nulll.pdconcurrent.SyLock}接口<br/>
 * 附带{@code try***}的功能接口包{@link fybug.nulll.pdconcurrent.fun}，在java原有的功能接口的基础上允许抛出异常
 *
 * @author fybug
 * @version 0.1.3
 * @since JDK 23+
 */
package fybug.nulll.pdconcurrent;