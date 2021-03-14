import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.*;


public class HillClimber {
	
	public static String saveDirectoryParent = "E:\\HillClimber\\";
	public static int minGridSize = 0;
	public static int maxGridSizeX = 7;
	public static int maxGridSizeY = 8;
	public static String saveDirectory;
	
	public static void main(String args[]) {
		
		for(int g = 21; g < 51; g++) {
			
			saveDirectory = saveDirectoryParent +"HC_" + g + "\\";
			
			long startTime = System.currentTimeMillis();
			
			int iterations = 100;
			
			//generate initial solution
			StatusValidateSolution svs = new StatusValidateSolution();
			int[] initialSolution ;//{1,1,2,3,1,5,4,3,1,7,5,5};
//			
//			System.out.println("Initial Solution:");
//			printSolution(initialSolution);
			
			do {
				
				initialSolution = generateRandomSolution();
				svs = isValidSolution(initialSolution);
				
			}while(svs.isValidSolution == false);
			
			printSolution(initialSolution);
			
			
			//create marcie file
			String candleFileName = generateMarcieFile(generateMarcieCoordinateString(initialSolution),"Init_Solution");
			
			//execute marcie file and calculate fitness
			double bestFitness = calculateFitness(candleFileName, initialSolution);
			
			//write solution and fitness 
			writeToSolutionsLog(candleFileName,initialSolution,bestFitness);
			
			int[] bestSolution = initialSolution;			
			writeToBestSolutionsLog(candleFileName, bestSolution, bestFitness);
			
			int[] newSolution;
			double newFitness;
			
			ArrayList<int[]> generatedSolutions = new ArrayList<int[]>();
			generatedSolutions.add(initialSolution);
			StatusValidateSolution svsNewSol = new StatusValidateSolution();
			boolean doesExists;
			
			//hill climber
			for(int y = 0; y < iterations; y++) {
				
				int count = 0;
				
				do {
					
					newSolution = smallChange(bestSolution);				
					doesExists = solutionAlreadyExists(generatedSolutions,newSolution);
					
					if(doesExists == true) {
						count++;
					}else {
						svsNewSol = isValidSolution(newSolution);
						if(svsNewSol.isValidSolution == true) {
							generatedSolutions.add(newSolution);
						}else {
							svsNewSol.isValidSolution = false;
						}
						
					}
					
				}while(svsNewSol.isValidSolution == false || count > 6);
				
				if(doesExists == false) {
					
					String fileNamePrefix = "Valid_Solution_Iteration_" + y;
					
					candleFileName = generateMarcieFile(generateMarcieCoordinateString(newSolution),
																fileNamePrefix);
					
					newFitness = calculateFitness(candleFileName, newSolution);
					
					writeToSolutionsLog(candleFileName,newSolution,newFitness);
					
					if(newFitness >= bestFitness) {
						
						bestSolution = newSolution;	
						bestFitness = newFitness;
						writeToBestSolutionsLog(candleFileName, bestSolution, bestFitness);
					}
					
				}

						
			}
			
			long endTime = System.currentTimeMillis();
			
			System.out.println("Iterations "+ iterations + " took " + (endTime - startTime) + " milliseconds");
			
			writeToTimeLog(g, (endTime - startTime));			
			
		}
		
		
	}
	
	public static int[] generateRandomSolution() {
		
		
		int[] solution = new int[12];
		int luckyIndex = 0;
		int luckyIndex2 = 0;
		ArrayList<int[]> usedNodes = new ArrayList<int[]>();
		
		//--------------------------------- INIT ---------------------------------------
		
		//init node
		int[] initNode = generateInitNode();
		solution[0] = initNode[0];
		solution[1] = initNode[1];
		usedNodes.add(initNode);
		
		//System.out.println("Init node");
		//printSolution(initNode);
	
		//--------------------------------- FORK ---------------------------------------
		
		//fork node
		ArrayList<int[]> possibleForkLocations =  generatePossibleLocations(initNode);
		luckyIndex = generateRandomInt(0,possibleForkLocations.size()-1);
		int[] forkNode = possibleForkLocations.get(luckyIndex);
		solution[2] = forkNode[0];
		solution[3] = forkNode[1];
		usedNodes.add(forkNode);
		
//		System.out.println("Fork node");
//		printArrayList(possibleForkLocations);
//		System.out.println("Fork node");
//		printSolution(forkNode);
		
		//--------------------------------- NORM ---------------------------------------
		
		//nodes for norm1 and norm2
		ArrayList<int[]> possibleNormLocations =  generatePossibleLocations(forkNode);
		
		//remove init node - used node
		possibleNormLocations = removeUsedNodes(usedNodes, possibleNormLocations);
		
		//remove nodes closer to init node		
		possibleNormLocations = removeTooCloseNodesToUsedNodes(usedNodes, possibleNormLocations);
				
		if(possibleNormLocations.size()>2) {
		
			luckyIndex = generateRandomInt(0,possibleNormLocations.size()-1);
			
//			System.out.println("Norm node");
//			printArrayList(possibleNormLocations);
		
			
			//--------------------------------- NORM 1 ---------------------------------------
			
			//norm 1 node
			int[] norm1 = possibleNormLocations.get(luckyIndex);
			solution[4] = norm1[0];
			solution[5] = norm1[1];
			usedNodes.add(norm1);
			
//			System.out.println("Norm 1 node");
//			printSolution(norm1);
			
			//--------------------------------- FINAL 1 ---------------------------------------
			
			//final 1 node
			ArrayList<int[]> possibleFinal1Locations =  generatePossibleLocations(norm1);
			
			//remove init node - used node
			possibleFinal1Locations = removeUsedNodes(usedNodes, possibleFinal1Locations);
			
			//remove nodes closer to init node		
			possibleFinal1Locations = removeTooCloseNodesToUsedNodes(usedNodes, possibleFinal1Locations);
			
			if(possibleFinal1Locations.size() > 0) {
				
				luckyIndex = generateRandomInt(0,possibleFinal1Locations.size()-1);
				int[] final1 = possibleFinal1Locations.get(luckyIndex);
				solution[8] = final1[0];
				solution[9] = final1[1];
				usedNodes.add(final1);
			
//				System.out.println("Final 1 node");
//				printArrayList(possibleFinal1Locations);
//				System.out.println("Final 1 node");
//				printSolution(final1);
				
			}
			
			//--------------------------------- NORM 2 ---------------------------------------
			
			//norm 2 node
			
			//remove init node - used node
			possibleNormLocations = removeUsedNodes(usedNodes, possibleNormLocations);
			
			//remove nodes closer to init node		
			possibleNormLocations = removeTooCloseNodesToUsedNodes(usedNodes, possibleNormLocations);
			
			if(possibleNormLocations.size() > 0) {
				
				luckyIndex2 = generateRandomInt(0,possibleNormLocations.size()-1);
				
				int[] norm2 = possibleNormLocations.get(luckyIndex2);
				solution[6] = norm2[0];
				solution[7] = norm2[1];
				usedNodes.add(norm2);
				
//				System.out.println("Norm 2 node");
//				//printArrayList(possibleFinal1Locations);
//				System.out.println("Norm 2 node");
//				printSolution(norm2);
				
				//--------------------------------- FINAL 2 ---------------------------------------
						
				//final 2 node
				ArrayList<int[]> possibleFinal2Locations =  generatePossibleLocations(norm2);
				
				//remove init node - used node
				possibleFinal2Locations = removeUsedNodes(usedNodes, possibleFinal2Locations);
				
				//remove nodes closer to init node		
				possibleFinal2Locations = removeTooCloseNodesToUsedNodes(usedNodes, possibleFinal2Locations);
//				System.out.println(possibleFinal2Locations.size());
				
				if(possibleFinal2Locations.size() > 0) {
					
					luckyIndex = generateRandomInt(0,possibleFinal2Locations.size()-1);
					int[] final2 = possibleFinal2Locations.get(luckyIndex);
					solution[10] = final2[0];
					solution[11] = final2[1];
					usedNodes.add(final2);
					
//					System.out.println("Final 2 node");
//					printArrayList(possibleFinal2Locations);
//					System.out.println("Final 2 node");
//					printSolution(final2);
				}
			}
			
		}
		
		return solution;
		
	}

	public static ArrayList<int[]> removeTooCloseNodesToUsedNodes(ArrayList<int[]> used, ArrayList<int[]> loc){
		
		for(int i =0; i < used.size(); i++) {
			
			for(int j = 0; j < loc.size(); j++) {
				
				if(distanceBetweenPoints(loc.get(j), used.get(i)) < 2) {
					
					loc.remove(j);
				}
				
				
			}
		}	
		
		return loc;
		
		
	}

	public static double distanceBetweenPoints(int[]a, int[]b) {
		
		double distance = 0.0;
		
		distance = Math.sqrt(Math.pow((a[0] - b[0]), 2) + Math.pow((a[1] - b[1]), 2));
		
		return distance;
		
	}
	
	public static ArrayList<int[]> removeUsedNodes(ArrayList<int[]> used, ArrayList<int[]> loc){
		
		for(int i =0; i < used.size(); i++) {
			
			for(int j = 0; j < loc.size(); j++) {
				
				if(isArrayElementEqual(loc.get(j), used.get(i)) == true) {
					loc.remove(j);
				}
			}
		}	
		
		return loc;
		
		
	}
	
	public static void writeToSolutionsLog(String fileNamePrefix, int[] solution, double fitness) {
		
		DecimalFormat df = new DecimalFormat("#.###");
		String status;
		if(fitness == 0.0) {
			status = "Check candle output file - May be transition(s) that can never fire";
		}else {
			status = "OK";
		}
		
		try
		{
		    String logFilename= saveDirectory + "SolutionsLog.txt";
		    FileWriter fw = new FileWriter(logFilename,true); //the true will append the new data
		    fw.write(fileNamePrefix + "\t" + convertArrayToString(solution) + "\t" +
		    		df.format(fitness) + "\t" + status + "\n");
		    fw.close();
		}
		catch(IOException ioe)
		{
		    System.err.println("IOException: " + ioe.getMessage());
		}
	}
	
	public static void writeToTimeLog(int iteration, long time) {
		
		try
		{
		    String logFilename= saveDirectoryParent + "TimeLog.txt";
		    FileWriter fw = new FileWriter(logFilename,true); //the true will append the new data
		    fw.write(iteration + "\t" + time + "\n");
		    fw.close();
		}
		catch(IOException ioe)
		{
		    System.err.println("IOException: " + ioe.getMessage());
		}
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
	
	public static boolean isArrayElementEqual(int[] a, int[] b) {
		
		boolean isEqual = false;
		
		if(a[0] == b[0] && a[1] == b[1]) {
			isEqual = true;
		}
		
		return isEqual;
		
	}

	public static ArrayList<int[]> generatePossibleLocations(int[] location) {
		
		int locX = location[0];
		int locY = location[1];
		
		int index = 0;
		
		int[][] newLocations = new int[8][2];
		
		for(int i = 0; i < 3; i++) {
			
			newLocations[index][1] = locY + 2; //y' is y + 2
			newLocations[index][0] = locX + (1 - i);
			index++;
		}
		
		for(int i = 0; i < 3; i++) {
			
			newLocations[index][1] = locY - 2; //y' is y - 2
			newLocations[index][0] = locX + (1 - i);
			index++;
		}
		
		for(int i = 0; i < 2; i++) {
			
			newLocations[index][1] = locY; //y' is y + 2
			newLocations[index][0] = locX + (2 - 4 *i);
			index++;
		}
		
		ArrayList<int[]> filteredCoordinates = new ArrayList<int[]>();
		
		for(int i=0; i<newLocations.length; i++) {
			
			if(newLocations[i][0] >= 1 && newLocations[i][0] < 7) {
				
				if(newLocations[i][1] >= 1 && newLocations[i][1] < 8) {
					
					filteredCoordinates.add(newLocations[i]);
					
				}
				
			}
			
		}
		
		
		return filteredCoordinates;
		
		
	}
	
	
	public static ArrayList<int[]> generatePossibleInitLocations(int[] location) {
		
		int locX = location[0];
		
		int index = 0;
		
		int[][] newLocations = new int[2][2];
		
		for(int i = 0; i < 2; i++) {
			
			if(i == 0) {
				newLocations[i][0] = locX - 1;
				newLocations[i][1] = 1;
			}else {
				newLocations[i][0] = locX + 1;
				newLocations[i][1] = 1;
			}
			
		}
		
		ArrayList<int[]> filteredCoordinates = new ArrayList<int[]>();
		
		for(int i=0; i<newLocations.length; i++) {
			
			if(newLocations[i][0] >= 1 && newLocations[i][0] < 7) {
					
					filteredCoordinates.add(newLocations[i]);
				
			}
			
		}
		
		
		return filteredCoordinates;
		
		
	}
	
	public static int[] generateInitNode() {
		
		int[] initNodeCoords = new int[2];
		initNodeCoords[0] = generateRandomInt(1,6);
		initNodeCoords[1] = 1;
		
		return initNodeCoords;
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
	public static void printSolution(int[] solution) {
		
		for(int i = 0; i < solution.length; i++) {
			System.out.print(solution[i]+",");
		}
		
		System.out.println("");
		
	}
	
	public static double calculateFitness(String candleFileName, int[] solution) {
		
    	double fitness = 0.0;
		
		double area = calculateArea(solution);
		
		int[] leaks = generateMarcieOutput(candleFileName);
		
		if(leaks == null) {
			
			System.out.println("No marcie output");
		}else {
			//printSolution(leaks);
			fitness = (leaks[0]*100 + leaks[1]*10 + leaks[2]*1) / area;
		}
		
		return fitness;
		
	}
	
	public static double calculateArea(int[] solution) {
		
		double gridArea = 0.0;
		int[] x = new int[6];
		int[] y = new int[6];
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
		
		return gridArea;
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
	
	//generate the marcie string with coordinates
		public static String generateMarcieCoordinateString(int[] coordinates) {
			
			String marcieCoordinateString = "bool Positions(CD1 x, CD2 y, Type z, Label w) {\n";
			
			String format1 = "\t(x=%d & y=%d & z=INIT & w=E) | (x=%d & y=%d & z=FORK & w=E)|\n";
			String format2 = "\t(x=%d & y=%d & z=NORM & w=X) | (x=%d & y=%d & z=NORM & w=NX)|\n";
			String format3 = "\t(x=%d & y=%d & z=FINAL & w=T)| (x=%d & y=%d & z=FINAL & w=F)\n";
			
			
			for(int i = 0; i < coordinates.length; i+=4) {
				
				if(i == 0) {
					marcieCoordinateString += String.format(format1, coordinates[i], coordinates[i+1], coordinates[i+2], coordinates[i+3]);
				}else if(i == 4) {
					marcieCoordinateString += String.format(format2, coordinates[i], coordinates[i+1], coordinates[i+2], coordinates[i+3]);
				}else {
					marcieCoordinateString += String.format(format3, coordinates[i], coordinates[i+1], coordinates[i+2], coordinates[i+3]);
				}
				

			}
			
			marcieCoordinateString += "};\n";
			
			return marcieCoordinateString;
			
			
			
		}
		
		public static String generateMarcieFile(String coordinates, String fileNamePrefix) {
		    
			String header = readFile(saveDirectory + "header.txt");		
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
				
			}else if(checkNeighbourRelationshipInit(solution) == false){
				status.isValidSolution = false;
				status.errorMessage = "INIT - short distances != 1";
				
			}else if(checkNeighbourRelationshipFork(solution) == false){
				status.isValidSolution = false;
				status.errorMessage = "FORK - short distances != 3";
							
			}else if(checkNeighbourRelationshipNormOne(solution) == false){
				status.isValidSolution = false;
				status.errorMessage = "NORM One - short distances != 2/3";
				
			}else if(checkNeighbourRelationshipNormTwo(solution) == false){
				status.isValidSolution = false;
				status.errorMessage = "NORM Two - short distances != 2/3";
			}else if(checkNeighbourRelationshipFinalOne(solution) == false) {
				status.isValidSolution = false;
				status.errorMessage = "Final One - short distances != 1";
			}else if(checkNeighbourRelationshipFinalTwo(solution) == false) {
				status.isValidSolution = false;
				status.errorMessage = "Final Two - short distances != 1";			
			}else {
				status.isValidSolution = true;
				status.errorMessage = "Correct Solution";
			}
			
			return status;
			
		}
		
		public static boolean checkInitNode(int[] solution) {
			
			boolean coorectInitNode = true;
			
			if(solution[1] != 1) {
				coorectInitNode = false;			
			}
			
			return coorectInitNode;
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
		
		public static boolean checkTooCloseNodes(int[] solution) {
			
			boolean isTooClose = false;
			
			double pointDistance = 0.0;
			
			int[][] coordinatePairs = convertToCoordinatePairs(solution);
			
			for(int i=0; i < coordinatePairs.length; i++) {
				
				for(int j = i+1; j < coordinatePairs.length; j++) {
					
					pointDistance = Math.sqrt(Math.pow((coordinatePairs[j][0] - coordinatePairs[i][0]), 2) +
							Math.pow((coordinatePairs[j][1] - coordinatePairs[i][1]), 2));
					
					if(pointDistance < 2.0) {
						isTooClose = true;
					}
					
				}
				
			}
			
			return isTooClose;
			
		}
		
		public static boolean checkNeighbourRelationshipInit(int[] solution) {
			
			boolean hasRequiredShortDistances = false;
			
			int[][] coordinatePairs = convertToCoordinatePairs(solution);
			
			int[] INIT = coordinatePairs[0];
			
			int numberOfShortDistances = 0;
			
			int shortDistanceManhatten = 0;
			int shortDistanceChebyshev = 0;
			
			for(int i=0; i < coordinatePairs.length; i++) {
				
				if(i !=0) {
					
					shortDistanceManhatten = Math.abs(INIT[0] - coordinatePairs[i][0]) + 
							Math.abs(INIT[1] - coordinatePairs[i][1]);
			
					shortDistanceChebyshev = Math.max(Math.abs(INIT[0] - coordinatePairs[i][0]), 
							Math.abs(INIT[1] - coordinatePairs[i][1]));
					
					if(shortDistanceManhatten <= 3 || shortDistanceChebyshev < 3) {
						numberOfShortDistances += 1;
					}
					
				}
				
				
			}
			
			if(numberOfShortDistances != 1) {			
				hasRequiredShortDistances = false;
			}else {
				hasRequiredShortDistances = true;
			}
			
			return hasRequiredShortDistances;
		}
		
		public static boolean checkNeighbourRelationshipFork(int[] solution) {
			
			boolean hasRequiredShortDistances = false;
			
			int[][] coordinatePairs = convertToCoordinatePairs(solution);
			
			int[] FORK = coordinatePairs[1];
			
			int numberOfShortDistances = 0;
			
			int shortDistanceManhatten = 0;
			int shortDistanceChebyshev = 0;
			
			for(int i=0; i < coordinatePairs.length; i++) {
				
				if(i != 1) {
					
					shortDistanceManhatten = Math.abs(FORK[0] - coordinatePairs[i][0]) + 
							Math.abs(FORK[1] - coordinatePairs[i][1]);
			
					shortDistanceChebyshev = Math.max(Math.abs(FORK[0] - coordinatePairs[i][0]), 
							Math.abs(FORK[1] - coordinatePairs[i][1]));
					
					if(shortDistanceManhatten <= 3 || shortDistanceChebyshev < 3) {
						numberOfShortDistances += 1;
					}
			
				}
				
				
			}
			
			if(numberOfShortDistances != 3) {			
				hasRequiredShortDistances = false;
			}else {
				hasRequiredShortDistances = true;
			}
			
			return hasRequiredShortDistances;
		}
		
		public static boolean checkNeighbourRelationshipFinalTwo(int[] solution) {
			
			boolean hasRequiredShortDistances = false;
			
			int[][] coordinatePairs = convertToCoordinatePairs(solution);
			
			int[] FinalTwo = coordinatePairs[5];
			
			int numberOfShortDistances = 0;
			
			int shortDistanceManhatten = 0;
			int shortDistanceChebyshev = 0;
			
			for(int i=0; i < coordinatePairs.length; i++) {
				
				if(i != 5) {
					
					shortDistanceManhatten = Math.abs(FinalTwo[0] - coordinatePairs[i][0]) + 
							Math.abs(FinalTwo[1] - coordinatePairs[i][1]);
			
					shortDistanceChebyshev = Math.max(Math.abs(FinalTwo[0] - coordinatePairs[i][0]), 
							Math.abs(FinalTwo[1] - coordinatePairs[i][1]));
					
					if(shortDistanceManhatten <= 3 || shortDistanceChebyshev < 3) {
						numberOfShortDistances += 1;
			}
					
				}
				
				
			}
			
			if(numberOfShortDistances != 1) {			
				hasRequiredShortDistances = false;
			}else {
				hasRequiredShortDistances = true;
			}
			
			return hasRequiredShortDistances;
		}
		
		public static boolean checkNeighbourRelationshipFinalOne(int[] solution) {
			
			boolean hasRequiredShortDistances = false;
			
			int[][] coordinatePairs = convertToCoordinatePairs(solution);
			
			int[] FinalOne = coordinatePairs[4];
			
			int numberOfShortDistances = 0;
			
			int shortDistanceManhatten = 0;
			int shortDistanceChebyshev = 0;
			
			for(int i=0; i < coordinatePairs.length; i++) {
				
				if(i != 4) {
					
					shortDistanceManhatten = Math.abs(FinalOne[0] - coordinatePairs[i][0]) + 
							Math.abs(FinalOne[1] - coordinatePairs[i][1]);
			
					shortDistanceChebyshev = Math.max(Math.abs(FinalOne[0] - coordinatePairs[i][0]), 
							Math.abs(FinalOne[1] - coordinatePairs[i][1]));
					
					if(shortDistanceManhatten <= 3 || shortDistanceChebyshev < 3) {
						numberOfShortDistances += 1;
					}
					
				}
				
				
			}
			
			if(numberOfShortDistances != 1) {			
				hasRequiredShortDistances = false;
			}else {
				hasRequiredShortDistances = true;
			}
			
			return hasRequiredShortDistances;
		}
		
		public static boolean checkNeighbourRelationshipNormTwo(int[] solution) {
			
			boolean hasRequiredShortDistances = false;
			
			int[][] coordinatePairs = convertToCoordinatePairs(solution);
			
			int[] NORMTwo = coordinatePairs[3]; //4th nodes pair is for norm 2 
			
			int numberOfShortDistances = 0;
			
			int shortDistanceManhatten = 0;
			int shortDistanceChebyshev = 0;
			
			for(int i=0; i < coordinatePairs.length; i++) {
				
				if(i != 3) {
					
					shortDistanceManhatten = Math.abs(NORMTwo[0] - coordinatePairs[i][0]) + 
							Math.abs(NORMTwo[1] - coordinatePairs[i][1]);
			
					shortDistanceChebyshev = Math.max(Math.abs(NORMTwo[0] - coordinatePairs[i][0]), 
							Math.abs(NORMTwo[1] - coordinatePairs[i][1]));
					
					if(shortDistanceManhatten <= 3 || shortDistanceChebyshev < 3) {
							numberOfShortDistances += 1;
			}
					
				}
				
				
			}
			if(numberOfShortDistances == 2 || numberOfShortDistances == 3) {			
				hasRequiredShortDistances = true;
			}else {
				hasRequiredShortDistances = false;
			}
			
			return hasRequiredShortDistances;
		}

		
		
		public static boolean checkNeighbourRelationshipNormOne(int[] solution) {
			
			boolean hasRequiredShortDistances = false;
			
			int[][] coordinatePairs = convertToCoordinatePairs(solution);
			
			int[] NORMOne = coordinatePairs[2];
			
			int numberOfShortDistances = 0;
			
			int shortDistanceManhatten = 0;
			int shortDistanceChebyshev = 0;
			
			for(int i=0; i < coordinatePairs.length; i++) {
				
				if(i != 2) {
					
					shortDistanceManhatten = Math.abs(NORMOne[0] - coordinatePairs[i][0]) + 
									Math.abs(NORMOne[1] - coordinatePairs[i][1]);
					
					shortDistanceChebyshev = Math.max(Math.abs(NORMOne[0] - coordinatePairs[i][0]), 
							Math.abs(NORMOne[1] - coordinatePairs[i][1]));
					
					if(shortDistanceManhatten <= 3 || shortDistanceChebyshev < 3) {
						numberOfShortDistances += 1;
					}
				}
				
				
			}
			
		
			if(numberOfShortDistances == 2 || numberOfShortDistances == 3) {			
				hasRequiredShortDistances = true;
			}else {
				hasRequiredShortDistances = false;
			}
			
			return hasRequiredShortDistances;
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
		
		public static int[] smallChange(int[] solution) {
			
			//clone the solution
			int[] solutionClone = solution.clone();
			
			//convert the solution into coordinate pairs
			int[][] coordinatePairs = convertToCoordinatePairs(solutionClone);
			
			//create a random arrangement of indexes
			ArrayList<Integer> availableIndexes = generateRandomAllocation(0, coordinatePairs.length - 1);
			
			int luckyIndex;
			ArrayList<int[]> newLocations = new ArrayList<int[]>();
			StatusValidateSolution svs = new StatusValidateSolution();
			
			boolean finished = false;
			
			
			for(int i = 0; i < availableIndexes.size() && !finished; i++) {
				
				luckyIndex = availableIndexes.get(i);
				//System.out.println(luckyIndex);
				//generate the possible locations
				if(luckyIndex == 0) {
					newLocations = generatePossibleInitLocations(coordinatePairs[luckyIndex]);
				}else {
					newLocations = generatePossibleLocations(coordinatePairs[luckyIndex]);
				}
				
				//printArrayList(newLocations);
				
				//iterate through the locations
				for(int y = 0; y < newLocations.size(); y++) {
					
					int[] luckyPair = newLocations.get(y);
					
					coordinatePairs[luckyIndex][0] = luckyPair[0];
					coordinatePairs[luckyIndex][1] = luckyPair[1];
					
					solutionClone = convertToLinearArray(coordinatePairs); 
					
					//printSolution(solutionClone);
					
					svs = isValidSolution(solutionClone);
					//System.out.print(" - valid:" + svs.isValidSolution);
					//System.out.println("");
					
					if(svs.isValidSolution == true) {
						finished = true;
						break;
					}else {
						//coordinatePairs = convertToCoordinatePairs(solution.clone());
					}
					
					
									
				}
				//revert back changes
				coordinatePairs = convertToCoordinatePairs(solution.clone());
				
			}
			
			return solutionClone;
			
		}
		
		public static ArrayList<Integer> addIndexes(int size){
			
			ArrayList<Integer> list = new ArrayList<Integer>();
			
			for(int i = 0; i < size; i++) {
				list.add(i);
			}
			
			return list;
			
		}
		
		public static int[] convertToLinearArray(int[][] sol) {
			
			int[] linearArray = new int[sol.length*2];
			int y = 0;
			for(int i = 0; i < sol.length; i++) {
				
				linearArray[y] = sol[i][0];
				linearArray[y+1] = sol[i][1];
				y+=2;
			}
			
			return linearArray;
		}
		
		public static void writeToBestSolutionsLog(String fileNamePrefix, int[] solution, double fitness) {
			
			DecimalFormat df = new DecimalFormat("#.###");
			String status;
			if(fitness == 0.0) {
				status = "Check candle output file - May be transition(s) that can never fire";
			}else {
				status = "OK";
			}
			
			try
			{
			    String logFilename= saveDirectory + "BestSolutionsLog.txt";
			    FileWriter fw = new FileWriter(logFilename,true); //the true will append the new data
			    fw.write(fileNamePrefix + "\t" + convertArrayToString(solution) + "\t" +
			    		df.format(fitness) + "\t" + status + "\n");
			    fw.close();
			}
			catch(IOException ioe)
			{
			    System.err.println("IOException: " + ioe.getMessage());
			}
		}
		
		public static ArrayList<Integer> generateRandomAllocation(int start, int end){
			
			ArrayList<Integer> list = new ArrayList<Integer>();
			int randomInt;
			
			do {
				
				randomInt = generateRandomInt(start, end);
				
				if(!list.contains(randomInt)) {
					list.add(randomInt);
				}
				
			}while(list.size() != end+1);
			
			return list;
			
		}
		
		public static void printArrayList(ArrayList<int[]> a) {
			
			for(int i = 0; i < a.size(); i++) {
				
				printSolution(a.get(i));
				
			}
			
			System.out.println("");
		}
		
		public static boolean solutionAlreadyExists(ArrayList<int[]> solutions, int[] newSolution) {
			
			boolean doesExists = false;
			
			for(int i = 0; i < solutions.size(); i++) {
				
				doesExists = Arrays.equals(solutions.get(i), newSolution);
				
				if(doesExists == true) {
					break;
				}
			}
			
			return doesExists;
			
		}

}

//
//try {
//
//    File f = new File("E:\\HillClimber\\AllSolutions.txt");
//
//    BufferedReader b = new BufferedReader(new FileReader(f));
//
//    String readLine = "";
//
//    while ((readLine = b.readLine()) != null) {
//    	String s = readLine;
//    	String[] strArray = s.split(",");
//    	int[] initialSolution = new int[strArray.length];
//    	for(int i = 0; i < strArray.length; i++) {
//    		initialSolution[i] = Integer.parseInt(strArray[i]);
//    	}
//    	
//		StatusValidateSolution svs = new StatusValidateSolution();
//		
//		svs = isValidSolution(initialSolution);
//		
//		System.out.println(svs.isValidSolution);
//
//    }
//
//} catch (IOException e) {
//    e.printStackTrace();
//}
