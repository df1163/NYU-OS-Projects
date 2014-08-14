import java.util.ArrayList;
import java.util.Collections;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


public class FCFS {
	public static void run(ArrayList<Process> processes,int verboseFlag){
		int cycle=0;
		int CPUTime=0;
		int IOTime=0;
		int finishedProcess = 0;
		int numOfProcesses = processes.size();
		//define array of process as the input one, sort these processes then put into running
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
		System.out.println("");
		//Put into tables: ready, or unstarted
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
		//Main loop of FCFS
		while(finishedProcess<numOfProcesses){
			//put unstarted process into ready
			for(int i=0;i<unstartedProcessTable.size();i++){
				if(unstartedProcessTable.get(i).arrivalTime == cycle){
					readyProcessTable.add(unstartedProcessTable.get(i));
				}
			}
			cycle++;			
			//if there are no running process then retrieve from ready
			if(runningProcess == null){
				runningProcess = readyProcessTable.poll();
				if(runningProcess != null){ 
					runningProcess.randomBurstTime();
				}
			}
			//if retrieved or is not null,run
			if(runningProcess!=null){
				runningProcess.run();
				CPUTime++;
			}
			/*verbose states:*/
			if(verboseFlag == 1){
				System.out.print(cycle+"   ");
				for(int i=0;i<numOfProcesses;i++){
					//System.out.print(" "+process[i].id+"  ");
					if(blockProcessTable.contains(process[i])) 	System.out.print("blocked  "+process[i].IOBurstTime+"  ");
					else if(readyProcessTable.contains(process[i])) 	System.out.print("ready   ");
					else if(finishedProcesses.contains(process[i])) 	System.out.print("terminated  "+"  ");
					else if(unstartedProcessTable.contains(process[i]) && cycle <= processes.get(i).arrivalTime) 
						System.out.print("unstart  "+"  ");
					else	System.out.print("running  "+(1+process[i].CPUBurstTime)+"  ");
				}
				System.out.println("");
				
				
			}
			//ready table, all process increase wait time at this cycle
			if(!readyProcessTable.isEmpty()){
				for(Process p:readyProcessTable){
					p.Waitingtime++;
				}
			}			
			//block IO, all process increase wait time at this cycle, if finished IO then put into ready queue
			if(!blockProcessTable.isEmpty()){
				IOTime++;
				int addToReadyNum = 0;
				ArrayList<Process> addToReadyProcess = new ArrayList<Process>();
				Process[] pArray = blockProcessTable.toArray(new Process [0]);
				for(int i=0;i<pArray.length;i++){
					pArray[i].IOBurstTime--;
					pArray[i].IOtime++;
					if(pArray[i].IOBurstTime == 0){
						addToReadyNum++;
						addToReadyProcess.add(pArray[i]);	
						blockProcessTable.remove(pArray[i]);
					}
				}
				//if there are multiple processes turned ready at the same time, then sort by priority
				if(addToReadyNum ==1){
					readyProcessTable.add(addToReadyProcess.get(0));
				}
				else{
					ComparatorByPriority comparatorByPriority = new ComparatorByPriority();
					Collections.sort(addToReadyProcess,comparatorByPriority);
					readyProcessTable.addAll(addToReadyProcess);
				}
			}
			//if running process is not null, deal with it after it has run: whether has exhausted burst time, or terminated, or continue running
			if(runningProcess!=null){
				//if process terminated
				if(runningProcess.remainingCPUTime == 0){
					runningProcess.Finishingtime = cycle;
					finishedProcesses.add(runningProcess);
					finishedProcess++;
					runningProcess =null;
				}
				//if process not finished but burst time is depleted then block, IO
				else if(runningProcess.remainingCPUTime != 0 && runningProcess.CPUBurstTime <= 0){
					runningProcess.randomIO();
					blockProcessTable.add(runningProcess);
					runningProcess =null;
				}
			}
		}
		ComparatorByID comparatorByID = new ComparatorByID();
		Collections.sort(finishedProcesses,comparatorByID);
		RandomNumberGenerator.reset();
		PrintOut.printa(finishedProcesses,cycle,CPUTime,IOTime);
	
	}
}
