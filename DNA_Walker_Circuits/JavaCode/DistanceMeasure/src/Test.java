import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Test {
	
	public static void main(String args[]) throws IOException {

		File file = new File("E:\\SA\\Toy\\NewGridSize\\Run2\\FilteredInitSolutions_3_of_5.txt");
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
		
		TreeMap<Integer, String> sortedSolutionMap = new TreeMap<>(solutionMap);
		
		
		
		int stop = sortedSolutionMap.firstKey() + sortedSolutionMap.size();
		
		for(int i = sortedSolutionMap.firstKey(); i < stop ; i++) {
			System.out.println("i is " + i);
			for(int j = i + 1; j < stop; j++) {
				
			}
			
		}
		

		  
	}

}
