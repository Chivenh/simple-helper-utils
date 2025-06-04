package com.fhtiger.helper.utils.helpful;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

/**
 * 自定义线程工具类
 * @author Chivenh
 */
public class CustomThreadFactory implements ThreadFactory {
	private static final AtomicInteger POOL_NUMBER = new AtomicInteger(1);
	private final ThreadGroup group;
	private final AtomicInteger threadNumber = new AtomicInteger(1);
	private final String namePrefix;

	@SuppressWarnings("removal")
	public CustomThreadFactory(String namePrefix) {
		SecurityManager s = System.getSecurityManager();
		group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
		this.namePrefix= StringUtils.hasText(namePrefix)?namePrefix:("pool-" + POOL_NUMBER.getAndIncrement() + "-thread-");
	}

	@Override
	public Thread newThread(@NonNull Runnable r) {
		Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
		if (t.isDaemon()) {
			t.setDaemon(false);
		}
		if (t.getPriority() != Thread.NORM_PRIORITY) {
			t.setPriority(Thread.NORM_PRIORITY);
		}
		return t;
	}
}