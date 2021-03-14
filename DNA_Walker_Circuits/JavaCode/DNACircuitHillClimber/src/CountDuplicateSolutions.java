import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Iterator;

public class CountDuplicateSolutions {
	
	public static HashSet<String> myUniqueSet = new HashSet<String>();
	public static String mySASolutionsFile;
	//solutions only in the format 3,1,4,3,3,5,5,5,1,5,6,7
	
	public static String saveDirectory = "E:\\SA\\Toy0_SA\\Iterations_5000\\RunStats\\BestSolutionLogs\\";
//	public static String saveDirectory = "E:\\SA\\Toy0_SA\\Iterations_5000\\RunStats\\SolutionLogs\\";
	
	
	
	public static void main(String args[]) {	
		
		////////////////////////////////////////////////////
		
//		run the R code first E:\SA\processSolutionLog.R
		
		///////////////////////////////////////////////////
		
		for(int i = 1; i <= 200; i++) {
			
			mySASolutionsFile = saveDirectory + "BestSolutionsLogRun"+i+".txt";
//			mySASolutionsFile = saveDirectory + "SolutionsLogRun"+i+".txt";
			identifyDuplicates();
			writeToDuplicateSolutionCount(i);
//			countDuplicates(i);
					
			myUniqueSet.clear();
		}
		
		
		
		
		
		
		
	}
	
	public static void countDuplicates(int i) {
		
		for(String sol : myUniqueSet) {
			
			int count = 0;
			
			try {
				BufferedReader in = new BufferedReader(new FileReader(mySASolutionsFile));
				String readLine = "";
				
				
				while((readLine = in.readLine()) != null) {
					
					if(readLine.equals(sol)) count++;
				
				}
			}catch(Exception e) {
				e.printStackTrace();
			}
			
			writeToFile(sol,count,i);
		}
		
		
		
	}
	
	public static void writeToFile(String sol, int count, int i) {
		
		try {
			String logFilename = saveDirectory + "BestSolutionsLogRun" + i + "_Duplicates.txt";
//			String logFilename = saveDirectory + "SolutionsLogRun" + i + "_Duplicates.txt";
			FileWriter fw = new FileWriter(logFilename, true); // the true will append the new data
			fw.write(sol + "\t" + "\t" + String.valueOf(count) + "\n");
			fw.close();
		} catch (IOException ioe) {
			System.err.println("IOException: " + ioe.getMessage());
		}
	}
	
	public static void identifyDuplicates() {
		
		try{
	        BufferedReader buf = new BufferedReader(new FileReader(mySASolutionsFile));
	        String lineJustFetched = null;
	        
	        while(true){
	            lineJustFetched = buf.readLine();
	            if(lineJustFetched == null){  
	                break; 
	            }else{
	            	
	            	myUniqueSet.add(lineJustFetched);
	   
	                
	            }
	        }

	        buf.close();

	    }catch(Exception e){
	        e.printStackTrace();
	    }
		
		//writeToFile();
	}
	

	public static void writeToDuplicateSolutionCount(int i) {
		
		String run = "Run" + i;
		
		try {
			String logFilename = saveDirectory + "BestSolutionsLogUniqueSolutionCount.txt";
//			String logFilename = saveDirectory + "SolutionsLogUniqueSolutionCount.txt";
			FileWriter fw = new FileWriter(logFilename, true); // the true will append the new data
			fw.write(run + "\t" + String.valueOf(myUniqueSet.size()) + "\n");
			fw.close();
		} catch (IOException ioe) {
			System.err.println("IOException: " + ioe.getMessage());
		}
		
		
	}
}
