import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

public class Test {
	
	private static LinkedHashMap<int[], String> nodeConnectionIdentifier = new LinkedHashMap<int[], String>();
	static int numberOfNodes = 8;
	static int maxGridSizeX = 10;
	static int maxGridSizeY = 10;
	public static void main(String args[]) {
		
		//coordinate String for marcie/array - I, F, N11, N12, N21, N22, F1, F2
				nodeConnectionIdentifier.put(new int[] {0,1},"Init-Fork");
				nodeConnectionIdentifier.put(new int[] {1,2},"Fork-Norm11");
				nodeConnectionIdentifier.put(new int[] {1,4},"Fork-Norm21");
				nodeConnectionIdentifier.put(new int[] {2,3},"Norm11-Norm12");
				nodeConnectionIdentifier.put(new int[] {4,5},"Norm21-Norm22");
				nodeConnectionIdentifier.put(new int[] {3,6},"Norm12-Final1");
				nodeConnectionIdentifier.put(new int[] {5,7},"Norm22-Final2");
				
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

						System.out.println("My Id is "+ currentConnection[i] + " and my i is " + i);
//						System.out.println("current i " + i);
						
						if(!populatedNodeIDs.contains(currentConnection[i])) {
							
							//checks if node is Init
							if(i == 0 && currentConnection[i] == 0) {
								System.out.println("My Id is "+ currentConnection[i] + " and i am init");
								coordinates = generateInitNode();
				        		solution[currentConnection[i]*2] = coordinates[0];
				        		solution[currentConnection[i]*2+1] = coordinates[1];
				        		usedNodes.add(coordinates);
				        		populatedNodeIDs.add(currentConnection[i]);
								
//							}else if (i == 0) {
//								System.out.println("My Id is "+ currentConnection[i] + " and i am in i is 0");
//								//find the ancestor of a node at i = 0 - need to go back and 
//								//check the wholle list 
//								int[] tempAncestorNodeId;
//								for (Map.Entry<int[], String> ancestry : nodeConnectionIdentifier.entrySet()) {
//									
//									tempAncestorNodeId = ancestry.getKey();
//									
//									if(tempAncestorNodeId[1] == currentConnection[i]) {
//										ancestorNodeId = tempAncestorNodeId[1];
//										break;
//									}
//									
//								}
//								
//								
//								ancestorCoordinates[0] = solution[ancestorNodeId * 2];
//								ancestorCoordinates[1] = solution[ancestorNodeId * 2 + 1];
//								
//								possibleMoveLocations = generatePossibleLocations(ancestorCoordinates);
//			        			
//			        			possibleMoveLocations = removeUsedNodes(usedNodes, possibleMoveLocations);
//			        			
//			        			possibleMoveLocations = removeTooCloseNodesToUsedNodes(usedNodes, possibleMoveLocations);
//			        						        			
//			        			luckyIndex = generateRandomInt(0,possibleMoveLocations.size()-1);
//			        			coordinates = possibleMoveLocations.get(luckyIndex);
//			        			
//			        			solution[currentConnection[i]*2] = coordinates[0];
//				        		solution[currentConnection[i]*2+1] = coordinates[1];
//				        		usedNodes.add(coordinates);
//				        		populatedNodeIDs.add(currentConnection[i]);
//				        		possibleMoveLocations.clear();
//								
//								
							}else if(i == 1) {
								
								System.out.println("My Id is "+ currentConnection[i] + " and i am in i is 1");
								
								//get ancestor - ancestor of a node at i = 1 is the node at i = 0 
								ancestorNodeId = currentConnection[0];
//								System.out.println("Node " + currentConnection[1] + "ancestor is " + currentConnection[0]);
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

					printSolution(solution);
				}
				
				printSolution(solution);
		
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
	
	public static double distanceBetweenPoints(int[]a, int[]b) {
		
		double distance = 0.0;
		
		distance = Math.sqrt(Math.pow((a[0] - b[0]), 2) + Math.pow((a[1] - b[1]), 2));
		
		return distance;
		
	}
	
	public static void printSolution(int[] solution) {
		
		for(int i = 0; i < solution.length; i++) {
			System.out.print(solution[i]+",");
		}
		
		System.out.println("");
		
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

}
