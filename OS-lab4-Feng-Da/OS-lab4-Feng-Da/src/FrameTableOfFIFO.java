import java.util.ArrayList;


public class FrameTableOfFIFO implements FrameTableInterface{
	
	int numOfFrames;
	ArrayList<int[]> frameTable;//each frame contains three information:page number, process this page belongs and when this frame is put into memory
	
	public FrameTableOfFIFO(String[] args) {
		this.numOfFrames = Integer.parseInt(args[0]) / Integer.parseInt(args[1]);
		this.frameTable = new ArrayList<int[]>();
	}

	
	@Override
	public boolean hasPageFault(int pageNumber, int processNumber, int currentTime) {
		for (int i = 0; i < frameTable.size(); i++) {
			int[] framePage = frameTable.get(i);
			//if the demanding page is in the frame table then obviously no page fault occurs
			if ((framePage[0] == pageNumber) && (framePage[1] == processNumber)) {
				return false;
			}
		}
		//if cannot find the demanding page, return false
		return true;
	}

	@Override
	public void replace(Process[] processes, int pageNumber, int processNumber, int currentTime) {
		if (numOfFrames == frameTable.size()) {
			int[] evictedFrame = frameTable.get(0);
			int evictedProcessNumber = evictedFrame[1];
			// get the evicted process
			Process evictedProcess = processes[evictedProcessNumber - 1];
			evictedProcess.increaseEvictTime();
			// add total resident time for the evicted process
			int loadTime = evictedFrame[2];
			int residencyTime = currentTime - loadTime;
			evictedProcess.addResidencyTime(residencyTime);
			// remove the first process in the queue
			frameTable.remove(0);
		} 
		// add new demanding page to the first in first out queue
		int[] replacedFrame = {pageNumber, processNumber, currentTime}; 
		frameTable.add(replacedFrame);
	}
	
}
