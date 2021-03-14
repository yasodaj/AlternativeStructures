import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckDeadStates {
	private static String saveDirectory = "E:\\TestDavidSolutions\\Toy0\\Test_dead\\";
	private static int numberOfNodes = 6;
	private static HashSet<Integer> uniqueNums = new HashSet<Integer>();
	private static LinkedHashMap<String, Integer> layoutStringAlphabet = new LinkedHashMap<String, Integer>();
	
	
	private static String logFilename = saveDirectory + "CheckDeadStatesToy0.txt";
	
	public static void main(String args[]) {
		
		populateLayoutStringAlphabet();
		
		String fitnessFileName = "E:\\TestDavidSolutions\\Toy0\\toy0_SDs2to3_layouts.txt";
		
//		checkDeadStates(fitnessFileName);
		
		printSolution(convertToIntArray("214161449136"));
		
		
	}
	
	
	public static void checkDeadStates(String fitnessFileName) {
		
		//read a solution
		
		try{
	        BufferedReader buf = new BufferedReader(new FileReader(fitnessFileName));
	        ///ArrayList<String> words = new ArrayList<>();
	        String lineJustFetched = null;
//	        String[] wordsArray;
	        String fname;
	        int count = 0;
	        int[] sol;
	        
	        generateRandomBrowserList(500,1,263920);
	        
	        
	        while(true){
	            lineJustFetched = buf.readLine();
	            if(lineJustFetched == null){  
	                break; 
	            }else{
	            	
	            	if(uniqueNums.contains(Integer.valueOf(count))) {
	            		
		            	sol = convertToIntArray(lineJustFetched);
		            	
		                fname = lineJustFetched;
		                computeDeadStates(sol, fname);
		                
	            	} //end of if(uniqueNums.contains(Integer.valueOf(count)))
	            	
	            	count++;
	            }
	        }
	         

	        buf.close();

	    }catch(Exception e){
	        e.printStackTrace();
	    }
	}
	
	public static int[] convertToIntArray(String coordinateString) {
		
//		String[] coordinateArray = coordinateString.split(",");
		String[] coordinateArray = coordinateString.split("");
		int[] solution = new int[numberOfNodes*2];
		
		for(int i = 0; i < coordinateArray.length; i++) {
			
			solution[i] = layoutStringAlphabet.get(coordinateArray[i]);
		}
		
		return solution;
	}
	
	
	public static void computeDeadStates(int[] solution, String candleFileNamePrefix) {
		
		//generate Marcie Coordinate String
		String marcieCoordinateString = generateMarcieCoordinateStringToy0(solution);
		//create marcie file
		String candleFileName = generateMarcieFile(marcieCoordinateString, candleFileNamePrefix);
		//execute marcie file
		generateMarcieDeadStateOutput(candleFileName);
		
	}
	
	public static String generateMarcieCoordinateStringToy0(int[] coordinates) {
		
		String marcieCoordinateString = "bool Positions(CD1 x, CD2 y, Type z, Label w) {\n";
		
		String format1 = "\t(x=%d & y=%d & z=INIT & w=E) | "; //int
		String format2 = "(x=%d & y=%d & z=NORM & w=X) |\n"; //norm 0
		String format3 = "\t(x=%d & y=%d & z=FORK & w=E) |\n"; //fork
		String format4 = "\t(x=%d & y=%d & z=NORM & w=X) | "; //norm 1
		String format5 = "(x=%d & y=%d & z=NORM & w=NX) |\n"; // norm 2
		String format6 = "\t(x=%d & y=%d & z=FINAL & w=T) | "; //final 1
		String format7 = "(x=%d & y=%d & z=FINAL & w=F)\n"; // final 2
		
		for(int i = 0; i < coordinates.length; i+=2) {
			
			switch(i) {
			
			case 0: marcieCoordinateString += String.format(format1, coordinates[i], coordinates[i+1]); break;
			case 2: marcieCoordinateString += String.format(format2, coordinates[i], coordinates[i+1]); break;
			case 4: marcieCoordinateString += String.format(format3, coordinates[i], coordinates[i+1]); break;
			case 6: marcieCoordinateString += String.format(format4, coordinates[i], coordinates[i+1]); break;
			case 8: marcieCoordinateString += String.format(format5, coordinates[i], coordinates[i+1]); break;
			case 10: marcieCoordinateString += String.format(format6, coordinates[i], coordinates[i+1]); break;
			case 12: marcieCoordinateString += String.format(format7, coordinates[i], coordinates[i+1]); break;
			default: System.out.println("Invalid Coordinates");
			}
			
			
		}
		
		marcieCoordinateString += "};\n";
		
		return marcieCoordinateString;
			
		
	}


	public static String generateMarcieFile(String coordinates, String fileNamePrefix) {
    
		//System.out.println(saveDirectory);
		String header = readFile(saveDirectory + "header_toy0.txt");		
		String coordinateString = coordinates;		
		String footer = readFile(saveDirectory + "footer.txt");
		
	    PrintWriter writer = null;
	    	
	    String fileName = saveDirectory + "ValidSolutions\\" + fileNamePrefix + ".candl";
	
	    try {
	    	writer = new PrintWriter(fileName, "UTF-8");
	    	writer.println(header);
	    	writer.println(coordinateString);
	    	writer.println(footer);
	    		
	    } catch (IOException ex) {
	    	//System.out.println("no files in " + header);
	        ex.printStackTrace();
	    	    
	    } finally {
	       try {writer.close();} catch (Exception ex) {/*ignore*/}
	    }
	    
	    return fileName;
	    	
	}
	public static String readFile(String fileName) {
		
		String content = null;
		
		try {
			content = new String(Files.readAllBytes(Paths.get(fileName)));
		}catch(IOException e) {
			e.printStackTrace();
		}
		
		return content;
		
	}
	
	public static void generateMarcieDeadStateOutput(String filePath) {
		
		//System.out.println(filePath);
		String marcieCommand = "marcie --net="+ filePath + " --dead-check";
		
		String line = null;
		
		int startPosition = filePath.lastIndexOf("\\");
		int endtPosition = filePath.indexOf("."); 
		String outputFilePrefix = filePath.substring(startPosition, endtPosition);
		
		String outputFileName = saveDirectory + "OutputFiles\\" + outputFilePrefix + ".txt";
	
	    try {
	        
	        Process p = Runtime.getRuntime().exec(marcieCommand); //exec("marcie --net=E:\\DNA\\toy.candl --detect")
	        
	        BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
	
	        BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
	
	        // read the output from the command
	                   
	        PrintWriter writer = new PrintWriter(outputFileName, "UTF-8");
	        
	        int haveDeadStates = -1;
	        
	        while ((line = stdInput.readLine()) != null) {
	        	writer.println(line);
	        	
	        	if(line.contains("-> no reachable dead states")){	        		
	        		haveDeadStates = 0;
	        		break;
	        		
	        	}else {
	        		haveDeadStates = 1;
	        	}
	        	
	        }
	        
	        while ((line = stdError.readLine()) != null) {
	            writer.println(line);
	        }
	        
	        writer.close();
	        
	        writeToFile(outputFilePrefix.substring(1, outputFilePrefix.length()), haveDeadStates);
	        
	
	    }
	    catch (IOException e) {
	        System.out.println("exception happened - here's what I know: ");
	        e.printStackTrace();
	        System.exit(-1);
	    }
	   
	    
	}
	
	public static void writeToFile(String layout, int haveDeadStates) {
		
		try {
			
			FileWriter fw = new FileWriter(logFilename, true); // the true will append the new data
			
			if(haveDeadStates == 1) {
				
				fw.write(layout + "\t" + "are reachable dead states" + "\n");
				
			}else if (haveDeadStates == 0) {
				
				fw.write(layout + "\t" + "no reachable dead states" + "\n");
			}else {
				
				fw.write(layout + "\t" + "Unknown" + "\n");
			}
			
			fw.close();
		} catch (IOException ioe) {
			System.err.println("IOException: " + ioe.getMessage());
		}
		
	}
	
	public static void generateRandomBrowserList(int size, int lower, int upper) {
		
		while(uniqueNums.size() < size) {
			uniqueNums.add(generateRandomInt(lower, upper));
		}
	}
	
	static public int generateRandomInt(int lowerBound,int upperBound){

		Random rand = null;
		
		int a = Math.min(lowerBound,upperBound);
		int b = Math.max(lowerBound,upperBound);
		
		rand = new Random();
		rand.setSeed(System.nanoTime());
		
		int d = b - a + 1;
		int x = rand.nextInt(d) + a;
		return(x);
	}
	
	public static void populateLayoutStringAlphabet() {
		
		layoutStringAlphabet.put("0", 0);
		layoutStringAlphabet.put("1", 1);
		layoutStringAlphabet.put("2", 2);
		layoutStringAlphabet.put("3", 3);
		layoutStringAlphabet.put("4", 4);
		layoutStringAlphabet.put("5", 5);
		layoutStringAlphabet.put("6", 6);
		layoutStringAlphabet.put("7", 7);
		layoutStringAlphabet.put("8", 8);
		layoutStringAlphabet.put("9", 9);
		layoutStringAlphabet.put("a", 10);
		layoutStringAlphabet.put("b", 11);
		layoutStringAlphabet.put("c", 12);
		layoutStringAlphabet.put("d", 13);
		layoutStringAlphabet.put("e", 14);
		layoutStringAlphabet.put("f", 15);
		layoutStringAlphabet.put("g", 16);
		layoutStringAlphabet.put("h", 17);
		layoutStringAlphabet.put("i", 18);
		layoutStringAlphabet.put("j", 19);
		layoutStringAlphabet.put("k", 20);
		layoutStringAlphabet.put("l", 21);
		layoutStringAlphabet.put("m", 22);
	}
	
	public static void printSolution(int[] solution) {
		
		for(int i = 0; i < solution.length; i++) {
			System.out.print(solution[i]+",");
		}
		
		System.out.println("");
		
	}

}
