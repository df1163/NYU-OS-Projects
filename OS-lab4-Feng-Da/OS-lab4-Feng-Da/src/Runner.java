import java.util.Scanner;


public class Runner {

	FrameTableInterface frameTable;
	Process[] processes;
	int machineSize;
	int pageSize;
	int processSize;
	int jobMix;
	int referenceNumber;
	String algorithm;
	Scanner random;
	
	static final int QUANTUM = 3;

	public Runner(String[] args, Scanner random, FrameTableInterface frameTable) {
		machineSize = Integer.parseInt(args[0]);
		pageSize = Integer.parseInt(args[1]);
		processSize = Integer.parseInt(args[2]);
		jobMix = Integer.parseInt(args[3]);
		referenceNumber = Integer.parseInt(args[4]);
		algorithm = args[5];
		this.random = random;
		this.frameTable = frameTable;
	}
	
	
	/*run by the job mix number differently*/
	public void run() {
		//only 1 process, easiest
		if (jobMix == 1) {
			int processNumber = 1;
			processes = new Process[1];
			processes[0] = new Process(processSize, processNumber);
			jobMixEquals1();
		} 
		
		//different conditions, the function differs by the jobMix
		else if(jobMix==2 || jobMix==3 || jobMix==4 ){
			processes = new Process[4];
			for(int i=0;i<4;i++){
				processes[i] = new Process(processSize,i+1);
			}
			jobMixIsNot1(jobMix);
		}
		
		else {
			System.out.println("Illegal job mix number!");
		}
	}
	
	/*J is 1 then fully sequential*/
	public void jobMixEquals1() {
		
		for (int runTime = 1; runTime <= referenceNumber; runTime++) {
			int pageNumber = processes[0].getNextWord() / pageSize;			
			//if has page fault then replace
			if (frameTable.hasPageFault(pageNumber, 1, runTime)) {
				frameTable.replace(processes, pageNumber, 1, runTime);
				processes[0].increaseFaultTime();
			}
			// change to the next reference word
			processes[0].nextReference(1, 0, 0, random);
		}
	}

	/*J is 2, 3 or 4 : 3 cases*/
	public void jobMixIsNot1(int jobMix){
		int totalCycle = referenceNumber / QUANTUM;
		double A[] = new double[4];
		double B[] = new double[4];
		double C[] = new double[4];

		/*Initiate A, B and C for each process*/
		//jobMix is 2: A = 1,B = 0, C = 0;
		if(jobMix == 2){
			for(int i=0;i<4;i++){
				A[i] = 1;
				B[i] = 0;
				C[i] = 0;
			}
		}

		//jobMix is 3:A = 0,B = 0, C = 0;
		else if(jobMix == 3){		
			for(int i=0;i<4;i++){
				A[i] = 0;
				B[i] = 0;
				C[i] = 0;
			}	
		}

		//jobMix is 4:A,B,C differs
		else if(jobMix == 4){
			A[0] = 0.75;B[0] = 0.25;  C[0] = 0;
			A[1] = 0.75;B[1] = 0;       C[1] = 0.25;
			A[2] = 0.75;B[2] = 0.125;C[2] = 0.125;
			A[3] = 0.5;  B[3] = 0.125;C[3] = 0.125;
		}
		else{}

		//run all processes, each process begins referencing by its A, B and C
		for (int cycle = 0; cycle <= totalCycle; cycle++) {
			for(int j=0;j<4;j++){
				runAProcess(j+1,A[j],B[j],C[j],cycle,totalCycle);
			}
		}
	}
	
	public void runAProcess(int processNumber, double A, double B, double C,
			int cycle, int totalCycle){
		int referenceTimes;//how many times will produce a reference word in one quantum
		//if is not the final cycle then run a full quantum
		if(cycle!=totalCycle){
			referenceTimes = QUANTUM;
		}
		//if is the final then run remaining reference time
		else{
			referenceTimes = referenceNumber % QUANTUM;
		}
		//a process starts referencing
		for (int ref = 0; ref < referenceTimes; ref++) {
			int time = QUANTUM * cycle * 4 + ref + 1 + (processNumber - 1) * referenceTimes;
			int pageNumber = processes[processNumber - 1].getNextWord() / pageSize;
			// if has page fault
			if (frameTable.hasPageFault(pageNumber, processNumber, time)) {
				frameTable.replace(processes, pageNumber, processNumber, time);
				processes[processNumber - 1].increaseFaultTime();
			}
			// referencing the next word.
			processes[processNumber - 1].nextReference(A, B, C, random);
		}
	}
	
	public void print() {
		int totalFaultTimes = 0;
		int totalResidencyTimes = 0;
		int totalEvictTimes = 0;
		// show inputting data from the beginning of the program
		System.out.println("The machine size is " + this.machineSize);
		System.out.println("The page size is " + this.pageSize);
		System.out.println("The process size is " + this.processSize);
		System.out.println("The job mix number is " + this.jobMix);
		System.out.println("The number of references per process is " + this.referenceNumber);
		System.out.println("The replacement algorithm is " + this.algorithm);
		System.out.println("The level of debugging output is 0\n");		
		//number of page faults and the average resident time of each process
		for (int i = 0; i < processes.length; i++) {
			int faultTime = processes[i].pageFaultTimes;
			int residencyTime = processes[i].totalResidencyTimes;
			int evictTime = processes[i].evictTimes;
			if (evictTime == 0) {
				System.out.println("Process " + (i + 1) + " had " + faultTime + " faults.\n\tWith no evictions, the average residence is undefined.");
			} 
			else {
				double averageResidency = (double) residencyTime / evictTime;
				System.out.println("Process " + (i + 1) + " had " + faultTime + " faults and " + averageResidency + " average residency.");
			}
			totalFaultTimes += faultTime;
			totalResidencyTimes += residencyTime;
			totalEvictTimes += evictTime;
		}
		
		//total number of page faults and the overall average resident time
		if (totalEvictTimes == 0) {
			System.out.println("\nThe total number of faults is " + totalFaultTimes + ".\n\tWith no evictions, the overall average residency is undifined.");
		} 
		else {
			double totalAverageResidency = (double)totalResidencyTimes / totalEvictTimes;
			System.out.println("\nThe total number of faults is "+ totalFaultTimes+ " and the overall average residency is " + totalAverageResidency);
		}		
	}
}
