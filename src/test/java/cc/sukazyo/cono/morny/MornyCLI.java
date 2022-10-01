package cc.sukazyo.cono.morny;

import cc.sukazyo.untitled.util.command.CommonCommand;

import java.util.*;

public class MornyCLI {
	
	public static void main (String[] args) {
		
		Scanner line = new Scanner(System.in);
		System.out.print("$ java -jar morny-coeur-"+GradleProjectConfigures.VERSION+".jar" );
		String x = line.nextLine();
		ServerMain.main(CommonCommand.format(x));
		
	}
	
}
