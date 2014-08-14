import java.util.ArrayList;
import java.util.Collections;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


public class RRWithQuantum2 {
	public static void run(ArrayList<Process> processes, int verboseFlag){
		int cycle=0;
		int CPUTime=0;
		int IOTime=0;
		int finishedProcess = 0;
		int quantum = 2;
		int numOfProcesses = processes.size();
		//process
		Process process[] = new Process[numOfProcesses];
		Process runningProcess = null;
		//process ready to be executed
		Queue<Process> readyProcessTable 	= new ConcurrentLinkedQueue<Process>();
		ArrayList<Process> blockProcessTable = new ArrayList<Process>();
		ArrayList<Process> finishedProcesses = new ArrayList<Process>();
		ArrayList<Process> unstartedProcessTable = new ArrayList<Process>();
		ArrayList<Process> ifMultiProcessBeReadyAtSameMoment = new ArrayList<Process>();
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
		//Main loop of rr
		while(finishedProcess<numOfProcesses){
			for(int i=0;i<unstartedProcessTable.size();i++){
				if(unstartedProcessTable.get(i).arrivalTime == cycle){
					readyProcessTable.add(unstartedProcessTable.get(i));
				}
			}
			cycle++;
			//if there are no running process then retrieve from ready
			if(runningProcess == null){
				runningProcess = readyProcessTable.poll();
				if(runningProcess != null && runningProcess.CPUBurstTime <= 0){ 
					runningProcess.randomBurstTime();
				}
			}
			quantum--;

			//print out verbose
			if(verboseFlag == 1){
				System.out.print(cycle+"   ");
				//System.out.print(cycle+"   "+quantum+"   ");
				for(int i=0;i<numOfProcesses;i++){
					//System.out.print(" "+process[i].id+"  ");
					if(blockProcessTable.contains(process[i])) 	System.out.print("blocked  "+process[i].IOBurstTime+"  ");
					else if(readyProcessTable.contains(process[i])) 	System.out.print("ready   ");
					else if(finishedProcesses.contains(process[i])) 	System.out.print("terminated   ");
					else if(unstartedProcessTable.contains(process[i]) && cycle <= processes.get(i).arrivalTime) 
						System.out.print("unstart    ");
					else	System.out.print("running  "+(1+process[i].CPUBurstTime)+"  ");
				}
				//System.out.print("    content   block size:  "+blockProcessTable.size()+"    ready size:  "+readyProcessTable.size()+"  ");
				System.out.println("");
			}
			//if retrieved or is not null
			if(runningProcess!=null){
				runningProcess.run();
				CPUTime++;				
			}
			//ready table, all process increase wait time
			if(!readyProcessTable.isEmpty()){
				for(Process p:readyProcessTable){
					p.Waitingtime++;
				}
			}			
			//block IO, all process ++, if finished IO then put into ready queue
			if(!blockProcessTable.isEmpty()){
				IOTime++;
				int addToReadyNum = 0;
				ArrayList<Process> addToReadyProcess = new ArrayList<Process>();
				Process[] pArray = blockProcessTable.toArray(new Process [0]);
				for(int i=0;i<pArray.length;i++){
					pArray[i].IOBurstTime--;
					pArray[i].IOtime++;
					if(pArray[i].IOBurstTime <= 0){
						addToReadyNum++;
						addToReadyProcess.add(pArray[i]);	
						blockProcessTable.remove(pArray[i]);
					}
				}
				//if multiple processes became ready at same time then put into a table, sort before the next cycle comes
				if(addToReadyNum ==1){
					//readyProcessTable.add(addToReadyProcess.get(0));
					ifMultiProcessBeReadyAtSameMoment.add(addToReadyProcess.get(0));
				}
				else{
					Collections.sort(addToReadyProcess,new ComparatorByPriority());
					for(int i=0;i<addToReadyProcess.size();i++){
						//readyProcessTable.add(addToReadyProcess.get(i));
						ifMultiProcessBeReadyAtSameMoment.add(addToReadyProcess.get(i));
					}
				}
			}
			//if exhausted quantum
			if(quantum <= 0){
				//if is not null, force to put into ready table if still continues
				if(runningProcess!=null){
					//if process finished
					if(runningProcess.remainingCPUTime == 0){
						runningProcess.Finishingtime = cycle;
						finishedProcesses.add(runningProcess);
						finishedProcess++;
					}
					//if burst time is used out
					else if(runningProcess.remainingCPUTime != 0 && runningProcess.CPUBurstTime <= 0){
						runningProcess.randomIO();
						blockProcessTable.add(runningProcess);
					}
					else{
						//readyProcessTable.add(runningProcess);
						ifMultiProcessBeReadyAtSameMoment.add(runningProcess);
					}
					//reset quantum
					runningProcess = null;
					quantum = 2;
				}
			}
			else{
				//if is not null
				if(runningProcess!=null){
					//if process finished
					if(runningProcess.remainingCPUTime == 0){
						runningProcess.Finishingtime = cycle;
						finishedProcesses.add(runningProcess);
						finishedProcess++;
						runningProcess = null;
						quantum = 2;
					}
					//if burst time is used
					else if(runningProcess.remainingCPUTime != 0 && runningProcess.CPUBurstTime <= 0){
						runningProcess.randomIO();
						blockProcessTable.add(runningProcess);
						quantum = 2;
						runningProcess = null;
					}
				}
				else{
					quantum = 2;
				}
			}
			/*
			System.out.print("  add "+ifMultiProcessBeReadyAtSameMoment.size()+"  ");
			for(int i=0;i<ifMultiProcessBeReadyAtSameMoment.size();i++){
				System.out.print(ifMultiProcessBeReadyAtSameMoment.get(i).id+"   ");
			}
			*/
			if(ifMultiProcessBeReadyAtSameMoment.size()!=1){
				Collections.sort(ifMultiProcessBeReadyAtSameMoment,new ComparatorByPriority());	
			}
			readyProcessTable.addAll(ifMultiProcessBeReadyAtSameMoment);
			
			ifMultiProcessBeReadyAtSameMoment.clear();
		}
		ComparatorByID comp2 = new ComparatorByID();
		Collections.sort(finishedProcesses,comp2);
		RandomNumberGenerator.reset();
		PrintOut.printa(finishedProcesses,cycle,CPUTime,IOTime);
	
	}
}