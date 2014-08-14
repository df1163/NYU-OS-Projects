import java.util.Comparator;

//Sort by ratio, if same sort by priority
public class ComparatorByRatio implements Comparator<Object>{
	public int compare(Object obj1, Object obj2 ){
		Process p1 = (Process)obj1;
		Process p2 = (Process)obj2;
		if(p1.getRatio()<p2.getRatio()){
			return 1;
		}		
		else if(p1.getRatio()>p2.getRatio()){
			return -1;
		}
		else return p1.sortedInputPriority>p2.sortedInputPriority?1:-1;
	}
}