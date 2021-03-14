import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Random;

public class SelectRandomSolutionSet {
	
	private static HashSet<Integer> uniqueNums = new HashSet<Integer>();	
	private static String saveDirectory = "E:\\LayoutComparison\\Toy\\NewGridSize\\";
	
	public static void main(String args[]) {
		
		String solutionsFileName = "E:\\TestDavidSolutions\\NewGridSize\\Toy\\Toy_14X14.txt";
		String fname;
		String folderName;
		
		for(int i = 11; i <= 11; i++) {
			
			folderName = saveDirectory + "Sample"+i;
			File dir = new File(folderName);
			dir.mkdir();
			
			fname = folderName + "\\" + "Toy_Sample_" + i + ".txt";
			
			generateRandomBrowserList(600, 1, 263921);
			
			try{
		        BufferedReader buf = new BufferedReader(new FileReader(solutionsFileName));
		        String lineJustFetched = null;
		        int count = 0;
		       
		        while(true){
		            lineJustFetched = buf.readLine();
		            if(lineJustFetched == null){  
		                break; 
		            }else{
		            	
		            	if(uniqueNums.contains(Integer.valueOf(count))) {
		            		
			            	writeSampleToFile(fname, lineJustFetched);
			                  
		            	} //end of if(uniqueNums.contains(Integer.valueOf(count)))
		            	
		            	count++;
		            }
		        }
		         

		        buf.close();

		    }catch(Exception e){
		        e.printStackTrace();
		    }
			
			uniqueNums.clear();
			
			System.out.println(i + " completed");
			
		}
		
		
		
	}
	
	public static void generateRandomBrowserList(int size, int lower, int upper) {
		
		while(uniqueNums.size() < size) {
			uniqueNums.add(generateRandomInt(lower, upper));
		}
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
	
	public static void writeSampleToFile(String fname, String layout) {
		
		try {
			
			FileWriter fw = new FileWriter(fname, true); // the true will append the new data
			fw.write(layout + "\n");
			fw.close();
			
		} catch (IOException ioe) {
			System.err.println("IOException: " + ioe.getMessage());
		}
	}

}
