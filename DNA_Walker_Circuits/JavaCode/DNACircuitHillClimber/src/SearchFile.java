import java.io.File;


public class SearchFile {

	public static void main(String args[]) {
		
		String file = "NewHashMapData.txt";
		String directory ;
		for(int i = 10; i <= 50; i++) {
			
			directory = "E:\\SA\\Run"+i;
			System.out.println(i + " " + new File(directory, file).exists());
			
		}
		
		
	}
}
