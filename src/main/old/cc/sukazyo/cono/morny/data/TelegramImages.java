package cc.sukazyo.cono.morny.data;

import cc.sukazyo.cono.morny.MornyAssets;
import cc.sukazyo.cono.morny.daemon.MornyReport;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;

import static cc.sukazyo.cono.morny.Log.exceptionLog;
import static cc.sukazyo.cono.morny.Log.logger;

/**
 * The images of morny will use.
 *
 * @since 1.0.0-RC4
 */
public class TelegramImages {
	
	/**
	 * Image that stored in the {@link MornyAssets#pack}.
	 * <p>
	 * It has a final {@link #assetsPath} record that is its store location,
	 * and has a {@link #cache} of the binary data.
	 *
	 * @since 1.0.0-RC4
	 */
	public static class AssetsFileImage {
		
		/** the path where the image is stored in {@link MornyAssets#pack}. */
		@Nonnull private final String assetsPath;
		/** the binary data cache of the image.<p>{@link null} means it hasn't been cached. */
		@Nullable private byte[] cache = null;
		
		/**
		 * An {@link AssetsFileImage}.
		 *
		 * @param path the image path relative to {@link MornyAssets#pack}'s root.
		 */
		AssetsFileImage (@Nonnull String path) {
			this.assetsPath = path;
		}
		
		/**
		 * Get the binary data.
		 * <p>
		 * Will read the {@link #cache} firstly. If read {@link null},
		 * then it will try {@link #read load the file}, and read again.
		 *
		 * @return The binary data of the image.
		 * @throws IllegalStateException While the {@link #read()} failed to read data and
		 *                               the result {@link #cache} is still null
		 */
		@Nonnull public byte[] get() {
			if (cache == null) read();
			if (cache == null) throw new IllegalStateException("Failed get assets file image.");
			return cache;
		}
		
		/**
		 * Load the file from {@link MornyAssets#pack}, and stored the binary data to {@link #cache}.
		 * <p>
		 * If failed, it will output the exception to the log and the {@link MornyReport},
		 * and remains the cache's current data.
		 */
		private void read() {
			try (InputStream stream = MornyAssets.pack.getResource(assetsPath).read()) {
				this.cache = stream.readAllBytes();
			} catch (IOException e) {
				logger.error("Cannot read resource file");
				logger.error(exceptionLog(e));
				MornyReport.exception(e, "Cannot read resource file");
			}
		}
		
	}
	
	public static final AssetsFileImage IMG_ABOUT = new AssetsFileImage("images/featured-image@0.5x.jpg");
	
}
