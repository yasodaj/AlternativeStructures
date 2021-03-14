import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;

public class SolutionStats {

	public static void main(String args[]) throws IOException{
		
		
		for(int i = 21; i < 51; i++) {
			
			String solutionFile = "E:\\HillClimber\\HC_"+i+"\\SolutionsLog.txt";
			String bestSolutionFile = "E:\\HillClimber\\HC_"+i+"\\BestSolutionsLog.txt";
			
			int solutionCount = countLines(new File(solutionFile)) - 1;
			int bestSolutionCount = countLines(new File(bestSolutionFile)) - 1;
			
			writeToSoutionCountLog(i,solutionCount,bestSolutionCount);
			
			
		}
		
	}
	
	public static void writeToSoutionCountLog(int iteration, int solutionCount, int bestSolutionCount) {
		
		try
		{
		    String logFilename= "E:\\HillClimber\\SolutionCount.txt";
		    FileWriter fw = new FileWriter(logFilename,true); //the true will append the new data
		    fw.write(iteration + "\t" + 100 + "\t" + solutionCount + "\t" + bestSolutionCount + "\n");
		    fw.close();
		}
		catch(IOException ioe)
		{
		    System.err.println("IOException: " + ioe.getMessage());
		}
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
}
