
public class FrameTableOfLRU implements FrameTableInterface{
	int numOfFrames;
	int frameTable[][];//each frame contains four information:page number, process this page belongs, least recent time and load time
	
	public FrameTableOfLRU(String[] args){
		numOfFrames = Integer.parseInt(args[0])/Integer.parseInt(args[1]);
		frameTable = new int[numOfFrames][4];
	}

	@Override
	public boolean hasPageFault(int pageNumber, int processNumber, int currentTime) {
		for (int i = 0; i < numOfFrames; i++) {
			//if the demanding page is in the frame table then obviously no page fault occurs
			if ((frameTable[i][0] == pageNumber) && (frameTable[i][1] == processNumber)) {
				frameTable[i][2] = currentTime; // find destination page then modify to the recent refered time
				return false;
			}
		}
		//if cannot find the demanding page, return false
		return true;
	}

	@Override
	public void replace(Process[] processes, int pageNumber, int processNumber, 
			int currentTime) {
		int leastRecentTime = currentTime;
		int replacedFrame = 0;
		
		for (int i = numOfFrames-1; i >= 0; i--) {
			//if there was an unused frame, use that frame element and end searching, 
			//searching begins from highest address
			if ((frameTable[i][0] == 0) && (frameTable[i][1] == 0)) {
				frameTable[i][0] = pageNumber;//page number
				frameTable[i][1] = processNumber;//process this page belongs
				frameTable[i][2] = currentTime;//least recent time 
				frameTable[i][3] = currentTime;//load at current time
				return;
			} 
			//find the least recently used frame, whose recent time should be the largest to current time
			else if (leastRecentTime > frameTable[i][2]) {
				replacedFrame = i;
				leastRecentTime = frameTable[i][2];
			}
		}
		//Process the evicted process page: add eviction time by one, and add its total resident time
		int evictedProcessNumber = frameTable[replacedFrame][1];
		//Get the process which was evicted
		Process evictedProcess = processes[evictedProcessNumber - 1];
		evictedProcess.increaseEvictTime();
		//add resident time
		int loadTime = frameTable[replacedFrame][3];
		int residencyTime = currentTime - loadTime;
		evictedProcess.addResidencyTime(residencyTime);	
		//put the new page into the destination frame
		frameTable[replacedFrame][0] = pageNumber;
		frameTable[replacedFrame][1] = processNumber;
		frameTable[replacedFrame][3] = frameTable[replacedFrame][2] = currentTime;
	}
	
}
