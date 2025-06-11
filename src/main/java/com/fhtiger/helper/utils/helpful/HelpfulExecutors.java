package com.fhtiger.helper.utils.helpful;

import java.time.Duration;
import java.util.concurrent.*;

/**
 * HelpfulExecutors
 * 扩展{@link Executors}功能，对一些非安全初始化进行安全的初始化简化
 *
 * @author Chivenh
 * @since 2022年08月18日 17:20
 * @see Executors
 */
@SuppressWarnings({ "unused" })

public final class HelpfulExecutors {

	private HelpfulExecutors() throws IllegalAccessException {
		throw new IllegalAccessException("此工具类不能被实例化!");
	}

	/**
	 * Shutdown
	 *
	 * @param processExecutor -
	 * @param waitTime        -
	 * @return -
	 * @throws InterruptedException -
	 */
	public static boolean shutdownExecutor(ExecutorService processExecutor, Duration waitTime) throws InterruptedException {
		if (processExecutor.isTerminated()) {
			return true;
		}
		processExecutor.shutdown();
		return processExecutor.awaitTermination(waitTime.toMillis(), TimeUnit.MILLISECONDS);
	}

	/**
	 * 创建固定线程数的线程池
	 *
	 * @param threadPrefix 线程名前缀
	 * @param nThreads     线程数
	 * @return -
	 * @see Executors#newFixedThreadPool(int)
	 */
	public static ExecutorService newFixedThreadPool(final String threadPrefix, final int nThreads) {
		return new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), new CustomThreadFactory(threadPrefix));
	}

	/**
	 * 创建固定线程数的线程池
	 *
	 * @param threadPrefix 线程名前缀
	 * @param queueLimit   工作队列数量，默认为{@link Integer#MAX_VALUE}
	 * @param nThreads     线程数
	 * @return -
	 * @see Executors#newFixedThreadPool(int)
	 */
	public static ExecutorService newFixedThreadPool(final String threadPrefix, final int queueLimit, final int nThreads) {
		return new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(queueLimit), new CustomThreadFactory(threadPrefix));
	}

	/**
	 * {@link Executors#newWorkStealingPool(int)}
	 *
	 * @param parallelism -
	 * @return -
	 * @see Executors#newWorkStealingPool(int)
	 */
	public static ExecutorService newWorkStealingPool(int parallelism) {
		return Executors.newWorkStealingPool(parallelism);
	}

	/**
	 * {@link Executors#newWorkStealingPool()}
	 *
	 * @return -
	 * @see Executors#newWorkStealingPool()
	 */
	public static ExecutorService newWorkStealingPool() {
		return Executors.newWorkStealingPool();
	}

	/**
	 * 创建单个线程的线程池
	 *
	 * @param threadPrefix 线程名前缀
	 * @return -
	 * @see Executors#newSingleThreadExecutor(ThreadFactory)
	 */
	public static ExecutorService newSingleThreadExecutor(final String threadPrefix) {
		return Executors.newSingleThreadExecutor(new CustomThreadFactory(threadPrefix));
	}

	/**
	 * {@link Executors#newCachedThreadPool(ThreadFactory)}
	 *
	 * @param threadPrefix 线程名前缀
	 * @return -
	 * @see Executors#newCachedThreadPool(ThreadFactory)
	 */
	public static ExecutorService newCachedThreadPool(final String threadPrefix) {
		return Executors.newCachedThreadPool(new CustomThreadFactory(threadPrefix));
	}

	/**
	 * {@link Executors#newCachedThreadPool(ThreadFactory)}
	 *
	 * @param threadPrefix 线程名前缀
	 * @param fair         队列任务添加时公平或公平
	 * @return -
	 * @see Executors#newCachedThreadPool(ThreadFactory)
	 */
	public static ExecutorService newCachedThreadPool(final String threadPrefix, final boolean fair) {
		return new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<>(fair), new CustomThreadFactory(threadPrefix));
	}

	/**
	 * {@link Executors#newCachedThreadPool(ThreadFactory)}
	 *
	 * @param threadPrefix 线程名前缀
	 * @param queueLimit   任务队列长度
	 * @return -
	 * @see Executors#newCachedThreadPool(ThreadFactory)
	 */
	public static ExecutorService newCachedThreadPool(final String threadPrefix, final int queueLimit) {
		return new ThreadPoolExecutor(0, queueLimit, 60L, TimeUnit.SECONDS, new SynchronousQueue<>(), new CustomThreadFactory(threadPrefix));
	}

	/**
	 * {@link Executors#newCachedThreadPool(ThreadFactory)}
	 *
	 * @param threadPrefix 线程名前缀
	 * @param queueLimit   任务队列长度
	 * @param fair         队列任务添加时公平或公平
	 * @return -
	 * @see Executors#newCachedThreadPool(ThreadFactory)
	 */
	public static ExecutorService newCachedThreadPool(final String threadPrefix, final int queueLimit, final boolean fair) {
		return new ThreadPoolExecutor(0, queueLimit, 60L, TimeUnit.SECONDS, new SynchronousQueue<>(fair), new CustomThreadFactory(threadPrefix));
	}

	/**
	 * {@link Executors#newCachedThreadPool(ThreadFactory)}
	 *
	 * @param threadPrefix  线程名前缀
	 * @param keepAliveTime 线程驻留时间
	 * @param fair          队列任务添加时公平或公平
	 * @return -
	 * @see Executors#newCachedThreadPool(ThreadFactory)
	 */
	public static ExecutorService newCachedThreadPool(final String threadPrefix, long keepAliveTime, final boolean fair) {
		return new ThreadPoolExecutor(0, Integer.MAX_VALUE, keepAliveTime, TimeUnit.SECONDS, new SynchronousQueue<>(fair), new CustomThreadFactory(threadPrefix));
	}

	/**
	 * {@link Executors#newCachedThreadPool(ThreadFactory)}
	 *
	 * @param threadPrefix  线程名前缀
	 * @param queueLimit    任务队列长度
	 * @param keepAliveTime 线程驻留时间
	 * @param fair          队列任务添加时公平或公平
	 * @return -
	 * @see Executors#newCachedThreadPool(ThreadFactory)
	 */
	public static ExecutorService newCachedThreadPool(final String threadPrefix, final int queueLimit, long keepAliveTime, final boolean fair) {
		return new ThreadPoolExecutor(0, queueLimit, keepAliveTime, TimeUnit.SECONDS, new SynchronousQueue<>(fair), new CustomThreadFactory(threadPrefix));
	}

	/**
	 * {@link Executors#newSingleThreadScheduledExecutor(ThreadFactory)}
	 *
	 * @param threadPrefix 线程名前缀
	 * @return -
	 * @see Executors#newSingleThreadScheduledExecutor(ThreadFactory)
	 */
	public static ScheduledExecutorService newSingleThreadScheduledExecutor(final String threadPrefix) {
		return Executors.newSingleThreadScheduledExecutor(new CustomThreadFactory(threadPrefix));
	}

	/**
	 * {@link Executors#newScheduledThreadPool(int, ThreadFactory)}
	 *
	 * @param threadPrefix 线程名前缀
	 * @param corePoolSize 核心线程数
	 * @return -
	 * @see Executors#newScheduledThreadPool(int, ThreadFactory)
	 */
	public static ScheduledExecutorService newScheduledThreadPool(final String threadPrefix, int corePoolSize) {
		return new ScheduledThreadPoolExecutor(corePoolSize, new CustomThreadFactory(threadPrefix));
	}

}
