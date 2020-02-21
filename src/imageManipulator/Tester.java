package imageManipulator;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Tester {

	public static void main(String[] args) throws IOException {
		
		Scanner sc = new Scanner(System.in);
		
		while(true) {
			
			System.out.println("What would you like to do?\n	a. Hide a message\n	b. Recover a message\n	c. Exit");
			System.out.print("Enter your selection: ");
			
			String input = sc.next();

			if (input.compareTo("a")==0) {
				
				System.out.print("Please specify the source PPM filename: ");
				String source = sc.next();
				System.out.print("Please specify the output PPM filename: ");
				String output = sc.next();
				
				System.out.println();
				sc.nextLine();
				
				System.out.print("Please enter a phrase to hide: ");
				String phrase = sc.nextLine();
				
				PPMImage ppmImg = new PPMImage(new File(source));
				ppmImg.hideData(phrase);
				ppmImg.writeImage(new File(output));
				
				System.out.println("Your message \""+phrase+"\" has been hidden in file: "+output);
				
				System.out.println();
				continue;
				
			} else if (input.compareTo("b")==0) {
				
				System.out.print("Please specify the source PPM filename: ");
				String source = sc.next();
				
				PPMImage ppmImg = new PPMImage(new File(source));
				System.out.println("The following message has been recovered from file "+source+": "+ppmImg.recoverData());
				
				System.out.println();
				continue;
				
			} else if (input.compareTo("c")==0) {		
				System.out.println("Execution terminated!");
				sc.close();
				System.exit(0);
			}
		}
		
	}
}
