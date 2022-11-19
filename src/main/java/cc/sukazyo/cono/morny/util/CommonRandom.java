package cc.sukazyo.cono.morny.util;

import javax.annotation.Nonnegative;
import java.util.concurrent.ThreadLocalRandom;

public class CommonRandom {
	
	public static boolean probabilityTrue (@Nonnegative int probability, @Nonnegative int base) {
		if (probability < 1) throw new IllegalArgumentException("the probability must be a positive value!");
		if (base < 1) throw new IllegalArgumentException("the probability base must be a positive value!");
		return probability > ThreadLocalRandom.current().nextInt(base);
	}
	
	public static boolean probabilityTrue (@Nonnegative int probabilityIn) {
		return (probabilityTrue(1, probabilityIn));
	}
	
	public static boolean iif () {
		return ThreadLocalRandom.current().nextBoolean();
	}
	
}
