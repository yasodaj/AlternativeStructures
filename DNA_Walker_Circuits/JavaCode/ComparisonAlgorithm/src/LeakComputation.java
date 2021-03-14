import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;

public class LeakComputation {
	
	private static LinkedHashMap<Integer, String> nodeIdentifier = new LinkedHashMap<Integer, String>();
	private static LinkedHashMap<Integer, Integer> nodeBranchRelationship = new LinkedHashMap<Integer, Integer>();
	public static int numberOfNodes = 8;
	private static int dS = 3, dM = 5, dL = 8;
	
	public static void main(String args[]) {
		
//		populateNodeIdentifierToy0();
//		populateNodeBranchRelationshipToy0();
		populateNodeIdentifierToy1();
		populateNodeBranchRelationshipToy1();
		
//		int[] sol = {1,1,3,1,5,1,7,1,3,4,6,4,9,1,8,4};
		int[] sol = convertToIntArray("2142728434379639");
		int[] leaks = computeLeaks(sol);
		printSolution(leaks);
		
//		
//		int[] sol = {1,1,3,1,5,1,8,1,5,4,8,3,2,4}; //0,3,1
////		int[] sol = {1,1,3,1,5,1,5,4,7,3,5,6,8,5};//2,2,0
////		int[] sol = {8,1,8,4,6,4,6,7,3,4,8,8,3,2};//0,2,2
////		int[] sol = {7,1,7,4,5,4,5,7,3,4,7,8,2,6};//0,4,0
////		int[] sol = {7,1,7,4,4,4,4,6,2,4,6,7,2,1};//2,1,1
//		
//		int[][] coordinatePairs = convertToCoordinatePairs(sol);		
//		int shortLeaksCount = 0, mediumLeaksCount = 0, longLeaksCount = 0;
//		int totShortLeaksCount = 0, totMediumLeaksCount = 0, totLongLeaksCount = 0;
//		int nodeBranchId;
//		
//		
//		for(int i = 0; i < coordinatePairs.length; i++) {
//			
//			nodeBranchId = nodeBranchRelationship.get(i);
//			
//			if(nodeBranchId != 0) {
//				
//				for(int j = i+1; j < coordinatePairs.length; j++) {
//					
//					if(nodeBranchId != nodeBranchRelationship.get(j)) {
//						
////						System.out.println(i + " " + j);
//						
//						if(!(nodeIdentifier.get(i) == "Final" && nodeIdentifier.get(j) == "Final")) {
//							
//							if(isShortDistance(coordinatePairs[i], coordinatePairs[j])){
//								if(nodeIdentifier.get(i) == "Norm" && nodeIdentifier.get(j) == "Norm") {
//									shortLeaksCount = shortLeaksCount + 2;
//								}else {
//									shortLeaksCount++;
//								}
//								System.out.println("Short " + i+ " " +j);
//							}
//							
//							if(isMediumDistance(coordinatePairs[i], coordinatePairs[j])){
//								
//								if(nodeIdentifier.get(i) == "Norm" && nodeIdentifier.get(j) == "Norm") {
//									mediumLeaksCount = mediumLeaksCount + 2;
//								}else {
//									mediumLeaksCount++;
//								}
//								
//								System.out.println("Medium " + i+ " " +j);
//							}
//							
//							if(isLongDistance(coordinatePairs[i], coordinatePairs[j])){
//								
//								if(nodeIdentifier.get(i) == "Norm" && nodeIdentifier.get(j) == "Norm") {
//									longLeaksCount = longLeaksCount + 2;
//								}else {
//									longLeaksCount++;
//								}
//								
//				
//								System.out.println("Long " + i+ " " +j);
//							}
//							
//						}
//						
//					}
//					
//				}// end for j
//				
//				totShortLeaksCount = totShortLeaksCount + shortLeaksCount;
//				totMediumLeaksCount = totMediumLeaksCount + mediumLeaksCount;
//				totLongLeaksCount = totLongLeaksCount + longLeaksCount;
//				
//			}// end if node type
//			
//
//////			System.out.println("");
////			System.out.println(nodeType + " " + shortLeaksCount + " " + totShortLeaksCount);
////			System.out.println();
//			shortLeaksCount = 0;
//			mediumLeaksCount = 0;
//			longLeaksCount = 0;
//			
//		} //end of for for i
//		
//		System.out.println(totShortLeaksCount);
//		System.out.println(totMediumLeaksCount);
//		System.out.println(totLongLeaksCount);
//		
		
		
		
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
								System.out.println("Short " + i+ " " +j);
							}
							
							if(isMediumDistance(coordinatePairs[i], coordinatePairs[j])){
								
								if(nodeIdentifier.get(i) == "Norm" && nodeIdentifier.get(j) == "Norm") {
									mediumLeaksCount = mediumLeaksCount + 2;
								}else {
									mediumLeaksCount++;
								}
								
								System.out.println("Medium " + i+ " " +j);
							}
							
							if(isLongDistance(coordinatePairs[i], coordinatePairs[j])){
								
								if(nodeIdentifier.get(i) == "Norm" && nodeIdentifier.get(j) == "Norm") {
									longLeaksCount = longLeaksCount + 2;
								}else {
									longLeaksCount++;
								}
								
				
								System.out.println("Long " + i+ " " +j);
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
	
	public static void populateNodeIdentifierToy0() {
		
		nodeIdentifier.put(Integer.valueOf(0),"Init");
		nodeIdentifier.put(Integer.valueOf(1),"Norm0");
		nodeIdentifier.put(Integer.valueOf(2),"Fork");
		nodeIdentifier.put(Integer.valueOf(3),"Norm");
		nodeIdentifier.put(Integer.valueOf(4),"Norm");
		nodeIdentifier.put(Integer.valueOf(5),"Final");
		nodeIdentifier.put(Integer.valueOf(6),"Final");
		
	}
	
	public static void populateNodeBranchRelationshipToy0() {
		//nodes appear in the same order they are encoded in the int[] solution
		//if on both branches the code is 0, if only in one branch it's 1 or 2
		//branch 1 is the positive branch (N1,F1)
		//branch 2 is the negative branch (N2, F2)
		nodeBranchRelationship.put(Integer.valueOf(0),Integer.valueOf(0));
		nodeBranchRelationship.put(Integer.valueOf(1),Integer.valueOf(0));
		nodeBranchRelationship.put(Integer.valueOf(2),Integer.valueOf(0));
		nodeBranchRelationship.put(Integer.valueOf(3),Integer.valueOf(1));
		nodeBranchRelationship.put(Integer.valueOf(4),Integer.valueOf(2));
		nodeBranchRelationship.put(Integer.valueOf(5),Integer.valueOf(1));
		nodeBranchRelationship.put(Integer.valueOf(6),Integer.valueOf(2));
		
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
	
	public static int[] convertToIntArray(String coordinateString) {
		
//		String[] coordinateArray = coordinateString.split(",");
		String[] coordinateArray = coordinateString.split("");
		int[] solution = new int[numberOfNodes*2];
		
		for(int i = 0; i < coordinateArray.length; i++) {
			
			solution[i] = Integer.valueOf(coordinateArray[i]);
		}
		
		return solution;
	}
	
	public static String convertArrayToString(int[] array) {
		
		String stringArray = "";
		
		for(int i = 0; i < array.length; i++) {
			
			if(i != array.length - 1) {
				stringArray += Integer.toString(array[i]);
//				stringArray += ",";
			}else {
				stringArray += Integer.toString(array[i]);
			}
			
		}
		
//		stringArray += "]";
		return stringArray;
		
	}
	public static void printSolution(int[] solution) {
		
		for(int i = 0; i < solution.length; i++) {
			System.out.print(solution[i]+",");
		}
		
		System.out.println("");
		
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

}
