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


public class SimulatedAnnealingNewValidTroubleshoot {
	
	public static String saveDirectoryParent = "E:\\SA\\TestSmallChange\\";
	public static int minGridSize = 0;
	public static int maxGridSizeX = 7;
	public static int maxGridSizeY = 8;
	public static String saveDirectory;
	
	private static HashMap<String, Double> fitnessMap = new HashMap<String, Double>();
	
	private static HashMap<String, Double> fitnessMapNew = new HashMap<String, Double>();
	
	//Fitness
	private static double fit = 0.0;
	//Number of iterations
	private static double iter = 30000;
	//Convergence point
	private static double cp = -1;
	//x - solution - xnew new solution
	private static int[] initialSolution, newSolution;
	
	public static void main(String args[]) throws Exception {
		
		
		
		int[] testSol = {6,1,5,5,6,3,3,3,4,7,1,3};//generateRandomSolution();1,1,2,5,2,3,4,3,3,7,5,1
//		System.out.println(checkNeighbourRelationshipNormOne(testSol));
//		
////		printSolution(testSol);
		StatusValidateSolution s = new StatusValidateSolution();
		s = isValidSolution(testSol);
		System.out.println(s.isValidSolution);
		System.out.println(s.errorMessage);
//		
//		int[] testSol = {6,1,4,2,2,4,5,4,1,7,3,7};
//		System.out.println(checkNeighbourRelationshipFork(testSol));
//		int[] b = {1,1};
//		ArrayList<int[]> a = generatePossibleInitLocations(b);
//		printArrayList(a);
//		System.out.println(a.size());
		
	}
	

	
	public static void runSA(int iteration) throws Exception {

		saveDirectory = saveDirectoryParent + "Run" + iteration + "\\";
		
		long startTime = System.currentTimeMillis();
		//Create a random starting point			
		initialSolution = generateRandomSolution();
		
		//elitef is the best fitness found so far, elitex is the best solution for elitef
		double elitef = ((Double.MAX_VALUE)/2.0);
		int[] elitex = initialSolution;		
		
		
		//Final cooling temperature
		double titer = 0.001; 
		//Compute starting temperature
		double t0 = ComputeTZero();
		//Compute cooling rate
		double lamda = ComputeLamda(t0,titer);
		double T = t0;
		double fdash;
		cp = -1;
		
		String fileNamePrefix;
		for(int i=0;i<iter*0.99;++i)
		{
			
			fileNamePrefix = "SA_Init_"+ i;
			fit = calculateFitnessMaster(initialSolution, fileNamePrefix);
			
			
			newSolution = smallChangeMaster(initialSolution);
			fileNamePrefix = "SA_NewSol_"+ i;
			fdash = calculateFitnessMaster(newSolution, fileNamePrefix);;
			if (fdash < fit) //maximization problem
			{
				fit = fdash;
				cp = i;
				initialSolution = newSolution;
			}
			else
			{
				//Compute pr(accept)
				double df =  Math.abs(fit-fdash);
				double p = Math.exp(-df/T);
				if (Math.random() < p)
				{
					//Accept worse
					fit = fdash;
					initialSolution = newSolution;
					cp = i;
				}
				else
				{
					//Keep previous
					//System.out.println("UDS");
					//UnDoSwap();
				}
			}
			T *= lamda;
			if (fit < elitef)
			{
				elitef = fit;
				elitex = initialSolution;
			}
			if (i % 10 == 0) 
			{
				System.out.println(i + " " + fit + " " + elitef);
			}
			
			writeToBestSolutionsLog(i, elitef, elitex);
		}
		
		fit = elitef;
		initialSolution = elitex;
		
		if(fitnessMapNew.size() != 0) {
			writeNewHashMapToFile();
		}
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(iteration + " took " + (endTime - startTime) + " milliseconds");
		
	}
	
	
	
	public static void fitnessStore(String fileName) {
		try{
	        BufferedReader buf = new BufferedReader(new FileReader(fileName));
	        ///ArrayList<String> words = new ArrayList<>();
	        String lineJustFetched = null;
	        String[] wordsArray;
	        //System.out.println("file read");
	        while(true){
	            lineJustFetched = buf.readLine();
	            if(lineJustFetched == null){  
	                break; 
	            }else{
	            	
	                wordsArray = lineJustFetched.split("\t");
	                fitnessMap.put(wordsArray[0], Double.parseDouble(wordsArray[1]));
	                   
	            }
	        }
	        

	        buf.close();

	    }catch(Exception e){
	        e.printStackTrace();
	    }
	
	}
	
	public static int[] convertToIntArray(String coordinateString) {
		
		String[] coordinateArray = coordinateString.split(",");
//		String[] coordinateArray = coordinateString.split("");
		int[] solution = new int[12];
		
		for(int i = 0; i < coordinateArray.length; i++) {
			
			solution[i] = Integer.valueOf(coordinateArray[i]);
		}
		
		return solution;
	}
	
	private static double ComputeLamda(double t0, double titer) 
	{
		double lamda = (Math.log(titer) - Math.log(t0)) / (iter*0.95);
		lamda = Math.exp(lamda);
		return(lamda);
	}
	
	private static double ComputeTZero() throws Exception 
	{
		double t0 = 0.0;
		double ctziter = 0.01*iter;
		String fileNamePrefix;
		for(int i=0;i<ctziter;++i)
		{
			initialSolution = generateRandomSolution();
			fileNamePrefix = "ComputeTZero_Init_"+ i;
			double f = calculateFitnessMaster(initialSolution, fileNamePrefix);
			//if (i % 10000 == 0) System.out.println("+++"+i);
			newSolution = smallChangeMaster(initialSolution);
			fileNamePrefix = "ComputeTZero_NewSol_"+i;
			double fdash = calculateFitnessMaster(newSolution, fileNamePrefix);
			double df = Math.abs(f-fdash);
			t0 += df;
			f = fdash;
		}
		t0 /= ctziter;
		return(t0);
	}
	
	public static int[] smallChangeMaster(int[] solution) {
		
		int[] newSolution = new int[12];
		StatusValidateSolution svsNewSol = new StatusValidateSolution();
		
		int selection;
		
		do {
			
			selection = generateRandomInt(1,3);
			
			if(selection == 1) {
//				System.out.println("Selection " + selection);
				newSolution = smallChange(solution,-1);
				svsNewSol = isValidSolution(newSolution);
				
			}else if(selection == 2) {
//				System.out.println("Selection " + selection);
				newSolution = smallChangeOneFixed(solution,-1);
				svsNewSol = isValidSolution(newSolution);
			}else {
//				System.out.println("Selection " + selection);
				newSolution = smallChangeTwoFixed(solution,-1);
				svsNewSol = isValidSolution(newSolution);
			}
			
			
		}while(svsNewSol.isValidSolution == false);
		
		return newSolution;
	}
	
	public static int[] smallChangeOneFixed(int[] solution, int skip) {
		
		//clone the solution
		int[] solutionClone = solution.clone();
		
		//convert the solution into coordinate pairs
		int[][] coordinatePairs = convertToCoordinatePairs(solutionClone);
		
		//create a random arrangement of indexes
		ArrayList<Integer> availableIndexesForFixedPosition = generateRandomAllocation(0, coordinatePairs.length - 1);
		
		if(skip != -1) {
			availableIndexesForFixedPosition.remove(Integer.valueOf(skip));
		}
		
		//variable declaration
		int luckyIndexFixed;
		ArrayList<int[]> newLocationsFixedNode = new ArrayList<int[]>();
		StatusValidateSolution svs = new StatusValidateSolution();
		boolean finished = false; //for loop breaker
		int[] luckyPair;
		
		
		//first for loop - go through each combination of fixed position
		for(int f = 0; f < availableIndexesForFixedPosition.size()  && !finished; f++) {
			//current index
			luckyIndexFixed = availableIndexesForFixedPosition.get(f);
//			System.out.println("fixed index " + luckyIndexFixed);
			
			//changed a fixed location 
			if(luckyIndexFixed == 0) {
				newLocationsFixedNode = generatePossibleInitLocations(coordinatePairs[luckyIndexFixed]);
			}else {
				newLocationsFixedNode = generatePossibleLocations(coordinatePairs[luckyIndexFixed]);
			}
			
			newLocationsFixedNode = removeAlreadyUsedCoordinates(coordinatePairs,newLocationsFixedNode);
			
//			System.out.println("f:" +f + " - luckyIndexFixed:"+ luckyIndexFixed);
//			printArrayList(newLocationsFixedNode);
			
			//create a fixed position
			for(int g = 0; g < newLocationsFixedNode.size(); g++) {
				
				//create a fixed position
				luckyPair = newLocationsFixedNode.get(g);
				coordinatePairs[luckyIndexFixed][0] = luckyPair[0];
				coordinatePairs[luckyIndexFixed][1] = luckyPair[1];
				
				//to revert back changes keep a copy
				int[] solutionCloneFixedOnePositionBK = convertToLinearArray(coordinatePairs);
//				System.out.println("Fixed solution " + g);
//				printSolution(solutionCloneFixedOnePositionBK);
				
				solutionClone = smallChange(solutionCloneFixedOnePositionBK, luckyIndexFixed);
				svs = isValidSolution(solutionClone);
				
				if(svs.isValidSolution == true) {
					
					finished = true;
//					System.out.println("Is valid " + svs.isValidSolution);
//					printSolution(solutionClone);
					break;
				}
				
				
				//revert back changes
				coordinatePairs = convertToCoordinatePairs(solution.clone());
//				System.out.println("");
			}
			
		}
					
		return solutionClone;
		
	}
	
	public static int[] smallChangeTwoFixed(int[] solution, int skip) {
		
		// clone the solution
		int[] solutionClone = solution.clone();

		// convert the solution into coordinate pairs
		int[][] coordinatePairs = convertToCoordinatePairs(solutionClone);
		
		//create a random arrangement of indexes
		ArrayList<Integer> availableIndexesForFixedPositionL1 = generateRandomAllocation(0, coordinatePairs.length - 1);
		
		if(skip != -1) {
			availableIndexesForFixedPositionL1.remove(Integer.valueOf(skip));
		}
		
		//variable declaration
		int luckyIndexFixedL1;
		ArrayList<int[]> newLocationsFixedNodeL1 = new ArrayList<int[]>();
		StatusValidateSolution svs = new StatusValidateSolution();
		boolean finished = false; // for loop breaker
		int[] luckyPair;
		
		//first for loop - level 1 - go through each combination of fixed position
		for (int f = 0; f < availableIndexesForFixedPositionL1.size() && !finished; f++) {
			// current index
			luckyIndexFixedL1 = availableIndexesForFixedPositionL1.get(f);
//			System.out.println(luckyIndexFixedL1);
			
			//changed a fixed location 
			if(luckyIndexFixedL1 == 0) {
				newLocationsFixedNodeL1 = generatePossibleInitLocations(coordinatePairs[luckyIndexFixedL1]);
			}else {
				newLocationsFixedNodeL1 = generatePossibleLocations(coordinatePairs[luckyIndexFixedL1]);
			}
			
			newLocationsFixedNodeL1 = removeAlreadyUsedCoordinates(coordinatePairs,newLocationsFixedNodeL1);
			
			for(int g = 0; g < newLocationsFixedNodeL1.size(); g++) {
				
				//create a fixed position
				luckyPair = newLocationsFixedNodeL1.get(g);
				coordinatePairs[luckyIndexFixedL1][0] = luckyPair[0];
				coordinatePairs[luckyIndexFixedL1][1] = luckyPair[1];
				
				//to revert back changes keep a copy
				int[] solutionCloneFixedOnePositionBK = convertToLinearArray(coordinatePairs);
//				System.out.println("Fixed solution L1 " + g);
//				printSolution(solutionCloneFixedOnePositionBK);
				
				solutionClone = smallChangeOneFixed(solutionCloneFixedOnePositionBK, luckyIndexFixedL1);
				svs = isValidSolution(solutionClone);
				
				if(svs.isValidSolution == true) {
					
					finished = true;
//					System.out.println("Is valid " + svs.isValidSolution);
//					printSolution(solutionClone);
					break;
				}
				
				
				//revert back changes
				coordinatePairs = convertToCoordinatePairs(solution.clone());
//				System.out.println("");
			}
		}
		
		return solutionClone;
		
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
	
	public static int[] generateRandomSolution() {
		
		
		int[] solution = new int[12];
		StatusValidateSolution svs = new StatusValidateSolution();
		
		do {
			
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
			
//			System.out.println("Fork node");
//			printArrayList(possibleForkLocations);
//			System.out.println("Fork node");
//			printSolution(forkNode);
			
			//--------------------------------- NORM ---------------------------------------
			
			//nodes for norm1 and norm2
			ArrayList<int[]> possibleNormLocations =  generatePossibleLocations(forkNode);
			
			//remove init node - used node
			possibleNormLocations = removeUsedNodes(usedNodes, possibleNormLocations);
			
			//remove nodes closer to init node		
			possibleNormLocations = removeTooCloseNodesToUsedNodes(usedNodes, possibleNormLocations);
					
			if(possibleNormLocations.size()>2) {
			
				luckyIndex = generateRandomInt(0,possibleNormLocations.size()-1);
				
//				System.out.println("Norm node");
//				printArrayList(possibleNormLocations);
			
				
				//--------------------------------- NORM 1 ---------------------------------------
				
				//norm 1 node
				int[] norm1 = possibleNormLocations.get(luckyIndex);
				solution[4] = norm1[0];
				solution[5] = norm1[1];
				usedNodes.add(norm1);
				
//				System.out.println("Norm 1 node");
//				printSolution(norm1);
				
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
				
//					System.out.println("Final 1 node");
//					printArrayList(possibleFinal1Locations);
//					System.out.println("Final 1 node");
//					printSolution(final1);
					
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
					
//					System.out.println("Norm 2 node");
//					//printArrayList(possibleFinal1Locations);
//					System.out.println("Norm 2 node");
//					printSolution(norm2);
					
					//--------------------------------- FINAL 2 ---------------------------------------
							
					//final 2 node
					ArrayList<int[]> possibleFinal2Locations =  generatePossibleLocations(norm2);
					
					//remove init node - used node
					possibleFinal2Locations = removeUsedNodes(usedNodes, possibleFinal2Locations);
					
					//remove nodes closer to init node		
					possibleFinal2Locations = removeTooCloseNodesToUsedNodes(usedNodes, possibleFinal2Locations);
//					System.out.println(possibleFinal2Locations.size());
					
					if(possibleFinal2Locations.size() > 0) {
						
						luckyIndex = generateRandomInt(0,possibleFinal2Locations.size()-1);
						int[] final2 = possibleFinal2Locations.get(luckyIndex);
						solution[10] = final2[0];
						solution[11] = final2[1];
						usedNodes.add(final2);
						
//						System.out.println("Final 2 node");
//						printArrayList(possibleFinal2Locations);
//						System.out.println("Final 2 node");
//						printSolution(final2);
					}
				}
				
			}
			
			svs = isValidSolution(solution);
		}while(svs.isValidSolution == false);
		
		
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
	
	public static void writeNewHashMapToFile() {
		
		try
		{
		    String logFilename= saveDirectory + "NewHashMapData.txt";
		    FileWriter fw = new FileWriter(logFilename,true); //the true will append the new data
		    for(HashMap.Entry<String, Double> entry: fitnessMapNew.entrySet()) {
		    	fw.write(entry.getKey() + "\t" + entry.getValue() + "\n");
			    fw.flush();
		    }
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
		
		int[][] newLocations = new int[20][2];
		
		//y + 2
		for(int i = 0; i < 5; i++) {
			
			newLocations[index][1] = locY + 2; //y' is y + 2
			newLocations[index][0] = locX + (2 - i);
			index++;
		}
		
		//y - 2
		for(int i = 0; i < 5; i++) {
			
			newLocations[index][1] = locY - 2; //y' is y - 2
			newLocations[index][0] = locX + (2 - i);
			index++;
		}
		
		//y -1, y = 0, and y + 1
		
		for(int i = 0; i < 3; i++) {			
			
			for(int j = 0; j < 2; j++) {
				newLocations[index][1] = locY + (1 - i); //y' is y +- 1
				newLocations[index][0] = locX + (2 - 4 *j);
				index++;
				
			}
			
		}
		
		// y = 0 and x +- 3
		newLocations[index][1] = locY;
		newLocations[index][0] = locX + 3;
		index++;
		
		newLocations[index][1] = locY;
		newLocations[index][0] = locX - 3;
		index++;
		
		// x = 0 and y +- 3
		newLocations[index][1] = locY + 3;
		newLocations[index][0] = locX;
		index++;
		
		newLocations[index][1] = locY - 3;
		newLocations[index][0] = locX;
		
		ArrayList<int[]> filteredCoordinates = new ArrayList<int[]>();
		
		for(int i=0; i<newLocations.length; i++) {
			
			if(newLocations[i][0] >= 1 && newLocations[i][0] < maxGridSizeX) {
				
				if(newLocations[i][1] >= 1 && newLocations[i][1] < maxGridSizeY) {
					
					filteredCoordinates.add(newLocations[i]);
					
				}
				
			}
			
		}
		
		
		return filteredCoordinates;
		
		
	}
	
	
	public static ArrayList<int[]> generatePossibleInitLocations(int[] location) {
		
		int locX = location[0];
		
		int[][] newLocations = new int[5][2];
		
		for(int i = 0; i < 5; i++) {
			
			if(2-i != 0) {
				newLocations[i][0] = locX + (2 - i);
				newLocations[i][1] = 1;
			}
			
		}
		
		ArrayList<int[]> filteredCoordinates = new ArrayList<int[]>();
		
		for(int i=0; i<newLocations.length; i++) {
			
			if(newLocations[i][0] >= 1 && newLocations[i][0] < maxGridSizeX) {
					
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
		
		//check whether solution exists in the hashmap
		if(fitnessMap.containsKey(convertSolutionToString(solution)) == true) {
			
			fitness = fitnessMap.get(convertSolutionToString(solution));
			writeToSolutionsLog("From HashMap",solution,-1,-1,-1,-1,fitness);
			
		} else if (fitnessMapNew.containsKey(convertSolutionToString(solution)) == true) {
			
			fitness = fitnessMapNew.get(convertSolutionToString(solution));
			writeToSolutionsLog("From HashMapNew",solution,-2,-2,-2,-2,fitness);
			
		}else {
			
			//generate Marcie Coordinate String
			String marcieCoordinateString = generateMarcieCoordinateString(solution);
			//create marcie file
			String candleFileName = generateMarcieFile(marcieCoordinateString, candleFileNamePrefix);
			//execute marcie file and calculate fitness
			fitness = calculateFitness(candleFileName, solution);
			
			fitnessMapNew.put(convertSolutionToString(solution), fitness);
			
		}
		

		
		return fitness;
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
		
//		public static boolean checkInitNode(int[] solution) {
//			
//			boolean coorectInitNode = true;
//			
//			if(solution[1] != 1) {
//				coorectInitNode = false;			
//			}
//			
//			return coorectInitNode;
//		}
		
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
		
		
//		public static boolean checkNeighbourRelationshipInitFork(int[] solution) {
//			
//			boolean hasRequiredShortDistances = false;
//			
//			int[][] coordinatePairs = convertToCoordinatePairs(solution);
//			
//			int[] INIT = coordinatePairs[0];
//			int[] FORK = coordinatePairs[1];
//			int shortDistanceManhatten = 0;
//			int shortDistanceChebyshev = 0;
//			
//			shortDistanceManhatten = Math.abs(INIT[0] - FORK[0]) + 
//					Math.abs(INIT[1] - FORK[1]);
//	
//			shortDistanceChebyshev = Math.max(Math.abs(INIT[0] - FORK[0]), 
//					Math.abs(INIT[1] - FORK[1]));
//			
//			if(shortDistanceManhatten <= 3 || shortDistanceChebyshev < 3) {
//				hasRequiredShortDistances = true;
//			}
//			
//			return hasRequiredShortDistances;
//		}
		
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
		
		public static boolean allNeighboursExists(ArrayList<Integer> a, int[] neighbours) {
			
			boolean everyOneExists = false;
			
			for(int i = 0; i < neighbours.length; i++) {
				
				if(a.contains(neighbours[i])) {					
					everyOneExists = true;
				}else {
					everyOneExists = false;
					break;
				}
			}
			
			return everyOneExists;
		}
		
		public static boolean allNeighboursExistsNorm(ArrayList<Integer> a, int[] neighbours) {
			
			boolean everyOneExists = false;
			
			if(a.size() <= 3) {
					if(a.contains(neighbours[0]) && a.contains(neighbours[1])) {					
						everyOneExists = true;
					}
				
			}
			
			return everyOneExists;
		}
		
		
//		public static boolean checkNeighbourRelationshipFork(int[] solution) {
//			
//			boolean hasRequiredShortDistances = false;
//			
//			int[][] coordinatePairs = convertToCoordinatePairs(solution);
//			
//			int[] FORK = coordinatePairs[1];
//			
//					
//			int numberOfShortDistances = 0;
//			
//			int shortDistanceManhatten = 0;
//			int shortDistanceChebyshev = 0;
//			
//			for(int i=0; i < 4; i++) {
////				System.out.println(i);
//				if(i != 1) {
//					
//					shortDistanceManhatten = Math.abs(FORK[0] - coordinatePairs[i][0]) + 
//							Math.abs(FORK[1] - coordinatePairs[i][1]);
////					System.out.println("man " + shortDistanceManhatten);
//					shortDistanceChebyshev = Math.max(Math.abs(FORK[0] - coordinatePairs[i][0]), 
//							Math.abs(FORK[1] - coordinatePairs[i][1]));
////					System.out.println("chess " + shortDistanceChebyshev);
//					if(shortDistanceManhatten <= 3 || shortDistanceChebyshev < 3) {
//						numberOfShortDistances += 1;
//					}
//			
//				}
//				
//				
//			}
////			System.out.println("tot "+numberOfShortDistances);
//			if(numberOfShortDistances != 3) {			
//				hasRequiredShortDistances = false;
//			}else {
//				hasRequiredShortDistances = true;
//			}
//			return hasRequiredShortDistances;
//		}

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
		
		
//		public static boolean checkNeighbourRelationshipFinalTwo(int[] solution) {
//			
//			boolean hasRequiredShortDistances = false;
//			
//			int[][] coordinatePairs = convertToCoordinatePairs(solution);
//			
//			int[] FinalTwo = coordinatePairs[5];
//			int[] NORM2 = coordinatePairs[3];
//			
//			int shortDistanceManhatten = 0;
//			int shortDistanceChebyshev = 0;
//			
//			shortDistanceManhatten = Math.abs(FinalTwo[0] - NORM2[0]) + 
//					Math.abs(FinalTwo[1] - NORM2[1]);
////			System.out.println("manhatan " + shortDistanceManhatten);
//	
//			shortDistanceChebyshev = Math.max(Math.abs(FinalTwo[0] - NORM2[0]), 
//					Math.abs(FinalTwo[1] - NORM2[1]));
////			System.out.println("chess " + shortDistanceChebyshev);
//			
//			if(shortDistanceManhatten <= 3 || shortDistanceChebyshev < 3) {
//				hasRequiredShortDistances = true;
//			}
//			
//			
//			return hasRequiredShortDistances;
//		}
		
		
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
		
//		public static boolean checkNeighbourRelationshipFinalOne(int[] solution) {
//			
//			boolean hasRequiredShortDistances = false;
//			
//			int[][] coordinatePairs = convertToCoordinatePairs(solution);
//			
//			int[] FinalOne = coordinatePairs[4];			
//			int[] NORM1 = coordinatePairs[2];
//			
//			int shortDistanceManhatten = 0;
//			int shortDistanceChebyshev = 0;
//			
//			shortDistanceManhatten = Math.abs(FinalOne[0] - NORM1[0]) + 
//					Math.abs(FinalOne[1] - NORM1[1]);
////			System.out.println("manhatan " + shortDistanceManhatten);
//	
//			shortDistanceChebyshev = Math.max(Math.abs(FinalOne[0] - NORM1[0]), 
//					Math.abs(FinalOne[1] - NORM1[1]));
////			System.out.println("chess " + shortDistanceChebyshev);
//			
//			if(shortDistanceManhatten <= 3 || shortDistanceChebyshev < 3) {
//				hasRequiredShortDistances = true;
//			}
//			
////			
//			return hasRequiredShortDistances;
//		}
		
//		public static boolean checkNeighbourRelationshipNormTwo(int[] solution) {
//			
//			boolean hasRequiredShortDistances = false;
//			
//			int[][] coordinatePairs = convertToCoordinatePairs(solution);
//			
//			int[] NORMTwo = coordinatePairs[3]; //4th nodes pair is for norm 2 
//			
//			int numberOfShortDistances = 0;
//			
//			int shortDistanceManhatten = 0;
//			int shortDistanceChebyshev = 0;
//			
//			for(int i=0; i < coordinatePairs.length; i++) {
//				
//				if(i == 1 || i == 5) {
//					
//					shortDistanceManhatten = Math.abs(NORMTwo[0] - coordinatePairs[i][0]) + 
//							Math.abs(NORMTwo[1] - coordinatePairs[i][1]);
//			
//					shortDistanceChebyshev = Math.max(Math.abs(NORMTwo[0] - coordinatePairs[i][0]), 
//							Math.abs(NORMTwo[1] - coordinatePairs[i][1]));
//					
//					if(shortDistanceManhatten <= 3 || shortDistanceChebyshev < 3) {
//							numberOfShortDistances += 1;
//			}
//					
//				}
//				
//				
//			}
//			if(numberOfShortDistances == 2 || numberOfShortDistances == 3) {			
//				hasRequiredShortDistances = true;
//			}else {
//				hasRequiredShortDistances = false;
//			}
//			
//			return hasRequiredShortDistances;
//		}

		
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

		
		
//		public static boolean checkNeighbourRelationshipNormOne(int[] solution) {
//			
//			boolean hasRequiredShortDistances = false;
//			
//			int[][] coordinatePairs = convertToCoordinatePairs(solution);
//			
//			int[] NORMOne = coordinatePairs[2];
//			
//			int numberOfShortDistances = 0;
//			
//			int shortDistanceManhatten = 0;
//			int shortDistanceChebyshev = 0;
//			
//			for(int i=0; i < coordinatePairs.length; i++) {
//				
//				if(i == 1 || i == 4) {
////					System.out.println(i);
//					shortDistanceManhatten = Math.abs(NORMOne[0] - coordinatePairs[i][0]) + 
//									Math.abs(NORMOne[1] - coordinatePairs[i][1]);
////					System.out.println("man "+ shortDistanceManhatten);
//					shortDistanceChebyshev = Math.max(Math.abs(NORMOne[0] - coordinatePairs[i][0]), 
//							Math.abs(NORMOne[1] - coordinatePairs[i][1]));
////					System.out.println("chess "+ shortDistanceChebyshev);
//					if(shortDistanceManhatten <= 3 || shortDistanceChebyshev < 3) {
//						numberOfShortDistances += 1;
//					}
//				}
//				
//				
//			}
//			
////			System.out.println("tot "+ numberOfShortDistances);
//			if(numberOfShortDistances == 2 || numberOfShortDistances == 3) {			
//				hasRequiredShortDistances = true;
//			}else {
//				hasRequiredShortDistances = false;
//			}
//			
//			return hasRequiredShortDistances;
//		}
		
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
		
		public static int[] smallChange(int[] solution, int skip) {
			
			//clone the solution
			int[] solutionClone = solution.clone();
			
			//convert the solution into coordinate pairs
			int[][] coordinatePairs = convertToCoordinatePairs(solutionClone);
			
			//create a random arrangement of indexes
			ArrayList<Integer> availableIndexes = generateRandomAllocation(0, coordinatePairs.length - 1);
			//printArrayInteger(availableIndexes);
			
			if(skip != -1) {
				availableIndexes.remove(Integer.valueOf(skip));
			}
			
			//printArrayInteger(availableIndexes);
			
			int luckyIndex;
			ArrayList<int[]> newLocations = new ArrayList<int[]>();
			StatusValidateSolution svs = new StatusValidateSolution();
			
			boolean finished = false;
			
			
			for(int i = 0; i < availableIndexes.size() && !finished; i++) {
				
				luckyIndex = availableIndexes.get(i);
//				System.out.println("Small change " + luckyIndex);
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
		
		public static void writeToBestSolutionsLog(int iteration, double elitef, int[] elitex) {
			
			DecimalFormat df = new DecimalFormat("#.##");
			
			try
			{
			    String logFilename= saveDirectory + "BestSolutionsLog.txt";
			    FileWriter fw = new FileWriter(logFilename,true); //the true will append the new data
			    fw.write(String.valueOf(iteration) + "\t" + df.format(fit) + "\t" + df.format(elitef) +
			    		"\t" + convertArrayToString(elitex) + "\t" +
			    		convertArrayToString(initialSolution) + "\t" + convertArrayToString(newSolution) + "\n");
			    fw.close();
			}
			catch(IOException ioe)
			{
			    System.err.println("IOException: " + ioe.getMessage());
			}
			
		}
		
//		public static void writeToBestSolutionsLog(String fileNamePrefix, int[] solution, double fitness) {
//			
//			DecimalFormat df = new DecimalFormat("#.###");
//			String status;
//			if(fitness == 0.0) {
//				status = "Check candle output file - May be transition(s) that can never fire";
//			}else {
//				status = "OK";
//			}
//			
//			try
//			{
//			    String logFilename= saveDirectory + "BestSolutionsLog.txt";
//			    FileWriter fw = new FileWriter(logFilename,true); //the true will append the new data
//			    fw.write(fileNamePrefix + "\t" + convertArrayToString(solution) + "\t" +
//			    		df.format(fitness) + "\t" + status + "\n");
//			    fw.close();
//			}
//			catch(IOException ioe)
//			{
//			    System.err.println("IOException: " + ioe.getMessage());
//			}
//		}
		
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
		
		
		public static void printArrayInteger(ArrayList<Integer> a) {
			
			for(int i = 0; i < a.size(); i++) {
				
				System.out.print(a.get(i) + ",");
				
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