import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;

public class CountUniqueSolutionsMaster {
	
	public static HashSet<String> myUniqueSetMaster = new HashSet<String>();
	public static String mySASolutionsFile;
	
	public static String saveDirectoryParent = "E:\\SA\\Toy0_SA\\Iterations_5000\\RunStats\\";
//	public static String saveDirectory = "E:\\SA\\Toy0_SA\\Iterations_5000\\RunStats\\BestSolutionLogs\\";
	public static String saveDirectory = "E:\\SA\\Toy0_SA\\Iterations_5000\\RunStats\\SolutionLogs\\";

	public static void main(String args[]) {
		
		
		for(int i = 1; i <= 200; i++) {
			
//			mySASolutionsFile = saveDirectory + "BestSolutionsLogRun"+i+".txt";
			mySASolutionsFile = saveDirectory + "SolutionsLogRun"+i+".txt";
			identifyUniqueSolutions();

		}
		
	}
	
	
	public static void identifyUniqueSolutions() {
		
		try{
	        BufferedReader buf = new BufferedReader(new FileReader(mySASolutionsFile));
	        String lineJustFetched = null;
	        
	        while(true){
	            lineJustFetched = buf.readLine();
	            if(lineJustFetched == null){  
	                break; 
	            }else{
	            	
	            	myUniqueSetMaster.add(lineJustFetched);
	   
	                
	            }
	        }

	        buf.close();
	        
	        writeUniqueSet(myUniqueSetMaster);

	    }catch(Exception e){
	        e.printStackTrace();
	    }
		
		
	}
	
	public static void writeUniqueSet(HashSet<String> mySet) throws Exception {
		
		String logFilename = saveDirectoryParent + "UniqueSolutionsMaster.txt";
//		String logFilename = saveDirectoryParent + "UniqueBestSolutionsMaster.txt";
		
		BufferedWriter out = new BufferedWriter(new FileWriter(logFilename));
		
		try {
			
			Iterator<String> it = mySet.iterator();
			while(it.hasNext()) {
			    out.write(it.next());
			    out.newLine();
			}
			out.close();

		} catch (IOException ioe) {
			System.err.println("IOException: " + ioe.getMessage());
		}
		
	}
}
