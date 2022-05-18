package cc.sukazyo.cono.morny.daemon;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.concurrent.locks.ReentrantLock;

import static cc.sukazyo.cono.morny.Log.logger;

public class TrackerDataManager {
	
	public static final ReentrantLock trackingLock = new ReentrantLock();
	
	private static final ReentrantLock recordLock = new ReentrantLock();
	private static HashMap<Long, HashMap<Long, TreeSet<Long>>> record = new HashMap<>();
	
	public static final TrackerDaemon DAEMON = new TrackerDaemon();
	public static class TrackerDaemon extends Thread {
		
		public TrackerDaemon () { this.setName("TRACKER"); }
		
		@Override
		public void run () {
			trackingLock.lock();
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
		}
		
	}
	
	public static void record (long chat, long user, long timestamp) {
		recordLock.lock();
		if (!record.containsKey(chat)) record.put(chat, new HashMap<>());
		HashMap<Long, TreeSet<Long>> chatUsers = record.get(chat);
		if (!chatUsers.containsKey(user)) chatUsers.put(user, new TreeSet<>());
		TreeSet<Long> userRecords = chatUsers.get(user);
		userRecords.add(timestamp);
		recordLock.unlock();
	}
	
	public static void init () {
		DAEMON.start();
	}
	
	public static void save () {
		logger.info("start writing tracker data.");
		save(reset());
		logger.info("done writing tracker data.");
	}
	
	private static HashMap<Long, HashMap<Long, TreeSet<Long>>> reset () {
		recordLock.lock();
		HashMap<Long, HashMap<Long, TreeSet<Long>>> recordOld = record;
		record = new HashMap<>();
		recordLock.unlock();
		return recordOld;
	}
	
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
					channelCurrent.write(ByteBuffer.wrap(
							String.format("%d\n", timestamp).getBytes(StandardCharsets.UTF_8)
					));
					
				} catch (Exception e) {
					logger.error(String.format("exception in write tracker data: %d/%d/%d", chat, user, timestamp));
					e.printStackTrace(System.out);
				}
			}
			
		}));
		
	}
	
	private static FileChannel openFile (long chat, long user, long day) throws IOException {
		File data = new File(String.format("./data/tracker/%d/%d", chat, user));
		if (!data.isDirectory()) if (!data.mkdirs()) throw new IOException("Cannot create file directory " + data.getPath());
		File file = new File(data, String.valueOf(day));
		if (!file.isFile()) if (!file.createNewFile()) throw new IOException("Cannot create file " + file.getPath());
		return new FileOutputStream(file, true).getChannel();
	}
	
}
