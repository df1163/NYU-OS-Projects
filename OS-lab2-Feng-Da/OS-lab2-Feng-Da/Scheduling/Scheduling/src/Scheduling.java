import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Scheduling {
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		ArrayList<Process> processes = new ArrayList<Process>();
		int verboseFlag;
		Scanner input;
		Scanner newInput;
		String path = "";
		String verbose = "";
		for(int i=0;i<2;i++){
			if(i==0){
				System.out.println("Please input the file absolute path:");
				input = new Scanner(System.in);
				path = input.next();
			}
			else{
				System.out.print("Please choose if you wanna display verbose information,");
				System.out.println("Y or y means you wanna it, other symbols mean not:");
				newInput = new Scanner(System.in);
				verbose = newInput.next();
			}
		}
		
		if(verbose.equals("Y")||verbose.equals("y")){
			verboseFlag = 1;
		}
		else{
			verboseFlag = 0;
		}
		//FileReader fr = new FileReader("d:\\Lab2\\FCFS\\2.txt");
		FileReader fr = new FileReader(path);
		Scanner scanner = new Scanner(fr);
		String a = scanner.next();
		int numOfProcess = Integer.parseInt(a);
		Process process[] = new Process[numOfProcess];
		//dealing with fault format, containing illegal line changing
		for(int i=0;i<numOfProcess;i++){
			Integer parameterOfProcess[] = new Integer[4];
			for(int j=0;j<4;j++){
				String b = scanner.next();
				String c = new String();
				if(b.contains("(")){
					c = b.replace("(", "");
				}
				else if(b.contains(")")){
					c = b.replace(")", "");
				}
				else{
					c = b;
				}
				parameterOfProcess[j] = Integer.parseInt(c);
			}
			//Initialize input processes
			process[i] = new Process(parameterOfProcess[0],
					parameterOfProcess[1],parameterOfProcess[2],parameterOfProcess[3],i);
			process[i].id = i;
			processes.add(process[i]);
		}
		System.out.println("FCFS:");
		new FCFS();
		FCFS.run(processes,verboseFlag);
		System.out.println("RR:");
		new RRWithQuantum2();
		RRWithQuantum2.run(processes,verboseFlag);
		System.out.println("uniprogrammed:");
		UniProgrammed.run(processes,verboseFlag);
		System.out.println("HRPN:");
		new HPRN();
		HPRN.run(processes,verboseFlag);
	}
}