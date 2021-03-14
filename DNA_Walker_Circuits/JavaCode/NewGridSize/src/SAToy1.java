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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SAToy1 {
	
	public static String saveDirectoryParent = "E:\\SA\\Toy1\\NewGridSize\\";
	private static String saveDirectory = "E:\\SA\\Toy1\\NewGridSize\\";
	private static HashMap<String, Double> fitnessMap = new HashMap<String, Double>();	
	private static HashMap<String, Double> fitnessMapNew = new HashMap<String, Double>();
	private static LinkedHashMap<int[], String> nodeConnectionIdentifier = new LinkedHashMap<int[], String>();
	private static LinkedHashMap<Integer, String> nodeIdentifier = new LinkedHashMap<Integer, String>();
	private static LinkedHashMap<Integer, Integer> nodeBranchRelationship = new LinkedHashMap<Integer, Integer>();
	private static int dS = 3, dM = 5, dL = 8;
	
	private static int numberOfNodes = 8;	
	public static int minGridSize = 0;
	public static int maxGridSizeX = 20;
	public static int maxGridSizeY = 14;
	
	
	
	//Fitness
	private static double fit = 0.0;
	//Number of iterations
	private static double iter = 2500;
	//Convergence point
	private static double cp = -1;
	//x - solution - xnew new solution
	private static int[] initialSolution, newSolution;
		
		
	public static void main(String args[]) throws Exception {
		
		populateNodeIdentifierToy1();
		populateNodeConnectionIdentifierToy1();
		populateNodeBranchRelationshipToy1();
		
//		String fitnessFileName = "E:\\TestDavidSolutions\\Toy0\\Solutions_toy0"
//				+ ".txt"; //txt file with two tab separated columns 1,1,3,2,5,2,2,4,6,4,2,6	1.6
//		createFitnessStore(fitnessFileName);
		
		for(int n = 1; n <= 1; n++) {
			
			runSA(n);
			
		}
		
		
	}
	
	public static void runSA(int iteration) throws Exception {

		saveDirectory = saveDirectoryParent + "Run" + iteration + "\\";
		
		long startTime = System.currentTimeMillis();
		System.out.println("begin random solution");
		//Create a random starting point			
		initialSolution = generateRandomSolutionMaster();
		System.out.println("done random solution");
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
				System.out.println(iteration + " "+ i + " " + fit + " " + elitef);
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
	
	private static double ComputeTZero() throws Exception 
	{
		double t0 = 0.0;
		double ctziter = 0.01*iter;
		String fileNamePrefix;
		for(int i=0;i<ctziter;++i)
		{
			initialSolution = generateRandomSolutionMaster();
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
	
	private static double ComputeLamda(double t0, double titer) 
	{
		double lamda = (Math.log(titer) - Math.log(t0)) / (iter*0.95);
		lamda = Math.exp(lamda);
		return(lamda);
	}
	
public static int[] smallChangeMaster(int[] solution) {
		
		int[] newSolution = new int[numberOfNodes*2];
		StatusValidateSolution svsNewSol = new StatusValidateSolution();
		
		int selection;
		
		do {
			
			selection = generateRandomInt(1,5);
			
			if(selection == 1) {
//				System.out.println("Selection " + selection);
				newSolution = smallChange(solution,-1);
				svsNewSol = isValidSolution(newSolution);
				
			}else if(selection == 2) {
//				System.out.println("Selection " + selection);
				newSolution = smallChangeOneFixed(solution,-1);
				svsNewSol = isValidSolution(newSolution);
			}else if(selection == 3){
//				System.out.println("Selection " + selection);
				newSolution = smallChangeTwoFixed(solution,-1);
				svsNewSol = isValidSolution(newSolution);
			}else if(selection == 4) {
				newSolution = smallChangeThreeFixed(solution,-1);
				svsNewSol = isValidSolution(newSolution);
				
			}else if(selection == 5) {
				newSolution = smallChangeFourFixed(solution,-1);
				svsNewSol = isValidSolution(newSolution);
				
			}
			
			
		}while(svsNewSol.isValidSolution == false);
		
		return newSolution;
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
//			System.out.println("Small change " + luckyIndex);
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
	
	
	public static int[] smallChangeThreeFixed(int[] solution, int skip) {
		
		// clone the solution
		int[] solutionClone = solution.clone();

		// convert the solution into coordinate pairs
		int[][] coordinatePairs = convertToCoordinatePairs(solutionClone);
		
		//create a random arrangement of indexes
		ArrayList<Integer> availableIndexesForFixedPositionL2 = generateRandomAllocation(0, coordinatePairs.length - 1);
		
		if(skip != -1) {
			availableIndexesForFixedPositionL2.remove(Integer.valueOf(skip));
		}
		
		//variable declaration
		int luckyIndexFixedL2;
		ArrayList<int[]> newLocationsFixedNodeL2 = new ArrayList<int[]>();
		StatusValidateSolution svs = new StatusValidateSolution();
		boolean finished = false; // for loop breaker
		int[] luckyPair;
		
		//first for loop - level 1 - go through each combination of fixed position
		for (int f = 0; f < availableIndexesForFixedPositionL2.size() && !finished; f++) {
			// current index
			luckyIndexFixedL2 = availableIndexesForFixedPositionL2.get(f);
//			System.out.println(luckyIndexFixedL1);
			
			//changed a fixed location 
			if(luckyIndexFixedL2 == 0) {
				newLocationsFixedNodeL2 = generatePossibleInitLocations(coordinatePairs[luckyIndexFixedL2]);
			}else {
				newLocationsFixedNodeL2 = generatePossibleLocations(coordinatePairs[luckyIndexFixedL2]);
			}
			
			newLocationsFixedNodeL2 = removeAlreadyUsedCoordinates(coordinatePairs,newLocationsFixedNodeL2);
			
			for(int g = 0; g < newLocationsFixedNodeL2.size(); g++) {
				
				//create a fixed position
				luckyPair = newLocationsFixedNodeL2.get(g);
				coordinatePairs[luckyIndexFixedL2][0] = luckyPair[0];
				coordinatePairs[luckyIndexFixedL2][1] = luckyPair[1];
				
				//to revert back changes keep a copy
				int[] solutionCloneFixedTwoPositionsBK = convertToLinearArray(coordinatePairs);
//				System.out.println("Fixed solution L1 " + g);
//				printSolution(solutionCloneFixedOnePositionBK);
				
				solutionClone = smallChangeTwoFixed(solutionCloneFixedTwoPositionsBK, luckyIndexFixedL2);
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
	
	
	public static int[] smallChangeFourFixed(int[] solution, int skip) {
		
		// clone the solution
		int[] solutionClone = solution.clone();

		// convert the solution into coordinate pairs
		int[][] coordinatePairs = convertToCoordinatePairs(solutionClone);
		
		//create a random arrangement of indexes
		ArrayList<Integer> availableIndexesForFixedPositionL3 = generateRandomAllocation(0, coordinatePairs.length - 1);
		
		if(skip != -1) {
			availableIndexesForFixedPositionL3.remove(Integer.valueOf(skip));
		}
		
		//variable declaration
		int luckyIndexFixedL3;
		ArrayList<int[]> newLocationsFixedNodeL3 = new ArrayList<int[]>();
		StatusValidateSolution svs = new StatusValidateSolution();
		boolean finished = false; // for loop breaker
		int[] luckyPair;
		
		//first for loop - level 1 - go through each combination of fixed position
		for (int f = 0; f < availableIndexesForFixedPositionL3.size() && !finished; f++) {
			// current index
			luckyIndexFixedL3 = availableIndexesForFixedPositionL3.get(f);
//			System.out.println(luckyIndexFixedL1);
			
			//changed a fixed location 
			if(luckyIndexFixedL3 == 0) {
				newLocationsFixedNodeL3 = generatePossibleInitLocations(coordinatePairs[luckyIndexFixedL3]);
			}else {
				newLocationsFixedNodeL3 = generatePossibleLocations(coordinatePairs[luckyIndexFixedL3]);
			}
			
			newLocationsFixedNodeL3 = removeAlreadyUsedCoordinates(coordinatePairs,newLocationsFixedNodeL3);
			
			for(int g = 0; g < newLocationsFixedNodeL3.size(); g++) {
				
				//create a fixed position
				luckyPair = newLocationsFixedNodeL3.get(g);
				coordinatePairs[luckyIndexFixedL3][0] = luckyPair[0];
				coordinatePairs[luckyIndexFixedL3][1] = luckyPair[1];
				
				//to revert back changes keep a copy
				int[] solutionCloneFixedTwoPositionsBK = convertToLinearArray(coordinatePairs);
//				System.out.println("Fixed solution L1 " + g);
//				printSolution(solutionCloneFixedOnePositionBK);
				
				solutionClone = smallChangeThreeFixed(solutionCloneFixedTwoPositionsBK, luckyIndexFixedL3);
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
	
	public static ArrayList<int[]> removeAlreadyUsedCoordinates(int[][] coordinatePairs, ArrayList<int[]> newLocations){
		
		HashSet<int[]> newLoc = new HashSet<int[]>();
				
		for(int i = 0; i < coordinatePairs.length; i++) {
			
			for(int j = 0; j < newLocations.size(); j++) {
				
				if(Arrays.equals(coordinatePairs[i], newLocations.get(j))) {
					
					//newLocations.remove(j);
					
				}else {
					newLoc.add(newLocations.get(j));
				}
			}
			
		}
		
		ArrayList<int[]> refinedNewLocations = new ArrayList<int[]>();
		refinedNewLocations.addAll(newLoc);
		return refinedNewLocations;
				
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
	
	public static int[] generateRandomSolutionMaster() {
		
		int[] solution = new int[numberOfNodes*2];
		StatusValidateSolution svs = new StatusValidateSolution();
		
		do {
			
			solution = generateRandomSolution();
			
			svs = isValidSolution(solution);
			
		}while(svs.isValidSolution == false);
		
		return solution;
		
	}
	
public static ArrayList<int[]> generatePossibleInitLocations(int[] location) {
		
		int locX = location[0];
		
		int[][] newLocations = new int[6][2]; //6 possible location init node can move (-3 to +3)
		int newLocationIndex = 0;
		for(int i = 0; i <= newLocations.length; i++) {
			
			if(3-i != 0) {
				newLocations[newLocationIndex][0] = locX + (3 - i);
				newLocations[newLocationIndex][1] = 1;
				newLocationIndex++;
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
	
	public static int[] generateRandomSolution() {
	
		int[] solution = new int[numberOfNodes*2];				
		int[] currentConnection;
		int[] coordinates;
		int ancestorNodeId = 0, luckyIndex;
		int[] ancestorCoordinates = new int[2];
		ArrayList<int[]> usedNodes = new ArrayList<int[]>();
		ArrayList<int[]> possibleMoveLocations = new ArrayList<int[]>();
		ArrayList<Integer> populatedNodeIDs = new ArrayList<Integer>();
		
		for (Map.Entry<int[], String> entry : nodeConnectionIdentifier.entrySet()) {
		    //stores a node ID pair (0,1) Init - fork
			currentConnection = entry.getKey();
			
			for(int i = 0; i< currentConnection.length; i++) {
					
				if(!populatedNodeIDs.contains(currentConnection[i])) {
					
					//checks if node is Init
					if(i == 0 && currentConnection[i] == 0) {
	//					System.out.println("My Id is "+ currentConnection[i] + " and i am init");
						coordinates = generateInitNode();
		        		solution[currentConnection[i]*2] = coordinates[0];
		        		solution[currentConnection[i]*2+1] = coordinates[1];
		        		usedNodes.add(coordinates);
		        		populatedNodeIDs.add(currentConnection[i]);
						
	
					}else if(i == 1) {
						
	//					System.out.println("My Id is "+ currentConnection[i] + " and i am in i is 1");
						
						//get ancestor - ancestor of a node at i = 1 is the node at i = 0 
						ancestorNodeId = currentConnection[0];
	//					System.out.println("Node " + currentConnection[1] + "ancestor is " + currentConnection[0]);
						ancestorCoordinates[0] = solution[ancestorNodeId * 2];
						ancestorCoordinates[1] = solution[ancestorNodeId * 2 + 1];
						
						possibleMoveLocations = generatePossibleLocations(ancestorCoordinates);
	        			
	        			possibleMoveLocations = removeUsedNodes(usedNodes, possibleMoveLocations);
	        			
	        			possibleMoveLocations = removeTooCloseNodesToUsedNodes(usedNodes, possibleMoveLocations);
	        						        			
	        			luckyIndex = generateRandomInt(0,possibleMoveLocations.size()-1);
	        			coordinates = possibleMoveLocations.get(luckyIndex);
	        			
	        			solution[currentConnection[i]*2] = coordinates[0];
		        		solution[currentConnection[i]*2+1] = coordinates[1];
		        		usedNodes.add(coordinates);
		        		populatedNodeIDs.add(currentConnection[i]);
		        		possibleMoveLocations.clear();
						
					}
					
				}
					
					
			}
	
	//		printSolution(solution);
		}
		
	//	printSolution(solution);
		return(solution);
		
	}

	public static ArrayList<int[]> removeTooCloseNodesToUsedNodes(ArrayList<int[]> used, ArrayList<int[]> loc){
		
		HashSet<int[]> newLoc = new HashSet<int[]>();
		
		for(int i = 0; i < used.size(); i++) {
			
			for(int j = 0; j < loc.size(); j++) {
				
				if(distanceBetweenPoints(loc.get(j), used.get(i)) < 2) {
					newLoc.add(loc.get(j));
				}				
				
			}
		}	
		
		return loc;
		
		
	}
	
	public static int[] generateInitNode() {
		
		int[] initNodeCoords = new int[2];
		initNodeCoords[0] = generateRandomInt(1,maxGridSizeX-1);
		initNodeCoords[1] = 1;
		
		return initNodeCoords;
	}
	
	public static double distanceBetweenPoints(int[]a, int[]b) {
	
		double distance = 0.0;
		
		distance = Math.sqrt(Math.pow((a[0] - b[0]), 2) + Math.pow((a[1] - b[1]), 2));
		
		return distance;
		
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

	public static ArrayList<int[]> removeUsedNodes(ArrayList<int[]> used, ArrayList<int[]> loc){
		
		HashSet<int[]> newLoc = new HashSet<int[]>();
		
		for(int i =0; i < used.size(); i++) {
			
			for(int j = 0; j < loc.size(); j++) {
				
				if(isArrayElementEqual(loc.get(j), used.get(i)) == true) {
					newLoc.add(loc.get(j));
				}
			}
		}	
		
		loc.removeAll(newLoc);
		
		return loc;
		
		
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
//						System.out.println("No required short distances in Init node");
						breakNow = true;
						break;
					}
				case "Fork": 
					if(neighbourCount == 3) {
						hasRequiredShortDistances = true; 
						break;
					} else {
						hasRequiredShortDistances = false;
//						System.out.println("No required short distances in Fork node");
						breakNow = true;
						break;
					}
					
				case "Norm": 
					if(neighbourCount == 2 || neighbourCount == 3) {
						hasRequiredShortDistances = true; 
						break;
					} else {
						hasRequiredShortDistances = false;
//						System.out.println("No required short distances in Norm " + i + " node");
						breakNow = true;
						break;
					}
					
				case "Final": 
					if(neighbourCount == 1) {
						hasRequiredShortDistances = true; 
						break;
					} else {
						hasRequiredShortDistances = false;
//						System.out.println("No required short distances in Final " + i + " node");
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
	
	public static String generateMarcieCoordinateStringToy1(int[] coordinates) {
		
		String marcieCoordinateString = "bool Positions(CD1 x, CD2 y, Type z, Label w) {\n";
		
		String format1 = "\t(x=%d & y=%d & z=INIT & w=E) | "; //int
		String format2 = "(x=%d & y=%d & z=FORK & w=E) |\n"; //fork
		String format3 = "\t(x=%d & y=%d & z=NORM & w=X) | "; //norm 11
		String format4 = "(x=%d & y=%d & z=NORM & w=X) |\n"; // norm 12
		String format5 = "\t(x=%d & y=%d & z=NORM & w=NX) | "; //norm 21
		String format6 = "(x=%d & y=%d & z=NORM & w=NX) |\n"; // norm 22
		String format7 = "\t(x=%d & y=%d & z=FINAL & w=T) | "; //final 1
		String format8 = "(x=%d & y=%d & z=FINAL & w=F)\n"; // final 2
		
		
		for(int i = 0; i < coordinates.length; i+=2) {
			
			switch(i) {
			
			case 0: marcieCoordinateString += String.format(format1, coordinates[i], coordinates[i+1]); break;
			case 2: marcieCoordinateString += String.format(format2, coordinates[i], coordinates[i+1]); break;
			case 4: marcieCoordinateString += String.format(format3, coordinates[i], coordinates[i+1]); break;
			case 6: marcieCoordinateString += String.format(format4, coordinates[i], coordinates[i+1]); break;
			case 8: marcieCoordinateString += String.format(format5, coordinates[i], coordinates[i+1]); break;
			case 10: marcieCoordinateString += String.format(format6, coordinates[i], coordinates[i+1]); break;
			case 12: marcieCoordinateString += String.format(format7, coordinates[i], coordinates[i+1]); break;
			case 14: marcieCoordinateString += String.format(format8, coordinates[i], coordinates[i+1]); break;
			default: System.out.println("Invalid Coordinates");
			}
			
			
		}
		
		marcieCoordinateString += "};\n";
		
		return marcieCoordinateString;
			
		
	}
	
	public static String generateMarcieFile(String coordinates, String fileNamePrefix) {
	    
		//System.out.println(saveDirectory);
		String header = readFile(saveDirectory + "header_toy1.txt");		
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
//		if(fitnessMap.containsKey(convertSolutionToString(solution)) == true) {
//			
//			fitness = fitnessMap.get(convertSolutionToString(solution));
//			writeToSolutionsLog("From HashMap",solution,-1,-1,-1,-1,fitness);
//			
//		} else if (fitnessMapNew.containsKey(convertSolutionToString(solution)) == true) {
//			
//			fitness = fitnessMapNew.get(convertSolutionToString(solution));
//			writeToSolutionsLog("From HashMapNew",solution,-2,-2,-2,-2,fitness);
//			
//		}else {
//			
//			//generate Marcie Coordinate String
//			String marcieCoordinateString = generateMarcieCoordinateStringToy1(solution);
//			//create marcie file
//			String candleFileName = generateMarcieFile(marcieCoordinateString, candleFileNamePrefix);
//			//execute marcie file and calculate fitness
//			fitness = calculateFitness(candleFileName, solution);
			
			int[] leaks = computeLeaks(solution);
			fitness = calculateFitnessWithouMarcie(leaks, solution);
			
//			fitnessMapNew.put(convertSolutionToString(solution), fitness);
//			
//		}
		

		
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
	
	public static String convertArrayToString(int[] array) {
		
		String stringArray = "";
		
		for(int i = 0; i < array.length; i++) {
					
			switch(array[i]) {
			
			case 10: stringArray += "a"; break;
			case 11: stringArray += "b"; break;
			case 12: stringArray += "c"; break;
			case 13: stringArray += "d"; break;
			case 14: stringArray += "e"; break;
			case 15: stringArray += "f"; break;
			case 16: stringArray += "g"; break;
			case 17: stringArray += "h"; break;
			case 18: stringArray += "i"; break;
			case 19: stringArray += "j"; break;
			case 20: stringArray += "k"; break;
			case 21: stringArray += "l"; break;
			default: stringArray += Integer.toString(array[i]); break;
			
			}
			
		}
		
//		stringArray += "]";
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
	
	public static String readFile(String fileName) {
		
		String content = null;
		
		try {
			content = new String(Files.readAllBytes(Paths.get(fileName)));
		}catch(IOException e) {
			e.printStackTrace();
		}
		
		return content;
		
	}
	
//	public static String convertArrayToString(int[] array) {
//		
//		String stringArray = "[";
//		
//		for(int i = 0; i < array.length; i++) {
//			
//			if(i != array.length - 1) {
//				stringArray += Integer.toString(array[i]);
//				stringArray += ",";
//			}else {
//				stringArray += Integer.toString(array[i]);
//			}
//			
//		}
//		
//		stringArray += "]";
//		return stringArray;
//		
//	}
	
	public static void populateNodeConnectionIdentifierToy1() {
		
		//coordinate String for marcie/array - I, F, N11, N12, N21, N22, F1, F2
		nodeConnectionIdentifier.put(new int[] {0,1},"Init-Fork");
		nodeConnectionIdentifier.put(new int[] {1,2},"Fork-Norm11");
		nodeConnectionIdentifier.put(new int[] {1,4},"Fork-Norm21");
		nodeConnectionIdentifier.put(new int[] {2,3},"Norm11-Norm12");
		nodeConnectionIdentifier.put(new int[] {4,5},"Norm21-Norm22");
		nodeConnectionIdentifier.put(new int[] {3,6},"Norm12-Final1");
		nodeConnectionIdentifier.put(new int[] {5,7},"Norm22-Final2");
		
		
	}
	
	public static void populateNodeIdentifierToy1() {
		
		nodeIdentifier.put(Integer.valueOf(0),"Init");
		nodeIdentifier.put(Integer.valueOf(1),"Fork");
		nodeIdentifier.put(Integer.valueOf(2),"Norm");
		nodeIdentifier.put(Integer.valueOf(3),"Norm");
		nodeIdentifier.put(Integer.valueOf(4),"Norm");
		nodeIdentifier.put(Integer.valueOf(5),"Norm");
		nodeIdentifier.put(Integer.valueOf(6),"Final");
		nodeIdentifier.put(Integer.valueOf(7),"Final");
		
	}
	
	public static void createFitnessStore(String fileName) {
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
	
	public static void populateNodeBranchRelationshipToy1() {
		//nodes appear in the same order they are encoded in the int[] solution
		//if on both branches the code is 0, if only in one branch it's 1 or 2
		//branch 1 is the positive branch (N1,F1)
		//branch 2 is the negative branch (N2, F2)
		nodeBranchRelationship.put(Integer.valueOf(0),Integer.valueOf(0));
		nodeBranchRelationship.put(Integer.valueOf(1),Integer.valueOf(0));
		nodeBranchRelationship.put(Integer.valueOf(2),Integer.valueOf(1));
		nodeBranchRelationship.put(Integer.valueOf(3),Integer.valueOf(1));
		nodeBranchRelationship.put(Integer.valueOf(4),Integer.valueOf(2));
		nodeBranchRelationship.put(Integer.valueOf(5),Integer.valueOf(2));
		nodeBranchRelationship.put(Integer.valueOf(6),Integer.valueOf(1));
		nodeBranchRelationship.put(Integer.valueOf(7),Integer.valueOf(2));	
		
	}
	
	public static int[] computeLeaks(int[] solution) {
		
		int[] leaksCout = new int[3];//short, medium, long
		
		int[][] coordinatePairs = convertToCoordinatePairs(solution);		
		int shortLeaksCount = 0, mediumLeaksCount = 0, longLeaksCount = 0;
		int totShortLeaksCount = 0, totMediumLeaksCount = 0, totLongLeaksCount = 0;
		int nodeBranchId;
		
		for(int i = 0; i < coordinatePairs.length; i++) {
			
			nodeBranchId = nodeBranchRelationship.get(i);
			
			if(nodeBranchId != 0) {
				
				for(int j = i+1; j < coordinatePairs.length; j++) {
					
					if(nodeBranchId != nodeBranchRelationship.get(j)) {
						
//						System.out.println(i + " " + j);
						
						if(!(nodeIdentifier.get(i) == "Final" && nodeIdentifier.get(j) == "Final")) {
							
							if(isShortDistance(coordinatePairs[i], coordinatePairs[j])){
								if(nodeIdentifier.get(i) == "Norm" && nodeIdentifier.get(j) == "Norm") {
									shortLeaksCount = shortLeaksCount + 2;
								}else {
									shortLeaksCount++;
								}
//								System.out.println("Short " + i+ " " +j);
							}
							
							if(isMediumDistance(coordinatePairs[i], coordinatePairs[j])){
								
								if(nodeIdentifier.get(i) == "Norm" && nodeIdentifier.get(j) == "Norm") {
									mediumLeaksCount = mediumLeaksCount + 2;
								}else {
									mediumLeaksCount++;
								}
								
//								System.out.println("Medium " + i+ " " +j);
							}
							
							if(isLongDistance(coordinatePairs[i], coordinatePairs[j])){
								
								if(nodeIdentifier.get(i) == "Norm" && nodeIdentifier.get(j) == "Norm") {
									longLeaksCount = longLeaksCount + 2;
								}else {
									longLeaksCount++;
								}
								
				
//								System.out.println("Long " + i+ " " +j);
							}
							
						}
						
					}
					
				}// end for j
				
				totShortLeaksCount = totShortLeaksCount + shortLeaksCount;
				totMediumLeaksCount = totMediumLeaksCount + mediumLeaksCount;
				totLongLeaksCount = totLongLeaksCount + longLeaksCount;
				
			}// end if node type
			
			shortLeaksCount = 0;
			mediumLeaksCount = 0;
			longLeaksCount = 0;
			
		}

		leaksCout[0] = totShortLeaksCount;
		leaksCout[1] = totMediumLeaksCount;
		leaksCout[2] = totLongLeaksCount;
		
		return leaksCout;
	}
	
	
	public static boolean isShortDistance(int[] a, int[] b) {
		
		boolean isShortD = false;
		double rDistance, cDistance;
		rDistance = computeRectilinearDistance(a, b);
		cDistance = computeChessboardDistance(a, b);
		
		if(rDistance > 0 && cDistance > 0)
			if(rDistance <= dS || cDistance < dS)
				isShortD = true;
		
		return isShortD;
	}
	
	public static boolean isMediumDistance(int[]a, int[]b) {
		
		boolean isMediumD = false;
		double rDistance, cDistance;
		rDistance = computeRectilinearDistance(a, b);
		cDistance = computeChessboardDistance(a, b);
		
		if(rDistance > 0 && cDistance > 0)
			if(rDistance > dS && cDistance >= dS && (rDistance <= dM || cDistance < dM))
				isMediumD = true;
		
		return isMediumD;
	}
	
	
	public static boolean isLongDistance(int[] a, int[] b) {
		
		boolean isLongD = false;
		double rDistance, cDistance;
		rDistance = computeRectilinearDistance(a, b);
		cDistance = computeChessboardDistance(a, b);
		
		if(rDistance > 0 && cDistance > 0)
			if(rDistance > dM && cDistance >= dM && (rDistance <= dL || cDistance < dL))
				isLongD = true;
		
		
		return isLongD;
	}
	
	
	public static double computeRectilinearDistance(int[] a, int[] b) {
		
		double rDistance = 0.0;
		rDistance = Math.abs(a[0] - b[0]) + Math.abs(a[1] - b[1]);
		return rDistance;
		
	}
	
	public static double computeChessboardDistance(int[] a, int[] b) {
		
		double cDistance = 0.0;
		cDistance = Math.max(Math.abs(a[0] - b[0]), Math.abs(a[1] - b[1]));
		return cDistance;
		
	}
	
public static double calculateFitnessWithouMarcie(int[] leaks, int[] solution) {
		
    	double fitness = 0.0;
		
		double area = calculateArea(solution);
		
//		int[] leaks = generateMarcieOutput(candleFileName);
		
		if(leaks == null) {
			
			System.out.println("No marcie output");
		}else {
			//printSolution(leaks);
//			fitness = (leaks[0]*100 + leaks[1]*10 + leaks[2]*1) / area;
			fitness = (leaks[0]*100 + leaks[1]*10 + leaks[2]*1) * area;
		}
		
		//write solution and fitness 
		writeToSolutionsLog("My-greatest-leaks-function",solution,leaks[0],leaks[1],leaks[2],area,fitness);
				
		
		return fitness;
		
	}

}
