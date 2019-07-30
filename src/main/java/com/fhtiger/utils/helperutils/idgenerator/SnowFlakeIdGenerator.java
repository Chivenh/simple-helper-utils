package com.fhtiger.utils.helperutils.idgenerator;

import com.fhtiger.utils.helperutils.helper.DateHelper;

/**
 * 雪花算法生成ID.id的结构如下(每部分用-分开):
 * 
 * 0 - 0000000000 0000000000 0000000000 0000000000 000 - 0000 - 0000 - 0000000000 00
 * 
 * 第一位为未使用,(主要是防止生成负数id，如果使用可能生成负数id)，接下来的43位为毫秒级时间(41位的长度可以使用69年)，然后是4位datacenterId和4位workerId(8位的长度最多支持部署256个节点）
 * ，最后12位是毫秒内的计数（12位的计数顺序号支持每个节点每毫秒产生4096个ID序号）
 * 
 * 一共加起来刚好64位，为一个Long型。(转换成字符串长度为18)
 * 
 * snowflake生成的ID整体上按照时间自增排序，并且整个分布式系统内不会产生ID碰撞（由datacenter和workerId作区分），并且效率较高。据说：snowflake每秒能够产生26万个ID。.
 *
 * @author LFH
 * @since 2019/7/30 17:44
 * @version 0.0.1
 */
public class SnowFlakeIdGenerator implements IdGenerator<Long> {
	/** 起始时间 */
	private long firstTimestamp = DateHelper.parse("2019-01-01").getTime();

	/** dataCenterId所占的位数 */
	private int dataCenterIdBits = 4;

	/** workId所占的位数 */
	private int workerIdBits = 4;

	/** 每毫秒可生成的序列所占的位数 */
	private int sequenceBits = 12;

	/** workId的最大值 */
	private long maxWorkerId = ~(-1L << workerIdBits);

	/** dataCenterId的最大值 */
	private long maxDatacenterId = ~(-1L << dataCenterIdBits);

	/** 1毫秒内可生成的序号的最大值 */
	private long maxSequence = ~(-1L << sequenceBits);

	/** workId在id中最终要左移位的位数 */
	private int workerIdShift = sequenceBits;

	/** dataCenterId在id中最终要左移位的位数 */
	private int dataCenterIdShift = sequenceBits + workerIdBits;

	/** 时间戳在id中最终要左移位的位数 */
	private int timestampShift = sequenceBits + workerIdBits + dataCenterIdBits;

	// private long sequenceMask = -1L ^ (-1L << sequenceBits);

	private long workerId = 0L;

	private long dataCenterId = 0L;

	private long sequence = 0L;

	/** 最后一次生成序号的时间 */
	private long lastTimestamp = -1L;

	/** 默认构造器，workerId和datacenterId为0 */
	public SnowFlakeIdGenerator() {
	}

	/**
	 * 根据所占位数构造
	 * 
	 * @param dataCenterIdBits dataCenterId所占的位数
	 * @param workerIdBits workerId所占的位数
	 * @param sequenceBits 每毫秒可生成的序列所占的位数
	 */
	public SnowFlakeIdGenerator(int dataCenterIdBits, int workerIdBits, int sequenceBits) {
		setup(dataCenterIdBits, workerIdBits, sequenceBits, dataCenterId, workerId);
	}

	/**
	 * 使用workerId和datacenterId构造
	 * 
	 * @param dataCenterId dataCenterId
	 * @param workerId workerId
	 */
	public SnowFlakeIdGenerator(long dataCenterId, long workerId) {
		setDataCenterId(dataCenterId);
		setWorkerId(workerId);
	}

	@Override
	public synchronized Long generate() {
		long timestamp = timestamp();
		if (timestamp < lastTimestamp) {
			throw new RuntimeException(String.format(
					"Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
		}

		if (lastTimestamp == timestamp) {
			sequence = (sequence + 1);// & sequenceMask;
			if (sequence == maxSequence) {
				timestamp = nextTimestamp(lastTimestamp);
			}
		} else {
			sequence = 0L;
		}
		lastTimestamp = timestamp;

		return ((timestamp - firstTimestamp) << timestampShift) | (dataCenterId << dataCenterIdShift)
				| (workerId << workerIdShift) | sequence;
	}

	/**
	 * 生成在lastTimestamp时间之后的时间戳
	 * 
	 * @param lastTimestamp  long
	 * @return long
	 */
	protected long nextTimestamp(long lastTimestamp) {
		long timestamp = timestamp();
		while (timestamp <= lastTimestamp) {
			timestamp = timestamp();
		}
		return timestamp;
	}

	/**
	 * 生成当前时间戳
	 * 
	 * @return long
	 */
	protected long timestamp() {
		return System.currentTimeMillis();
	}

	/**
	 * 设置参数
	 * 
	 * @param dataCenterIdBits dataCenterId所占的位数
	 * @param workerIdBits workerId所占的位数
	 * @param sequenceBits 每毫秒可生成的序列所占的位数
	 * @param dataCenterId   dataCenterId
	 * @param workerId workerID
	 */
	public void setup(int dataCenterIdBits, int workerIdBits, int sequenceBits, long dataCenterId, long workerId) {
		if (dataCenterIdBits > 10 || dataCenterIdBits < 0) {
			throw new IllegalArgumentException("datacenter Id的位数必须为0~10之间");
		}
		if (workerIdBits > 10 || workerIdBits < 0) {
			throw new IllegalArgumentException("worker Id的位数必须为0~10之间");
		}
		if (sequenceBits > 20 || sequenceBits < 1) {
			throw new IllegalArgumentException("sequence的位数必须为1~20之间");
		}

		this.dataCenterIdBits = dataCenterIdBits;
		this.workerIdBits = workerIdBits;
		this.sequenceBits = sequenceBits;

		workerIdShift = sequenceBits;
		dataCenterIdShift = sequenceBits + workerIdBits;
		timestampShift = sequenceBits + workerIdBits + dataCenterIdBits;

		maxWorkerId = ~(-1L << workerIdBits);
		maxDatacenterId = ~(-1L << dataCenterIdBits);
		maxSequence = ~(-1L << sequenceBits);

		this.setDataCenterId(dataCenterId);
		this.setWorkerId(workerId);

	}

	public long getFirstTimestamp() {
		return firstTimestamp;
	}

	public void setFirstTimestamp(long firstTimestamp) {
		this.firstTimestamp = firstTimestamp;
	}

	public long getWorkerId() {
		return workerId;
	}

	public void setWorkerId(long workerId) {
		if (workerId > maxWorkerId || workerId < 0) {
			throw new IllegalArgumentException(
					String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
		}

		this.workerId = workerId;
	}

	public long getDataCenterId() {
		return dataCenterId;
	}

	public void setDataCenterId(long dataCenterId) {
		if (dataCenterId > maxDatacenterId || dataCenterId < 0) {
			throw new IllegalArgumentException(
					String.format("datacenter Id can't be greater than %d or less than 0", maxDatacenterId));
		}

		this.dataCenterId = dataCenterId;
	}

	public long getMaxWorkerId() {
		return maxWorkerId;
	}

	public long getMaxDatacenterId() {
		return maxDatacenterId;
	}

	public int getWorkerIdBits() {
		return workerIdBits;
	}

	public int getSequenceBits() {
		return sequenceBits;
	}

	public int getDataCenterIdShift() {
		return dataCenterIdShift;
	}

}
