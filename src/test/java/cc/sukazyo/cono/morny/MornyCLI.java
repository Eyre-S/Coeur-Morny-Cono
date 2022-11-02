package cc.sukazyo.cono.morny;

import cc.sukazyo.cono.morny.util.UniversalCommand;

import java.util.*;

public class MornyCLI {
	
	public static void main (String[] args) {
		
		Scanner line = new Scanner(System.in);
		System.out.print("$ java -jar morny-coeur-"+GradleProjectConfigures.VERSION+".jar " );
		String x = line.nextLine();
		ServerMain.main(UniversalCommand.format(x));
		
	}
	
}
