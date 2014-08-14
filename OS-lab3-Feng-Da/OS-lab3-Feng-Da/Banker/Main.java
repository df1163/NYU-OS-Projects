import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;


public class Main {
	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		//Two classes, claim first
		Bank myBank = new Bank();
		Fifo myFIFO = new Fifo();
		System.out.println("Program starts!");
		System.out.println("Please input the file path:");
		Scanner input = new Scanner(System.in);
		String path = input.next();
		//FileReader fr = new FileReader("./src/InputTasks/input-11.txt");
		FileReader fr = new FileReader(path);
		Scanner scanner = new Scanner(fr);
		//read in the first number: total task number
		String firstLine = scanner.nextLine();
		String split[] = firstLine.split("\\s+");
		int MAX_TASK = Integer.parseInt(split[0]);

		//resource number
		int MAX_RESOURCE = Integer.parseInt(split[1]);
		//allocate space for necessary data structure, initialize all matrix and class members
		myBank.initAlgorithm(MAX_TASK, MAX_RESOURCE);
		myFIFO.initAlgorithm(MAX_TASK, MAX_RESOURCE);
		
		//Resource Number of each type
		for(int i=1;i<=split.length-2;i++){
			myBank.Available[i-1] = Integer.parseInt(split[i+1]);
			myFIFO.Available[i-1] = Integer.parseInt(split[i+1]);
		}
		
		//initialize task arrays
		Task[] tasks = new Task[MAX_TASK];
		for(int i=0;i<tasks.length;i++){
			tasks[i] = new Task(i);
		}
		//read activity lines
		while(scanner.hasNext()){
			String activities = scanner.nextLine();
			//If not null line then put this line into activities list
			if(!activities.contentEquals("")){
				String splitActivities[] = activities.split("\\s+");
				int taskID = Integer.parseInt(splitActivities[1]);
				tasks[taskID-1].Activities.add(activities);
			}
		}
		myFIFO.method(tasks);
		//reset task to unexecuted states
		for(int i=0;i<tasks.length;i++){
			tasks[i].reset(i);
		}
		myBank.bankmethod(tasks);
	}
}
