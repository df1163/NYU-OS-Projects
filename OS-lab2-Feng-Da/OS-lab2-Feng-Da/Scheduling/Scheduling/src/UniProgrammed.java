import java.util.ArrayList;
import java.util.Collections;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


public class UniProgrammed {
	public static void run(ArrayList<Process> processes, int verboseFlag){
		int cycle=0;
		int CPUTime=0;
		int IOTime=0;
		int finishedProcess = 0;
		int numOfProcesses = processes.size();
		//process
		Process process[] = new Process[numOfProcesses];
		Process runningProcess = null;
		//process ready to be executed
		Queue<Process> readyProcessTable 	= new ConcurrentLinkedQueue<Process>();
		ArrayList<Process> blockProcessTable = new ArrayList<Process>();
		ArrayList<Process> finishedProcesses = new ArrayList<Process>();
		ArrayList<Process> unstartedProcessTable = new ArrayList<Process>();
		//Print out original input
		System.out.print("The original input was: ");
		for(int i=0;i<processes.size();i++){
			processes.get(i).printOut();
		}
		System.out.println("");
		//Sort by arrival time and print out
		System.out.print("The (sorted) input is: ");
		ComparatorByArrivalTime comp = new ComparatorByArrivalTime();
		Collections.sort(processes,comp);
		for(int i=0;i<processes.size();i++){
			Process p = processes.get(i);
			p.printOut();
		}
		//Put into tables
		for(int i=0;i<numOfProcesses;i++){
			Process tmpProcess = (Process) processes.get(i);
			process[i] = new Process(tmpProcess.arrivalTime,
					tmpProcess.B,tmpProcess.remainingCPUTime,tmpProcess.IO,i);
			process[i].id = tmpProcess.id;
			process[i].sortedInputPriority = i;
			if(process[i].arrivalTime==0){
				readyProcessTable.add(process[i]);
			}
			else{
				unstartedProcessTable.add(process[i]);
			}
		}
		//Main loop of uniprogram
		while(finishedProcess<numOfProcesses){
			for(int i=0;i<unstartedProcessTable.size();i++){
				if(unstartedProcessTable.get(i).arrivalTime == cycle){
					readyProcessTable.add(unstartedProcessTable.get(i));
				}
			}
			cycle++;			
			//if there are no running process then retrieve from ready
			if(runningProcess == null && blockProcessTable.isEmpty()){
				runningProcess = readyProcessTable.poll();
				runningProcess.randomBurstTime();
			}
			//retrieve running process
			if(runningProcess != null){
				runningProcess.run();
				CPUTime++;
			}
			if(verboseFlag == 1){
				System.out.print(cycle+"   ");
				for(int i=0;i<numOfProcesses;i++){
					//System.out.print(" "+process[i].id+"  ");
					if(blockProcessTable.contains(process[i])) 	System.out.print("blocked  "+process[i].IOBurstTime+"  ");
					else if(readyProcessTable.contains(process[i])) 	System.out.print("ready   ");
					else if(finishedProcesses.contains(process[i])) 	System.out.print("terminated   ");
					else if(unstartedProcessTable.contains(process[i]) && cycle <= processes.get(i).arrivalTime) 
						System.out.print("unstart    ");
					else	System.out.print("running  "+(1+process[i].CPUBurstTime)+"  ");
				}
				System.out.println("");
				
				
			}
			//ready table, all process ++, take out the process, operates, then put back
			if(!readyProcessTable.isEmpty()){
				for(Process p:readyProcessTable){
					p.Waitingtime++;
				}
			}			
			//block IO, all process ++, if finished IO then put into ready queue
			if(!blockProcessTable.isEmpty()){
				IOTime++;
				Process[] pArray = blockProcessTable.toArray(new Process [0]);
				for(int i=0;i<pArray.length;i++){
					pArray[i].IOBurstTime--;
					pArray[i].IOtime++;
					if(pArray[i].IOBurstTime == 0){
						runningProcess = pArray[i];
						runningProcess.randomBurstTime();
						blockProcessTable.remove(pArray[i]);
					}
				}
			}
			//if is not null
			if(runningProcess!=null){
				//if process finished or burst time is used
				if(runningProcess.remainingCPUTime == 0){
					runningProcess.Finishingtime = cycle;
					finishedProcesses.add(runningProcess);
					finishedProcess++;
					runningProcess =null;
				}
				else if(runningProcess.remainingCPUTime != 0 && runningProcess.CPUBurstTime <= 0){
					runningProcess.randomIO();
					blockProcessTable.add(runningProcess);
					runningProcess =null;
				}
			}
		}
		ComparatorByID comp2 = new ComparatorByID();
		Collections.sort(finishedProcesses,comp2);
		RandomNumberGenerator.reset();
		PrintOut.printa(finishedProcesses,cycle,CPUTime,IOTime);
	
	}
}
