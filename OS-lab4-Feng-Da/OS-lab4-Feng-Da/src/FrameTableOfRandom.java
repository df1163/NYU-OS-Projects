import java.util.Scanner;


public class FrameTableOfRandom implements FrameTableInterface {

	int numOfFrames;
	Scanner random;
	int[][] frameTable;//each frame contains three information:page number, process this page belongs and when this frame is put into memory
	
	public FrameTableOfRandom(String[] args, Scanner random){
		this.numOfFrames = Integer.parseInt(args[0]) / Integer.parseInt(args[1]);
		this.random = random;
		this.frameTable = new int[numOfFrames][3];
		
	}

	@Override
	public boolean hasPageFault(int pageNumber, int processNumber, int currentTime) {
		for (int i = 0; i < numOfFrames; i++) {
			//if the demanding page is in the frame table then obviously no page fault occurs
			if ((frameTable[i][0] == pageNumber) && (frameTable[i][1] == processNumber)) {
				return false;
			}
		}
		//if cannot find the demanding page, return false
		return true;
	}

	@Override
	public void replace(Process[] processes, int pageNumber, int processNumber, 
			int currentTime) {
		//if there was an unused frame, use that frame element and end searching, 
		//searching begins from highest address
		for (int i = (numOfFrames - 1); i >= 0; i--) {
			if ((frameTable[i][0] == 0) && (frameTable[i][1] == 0)) {
				frameTable[i][0] = pageNumber;
				frameTable[i][1] = processNumber;
				frameTable[i][2] = currentTime;
				return;
			}
		}	
		//Process the evicted process page: add eviction time by one, and add its total resident time
		//find the evicted by generating random number
		int randomNumber = random.nextInt();
		int frameEvicted = randomNumber % numOfFrames;
		int evictedProcessNumber = frameTable[frameEvicted][1];
		//get the evicted process
		Process evictedProcess = processes[evictedProcessNumber - 1];
		evictedProcess.increaseEvictTime();//evicted once
		int loadTime = frameTable[frameEvicted][2];
		int residencyTime = currentTime - loadTime;
		evictedProcess.addResidencyTime(residencyTime);//has been residence for such a long time	
		//frame refreshed with new information
		frameTable[frameEvicted][0] = pageNumber;
		frameTable[frameEvicted][1] = processNumber;
		frameTable[frameEvicted][2] = currentTime;
		
	}	
}