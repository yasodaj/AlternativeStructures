import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestDavidSolutions {
	
	public static String saveDirectoryParent = "E:\\TestDavidSolutions\\NewSolutions_toy_revised\\";
	public static int minGridSize = 0;
	public static int maxGridSizeX = 7;
	public static int maxGridSizeY = 8;
	public static String saveDirectory = "E:\\TestDavidSolutions\\Toy0\\";
	
	
	public static void main(String args[]) {
		
		String fitnessFileName = "E:\\TestDavidSolutions\\Toy0\\toy0_layouts_revised.txt";
		
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
	        //System.out.println("file read");
	        while(true){
	            lineJustFetched = buf.readLine();
	            if(lineJustFetched == null){  
	                break; 
	            }else{
	            	
//	                wordsArray = lineJustFetched.split("\t");
//	                int[] sol = convertToIntArray(wordsArray[0]);
	            	
	            	sol = convertToIntArray(lineJustFetched);
	            	
	                fname = "Solution_"+count;
	                calculateFitnessMaster(sol, fname);
	                count ++;
	                
	                svs = isValidSolution(sol);
	                isValid = svs.isValidSolution;
	                
	                writeToFile(sol, isValid);
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
		int[] solution = new int[12];
		
		for(int i = 0; i < coordinateArray.length; i++) {
			
			solution[i] = Integer.valueOf(coordinateArray[i]);
		}
		
		return solution;
	}
	
	
	
	public static ArrayList<int[]> removeAlreadyUsedCoordinates(int[][] coordinatePairs, ArrayList<int[]> newLocations){
		
		for(int i = 0; i < coordinatePairs.length; i++) {
			
			for(int j = 0; j < newLocations.size(); j++) {
				
				if(Arrays.equals(coordinatePairs[i], newLocations.get(j))) {
					
					newLocations.remove(j);
					
				}
			}
			
		}
		
		return newLocations;
		
		
		
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
	
//	public static void writeToSolutionsLog(String fileNamePrefix, int[] solution, double fitness) {
//		
//		DecimalFormat df = new DecimalFormat("#.###");
//		String status;
//		if(fitness == 0.0) {
//			status = "Check candle output file - May be transition(s) that can never fire";
//		}else {
//			status = "OK";
//		}
//		
//		try
//		{
//		    String logFilename= saveDirectory + "SolutionsLog.txt";
//		    FileWriter fw = new FileWriter(logFilename,true); //the true will append the new data
//		    fw.write(fileNamePrefix + "\t" + convertArrayToString(solution) + "\t" +
//		    		df.format(fitness) + "\t" + status + "\n");
//		    fw.close();
//		}
//		catch(IOException ioe)
//		{
//		    System.err.println("IOException: " + ioe.getMessage());
//		}
//	}
	
	
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
//			fitness = (leaks[0]*100 + leaks[1]*10 + leaks[2]*1) / area;
			fitness = (leaks[0]*100 + leaks[1]*10 + leaks[2]*1) * area;
		}
		
		//write solution and fitness 
		writeToSolutionsLog(candleFileName,solution,leaks[0],leaks[1],leaks[2],area,fitness);
				
		
		return fitness;
		
	}
	
	public static double calculateFitnessMaster(int[] solution, String candleFileNamePrefix) {
		
		double fitness = 0.0;
		
		//generate Marcie Coordinate String
		String marcieCoordinateString = generateMarcieCoordinateString(solution);
		//create marcie file
		String candleFileName = generateMarcieFile(marcieCoordinateString, candleFileNamePrefix);
		//execute marcie file and calculate fitness
		fitness = calculateFitness(candleFileName, solution);
		
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
		String marcieCommand = "marcie --detect --net="+ filePath;
		
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
		    
			//System.out.println(saveDirectory);
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
		
		public static StatusValidateSolution isValidSolution(int[] solution) {
			
			StatusValidateSolution status = new StatusValidateSolution();
			
			//check
//			if(checkInitNode(solution) == false) {
//				status.isValidSolution = false;
//				status.errorMessage = "Incorrect INIT node";
//				
//			}else 
				
			if(checkOverLappingNodes(solution) == true) {
				status.isValidSolution = false;
				status.errorMessage = "Overlapping nodes";
				
			}else if(checkTooCloseNodes(solution) == true) {
				status.isValidSolution = false;
				status.errorMessage = "Too close neighbours";
				
			}else if(checkConnectedNeighbourShortDistance(solution) == true) {
				status.isValidSolution = false;
				status.errorMessage = "Neighbour distance is longer than short";
			
			}else if(checkNeighbourRelationshipInitFork(solution) == false){
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
		
		public static boolean checkConnectedNeighbourShortDistance(int[] solution) {
			
			int[][] coordinatePairs = convertToCoordinatePairs(solution);
						
			boolean notShort = false;
			double pointDistance = 0.0;

			
			//init and fork
			pointDistance = Math.sqrt(Math.pow((coordinatePairs[0][0] - coordinatePairs[1][0]), 2) +
					Math.pow((coordinatePairs[0][1] - coordinatePairs[1][1]), 2));
			
			if(pointDistance > 3.0) {				
				return notShort = true;
				
			}
			
			//fork and norm one
			pointDistance = Math.sqrt(Math.pow((coordinatePairs[1][0] - coordinatePairs[2][0]), 2) +
					Math.pow((coordinatePairs[1][1] - coordinatePairs[2][1]), 2));
			
			if(pointDistance > 3.0) {				
				return notShort = true;
				
			}
			
			//fork and norm two
			pointDistance = Math.sqrt(Math.pow((coordinatePairs[1][0] - coordinatePairs[3][0]), 2) +
					Math.pow((coordinatePairs[1][1] - coordinatePairs[3][1]), 2));
			
			if(pointDistance > 3.0) {				
				return notShort = true;
				
			}
			
			//final one and norm one
			pointDistance = Math.sqrt(Math.pow((coordinatePairs[2][0] - coordinatePairs[4][0]), 2) +
					Math.pow((coordinatePairs[2][1] - coordinatePairs[4][1]), 2));
			
			if(pointDistance > 3.0) {				
				return notShort = true;
				
			}
			
			//final two and norm two
			pointDistance = Math.sqrt(Math.pow((coordinatePairs[3][0] - coordinatePairs[5][0]), 2) +
					Math.pow((coordinatePairs[3][1] - coordinatePairs[5][1]), 2));
			
			if(pointDistance > 3.0) {				
				return notShort = true;
				
			}
			
			return notShort;
			
		}
		
		public static boolean checkNeighbourRelationshipInitFork(int[] solution) {
			
			boolean hasRequiredShortDistances = false;			
			int[][] coordinatePairs = convertToCoordinatePairs(solution);
			
			int[] INIT = coordinatePairs[0];
			int neighbourCount = 0;
//			int neightbourID = 0;
			double pointDistance = 0.0;
			
			//checks no of nodes within a radius of 3.0
			for(int i = 1; i < coordinatePairs.length; i++) {
				
				pointDistance = Math.sqrt(Math.pow((coordinatePairs[i][0] - INIT[0]), 2) +
						Math.pow((coordinatePairs[i][1] - INIT[1]), 2));
				
				if(pointDistance <= 3.0) {
					
					neighbourCount++;
//					neightbourID = i;
				}
			}
			
//			System.out.println("count " + neighbourCount);
//			System.out.println("ID " + neightbourID);
			
			//checks whether it's only one node and it's the fork node
//			if(neighbourCount == 1 && neightbourID == 1) {
			if(neighbourCount == 1) {
				hasRequiredShortDistances = true;
			}else {
				hasRequiredShortDistances = false;
			}
			
			return hasRequiredShortDistances;
		}
		
		
public static boolean checkNeighbourRelationshipFork(int[] solution) {
			
			boolean hasRequiredShortDistances = false;
			int[][] coordinatePairs = convertToCoordinatePairs(solution);
			
			int[] FORK = coordinatePairs[1];
//			int[] neighbours = {0,2,3};
			int neighbourCount = 0;
//			ArrayList<Integer> neightbourID = new ArrayList<Integer>();
			double pointDistance = 0.0;
			
			//checks no of nodes within a radius of 3.0
			for(int i = 0; i < coordinatePairs.length; i++) {
				
				if(i != 1) {
					
					pointDistance = Math.sqrt(Math.pow((coordinatePairs[i][0] - FORK[0]), 2) +
							Math.pow((coordinatePairs[i][1] - FORK[1]), 2));
					
					if(pointDistance <= 3.0) {
						
						neighbourCount++;
//						neightbourID.add(Integer.valueOf(i));
					}
					
				}
			}
			
//			System.out.println("count " + neighbourCount);
//			System.out.println("ID " + allNeighboursExists(neightbourID,neighbours));
			
			//checks whether it's only one node and it's the fork node
//			if(neighbourCount == 3 && allNeighboursExists(neightbourID,neighbours) == true) {
			if(neighbourCount == 3) {
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

//			int[] neighbours = {1,4};
			int neighbourCount = 0;
//			ArrayList<Integer> neightbourID = new ArrayList<Integer>();
			double pointDistance = 0.0;
			
			//checks no of nodes within a radius of 3.0
			for(int i = 0; i < coordinatePairs.length; i++) {
				
				if(i != 2) {
					
					pointDistance = Math.sqrt(Math.pow((coordinatePairs[i][0] - NORMOne[0]), 2) +
							Math.pow((coordinatePairs[i][1] - NORMOne[1]), 2));
					
					if(pointDistance <= 3.0) {
						
						neighbourCount++;
//						neightbourID.add(Integer.valueOf(i));
					}
					
				}
			}
//			printArrayInteger(neightbourID);
//			System.out.println("count " + neighbourCount);
//			System.out.println("ID " + allNeighboursExistsNorm(neightbourID,neighbours));
			
			//checks whether it's only one node and it's the fork node
//			if((neighbourCount == 3 || neighbourCount == 2) && allNeighboursExistsNorm(neightbourID,neighbours) == true) {
			if(neighbourCount == 3 || neighbourCount == 2){
				hasRequiredShortDistances = true;
			}else {
				hasRequiredShortDistances = false;
			}
			
			return hasRequiredShortDistances;
		}

		public static boolean checkNeighbourRelationshipNormTwo(int[] solution) {
			
			boolean hasRequiredShortDistances = false;
			
			int[][] coordinatePairs = convertToCoordinatePairs(solution);
			
			int[] NORMTwo = coordinatePairs[3]; //4th nodes pair is for norm 2 
			
//			int[] neighbours = {1,5};
			int neighbourCount = 0;
//			ArrayList<Integer> neightbourID = new ArrayList<Integer>();
			double pointDistance = 0.0;
			
			//checks no of nodes within a radius of 3.0
			for(int i = 0; i < coordinatePairs.length; i++) {
				
				if(i != 3) {
					
					pointDistance = Math.sqrt(Math.pow((coordinatePairs[i][0] - NORMTwo[0]), 2) +
							Math.pow((coordinatePairs[i][1] - NORMTwo[1]), 2));
					
					if(pointDistance <= 3.0) {
						
						neighbourCount++;
//						neightbourID.add(Integer.valueOf(i));
					}
					
				}
			}
//			printArrayInteger(neightbourID);
//			System.out.println("count " + neighbourCount);
//			System.out.println("ID " + allNeighboursExistsNorm(neightbourID,neighbours));
			
			//checks whether it's only one node and it's the fork node
//			if((neighbourCount == 3 || neighbourCount == 2) && allNeighboursExistsNorm(neightbourID,neighbours) == true) {
			if(neighbourCount == 3 || neighbourCount == 2) {
				hasRequiredShortDistances = true;
			}else {
				hasRequiredShortDistances = false;
			}
			return hasRequiredShortDistances;
		}
		
		
		public static boolean checkNeighbourRelationshipFinalOne(int[] solution) {
			
			boolean hasRequiredShortDistances = false;
			
			int[][] coordinatePairs = convertToCoordinatePairs(solution);
			
			int[] FinalOne = coordinatePairs[4];
			//int neighbourID = 0;
			int neighbourCount = 0;
			
			double pointDistance = 0.0;
			
			//checks no of nodes within a radius of 3.0
			for(int i = 0; i < coordinatePairs.length; i++) {
				
				if(i != 4) {
					
					pointDistance = Math.sqrt(Math.pow((coordinatePairs[i][0] - FinalOne[0]), 2) +
							Math.pow((coordinatePairs[i][1] - FinalOne[1]), 2));
					
					if(pointDistance <= 3.0) {
						
						neighbourCount++;
				//		neighbourID = i;
					}
					
				}
			}
			
//			System.out.println("count " + neighbourCount);
//			System.out.println("ID " + neighbourID);
			
			//checks whether it's only one node and it's the fork node
//			if(neighbourCount == 1 && neighbourID == 2) {
			if(neighbourCount == 1) {
				hasRequiredShortDistances = true;
			}else {
				hasRequiredShortDistances = false;
			}

			return hasRequiredShortDistances;
		}
		
		public static boolean checkNeighbourRelationshipFinalTwo(int[] solution) {
			
			boolean hasRequiredShortDistances = false;
			
			int[][] coordinatePairs = convertToCoordinatePairs(solution);
			
			int[] FinalTwo = coordinatePairs[5];
//			int neighbourID = 0;
			int neighbourCount = 0;
			
			double pointDistance = 0.0;
			
			//checks no of nodes within a radius of 3.0
			for(int i = 0; i < coordinatePairs.length; i++) {
				
				if(i != 5) {
					
					pointDistance = Math.sqrt(Math.pow((coordinatePairs[i][0] - FinalTwo[0]), 2) +
							Math.pow((coordinatePairs[i][1] - FinalTwo[1]), 2));
					
					if(pointDistance <= 3.0) {
						
						neighbourCount++;
//						neighbourID = i;
					}
					
				}
			}
			
//			System.out.println("count " + neighbourCount);
//			System.out.println("ID " + neighbourID);
			
			//checks whether it's only one node and it's the fork node
//			if(neighbourCount == 1 && neighbourID == 3) {
				if(neighbourCount == 1) {
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
