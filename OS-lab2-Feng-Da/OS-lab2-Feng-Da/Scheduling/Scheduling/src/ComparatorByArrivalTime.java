import java.util.Comparator;

class ComparatorByArrivalTime implements Comparator<Object>{
	public int compare(Object obj1, Object obj2 ){
		Process p1 = (Process)obj1;
		Process p2 = (Process)obj2;
		if(p1.arrivalTime>p2.arrivalTime){
			return 1;
		}
		
		else if(p1.arrivalTime<p2.arrivalTime){
			return -1;
		}
		
		else{
			if(p1.CPUBurstTime>p2.CPUBurstTime)	return 1;
			else return -1;
		}
	}
}