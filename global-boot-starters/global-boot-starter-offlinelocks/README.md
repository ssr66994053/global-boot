## 1. 组件介绍
此组件自动集成[yiji-offlinelocks](http://gitlab.yiji/agraellee/yiji-offlinelocks)。

## 2. 配置

1) 增加组件依赖

    <dependency>
       <groupId>com.yiji.boot</groupId>
       <artifactId>yiji-boot-starter-offlinelocks</artifactId>
    </dependency>

2) 配置组件参数    


|参数名|参数描述|是否通用配置|
|:---:|:------|:-----|
|`yiji.offlinelocks.lockManagerEnv`|锁管理器环境，支持`zk`和`vm`两种，默认为`zk`,`vm`仅用于测试|否|
|`yiji.offlinelocks.fairLock`| 属性可以设置是否使用公平锁（默认不使用）|否|
|`yiji.offlinelocks.serverTimeout`|服务器超时时间（单位为毫秒），如果小于等于0则表示使用默认超时时间`60000`毫秒|否|
|`yiji.offlinelocks.lockTimeout`|锁超时时间（单位为毫秒），如果小于等于0则表示锁不超时|否|

**以上参数全是可选参数**

更多参考[com.yiji.boot.offlinelocks.OfflineLocksProperties](src/main/java/com/yiji/boot/offlinelocks/OfflineLocksProperties.java)

## 3. 使用说明
         
	@Autowired
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

具体使用细节请参考[yiji-offlinelocks](http://gitlab.yiji/agraellee/yiji-offlinelocks)项目的[WIKI](http://gitlab.yiji/agraellee/yiji-offlinelocks/wikis/home)。
