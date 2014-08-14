

public class Process implements Comparable<Process> {
	int arrivalTime = 0;
	int CPUBurstTime = 0;
	int remainingCPUTime = 0;
	int IOBurstTime = 0;
	
	int B;
	int totalCPUTime;
	int IO;
	int sortedInputPriority;
	
	int id;
	int Finishingtime;
	int Turnaroundtime;
	int IOtime;
	int Waitingtime;
	int originalID;
	int nowCycle;
	
	public Process(){
		
	}
	
	public Process(int arrivalTime, int B, int totalCpuTime, int IO,int originalID){
		this.arrivalTime = arrivalTime;
		this.B = B;
		this.remainingCPUTime = totalCpuTime;
		totalCPUTime = totalCpuTime;
		this.IOBurstTime = IO;
		this.IO = IO;
		this.originalID = originalID;
	}
	
	public void printOut(){
		System.out.print("(");
		System.out.print(arrivalTime + " ");
		System.out.print(B + " ");
		System.out.print(totalCPUTime + " ");
		System.out.print(IO + ")");
	}
	
	public void run(){
		remainingCPUTime--;
		CPUBurstTime--;
	}

	public void randomBurstTime() {
		//System.out.println("");
		//System.out.print("Find burst when blocking a process ");
		this.CPUBurstTime = RandomNumberGenerator.getRandomNumber(this.B);
		//System.out.println(CPUBurstTime);
	}
	
	public void randomIO() {
		//System.out.println("");
		//System.out.print("Find I/O burst when blocking a process ");
		this.IOBurstTime = RandomNumberGenerator.getRandomNumber(this.IO);
		//System.out.println(IOBurstTime);
	}
	
	public int compareTo(Process arg0) {
		if(this.arrivalTime!=arg0.arrivalTime){
			return this.arrivalTime>arg0.arrivalTime?1:-1;
		}
		return new Integer(this.originalID).compareTo(arg0.originalID);
	}
	
	public double getRatio(){
		if((totalCPUTime-remainingCPUTime) == 0)	
			return (double)nowCycle - arrivalTime;
		else
			return (double)(nowCycle - arrivalTime)/(totalCPUTime-remainingCPUTime);
	}
}
