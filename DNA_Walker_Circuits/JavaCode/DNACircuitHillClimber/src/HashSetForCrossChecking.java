import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;

public class HashSetForCrossChecking {
	
	public static String fn = "E:\\TestDavidSolutions\\NewSolutions_toy\\Solutions_toy.txt";
	// txt file containing two tab separated columns 1,1,3,2,5,2,2,4,6,4,2,6	1.6
	
	public static String mySolutions = "E:\\SA\\Run2\\My_Unique_Solutions_Run2.txt";
	//txt file containing only the solution String 6,1,5,3,3,3,6,5,1,3,5,7
	
	public static String saveDirectory = "E:\\SA\\Run2\\";
	
	public static void main(String args[]) {
		
		HashSet<String> davidSolutions = new HashSet<String>();
		
		addElementsToHashSet(davidSolutions, fn);
		
		checkIfExists(davidSolutions, mySolutions);
		
	}
	
	public static void checkIfExists(HashSet<String> hashSet, String solutionFile) {
		
		try{
	        BufferedReader buf = new BufferedReader(new FileReader(solutionFile));
	        String lineJustFetched = null;
	        //String[] wordsArray;
	        boolean doesContain;
	        
	        while(true){
	            lineJustFetched = buf.readLine();
	            if(lineJustFetched == null){  
	                break; 
	            }else{
	            	
	                //wordsArray = lineJustFetched.split("\n");
	                doesContain = hashSet.contains(lineJustFetched);
	                
	                writeToFile(lineJustFetched,doesContain);
	                
	            }
	        }

	        buf.close();

	    }catch(Exception e){
	        e.printStackTrace();
	    }
	}
	
	public static void writeToFile(String sol, boolean doesContain) {
		
		try {
			String logFilename = saveDirectory + "MyUniqueSolutionCheck_Run2.txt";
			FileWriter fw = new FileWriter(logFilename, true); // the true will append the new data
			fw.write(sol + "\t" + String.valueOf(doesContain) + "\n");
			fw.close();
		} catch (IOException ioe) {
			System.err.println("IOException: " + ioe.getMessage());
		}
	}
	
	public static void addElementsToHashSet(HashSet<String> hashSet, String fileName) {
		
		try{
	        BufferedReader buf = new BufferedReader(new FileReader(fileName));
	        String lineJustFetched = null;
	        String[] wordsArray;
	        while(true){
	            lineJustFetched = buf.readLine();
	            if(lineJustFetched == null){  
	                break; 
	            }else{
	            	
	                wordsArray = lineJustFetched.split("\t");
	                hashSet.add(wordsArray[0]);
	                   
	            }
	        }

	        buf.close();

	    }catch(Exception e){
	        e.printStackTrace();
	    }
		
	}
}
