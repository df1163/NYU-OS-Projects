import java.util.ArrayList;


public class PrintOut {
	public static void printa(ArrayList<Process> finishedProcesses,int totalTime,int CPUTime,int IOTime){
		System.out.println("");
		int totalTurnAroundTime = 0;
		int totalWaitingTime = 0;
		for(int i=0;i<finishedProcesses.size();i++){
			Process p = finishedProcesses.get(i);
			p.Turnaroundtime = p.Finishingtime-p.arrivalTime;
			totalTurnAroundTime += p.Turnaroundtime;
			totalWaitingTime += p.Waitingtime;
			System.out.println("ProcessID "+p.id+":");
			System.out.print("              (A,B,C,IO) = ");
			p.printOut();
			System.out.println("");
			System.out.print("              Finishing time: ");
			System.out.println(p.Finishingtime);
			System.out.print("              Turnaround time:  ");
			System.out.println(p.Turnaroundtime);
			System.out.print("              I/O time: ");
			System.out.println(p.IOtime);
			System.out.print("              Waiting time: ");
			System.out.println(p.Waitingtime);
		}
		System.out.println("Summing Data:");
		System.out.println("              Finishing time: "+totalTime);
		System.out.println("              CPU Utilization: "+(double)CPUTime/totalTime);
		System.out.println("              I/O Utilization: "+(double)IOTime/totalTime);
		System.out.println("              Throughput: "+(double)finishedProcesses.size()*100/totalTime+" processes per hundred cycles");
		System.out.println("              Average turnaround time: "+(double)totalTurnAroundTime/finishedProcesses.size());
		System.out.println("              Average waiting time: "+(double)totalWaitingTime/finishedProcesses.size());
	}
}
