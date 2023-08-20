package cc.sukazyo.cono.morny.daemon;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.concurrent.locks.ReentrantLock;

import static cc.sukazyo.cono.morny.Log.exceptionLog;
import static cc.sukazyo.cono.morny.Log.logger;

public class TrackerDataManager {
	
	/** {@link TrackerDaemon} 的锁。保证在程序中只有一个 TrackerDaemon 会运行。 */
	public static final ReentrantLock trackingLock = new ReentrantLock();
	/** {@link #record Tracker 缓存}的锁 <p> 为保证对 Tracker 缓存的操作不会造成线程冲突，在操作缓存数据前应先取得此锁。 */
	private static final ReentrantLock recordLock = new ReentrantLock();
	/** Tracker 数据的内存缓存 <p> 进行数据操作前请先取得对应的{@link #recordLock 锁} */
	private static HashMap<Long, HashMap<Long, TreeSet<Long>>> record = new HashMap<>();
	
	public static final TrackerDaemon DAEMON = new TrackerDaemon();
	public static class TrackerDaemon extends Thread {
		
		public TrackerDaemon () { this.setName("TRACKER"); }
		
		@Override
		public void run () {
			trackingLock.lock();
			logger.info("Tracker started.");
			long lastWaitTimestamp = System.currentTimeMillis();
			boolean postProcess = false;
			do {
				lastWaitTimestamp += 10 * 60 * 1000;
				long sleeping = lastWaitTimestamp - System.currentTimeMillis();
				if (sleeping > 0) {
					try { Thread.sleep(sleeping); } catch (InterruptedException e) { interrupt(); }
				} else {
					logger.warn("Tracker may be too busy to process data!!");
					lastWaitTimestamp = System.currentTimeMillis();
				}
				if (interrupted()) {
					postProcess = true;
					logger.info("CALLED TO EXIT! writing cache.");
				}
				if (record.size() != 0) {
					save();
				}
				else logger.info("nothing to do yet");
			} while (!postProcess);
			trackingLock.unlock();
			logger.info("Tracker exited.");
		}
		
	}
	
	/**
	 * 向 Tracker 缓存写入一条 tracker 数据.
	 * <p>
	 * <font color=green>这个方法对于 Tracker 缓存是原子化的。</font>
	 * 
	 * @param chat tracker 所属的 Telegram Chat ID
	 * @param user tracker 所记录的 Telegram User ID
	 * @param timestamp tracker 被生成时的 UTC 时间戳
	 */
	public static void record (long chat, long user, long timestamp) {
		recordLock.lock();
		if (!record.containsKey(chat)) record.put(chat, new HashMap<>());
		HashMap<Long, TreeSet<Long>> chatUsers = record.get(chat);
		if (!chatUsers.containsKey(user)) chatUsers.put(user, new TreeSet<>());
		TreeSet<Long> userRecords = chatUsers.get(user);
		userRecords.add(timestamp);
		recordLock.unlock();
	}
	
	/**
	 * 开启 {@link TrackerDaemon}.
	 * <p>
	 * <font color=orange>由于 Tracker 已废弃，这个方法已无作用。</font>
	 */
	@SuppressWarnings("unused")
	public static void init () {
		DAEMON.start();
	}
	
	/**
	 * 执行 Tracker 的保存逻辑.
	 * @see #reset() 弹出 Tracker 缓存
	 * @see #save(HashMap) 执行硬盘写入操作
	 */
	public static void save () {
		logger.info("start writing tracker data.");
		save(reset());
		logger.info("done writing tracker data.");
	}
	
	/**
	 * 将 Tracker 的缓存数据弹出.
	 * <p>
	 * 这个方法将返回现在 Tracker 的所有缓存数据，然后清除缓存。
	 * <p>
	 * <font color=green>这个方法对于 Tracker 缓存是原子化的。</font>
	 * 
	 * @return 当前 Tracker 所包含的内容
	 */
	private static HashMap<Long, HashMap<Long, TreeSet<Long>>> reset () {
		recordLock.lock();
		HashMap<Long, HashMap<Long, TreeSet<Long>>> recordOld = record;
		record = new HashMap<>();
		recordLock.unlock();
		return recordOld;
	}
	
	/**
	 * 将 Tracker 数据写入到硬盘.
	 *
	 * @param record 需要保存的 Tracker 数据集
	 */
	private static void save (HashMap<Long, HashMap<Long, TreeSet<Long>>> record) {
		
		{
			if (!record.containsKey(0L)) record.put(0L, new HashMap<>());
			HashMap<Long, TreeSet<Long>> chatUsers = record.get(0L);
			if (!chatUsers.containsKey(0L)) chatUsers.put(0L, new TreeSet<>());
			TreeSet<Long> userRecords = chatUsers.get(0L);
			userRecords.add(System.currentTimeMillis());
		}
		
		record.forEach((chat, chatUsers) -> chatUsers.forEach((user, userRecords) -> {
			
			long dayCurrent = -1;
			FileChannel channelCurrent = null;
			
			for (long timestamp : userRecords) {
				try {
					
					long day = timestamp / (24 * 60 * 60 * 1000);
					if (dayCurrent != day) {
						if (channelCurrent != null) channelCurrent.close();
						channelCurrent = openFile(chat, user, day);
						dayCurrent = day;
					}
					
					assert channelCurrent != null;
					final int result = channelCurrent.write(ByteBuffer.wrap(
							String.format("%d\n", timestamp).getBytes(StandardCharsets.UTF_8)
					));
					
					if (result == 0) logger.warn("writing tracker data %d/%d/%d: write only 0 bytes! is anything wrong?");
					
				} catch (Exception e) {
					final String message = String.format("exception in write tracker data: %d/%d/%d", chat, user, timestamp);
					logger.error(message);
					logger.error(exceptionLog(e));
					MornyReport.exception(e, message);
				}
			}
			
		}));
		
	}
	
	private static FileChannel openFile (long chat, long user, long day) throws IOException {
		File data = new File(String.format("./data/tracker/%d/%d", chat, user));
		if (!data.isDirectory()) if (!data.mkdirs()) throw new IOException("Cannot create file directory " + data.getPath());
		File file = new File(data, String.valueOf(day));
		if (!file.isFile()) if (!file.createNewFile()) throw new IOException("Cannot create file " + file.getPath());
		return FileChannel.open(file.toPath(), StandardOpenOption.APPEND);
	}
	
}
