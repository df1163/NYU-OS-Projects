import java.util.Comparator;

//By the priority according to the arrival time
public class ComparatorByPriority implements Comparator<Object> {

	public int compare(Object obj1, Object obj2 ){
		Process p1 = (Process)obj1;
		Process p2 = (Process)obj2;
		if(p1.sortedInputPriority>p2.sortedInputPriority){
			return 1;
		}
		
		else {
			return -1;
		}
	}

}
