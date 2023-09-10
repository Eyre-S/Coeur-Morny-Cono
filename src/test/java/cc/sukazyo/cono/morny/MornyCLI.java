package cc.sukazyo.cono.morny;

import cc.sukazyo.cono.morny.util.UniversalCommand;

import java.util.*;

public class MornyCLI {
	
	public static void main (String[] args) {
		
		System.out.print("$ java -jar morny-coeur-"+MornySystem.VERSION_FULL()+".jar " );
		String x;
		try (Scanner line = new Scanner(System.in)) { x = line.nextLine(); }
		ServerMain.main(UniversalCommand.format(x));
		
	}
	
}
