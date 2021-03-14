import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Temp {

	public static void main(String args[]) throws IOException {
		
		ArrayList<Integer> t =  new ArrayList<Integer>();
		t.add(1);
		t.add(2);
		t.add(3);
//		int x = 2;
//		t.remove(Integer.valueOf(x));
		System.out.println(t.contains(2));
		
//		for(int i = 0; i<11;i++) System.out.println(generateRandomInt(1,3));
		
	}
	
	public static int countLines(File aFile) throws IOException {
	    LineNumberReader reader = null;
	    try {
	        reader = new LineNumberReader(new FileReader(aFile));
	        while ((reader.readLine()) != null);
	        return reader.getLineNumber();
	    } catch (Exception ex) {
	        return -1;
	    } finally { 
	        if(reader != null) 
	            reader.close();
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
	
	public static void printArrayListInt(ArrayList<Integer> a) {
		
		for(int i = 0; i < a.size(); i++) {
			
			System.out.print(a.get(i));
			System.out.print(",");
			
		}
		
		System.out.println("");
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
				
				//svs = isValidSolution(solutionClone);
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
	
	public static ArrayList<int[]> generatePossibleInitLocations(int[] location) {
		
		int locX = location[0];
		
		int index = 0;
		
		int[][] newLocations = new int[2][2];
		
		for(int i = 0; i < 2; i++) {
			
			if(i == 0) {
				newLocations[i][0] = locX - 2;
				newLocations[i][1] = 1;
			}else {
				newLocations[i][0] = locX + 2;
				newLocations[i][1] = 1;
			}
			
		}
		
		ArrayList<int[]> filteredCoordinates = new ArrayList<int[]>();
		
		for(int i=0; i<newLocations.length; i++) {
			
			if(newLocations[i][0] >= 1 && newLocations[i][0] < 7) {
					
					filteredCoordinates.add(newLocations[i]);
				
			}
			
		}
		
		System.out.println("array size:"+filteredCoordinates.size());
		return filteredCoordinates;
		
		
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
	
	public static void printArrayList(ArrayList<int[]> a) {
		
		for(int i = 0; i < a.size(); i++) {
			
			printSolution(a.get(i));
			
		}
		
		System.out.println("");
	}
	
	public static void printSolution(int[] solution) {
		
		for(int i = 0; i < solution.length; i++) {
			System.out.print(solution[i]+",");
		}
		System.out.println("");
		
	}
}
