import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class RandomNumberGenerator {
	private static ArrayList<Integer> randomNums = new ArrayList<Integer>();
	private static int count = 0;
	static{
		//File file = new File("d:\\Lab2\\Random Number.txt");
		File file = new File("./src/RandomNumbers/Random Number.txt");
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = null;
			while((line = reader.readLine())!=null){
				randomNums.add(Integer.parseInt(line.trim()));
			}
			reader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void reset(){
		count = 0;
	}
	
	public static int getRandomNumber(int upperbound){
		int rand = randomNums.get(count++);
		//System.out.print(rand+" "+upperbound);
		return 1+(rand%upperbound);
	}
}
