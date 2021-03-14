import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

public class Test {
	
	private static LinkedHashMap<Integer, String> nodeIdentifier = new LinkedHashMap<Integer, String>();
	private static LinkedHashMap<int[], String> nodeConnectionIdentifier = new LinkedHashMap<int[], String>();
	private static int numberOfNodes = 6;
	public static int minGridSize = 0;
	public static int maxGridSizeX = 7;
	public static int maxGridSizeY = 8;
	
	
	public static void main(String args[]) {
		
//		populateNodeIdentifierToy0();
//		populateNodeConnectionIdentifierToy0();
		int[] sol = {1,1,3,1,5,1,7,1,4,3,7,4,4,6};
		
//		System.out.print(checkRequiredShortDistanceCount(sol));
		
//		
//		for(int i = 0; i<200;i++) {
//			
//			System.out.print(i + "  ");
//			printSolution(generateRandomSolution());
//			System.out.println("  ");
//		}
//		
		 
//		HashSet<int[]> a1 = new HashSet<int[]>();
//		a1.add(new int[] {1,2});
//		a1.add(new int[] {1,3});
//		a1.add(new int[] {1,4});
//		ArrayList<int[]> a = new ArrayList<int[]>();
//		a.addAll(a1);
//		
//		printArrayList(a);
		
//		ArrayList<int[]> used = new ArrayList<int[]>();
//		used.add(new int[] {2,1});
//		used.add(new int[] {4,2});
//		used.add(new int[] {2,3});
//		used.add(new int[] {4,6});
//		used.add(new int[] {4,4});
//		
//		ArrayList<int[]> loc = new ArrayList<int[]>();
//		loc = generatePossibleLocations(new int[] {2,3});
//		printArrayList(loc);
//		System.out.println("");
//		
//		loc = removeTooCloseNodesToUsedNodes(used,loc);
//		printArrayList(loc);
	}
	
	public static int[] generateRandomSolution() {
		
		int[] solution = new int[numberOfNodes*2];
		
		int[] currentConnection;
		String nodeType;
		ArrayList<int[]> usedNodes = new ArrayList<int[]>();
		int[] coordinates;
		ArrayList<int[]> possibleMoveLocations = new ArrayList<int[]>();
		ArrayList<Integer> populatesNodeIDs = new ArrayList<Integer>();
		int[] ancestor = new int[2];
		int luckyIndex;
		
		Iterator<Entry <int[], String>> it = nodeConnectionIdentifier.entrySet().iterator();
	    
		while (it.hasNext()) {
	        Map.Entry<int[], String> pair = (Map.Entry<int[], String>)it.next();
	        currentConnection = pair.getKey();
	        
//	        first = currentConnection[0];
//	        second = currentConnection[1];
	       	        
	        for(int i = 0; i < 2; i++) {
	        	
//	        	System.out.println(currentConnection[i]);
	        	
	        	nodeType = nodeIdentifier.get(currentConnection[i]);
	        	
	        	switch(nodeType){
	        		
	        	case "Init":
	        		coordinates = generateInitNode();
	        		solution[currentConnection[i]*2] = coordinates[0];
	        		solution[currentConnection[i]*2+1] = coordinates[1];
	        		usedNodes.add(coordinates);
	        		populatesNodeIDs.add(currentConnection[i]);
	        		break;
	        		
	        	default:
	        		try {
	        			
	        			if(!populatesNodeIDs.contains(Integer.valueOf(currentConnection[i]))) {
		        			
		        			ancestor[0] = solution[currentConnection[i - 1] * 2];
		        			ancestor[1] = solution[currentConnection[i - 1] * 2 + 1];
		        			
//		        			System.out.println("Ancestor set");
//		        			printSolution(ancestor);
//		        			System.out.println();
		        			
		        			possibleMoveLocations = generatePossibleLocations(ancestor);
//		        			System.out.println("Initial set");
//		        			printArrayList(possibleMoveLocations);
		        			
		        			possibleMoveLocations = removeUsedNodes(usedNodes, possibleMoveLocations);
		        			
//		        			System.out.println("After removing used nodes");
//		        			printArrayList(possibleMoveLocations);
		        			
		        			possibleMoveLocations = removeTooCloseNodesToUsedNodes(usedNodes, possibleMoveLocations);
		        			
//		        			System.out.println("After removing too close nodes to used nodes");
//		        			printArrayList(possibleMoveLocations);
		        			
		        			luckyIndex = generateRandomInt(0,possibleMoveLocations.size()-1);
		        			coordinates = possibleMoveLocations.get(luckyIndex);
		        			
		        			solution[currentConnection[i]*2] = coordinates[0];
			        		solution[currentConnection[i]*2+1] = coordinates[1];
			        		usedNodes.add(coordinates);
			        		populatesNodeIDs.add(currentConnection[i]);
			        		possibleMoveLocations.clear();
		        			
		        		}
	        			
	        		}catch(Exception e){
//	        			solution = null;
	        			generateRandomSolution();
	        			
	        		}
	    
	        		break;
	        	}
	        	
//	        	System.out.println("");
	        	
	        }
	        
			}
	  
		return solution;
		
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
		
		loc.removeAll(newLoc);
				
		return loc;
		
		
	}
	
	public static double distanceBetweenPoints(int[]a, int[]b) {
		
		double distance = 0.0;
		
		distance = Math.sqrt(Math.pow((a[0] - b[0]), 2) + Math.pow((a[1] - b[1]), 2));
		
		return distance;
		
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
	
	
	public static int[] generateInitNode() {
		
		int[] initNodeCoords = new int[2];
		initNodeCoords[0] = generateRandomInt(1,maxGridSizeX-1);
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
	
	//assign a lable to each node
	public static void populateNodeIdentifierToy0() {
		
		nodeIdentifier.put(Integer.valueOf(0),"Init");
		nodeIdentifier.put(Integer.valueOf(1),"Norm");
		nodeIdentifier.put(Integer.valueOf(2),"Fork");
		nodeIdentifier.put(Integer.valueOf(3),"Norm");
		nodeIdentifier.put(Integer.valueOf(4),"Norm");
		nodeIdentifier.put(Integer.valueOf(5),"Final");
		nodeIdentifier.put(Integer.valueOf(6),"Final");
	}
	
	public static void populateNodeConnectionIdentifierToy0() {
		
		nodeConnectionIdentifier.put(new int[] {0,1},"Init-Norm0");
		nodeConnectionIdentifier.put(new int[] {1,2},"Norm0-Fork");
		nodeConnectionIdentifier.put(new int[] {2,3},"Fork-Norm1");
		nodeConnectionIdentifier.put(new int[] {2,4},"Fork-Norm2");
		nodeConnectionIdentifier.put(new int[] {3,5},"Norm1-Final1");
		nodeConnectionIdentifier.put(new int[] {4,6},"Norm2-Final2");
		
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
	
	public static void printSolution(int[] solution) {
		
		for(int i = 0; i < solution.length; i++) {
			System.out.print(solution[i]+",");
		}
		
		System.out.print(" - ");
		
	}
	
	public static void printArrayList(ArrayList<int[]> a) {
		
		for(int i = 0; i < a.size(); i++) {
			
			printSolution(a.get(i));
			
		}
		
		System.out.println("");
	}

}
