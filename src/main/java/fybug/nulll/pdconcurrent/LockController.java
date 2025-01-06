package fybug.nulll.pdconcurrent;

public
interface LockController {
	// todo 降级为读锁
	boolean toreadlock();

	// todo 写锁次数
	long writelocknum();

	// todo 读锁次数
	long readlocknum();

	// todo 是否上过锁
	boolean islock();
}
