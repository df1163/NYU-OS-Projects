import java.util.ArrayList;


public class Task {
	ArrayList<String> Activities = new ArrayList<String>();
	int iterator;//pointer,pointing to current activities
	int id;//task id
	int finishTime;
	int computeTime;
	int blockedNum;
	Boolean aborted;
	//initiate with ID
	public Task(int i){
		iterator = 0;
		aborted = false;
		id = i+1;
		computeTime = 0;
		blockedNum = 0;
	}

	public void reset(int i){
		iterator = 0;
		id = i+1;
		finishTime = 0;
		computeTime = 0;
		blockedNum = 0;
		aborted = false;
	}
	
	public Boolean hasNextActivity(){
		if(iterator == Activities.size()-1) return false;
		else return true;
	}
	//get current activity, as the iterator
	public String getNext() {
		return Activities.get(iterator);
	}
	
	public Boolean isFinished(){
		if(getNext().contains("terminate") && computeTime==0){
			return true;
		}
		else return false;
	}
	
	public Boolean isAborted(){
		if(aborted == true) return true;
		else return false;
	}

	//pointer jump to the next activities
	public void next(){
		iterator++;
	}
	
	public void finishTask(int finish){
		finishTime = finish;
	}
	
	public void abortTask(){
		//finish
		iterator = Activities.size()-1;
		aborted = true;
		blockedNum = 0;
		//System.out.println("tasks "+id+" is aborted");
	}

	public void compute() {
		computeTime--;
	}

	public void block() {
		blockedNum++;
	}
	
}
