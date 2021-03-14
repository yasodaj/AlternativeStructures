import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestDavidSolutionsToy0 {
	
	private static String saveDirectory = "E:\\TestDavidSolutions\\Toy0\\TheRest\\";
	private static HashMap<String, Double> fitnessMap = new HashMap<String, Double>();	
	private static HashMap<String, Double> fitnessMapNew = new HashMap<String, Double>();
	private static LinkedHashMap<int[], String> nodeConnectionIdentifier = new LinkedHashMap<int[], String>();
	private static LinkedHashMap<Integer, String> nodeIdentifier = new LinkedHashMap<Integer, String>();
	private static HashSet<Integer> uniqueNums = new HashSet<Integer>();
	private static int numberOfNodes = 7;	
	public static int minGridSize = 0;
	public static int maxGridSizeX = 9;
	public static int maxGridSizeY = 9;
	
	public static void main(String args[]) {
		
		populateNodeIdentifierToy0();
		populateNodeConnectionIdentifierToy0();
		
		DavidSolutionsTest_Toy0();
		
		
	
    
		
	}
	
	
	
	public static void DavidSolutionsTest_Toy0() {
		
		String fitnessFileName = "E:\\TestDavidSolutions\\Toy0\\theRest.txt";
		
		//read a solution
		
		try{
	        BufferedReader buf = new BufferedReader(new FileReader(fitnessFileName));
	        ///ArrayList<String> words = new ArrayList<>();
	        String lineJustFetched = null;
//	        String[] wordsArray;
	        String fname;
	        int count = 0;
	        boolean isValid;
	        int[] sol;
	        StatusValidateSolution svs = new StatusValidateSolution();
	        
//	        generateRandomBrowserList(500,1,208492);
	        
	        
	        while(true){
	            lineJustFetched = buf.readLine();
	            if(lineJustFetched == null){  
	                break; 
	            }else{
	            	
//	            	if(uniqueNums.contains(Integer.valueOf(count))) {
	            		
//		                wordsArray = lineJustFetched.split("\t");
//		                int[] sol = convertToIntArray(wordsArray[0]);
		            	
		            	sol = convertToIntArray(lineJustFetched);
		            	
		                fname = lineJustFetched;
		                calculateFitnessMaster(sol, fname);
//		                count ++;
		                
//		                svs = isValidSolution(sol);
//		                isValid = svs.isValidSolution;
//		                
//		                writeToFile(sol, isValid);
	            		
//	            	} //end of if(uniqueNums.contains(Integer.valueOf(count)))
	            	
//	            	count++;
	            }
	        }
	         

	        buf.close();

	    }catch(Exception e){
	        e.printStackTrace();
	    }
	}
	
	public static StatusValidateSolution isValidSolution(int[] solution) {
		
		StatusValidateSolution status = new StatusValidateSolution();
		
		//check
		if(checkInitNode(solution) == false) {
			status.isValidSolution = false;
			status.errorMessage = "Incorrect INIT node";
			
		}else if(checkOverLappingNodes(solution) == true) {
			status.isValidSolution = false;
			status.errorMessage = "Overlapping nodes";
			
		}else if(checkTooCloseNodes(solution) == true) {
			status.isValidSolution = false;
			status.errorMessage = "Too close neighbours";
			
		}else if(checkConnectedNeighbourShortDistance(solution) == true) {
			status.isValidSolution = false;
			status.errorMessage = "Neighbour distance is longer than short";
		
		}else if(checkRequiredShortDistanceCount(solution) == false){
			status.isValidSolution = false;
			status.errorMessage = "Doesn't have the required no of short distances";
			
		}else {
			status.isValidSolution = true;
			status.errorMessage = "Correct Solution";
		}
		
		return status;
		
	}
	
	public static boolean checkOverLappingNodes(int[] solution) {
		
		boolean overLappingNodesExists = false;
		
		int[][] coordinatePairs = convertToCoordinatePairs(solution);
		
		for(int i =0; i < coordinatePairs.length; i++) {
			
			for(int j = i+1; j < coordinatePairs.length; j++) {
				
				if((coordinatePairs[i][0]==coordinatePairs[j][0]) && (coordinatePairs[i][1]==coordinatePairs[j][1])) {
					
					overLappingNodesExists = true;
					//System.out.println("Ovelapping nodes");
					
				}
			}
			
			
		}
		
		return overLappingNodesExists;

	}
	
	public static boolean checkInitNode(int[] solution) {
	
		boolean coorectInitNode = true;
		
		if(solution[1] != 1) {
			coorectInitNode = false;			
		}
		
		return coorectInitNode;
	}
	
	public static boolean checkTooCloseNodes(int[] solution) {
		
		boolean isTooClose = false;
		
		double pointDistance = 0.0;
		
		boolean found = false;
		int[][] coordinatePairs = convertToCoordinatePairs(solution);
		
		for(int i=0; i < coordinatePairs.length && !found; i++) {
			
			for(int j = i+1; j < coordinatePairs.length; j++) {
				
				pointDistance = Math.sqrt(Math.pow((coordinatePairs[j][0] - coordinatePairs[i][0]), 2) +
						Math.pow((coordinatePairs[j][1] - coordinatePairs[i][1]), 2));
				
				if(pointDistance < 2.0) {
					isTooClose = true;
					found = true;
					break;
				}
				
			}
			
		}
		
		return isTooClose;
		
	}
	
	public static boolean checkConnectedNeighbourShortDistance(int[] solution) {
		
		int[][] coordinatePairs = convertToCoordinatePairs(solution);
					
		boolean notShort = false;
		double pointDistance = 0.0;
		int[] currentConnection;
		int x,y;
		
		Iterator<Entry <int[], String>> it = nodeConnectionIdentifier.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<int[], String> pair = (Map.Entry<int[], String>)it.next();
	        currentConnection = pair.getKey();
	        
	        x = currentConnection[0];
	        y = currentConnection[1];
	       	        
	        pointDistance = Math.sqrt(Math.pow((coordinatePairs[x][0] - coordinatePairs[y][0]), 2) +
					Math.pow((coordinatePairs[x][1] - coordinatePairs[y][1]), 2));
	        
			if(pointDistance > 3.0) {				
				return notShort = true;
				
			}
	        
	        
	    }

		return notShort;
		
	}
	
	public static boolean checkRequiredShortDistanceCount(int[] solution) {
		
		boolean hasRequiredShortDistances = false;			
		int[][] coordinatePairs = convertToCoordinatePairs(solution);
		int neighbourCount = 0;
		double pointDistance = 0.0;
		String nodeType;
		boolean breakNow = false;
		
		for(int i = 0; i < coordinatePairs.length && !breakNow; i++) {
			
			for(int j = 0; j < coordinatePairs.length; j++) {
				
				if(j != i) {
					
					pointDistance = Math.sqrt(Math.pow((coordinatePairs[i][0] - coordinatePairs[j][0]), 2) +
							Math.pow((coordinatePairs[i][1] - coordinatePairs[j][1]), 2));
					
					if(pointDistance <= 3.0) {
						
						neighbourCount++;

					}
				}
				
			}
			
			nodeType = nodeIdentifier.get(i);
			
			switch(nodeType) {
				case "Init": 
					if(neighbourCount == 1) {
						hasRequiredShortDistances = true; 
						break;
					} else {
						hasRequiredShortDistances = false;
						System.out.println("No required short distances in Init node");
						breakNow = true;
						break;
					}
				case "Fork": 
					if(neighbourCount == 3) {
						hasRequiredShortDistances = true; 
						break;
					} else {
						hasRequiredShortDistances = false;
						System.out.println("No required short distances in Fork node");
						breakNow = true;
						break;
					}
					
				case "Norm0":
					if(neighbourCount == 2) {
						hasRequiredShortDistances = true; 
						break;
					} else {
						hasRequiredShortDistances = false;
//						System.out.println("No required short distances in Norm0 " + i + " node");
						breakNow = true;
						break;
					}
					
				case "Norm": 
					if(neighbourCount == 2 || neighbourCount == 3) {
						hasRequiredShortDistances = true; 
						break;
					} else {
						hasRequiredShortDistances = false;
						System.out.println("No required short distances in Norm " + i + " node");
						breakNow = true;
						break;
					}
					
				case "Final": 
					if(neighbourCount == 1) {
						hasRequiredShortDistances = true; 
						break;
					} else {
						hasRequiredShortDistances = false;
						System.out.println("No required short distances in Final " + i + " node");
						breakNow = true;
						break;
					}
					
				default: hasRequiredShortDistances = false; break;
			};
			
//			System.out.println("i " + i + " node type " + nodeType);
//			System.out.println("neighbourCount " + neighbourCount);
//			System.out.println("hasRequiredShortDistances " + hasRequiredShortDistances);
//			System.out.println();
			neighbourCount = 0;
			
		}
		
		
		return hasRequiredShortDistances;
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
	
	public static double calculateFitnessMaster(int[] solution, String candleFileNamePrefix) {
		
		double fitness = 0.0;
		
		//check whether solution exists in the hashmap
		if(fitnessMap.containsKey(convertSolutionToString(solution)) == true) {
			
			fitness = fitnessMap.get(convertSolutionToString(solution));
			writeToSolutionsLog("From HashMap",solution,-1,-1,-1,-1,fitness);
			
		} else if (fitnessMapNew.containsKey(convertSolutionToString(solution)) == true) {
			
			fitness = fitnessMapNew.get(convertSolutionToString(solution));
			writeToSolutionsLog("From HashMapNew",solution,-2,-2,-2,-2,fitness);
			
		}else {
			
			//generate Marcie Coordinate String
			String marcieCoordinateString = generateMarcieCoordinateStringToy0(solution);
			//create marcie file
			String candleFileName = generateMarcieFile(marcieCoordinateString, candleFileNamePrefix);
			//execute marcie file and calculate fitness
			fitness = calculateFitness(candleFileName, solution);
			
			fitnessMapNew.put(convertSolutionToString(solution), fitness);
			
		}
		

		
		return fitness;
	}
	
	public static double calculateArea(int[] solution) {
		
		double gridArea = 0.0;
		int[] x = new int[numberOfNodes];
		int[] y = new int[numberOfNodes];
		int index = 0;
		int minX, maxX, minY, maxY;
		
		for(int i = 0; i < solution.length; i+=2) {
			
			x[index] = solution[i];
			y[index] = solution[i+1];
			index++;
		}
		
		
		Arrays.sort(x);
		Arrays.sort(y);
		
		minX = x[0];
		maxX = x[x.length - 1];
		minY = y[0];
		maxY = y[y.length - 1];
				
		gridArea = (maxX - minX) * (maxY - minY);
		
//		System.out.println("minX:"+minX);
//		System.out.println("maxX:"+maxX);
//		System.out.println("maxY:"+maxY);
//		System.out.println("minY:"+minY);
//		System.out.println("area:"+gridArea);
		return gridArea;
	}
	
	public static double calculateFitness(String candleFileName, int[] solution) {
		
    	double fitness = 0.0;
		
		double area = calculateArea(solution);
		
		int[] leaks = generateMarcieOutput(candleFileName);
		
		if(leaks == null) {
			
			System.out.println("No marcie output");
		}else {
			//printSolution(leaks);
//			fitness = (leaks[0]*100 + leaks[1]*10 + leaks[2]*1) / area;
			fitness = (leaks[0]*100 + leaks[1]*10 + leaks[2]*1) * area;
		}
		
		//write solution and fitness 
		writeToSolutionsLog(candleFileName,solution,leaks[0],leaks[1],leaks[2],area,fitness);
				
		
		return fitness;
		
	}
	
	public static void writeToSolutionsLog(String fileNamePrefix, int[] solution, int shortL, int mediumL, int longL,
			double area, double fitness) {

		DecimalFormat df = new DecimalFormat("#.##");
		String status;
		if (fitness == 0.0) {
			status = "Check candle output file - May be transition(s) that can never fire";
		} else {
			status = "OK";
		}

		try {
			String logFilename = saveDirectory + "SolutionsLog.txt";
			FileWriter fw = new FileWriter(logFilename, true); // the true will append the new data
			fw.write(fileNamePrefix + "\t" + convertArrayToString(solution) + "\t" + String.valueOf(shortL) + "\t"
					+ String.valueOf(mediumL) + "\t" + String.valueOf(longL) + "\t" + df.format(area) + "\t"
					+ df.format(fitness) + "\t" + status + "\n");
			fw.close();
		} catch (IOException ioe) {
			System.err.println("IOException: " + ioe.getMessage());
		}
	}
	
	public static int[] generateMarcieOutput(String filePath) {
		
		int[] leaks = new int[3];
		
		//System.out.println(filePath);
		String marcieCommand = "marcie --net="+ filePath + " --detect";
		
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
	        
	        while ((line = stdInput.readLine()) != null) {
	        	writer.println(line);
	        	
	        	if(line.contains("leak:")){
	        		
	        		Pattern pattern = Pattern.compile("leak:(?:\\s+short\\((\\d+)\\))?(?:\\s+medium\\((\\d+)\\))?(?:\\s+long\\((\\d+)\\))?");
	        		Matcher matcher = pattern.matcher(line);
	        		while(matcher.find()) {
	        			
	        			if(matcher.group(1) != null) {
	        				leaks[0] = Integer.parseInt(matcher.group(1));
	        			}else {
	        				leaks[0] = 0;
	        			}
	        			
	        			if(matcher.group(2) != null) {
	        				leaks[1] = Integer.parseInt(matcher.group(2));
	        			}else {
	        				leaks[1] = 0;
	        			}
	        			
	        			if(matcher.group(3) != null) {
	        				leaks[2] = Integer.parseInt(matcher.group(3));
	        			}else {
	        				leaks[2] = 0;
	        			}
	        		
	        		
	        		}
	        		
	        		
	        	}
	        	
	        }
	        
	        while ((line = stdError.readLine()) != null) {
	            writer.println(line);
	        }
	        
	        writer.close();
	        
	
	    }
	    catch (IOException e) {
	        System.out.println("exception happened - here's what I know: ");
	        e.printStackTrace();
	        System.exit(-1);
	    }
	    
	    return leaks;
	}
	
	public static String convertSolutionToString(int[] solution) {
		
		String sol = "";
		
		for(int i = 0; i < solution.length; i++) {
			
			if(i != solution.length - 1) {
				sol = sol + String.valueOf(solution[i]) + ",";
			}else {
				sol = sol + String.valueOf(solution[i]);
			}
		}
		
		return sol;
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
	
	public static String convertArrayToString(int[] array) {
		
		String stringArray = "[";
		
		for(int i = 0; i < array.length; i++) {
			
			if(i != array.length - 1) {
				stringArray += Integer.toString(array[i]);
				stringArray += ",";
			}else {
				stringArray += Integer.toString(array[i]);
			}
			
		}
		
		stringArray += "]";
		return stringArray;
		
	}
	
	public static int[][] convertToCoordinatePairs(int[] solution){
		
		int[][] coordinatePairs = new int[solution.length/2][2];
		
		int arrayIndex = 0;
		
		for(int j = 0; j < solution.length; j+=2) {

				coordinatePairs[arrayIndex][0] = solution[j];
				coordinatePairs[arrayIndex][1]=solution[j+1];
				
				arrayIndex ++;
			
		}
		
		return coordinatePairs;
	}
	
	public static void populateNodeConnectionIdentifierToy0() {
		
		nodeConnectionIdentifier.put(new int[] {0,1},"Init-Norm0");
		nodeConnectionIdentifier.put(new int[] {1,2},"Norm0-Fork");
		nodeConnectionIdentifier.put(new int[] {2,3},"Fork-Norm1");
		nodeConnectionIdentifier.put(new int[] {2,4},"Fork-Norm2");
		nodeConnectionIdentifier.put(new int[] {3,5},"Norm1-Final1");
		nodeConnectionIdentifier.put(new int[] {4,6},"Norm2-Final2");
		
	}
	
	public static void populateNodeIdentifierToy0() {
		
		nodeIdentifier.put(Integer.valueOf(0),"Init");
		nodeIdentifier.put(Integer.valueOf(1),"Norm0");
		nodeIdentifier.put(Integer.valueOf(2),"Fork");
		nodeIdentifier.put(Integer.valueOf(3),"Norm");
		nodeIdentifier.put(Integer.valueOf(4),"Norm");
		nodeIdentifier.put(Integer.valueOf(5),"Final");
		nodeIdentifier.put(Integer.valueOf(6),"Final");
	}
	
	public static int[] convertToIntArray(String coordinateString) {
		
//		String[] coordinateArray = coordinateString.split(",");
		String[] coordinateArray = coordinateString.split("");
		int[] solution = new int[numberOfNodes*2];
		
		for(int i = 0; i < coordinateArray.length; i++) {
			
			solution[i] = Integer.valueOf(coordinateArray[i]);
		}
		
		return solution;
	}
	
	public static void writeToFile(int[] sol, boolean isValid) {
		
		try {
			String logFilename = saveDirectory + "TestMyValidFunction.txt";
			FileWriter fw = new FileWriter(logFilename, true); // the true will append the new data
			fw.write(convertArrayToString(sol) + "\t" + String.valueOf(isValid) + "\n");
			fw.close();
		} catch (IOException ioe) {
			System.err.println("IOException: " + ioe.getMessage());
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
	
	public static void generateRandomBrowserList(int size, int lower, int upper) {
		
		while(uniqueNums.size() < size) {
			uniqueNums.add(generateRandomInt(lower, upper));
		}
	}

}
