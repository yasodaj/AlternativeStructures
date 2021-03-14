import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class testFile {
	
	public static void main(String args[]) {
		
		
		File folder = new File("E:\\SA\\Toy0_SA\\Test\\OutputFiles");
		File[] listOfFiles = folder.listFiles();

		
	    String last="", line;

	    for(int i = 0; i < listOfFiles.length; i++) {
	    	
		    try{
		        BufferedReader input = new BufferedReader(new FileReader(listOfFiles[i]));
		        ///ArrayList<String> words = new ArrayList<>();
		        
			    while ((line = input.readLine()) != null) { 
			    	
			    		if(! line.trim().equals("")) {
			    				last = line;
			    		}
			    				    		
			    	
			    }
			    
			    
			    String logFilename = "E:\\SA\\Toy0_SA\\Test\\ErrorMessage_2.txt";
				FileWriter fw = new FileWriter(logFilename, true); // the true will append the new data
				fw.write(listOfFiles[i] + "\t" +last + "\n");
				fw.close();
				input.close();
		    }catch(Exception e){
		        e.printStackTrace();
		    }
	    	
	    }
	    
	    

	}

}
