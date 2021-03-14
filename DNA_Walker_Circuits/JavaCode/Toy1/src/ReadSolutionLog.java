import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ReadSolutionLog {
	
	public static void main(String args[]) {
		
		File folder = new File("E:\\TestDavidSolutions\\Toy1\\Test3\\OutputFiles");
		File[] listOfFiles = folder.listFiles();
		String modelNo;
		
		for(int i = 0; i < listOfFiles.length; i++) {
//			for(int i = 0; i < 1; i++) {
			
			modelNo = listOfFiles[i].toString().substring(54, listOfFiles[i].toString().length()-4);
			
			try{
		        BufferedReader buf = new BufferedReader(new FileReader(listOfFiles[i]));

		        String lineJustFetched = null;
		        
		        while(true){
		            lineJustFetched = buf.readLine();
		            if(lineJustFetched == null){  
		                break; 
		            }else{
		            	
		            	if(lineJustFetched.contains("Exception: Analysis failed")) {
		            		
		            		writeToFile(modelNo, lineJustFetched);
		            	}
		            	
		            }
		        }
		         

		        buf.close();

		    }catch(Exception e){
		        e.printStackTrace();
		
			
		}
		
		
    }
	}
	
	public static void writeToFile(String modelNo, String error) {
		
		try {
			String logFilename = "E:\\TestDavidSolutions\\Toy1\\Test3\\ProcessMarcieOutputError.txt";
			FileWriter fw = new FileWriter(logFilename, true); // the true will append the new data
			fw.write( modelNo + "\t" + error + "\n");
			fw.close();
		} catch (IOException ioe) {
			System.err.println("IOException: " + ioe.getMessage());
		}
		
	}


}
