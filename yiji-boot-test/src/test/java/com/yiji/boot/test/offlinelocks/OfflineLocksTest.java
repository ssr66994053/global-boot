/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved.
 */

/*
 * 修订记录：
 * LiXiang 2016年2月18日 下午5:19:48 创建
 */
package com.yiji.boot.test.offlinelocks;

import com.yiji.boot.test.TestBase;
import com.yiji.concurrent.offlinelocks.Lock;
import com.yiji.concurrent.offlinelocks.LockManager;
import com.yiji.concurrent.offlinelocks.SimpleLockHolder;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 离线锁插件测试。
 *
 * @author Agreal·Lee (e-mail:lixiang@yiji.com)
 *
 */
public class OfflineLocksTest extends TestBase {
	
	@Autowired(required = false)
	private LockManager lockManager;
	
	@Test
	public void testLock() {
		Object lockedObj = new Object();
		Lock lock = this.lockManager.acquireLock(lockedObj, new SimpleLockHolder("holder1", 0));
		try {
			lock.lock();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}
	
}
