import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;


public class Main {

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub
		Runner run = null;
		FrameTableInterface frame = null;
		Scanner scanner = new Scanner(new FileReader("Random Number.txt"));
		
		if(args[5].contains("lru")){
			frame = new FrameTableOfLRU(args);
		}
		
		else if(args[5].contains("fifo")){
			frame = new FrameTableOfFIFO(args);
		}
		
		else if(args[5].contains("random")){
			frame = new FrameTableOfRandom(args,scanner);
		}
		else{
			System.out.println("ERROR INPUT FORMAT!");
		}
		
		run = new Runner(args,scanner,frame);
		run.run();
		run.print();
	}

}
