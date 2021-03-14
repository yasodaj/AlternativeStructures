import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public class RMSDForSlidingWindow {
	
	private static int numberOfNodes = 6;	
	public static int minGridSize = 0;
	public static int maxGridSizeX = 14;
	public static int maxGridSizeY = 14;
	public static TreeMap<Integer, String> sortedSolutionMap;
	
	private static String saveDirectory = "E:\\LayoutComparison\\Toy\\RMSDWithRotation\\Sample4\\InitSolutionsClustering\\100\\";
	
	private static LinkedHashMap<Integer, String> nodeIdentifier = new LinkedHashMap<Integer, String>();
	private static ArrayList<int[]> solutions = new ArrayList<int[]>();
	
	public static void main(String args[]) throws IOException {
		
		populateNodeIdentifierToy();
		
		String solutionFile, rmsdOutput, rotation="";
		boolean isRotated;
		double rotationAngle;
		
		for(int a = 1; a <= 25; a++) {
			
			solutionFile = saveDirectory + "FilteredInitSolutions_100_" + a + "_of_25.txt";
			rmsdOutput = saveDirectory + "FilteredNewSolutionsOverlappingSections_PairwiseRMSD_ToySample_" + a + ".txt";
					
			//read in the solutions
//			populateSolutionsArrayList(solutionFile);
			populateTreeMap(solutionFile);
			
			double rmsd;
			//compute pair-wise distance
			
			
			int stop = sortedSolutionMap.firstKey() + sortedSolutionMap.size();
			
			for(int i = sortedSolutionMap.firstKey(); i < stop ; i++) {
//				System.out.println("i is " + i);
				for(int j = i + 1; j < stop; j++) {
					
					int[][] sc1 = convertToCoordinatePairs(convertToIntArray(sortedSolutionMap.get(i)));
					int[][] sc2 = convertToCoordinatePairs(convertToIntArray(sortedSolutionMap.get(j)));

					
					if(isCommonBranchParallel(sc1,sc2) == true) {
						
						rmsd = computePairWiseRMSD(convertToLinearArray(sc1),convertToLinearArray(sc2));
						isRotated = false;
						rotation = "not rotated";
					}
					else {
						
						isRotated = true;
						
						//swap the left and right if the left is not horizontal
						//one that rotates should be the one to move because in the computePairWiseDistance
						//it's always the second layout (sc2) that moves
						
						if(isCommonBranchHorizontal(sc1) == false && isCommonBranchVertical(sc1) == false) {
							
							int[][] temp = sc2.clone();
							sc2 = sc1.clone();
							sc1 = temp.clone();
							
						}
						
						//compute 1st RMSD
						double rmsd1 = computePairWiseRMSD(convertToLinearArray(sc1),convertToLinearArray(sc2));
						
						//compute 2nd RMSD
						double angleBetweenLines = calculateAngleBetweenLines(sc1,sc2);
						
						boolean isClockwise;
						if(angleBetweenLines > 0) { //xI < xF - +ve angle
							isClockwise = false;
						}else {
							isClockwise = true; //xI > xF -ve angle
						}
						
						//rotate the layout 
						if(isCommonBranchHorizontal(sc1) == true && isClockwise == true) { //first branch horizontal
							rotationAngle = 90-Math.toDegrees(Math.atan(Math.abs(angleBetweenLines)));
							sc2 = coordinatesForClockwise(sc2, rotationAngle);
//							printSolution(convertToLinearArray(sc2));
							
							rotation = "horizontal and clockwise rotation by " + rotationAngle + " degrees";
						
						}else if(isCommonBranchHorizontal(sc1) == true && isClockwise == false) { //first branch horizontal
						
							rotationAngle = 90-Math.toDegrees(Math.atan(angleBetweenLines));
							sc2 = coordinatesForAntiClockwise(sc2, rotationAngle);
//							printSolution(convertToLinearArray(sc2));
							rotation = "horizontal and anti-clockwise rotation by " + rotationAngle + " degrees";
							
						}else if(isCommonBranchVertical(sc1) == true && isClockwise == true) {
							
//							System.out.println("first branch vertical and clockwise");
							rotationAngle = Math.toDegrees(Math.atan(Math.abs(angleBetweenLines)));
							
							if(rotationAngle == 0) {
								rotationAngle = 90.0;
								sc2 = coordinatesForAntiClockwise(sc2, rotationAngle);
							}else {
								sc2 = coordinatesForAntiClockwise(sc2, rotationAngle);
							}
							
							
							
//							printSolution(convertToLinearArray(sc2));
							rotation = "vertical and clockwise rotation by " + rotationAngle + " degrees";
							
						}else if(isCommonBranchVertical(sc1) == true && isClockwise == false) {
							rotationAngle = 90-Math.toDegrees(angleBetweenLines);
//							System.out.println("first branch vertical and anti - clockwise");
							sc2 = coordinatesForClockwise(sc2, rotationAngle);
//							printSolution(convertToLinearArray(sc2));
							rotation = "vertical and anti-clockwise rotation by " + rotationAngle + " degrees";
						}
						
						double rmsd2 = computePairWiseRMSD(convertToLinearArray(sc1),convertToLinearArray(sc2));
						
						rmsd = Math.min(rmsd1, rmsd2);
//						
					}
					
//					writeToFile(solutions.get(i), solutions.get(j), rmsd, isRotated, rotation, rmsdOutput);
					writeToFile(i, j, rmsd, isRotated, rotation, rmsdOutput);
					
					
				}
				
			}
			
			
//			
//			
//			
//			
//			for(int i = 0; i < solutions.size(); i++) {
//				
//				for(int j = i + 1; j < solutions.size(); j++) {
//										
//					int[][] sc1 = convertToCoordinatePairs(solutions.get(i));
//					int[][] sc2 = convertToCoordinatePairs(solutions.get(j));
//
//					
//					if(isCommonBranchParallel(sc1,sc2) == true) {
//						
//						rmsd = computePairWiseRMSD(convertToLinearArray(sc1),convertToLinearArray(sc2));
//						isRotated = false;
//						rotation = "not rotated";
//					}
//					else {
//						
//						isRotated = true;
//						
//						//swap the left and right if the left is not horizontal
//						//one that rotates should be the one to move because in the computePairWiseDistance
//						//it's always the second layout (sc2) that moves
//						
//						if(isCommonBranchHorizontal(sc1) == false && isCommonBranchVertical(sc1) == false) {
//							
//							int[][] temp = sc2.clone();
//							sc2 = sc1.clone();
//							sc1 = temp.clone();
//							
//						}
//						
//						//compute 1st RMSD
//						double rmsd1 = computePairWiseRMSD(convertToLinearArray(sc1),convertToLinearArray(sc2));
//						
//						//compute 2nd RMSD
//						double angleBetweenLines = calculateAngleBetweenLines(sc1,sc2);
//						
//						boolean isClockwise;
//						if(angleBetweenLines > 0) { //xI < xF - +ve angle
//							isClockwise = false;
//						}else {
//							isClockwise = true; //xI > xF -ve angle
//						}
//						
//						//rotate the layout 
//						if(isCommonBranchHorizontal(sc1) == true && isClockwise == true) { //first branch horizontal
//							rotationAngle = 90-Math.toDegrees(Math.atan(Math.abs(angleBetweenLines)));
//							sc2 = coordinatesForClockwise(sc2, rotationAngle);
////							printSolution(convertToLinearArray(sc2));
//							
//							rotation = "horizontal and clockwise rotation by " + rotationAngle + " degrees";
//						
//						}else if(isCommonBranchHorizontal(sc1) == true && isClockwise == false) { //first branch horizontal
//						
//							rotationAngle = 90-Math.toDegrees(Math.atan(angleBetweenLines));
//							sc2 = coordinatesForAntiClockwise(sc2, rotationAngle);
////							printSolution(convertToLinearArray(sc2));
//							rotation = "horizontal and anti-clockwise rotation by " + rotationAngle + " degrees";
//							
//						}else if(isCommonBranchVertical(sc1) == true && isClockwise == true) {
//							
////							System.out.println("first branch vertical and clockwise");
//							rotationAngle = Math.toDegrees(Math.atan(Math.abs(angleBetweenLines)));
//							
//							if(rotationAngle == 0) {
//								rotationAngle = 90.0;
//								sc2 = coordinatesForAntiClockwise(sc2, rotationAngle);
//							}else {
//								sc2 = coordinatesForAntiClockwise(sc2, rotationAngle);
//							}
//							
//							
//							
////							printSolution(convertToLinearArray(sc2));
//							rotation = "vertical and clockwise rotation by " + rotationAngle + " degrees";
//							
//						}else if(isCommonBranchVertical(sc1) == true && isClockwise == false) {
//							rotationAngle = 90-Math.toDegrees(angleBetweenLines);
////							System.out.println("first branch vertical and anti - clockwise");
//							sc2 = coordinatesForClockwise(sc2, rotationAngle);
////							printSolution(convertToLinearArray(sc2));
//							rotation = "vertical and anti-clockwise rotation by " + rotationAngle + " degrees";
//						}
//						
//						double rmsd2 = computePairWiseRMSD(convertToLinearArray(sc1),convertToLinearArray(sc2));
//						
//						rmsd = Math.min(rmsd1, rmsd2);
////						
//					}
//					
//					writeToFile(solutions.get(i), solutions.get(j), rmsd, isRotated, rotation, rmsdOutput);
////					writeToFile(i, j, rmsd, isRotated, rotation, rmsdOutput);
//					
//				}
//				
//			}
			sortedSolutionMap.clear();
			System.out.println(a + " completed");
			
		}
//		
//		int[] s1 = {13,1,11,1,8,1,11,4,6,2,11,7};// 6,1,6,4,3,4,8,4,1,2,10,5
//		//     3,1,3,3,5,5,0,5,6,7,-1,3    3,1,5,2,7,1,5,5,10,1,3,6
//		//8,1,8,4,6,5,10,5,3,5,12,7
//		// 10,1,8,2,5,2,8,5,3,1,11,5
//		//13,1,11,2,11,4,8,2,13,5,5,2
//		//13,1,11,1,8,1,11,4,6,2,11,7
//
//		int[] s2 = {10,1,8,2,5,2,8,5,3,1,11,5};//   4,1,6,3,4,5,9,3,1,5,10,1   9,1,9,4,11,6,6,4,12,8,4,2  7,1,7,4,5,6,9,5,3,7,11,7    
//		//7,1,7,3,9,54,3,12,5,2,3
//		//4,1,6,3,4,5,9,3,1,5,10,1
		
		
		
	}
	
	public static void populateTreeMap(String fileName) throws IOException{
		

		File file = new File(fileName);
		HashMap<Integer, String> solutionMap = new HashMap<>();

		BufferedReader br = new BufferedReader(new FileReader(file));
		int lineNo = 0;
		String st;
		while ((st = br.readLine()) != null) {

			if(lineNo > 0) {
				
				String[] tempStr = st.split("\\t");
				solutionMap.put(Integer.valueOf(tempStr[8]), tempStr[1]);
			}
			
			lineNo += 1;
		}
		
		sortedSolutionMap = new TreeMap<>(solutionMap);
		
		
	}
	
	public static void populateSolutionsArrayList(String solutionFileName) {
		
		//read a solution
		
		try{
	        BufferedReader buf = new BufferedReader(new FileReader(solutionFileName));
	 
	        String lineJustFetched = null;

	        //System.out.println("file read");
	        while(true){
	            lineJustFetched = buf.readLine();
	            if(lineJustFetched == null){  
	                break; 
	            }else{
	            	
	            	solutions.add(convertToIntArray(lineJustFetched));
		            	
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
		int[] solution = new int[numberOfNodes * 2];
		
		for(int i = 0; i < coordinateArray.length; i++) {
			
			switch(coordinateArray[i]) {
			case "a": solution[i] = 10; break;
			case "b": solution[i] = 11; break;
			case "c": solution[i] = 12; break;
			case "d": solution[i] = 13; break;
			case "e": solution[i] = 14; break;
			case "f": solution[i] = 15; break;
			case "g": solution[i] = 16; break;
			case "h": solution[i] = 17; break;
			case "i": solution[i] = 18; break;
			case "j": solution[i] = 19; break;
			case "k": solution[i] = 20; break;
			case "l": solution[i] = 21; break;
			default: solution[i] = Integer.valueOf(coordinateArray[i]); break;
			}
			
			
		}
		
		return solution;
	}
	
	public static int[][] rotateLayoutByForkNode(int[][] solCoordinatePairs){
		
		int forkNodeId = getForkNodeId();
		int rotationAxis = solCoordinatePairs[forkNodeId][0]; //x coordinate of the fork node
		int xDifference = 0;
		for(int i = 0; i < solCoordinatePairs.length; i++) {
			
			if(i != forkNodeId) {
				xDifference = rotationAxis - solCoordinatePairs[i][0];
				solCoordinatePairs[i][0] = xDifference + rotationAxis;
			}
		}
		
		return solCoordinatePairs;
	}
	
	public static int[][] coordinatesForAntiClockwise(int[][] solCoordinatePairs, double angle) {
		
		int translationX = solCoordinatePairs[0][0]; //x 
		int translationY = solCoordinatePairs[0][1]; //y
		
		//move to origin
		solCoordinatePairs = translateToOrigin(solCoordinatePairs);
		
//		System.out.println("Anti clockwise" + angle);
		
		double sin = Math.sin(Math.toRadians(angle));
		double cos = Math.cos(Math.toRadians(angle));
		
		int currentX, currentY, newX, newY;
		
		for(int i = 0; i < solCoordinatePairs.length; i++) {
			
			if(i != 0) {
				currentX = solCoordinatePairs[i][0];
				currentY = solCoordinatePairs[i][1];
				
//				System.out.println("i " + i + "  x " + (currentX*cos - currentY*sin)
//							+ "  y " + (currentX*sin + currentY*cos)
//						);
				
				newX = (int)Math.round(currentX*cos - currentY*sin);//x
				newY = (int)Math.round(currentX*sin + currentY*cos);//y
				
//				System.out.println("i " + i + "  rx " + newX+ "  ry " + newY
//					);
//				
				solCoordinatePairs[i][0] = translationX + newX;
				solCoordinatePairs[i][1] = translationY + newY;
			}else {
				
				solCoordinatePairs[i][0] = translationX;
				solCoordinatePairs[i][1] = translationY;
			}
		}
		
		return solCoordinatePairs;
	}
	
	public static int[][] coordinatesForClockwise(int[][] solCoordinatePairs, double angle) {
		
//		System.out.println("inside coordinatesForClockwise");
//		System.out.println("angle " + angle);
		
		int translationX = solCoordinatePairs[0][0]; //x 
		int translationY = solCoordinatePairs[0][1]; //y
		
		//move to origin
		solCoordinatePairs = translateToOrigin(solCoordinatePairs);
		double sin = Math.sin(Math.toRadians(angle));
		double cos = Math.cos(Math.toRadians(angle));
		
		int currentX, currentY, newX, newY;
		
		for(int i = 0; i < solCoordinatePairs.length; i++) {
			
			if(i != 0) {
				currentX = solCoordinatePairs[i][0];
				currentY = solCoordinatePairs[i][1];
				newX = (int)Math.round(currentX*cos + currentY*sin);//x
				newY = (int)Math.round(currentX*sin*(-1) + currentY*cos);//y
				
//				System.out.println(i + "x "  + newX + "  y " + newY);
				
				solCoordinatePairs[i][0] = translationX + newX;
				solCoordinatePairs[i][1] = translationY + newY;
			}else {
				
				solCoordinatePairs[i][0] = translationX;
				solCoordinatePairs[i][1] = translationY;
			}
		}
		
		return solCoordinatePairs;
	}
	
	public static int getForkNodeId() {
		
		int forkNodeID = 0;
		
		for (Map.Entry<Integer, String> entry : nodeIdentifier.entrySet()) {
		    
		    if(entry.getValue() == "Fork") {
		    	forkNodeID = entry.getKey();
		    	return forkNodeID;
		    }
		    // now work with key and value...
		}
		
		return forkNodeID;
	}
	
	public static int[][] traslateLayoutToInitPosition(int[][] solCoordinatePairs) {
		//this function will move the layout to the starting position of the algorithm
		
		//computes the distance from init node to (1,1)
		int xMoveFactor = 1 - solCoordinatePairs[0][0];
		int currentXValue;
		
		for(int i = 0; i < solCoordinatePairs.length; i++) {
			currentXValue = solCoordinatePairs[i][0];
			
			solCoordinatePairs[i][0] = currentXValue + xMoveFactor;
			
		}
		
		return solCoordinatePairs;
				
	}
	
	public static double computePairWiseRMSD(int[] solOne,int[] solTwo) {
		
		int[][] solOneCoordinatePairs = convertToCoordinatePairs(solOne);
		int[][] solTwoCoordinatePairs = convertToCoordinatePairs(solTwo);
		
		//shift layout two to (1,1)
		solTwoCoordinatePairs = traslateLayoutToInitPosition(solTwoCoordinatePairs);
		
		double minRMSD=0, currentRMSD;
		int validLayoutCount = 0;
		
		//iterate through all the available init positions x = 1 to (maxX-1)
		for(int i = 1; i < maxGridSizeX; i++) {
			
			if(isValidLayoutTranslation(solTwoCoordinatePairs) == true) {
				validLayoutCount ++;
				currentRMSD = computeRMSD(solOneCoordinatePairs, solTwoCoordinatePairs);
//				System.out.println("Without rotation " + solTwoCoordinatePairs[0][0] + " " + currentRMSD);
//				System.out.println("current RMSD " + validLayoutCount + "  " + currentRMSD);
//				printSolution(convertToLinearArray(solTwoCoordinatePairs));
//				System.out.println();
				if(validLayoutCount == 1) {
					minRMSD = currentRMSD;
				} else {
					if(currentRMSD < minRMSD) {
						minRMSD = currentRMSD;
					}
				}
				
				
				
			}else {
				
			}
			
			solTwoCoordinatePairs = translateLayoutByPositiveUnitX(solTwoCoordinatePairs);
			
		}
		
		
		//rotation of the second layout
		int[][] solTwoCoordinatePairsR= rotateLayoutByForkNode(convertToCoordinatePairs(solTwo));
		solTwoCoordinatePairsR = traslateLayoutToInitPosition(solTwoCoordinatePairsR);
		
//		validLayoutCount = 0;
		
		//iterate through all the available init positions x = 1 to (maxX-1)
		for(int i = 1; i < maxGridSizeX; i++) {
			
			if(isValidLayoutTranslation(solTwoCoordinatePairsR) == true) {
				validLayoutCount ++;
				currentRMSD = computeRMSD(solOneCoordinatePairs, solTwoCoordinatePairsR);
//				System.out.println("With rotation " + currentRMSD);
//				System.out.println("current RMSD " + validLayoutCount + "  " + currentRMSD);
//				printSolution(convertToLinearArray(solTwoCoordinatePairs));
//				System.out.println();
				if(currentRMSD < minRMSD) {
					minRMSD = currentRMSD;
				}
				
//				if(validLayoutCount == 1) {
//					minRMSD = currentRMSD;
//				} else {
//					if(currentRMSD < minRMSD) {
//						minRMSD = currentRMSD;
//					}
//				}
//				
				
				
			}else {
				
			}
			
			solTwoCoordinatePairsR = translateLayoutByPositiveUnitX(solTwoCoordinatePairsR);
			
		}
		
		return minRMSD;
		
	}
	
	public static int[][] translateLayoutByPositiveUnitX(int[][] solCoordinatePairs) {
		
		int currentXValue;
		
		for(int i = 0; i < solCoordinatePairs.length; i++) {
			currentXValue = solCoordinatePairs[i][0];
		
			solCoordinatePairs[i][0] = currentXValue + 1;

		}
		
		return solCoordinatePairs;
	}
	
	public static int[][] translateToOrigin(int[][] solCoordinatePairs) {
		
		int translationX = solCoordinatePairs[0][0];
		int translationY = solCoordinatePairs[0][1];
		
		for(int i = 0; i < solCoordinatePairs.length; i++) {
			
			solCoordinatePairs[i][0] = solCoordinatePairs[i][0] - translationX;
			solCoordinatePairs[i][1] = solCoordinatePairs[i][1] - translationY;
			
		}
		
		return solCoordinatePairs;
	}
	
	public static boolean isCommonBranchHorizontal(int[][] solCoordinatePairs) {
		//x shoudl be the same
		boolean isHorizontal = false;
		
		if(solCoordinatePairs[0][0] == solCoordinatePairs[1][0]) {
			
			isHorizontal = true;
		}
		
		return isHorizontal;
		
	}
	
	public static boolean isCommonBranchVertical(int[][] solCoordinatePairs) {
		//y should be the same
		boolean isVertical = false;
		
		if(solCoordinatePairs[0][1] == solCoordinatePairs[1][1]) {
			
			isVertical = true;
		}
		
		return isVertical;
		
	}
	
	
	//checks whether the common branch is vertical/ horizontal in both
	public static boolean isCommonBranchSameShape(int[][] solOneCoordinatePairs, int[][] solTwoCoordinatePairs) {
		
		boolean isSimilar = false;
		//if horizontal both y values should be equal
		//if vertical both x values should be equal
		
		
		if((solOneCoordinatePairs[0][0] == solOneCoordinatePairs[1][0] && 
				solTwoCoordinatePairs[0][0] == solTwoCoordinatePairs[1][0]) ||
				(solOneCoordinatePairs[0][1] == solOneCoordinatePairs[1][1] && 
						solTwoCoordinatePairs[0][1] == solTwoCoordinatePairs[1][1])) {
			
			isSimilar = true;
		}
		
		
		return isSimilar;
	}
	
	public static double calculateLineSlope(int[][] solCoordinatePairs) {
		
		double slope = 0.0;
		//checking whether denominator is zero
		if(solCoordinatePairs[1][0] - solCoordinatePairs[0][0] == 0) {
			
			slope = 0;
		}else {
			
			slope = (double) (solCoordinatePairs[1][1] - solCoordinatePairs[0][1]) / (solCoordinatePairs[1][0] - solCoordinatePairs[0][0]);
			
		}
		
		//y2-y1/x2-x1
		
		
		return slope;
	}
	
	
	public static boolean isCommonBranchParallel(int[][] solOneCoordinatePairs, int[][] solTwoCoordinatePairs) {
		
		boolean isParallel = false;
		
		double solOneSlope = calculateLineSlope(solOneCoordinatePairs);
		double solTwoSlope = calculateLineSlope(solTwoCoordinatePairs);
		
		if(solOneSlope == 0 && solTwoSlope == 0 && isCommonBranchSameShape(solOneCoordinatePairs,solTwoCoordinatePairs) == true) {
			
			isParallel = true;
		}else if((solOneSlope != 0 || solTwoSlope != 0) && (solOneSlope == solTwoSlope)) {
			isParallel = true;
		}
		
		return isParallel;
	}
	
	//consider's only 
	public static void decideRotationFactor(int[][] solOneCoordinatePairs, int[][] solTwoCoordinatePairs) {
		
//		solOneX
//		solOneY
//		solTwoX
//		solOneY
		
		
	}
	
	public static boolean isValidLayoutTranslation(int[][] solCoordinatePairs) {
		
		boolean isValidTranslation = true;
	
		int maxXValue = maxGridSizeX - 1;
		for(int i = 0; i < solCoordinatePairs.length; i++) {
			
			if(solCoordinatePairs[i][0] > maxXValue || solCoordinatePairs[i][0] < 1) {
				isValidTranslation = false;
				break;
			}
			
		}
		
		return isValidTranslation;
	}
	
	public static double calculateAngleBetweenLines(int[][] solOneCoordinatePairs, int[][] solTwoCoordinatePairs) {
		
		double angle = 0.0;
		
		double slopeOne = calculateLineSlope(solOneCoordinatePairs);
		double slopeTwo = calculateLineSlope(solTwoCoordinatePairs);
//		System.out.println(slopeOne);
//		System.out.println(slopeTwo);
//		angle = Math.abs((slopeTwo - slopeOne)/(1 - slopeOne * slopeTwo));
		angle = (slopeTwo - slopeOne)/(1 - (slopeOne * slopeTwo));
		
//		Math.toRadians(angle);
		
		return angle;
	}
	
	public static double computeRMSD(int[][] solOneCoordinatePairs, int[][] solTwoCoordinatePairs) {
		
		double rmsdValue = 0.0;
		
		if(solOneCoordinatePairs.length == solTwoCoordinatePairs.length) {
					
			double nodeDifference = 0.0;
			double temp = 0.0;
			
			for(int i = 0; i < solOneCoordinatePairs.length; i++) {
				
				temp = Math.pow(Math.abs(solOneCoordinatePairs[i][0] - solTwoCoordinatePairs[i][0]), 2) + 
						Math.pow(Math.abs(solOneCoordinatePairs[i][1] - solTwoCoordinatePairs[i][1]), 2);
				
//				System.out.println(i + " x1 " + solOneCoordinatePairs[i][0]);
//				System.out.println(i + " x2 " + solTwoCoordinatePairs[i][0]);
//				System.out.println(i + " y1 " + solOneCoordinatePairs[i][1]);
//				System.out.println(i + " y2 " + solTwoCoordinatePairs[i][1]);
//				System.out.println(i + " " + temp);
				nodeDifference = nodeDifference + temp;
			}
			
			rmsdValue = Math.sqrt(nodeDifference/solOneCoordinatePairs.length);
			
			
		}else {
			System.out.println("Solutions are not from the same layout");
		}
		
		return rmsdValue;
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
	
	public static void printSolution(int[] solution) {
		
		for(int i = 0; i < solution.length; i++) {
			System.out.print(solution[i]+",");
		}
		
//		System.out.println("");
		
	}

	
	public static void populateNodeIdentifierToy() {
		
		nodeIdentifier.put(Integer.valueOf(0),"Init");
		nodeIdentifier.put(Integer.valueOf(1),"Fork");
		nodeIdentifier.put(Integer.valueOf(2),"Norm");
		nodeIdentifier.put(Integer.valueOf(3),"Norm");
		nodeIdentifier.put(Integer.valueOf(4),"Final");
		nodeIdentifier.put(Integer.valueOf(5),"Final");
	}
	
//	public static void writeToFile(int[] solOne, int[] solTwo, double rmsd, boolean isRotated , String rotation, String fileName) {
		public static void writeToFile(int solOne, int solTwo, double rmsd, boolean isRotated , String rotation, String fileName) {
		
		DecimalFormat df = new DecimalFormat("#.####");
		
		try {
			
			FileWriter fw = new FileWriter(fileName, true); // the true will append the new data
//			fw.write(convertArrayToString(solOne) + "\t" + convertArrayToString(solTwo) 
			fw.write(String.valueOf(solOne) + "\t" + String.valueOf(solTwo)
			+ "\t" + df.format(rmsd) + "\t" + isRotated + "\t" + rotation + "\n");
			fw.close();
		} catch (IOException ioe) {
			System.err.println("IOException: " + ioe.getMessage());
		}
		
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
}
