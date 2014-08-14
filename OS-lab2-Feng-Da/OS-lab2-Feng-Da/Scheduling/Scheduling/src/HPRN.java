import java.util.ArrayList;
import java.util.Collections;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


public class HPRN {
	public static void run(ArrayList<Process> processes, int verboseFlag){
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
		//Put into ready or unstarted tables
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
		//Main loop of HPRN
		while(finishedProcess<numOfProcesses){
			//put unstarted process into ready
			for(int i=0;i<unstartedProcessTable.size();i++){
				if(unstartedProcessTable.get(i).arrivalTime == cycle){
					unstartedProcessTable.get(i).nowCycle = cycle;
					readyProcessTable.add(unstartedProcessTable.get(i));
				}
			}
			cycle++;
			//if there are no running process then retrieve from ready
			if(runningProcess == null){
				runningProcess = readyProcessTable.poll();
				if(runningProcess != null){ 
					//set cpu burst time when get from ready to running
					runningProcess.randomBurstTime();
				}
			}
			//if not null then execute
			if(runningProcess!=null){
				runningProcess.run();
				CPUTime++;
				runningProcess.nowCycle = cycle;
			}
			if(verboseFlag == 1){
				System.out.print(cycle+"   ");
				for(int i=0;i<numOfProcesses;i++){
					//System.out.print(" "+process[i].id+"  ");
					if(blockProcessTable.contains(process[i])) 	System.out.print("blocked  "+process[i].IOBurstTime+"  ");
					else if(readyProcessTable.contains(process[i])) 	System.out.print("ready   ");
					else if(finishedProcesses.contains(process[i])) 	System.out.print("terminated   ");
					else if(unstartedProcessTable.contains(process[i]) && cycle <= processes.get(i).arrivalTime) 
						System.out.print("unstart  ");
					else	System.out.print("running  "+(1+process[i].CPUBurstTime)+"  ");
				}
				System.out.println("");
				
				
			}
			//ready table, all process ++
			if(!readyProcessTable.isEmpty()){
				for(Process p:readyProcessTable){
					p.Waitingtime++;
					p.nowCycle = cycle;
				}
			}
			//block IO, all process ++, if finished IO then put into ready queue
			if(!blockProcessTable.isEmpty()){
				IOTime++;
				ArrayList<Process> addToReadyProcess = new ArrayList<Process>();
				Process[] pArray = blockProcessTable.toArray(new Process [0]);
				for(int i=0;i<pArray.length;i++){
					pArray[i].IOBurstTime--;
					pArray[i].IOtime++;
					pArray[i].nowCycle = cycle;
					if(pArray[i].IOBurstTime == 0){
						readyProcessTable.add(pArray[i]);
						blockProcessTable.remove(pArray[i]);
					}
				}
				readyProcessTable.addAll(addToReadyProcess);
			}
			//deal with running process
			if(runningProcess!=null){
				//if process finished
				if(runningProcess.remainingCPUTime == 0){
					runningProcess.Finishingtime = cycle;
					finishedProcesses.add(runningProcess);
					finishedProcess++;
					runningProcess =null;
				}
				//burst time depleted
				else if(runningProcess.remainingCPUTime != 0 && runningProcess.CPUBurstTime <= 0){
					runningProcess.randomIO();
					blockProcessTable.add(runningProcess);
					runningProcess =null;
				}
			}
			//sort ready table by ratio
			if(!readyProcessTable.isEmpty()){
				ArrayList<Process> tmp = new ArrayList<Process>();
				while(!readyProcessTable.isEmpty()){
					tmp.add(readyProcessTable.poll());
				}
				Collections.sort(tmp,new ComparatorByRatio());
				readyProcessTable.addAll(tmp);
			}
		}
		Collections.sort(finishedProcesses,new ComparatorByID());
		RandomNumberGenerator.reset();
		PrintOut.printa(finishedProcesses,cycle,CPUTime,IOTime);
	
	}
}
