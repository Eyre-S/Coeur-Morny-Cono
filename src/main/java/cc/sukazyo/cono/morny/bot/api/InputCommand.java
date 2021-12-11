package cc.sukazyo.cono.morny.bot.api;

import cc.sukazyo.cono.morny.util.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;

public class InputCommand {
	
	private final String target;
	private final String command;
	private final String[] args;
	
	private InputCommand (@Nullable String target, @Nonnull String command, @Nonnull String[] args) {
		this.target = target;
		this.command = command;
		this.args = args;
	}
	
	public InputCommand (@Nonnull String[] inputArray) {
		this(parseInputArray(inputArray));
	}
	
	public InputCommand (@Nonnull String input) {
		this(StringUtils.formatCommand(input));
	}
	
	public InputCommand (@Nonnull InputCommand source) {
		this(source.target, source.command, source.args);
	}
	
	public static InputCommand parseInputArray (@Nonnull String[] inputArray) {
		final String[] cx = inputArray[0].split("@", 2);
		final String[] args = new String[inputArray.length-1];
		System.arraycopy(inputArray, 1, args, 0, inputArray.length - 1);
		return new InputCommand(cx.length == 1 ? null : cx[1], cx[0], args);
	}
	
	@Nullable
	public String getTarget () {
		return target;
	}
	
	@Nonnull
	public String getCommand () {
		return command;
	}
	
	@Nonnull
	public String[] getArgs () {
		return args;
	}
	
	public boolean hasArgs () {
		return args.length != 0;
	}
	
	@Override
	@Nonnull
	public String toString() {
		return String.format("{{%s}@{%s}#{%s}}", command, target, Arrays.toString(args));
	}
	
}
