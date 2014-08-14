
/*
 * interface for 3 different frame table, since they have a common page fault 
 * detect and replace argument but different operations and data storage thus 
 * a interface is the best way
 * */
public interface FrameTableInterface {

	boolean hasPageFault(int pageNumber, int processNumber, int currentTime);//different data structures

	void replace(Process[] processes, int pageNumber, int processNumber, int currentTime);
}