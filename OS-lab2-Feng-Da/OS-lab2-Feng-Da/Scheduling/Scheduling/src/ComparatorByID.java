import java.util.Comparator;

//ID is the input sequence before sort by arrival time
class ComparatorByID implements Comparator<Object>{
	
	public int compare(Object obj1, Object obj2 ){
		Process p1 = (Process)obj1;
		Process p2 = (Process)obj2;
		if(p1.id>p2.id){
			return 1;
		}
		
		else {
			return -1;
		}
	}
}
