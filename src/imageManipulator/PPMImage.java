package imageManipulator;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class PPMImage {
	
	String magicNumber;
	int width, height, maxColorValue;
	char[][][] raster;

	public PPMImage(File image) { 
		readImage(image);
	}
	
	public void writeImage(File filename) throws IOException {
		
		if (!filename.getName().endsWith(".ppm")) { //if not ppm throw exception
			throw new IOException("Incorrect file type!");
		}
		
		try {
			OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(filename));
			char[] magicNumberChars = magicNumber.toCharArray();  //get chars from magicnumber
			for (int i = 0; i < magicNumberChars.length; i++) {
				outputStream.write((int)magicNumberChars[i]);  //convert into int to write into file
			}
			
			outputStream.write(10); //10 = linefeed
			
			char[] widthChars = (""+width).toCharArray(); //getting char form width
			for (int i = 0; i < widthChars.length; i++) {
				outputStream.write((int)widthChars[i]); //convert into int and write into file
			}
			
			outputStream.write((int)' '); //space
			
			char[] heightChars = (""+height).toCharArray(); //getting height
			for (int i = 0; i < heightChars.length; i++) {
				outputStream.write((int)heightChars[i]); // convert into int
			}
			
			outputStream.write(10);
			
			char[] maxColorValueChars = (""+maxColorValue).toCharArray(); // getting max color value
			for (int i = 0; i < maxColorValueChars.length; i++) {
				outputStream.write((int)maxColorValueChars[i]);
			}
			
			outputStream.write(10);
			
			for (int a = 0; a < height; a++) {
				for (int b = 0; b < width; b++) {
					for (int c = 0; c < 3; c++) {
						outputStream.write((int)raster[a][b][c]);
					}
				}
			}
			
			outputStream.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void readImage(File image) {
	
		 try {
			 
			InputStream inputStream = new BufferedInputStream(new FileInputStream(image));
			
			magicNumber = (char)inputStream.read() + "" + (char)inputStream.read(); //getting the magic number
			
			if (!magicNumber.contentEquals("P6")) { //confirm is it ppm file
				inputStream.close();
				throw new IOException("Wrong file type!");
			} else {
				inputStream.read(); //get rid of line feed
				char buffer;
				String widthString = "";
				do {
					buffer = (char)inputStream.read(); //getting width
					widthString += buffer;
				} while (buffer!=' '); // stop at the space
		
				width = Integer.parseInt(widthString.substring(0, widthString.length()-1)); //including space so len -1 
				
				String heightString = "";
				do {
					buffer = (char)inputStream.read();  // getting height
					heightString += buffer;
				} while (buffer!='\n');
				
				height = Integer.parseInt(heightString.substring(0, heightString.length()-1)); //getting height
				
				String maxColorValueString = "";
				do {
					buffer = (char)inputStream.read(); //getting maxcolorvalue
					maxColorValueString += buffer;
				} while (buffer!='\n');
				
				maxColorValue = Integer.parseInt(maxColorValueString.substring(0, maxColorValueString.length()-1));
				
				raster = new char[height][width][3];
		
				//getting all the raster info
				
				for (int a = 0; a < height; a++) { 
					for (int b = 0; b < width; b++) {
						for (int c = 0; c < 3; c++) {
							raster[a][b][c] = (char)inputStream.read();
						}
					}
				}
				
				inputStream.close();
				
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void hideData(String message) {
		
		message = message +'\0'; //append \0 to know where to stop
		
		char[] charMessageArray = message.toCharArray();
		
		int row = 0; int col = 0; int channel = -1;
		
		for (int i = 0; i < charMessageArray.length; i++) {
			
			String binaryStringToEncode = Integer.toBinaryString(charMessageArray[i]); //return binary into string value
			while (binaryStringToEncode.length() < 8) {
				binaryStringToEncode = "0"+binaryStringToEncode; //make it to 8bits 
			}
			
			for (int a = 0; a < binaryStringToEncode.length(); a++) {
				channel++; //rgb location
				if (channel > 2 && col < width) {
					col++; channel = 0;
				} else if (channel > 2 && col == width) {
					channel = 0; col = 0; row++;
				}
								
				char charToManipulate = raster[row][col][channel];
				
				char statusMask = 1<<0;
				//if it is same continue
				if (((charToManipulate&statusMask)==0 && binaryStringToEncode.charAt(a)=='0')||((charToManipulate&statusMask)==1 && binaryStringToEncode.charAt(a)=='1')) { 
					continue;
				} else {
					if ((charToManipulate&statusMask)==0) {  //if zero to 1
						char mask = 1<<0;
						charToManipulate = (char) (charToManipulate|mask);
						raster[row][col][channel] = charToManipulate;
					} else {
						char mask = (char) ~(1<<0); //if 1 then to zero
						charToManipulate = (char) (charToManipulate&mask);
						raster[row][col][channel] = charToManipulate;
					}
				}

			}
			
		}
		
	}
	
	public String recoverData() {
		
		String message = ""; //the result
		String tempMessage = ""; //contain bit to char
		
		int counter = 0;
		
		for (int a = 0; a < height; a++) {
			for (int b = 0; b < width; b++) {
				for (int c = 0; c < 3; c++) {
					if (counter == 8) { //if has 8 bit convert to char
						char decodedChar = (char)Integer.parseInt(tempMessage, 2);
						if ((int)decodedChar==0) {
							return message;
						} else { //keep adding into the message 
							message = message + decodedChar;
						}
						counter = 0; tempMessage = "";
					}
					char manipulatedChar = raster[a][b][c];
					char mask = 1<<0;
					
					if ((manipulatedChar&mask)==0) { //create tmpmessage with 8 bits
						tempMessage = tempMessage + '0';
					} else {
						tempMessage = tempMessage + '1';
					}
					counter++;
				}
			}
		}
		return null;
	}
	
}
