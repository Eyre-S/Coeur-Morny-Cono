package cc.sukazyo.cono.morny;

import java.io.IOException;

/**
 * Some of the static information of Morny.
 */
public class MornyAbout {
	
	/**
	 * ASCII art of Morny Featured Image.
	 * <p>
	 * used for Coeur starting welcome screen.
	 * <p>
	 * stored at <i><u>/assets_morny/texts/server-hello.txt</u></i>
	 */
	public static final String MORNY_PREVIEW_IMAGE_ASCII;
	static {
		try {
			MORNY_PREVIEW_IMAGE_ASCII = MornyAssets.pack.getResource("texts/server-hello.txt").readAsString();
		} catch (IOException e) {
			throw new RuntimeException("Cannot read MORNY_PREVIEW_IMAGE_ASCII from assets pack", e);
		}
	}
	
	public static final String MORNY_SOURCECODE_LINK = "https://github.com/Eyre-S/Coeur-Morny-Cono";
	public static final String MORNY_SOURCECODE_SELF_HOSTED_MIRROR_LINK = "https://storage.sukazyo.cc/Eyre_S/Coeur-Morny-Cono";
	public static final String MORNY_ISSUE_TRACKER_LINK = "https://github.com/Eyre-S/Coeur-Morny-Cono/issues";
	public static final String MORNY_USER_GUIDE_LINK = "https://book.sukazyo.cc/morny";
	
}
