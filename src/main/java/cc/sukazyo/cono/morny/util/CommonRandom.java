package cc.sukazyo.cono.morny.util;

import javax.annotation.Nonnegative;
import java.util.concurrent.ThreadLocalRandom;

public class CommonRandom {
	
	/**
	 * 通过 {@link ThreadLocalRandom} 以指定的一定几率返回 true.
	 * @param probability 一个正整数，决定在样本空间中有多大的可能性为 true。
	 * @param base 一个正整数，决定样本空间有多大。
	 * @return 有 {@code base} 分之 {@code probability} 的几率，返回值为 {@link true}.
	 *         如果 {@code probability} 大于 {@code base}，也就是为 true 的可能性大于 100%，则会永远为 true。
	 * @throws IllegalArgumentException
	 *         当参数 base 或是 probability 不为正整数时
	 * @since 1.0.0-RC3.2
	 */
	public static boolean probabilityTrue (@Nonnegative int probability, @Nonnegative int base) {
		if (probability < 1) throw new IllegalArgumentException("the probability must be a positive value!");
		if (base < 1) throw new IllegalArgumentException("the probability base must be a positive value!");
		return probability > ThreadLocalRandom.current().nextInt(base);
	}
	
	/**
	 * 以一定几率返回 true.
	 * @return {@code probabilityIn} 分之 {@link 1} 的几率为 {@link true}.
	 * @see #probabilityTrue(int, int)
	 * @since 1.0.0-RC3.2
	 */
	public static boolean probabilityTrue (@Nonnegative int probabilityIn) {
		return (probabilityTrue(1, probabilityIn));
	}
	
	/**
	 * 通过 {@link ThreadLocalRandom} 实现的随机 boolean 取值.
	 * @return 随机的 {@link true} 或 {@link false}，各占(近似)一半可能性.
	 * @see ThreadLocalRandom#nextBoolean()
	 * @since 1.0.0-RC3.2
	 */
	public static boolean iif () {
		return ThreadLocalRandom.current().nextBoolean();
	}
	
}
