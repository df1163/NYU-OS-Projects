import java.util.Scanner;


public class Process {
	int processSize;
	int nextWord;
	int pageFaultTimes;
	int evictTimes;
	int totalResidencyTimes;
	
	public Process(int processSize, int processName) {
		this.processSize = processSize;
		this.nextWord = (111 * processName) % processSize;
		this.pageFaultTimes = 0;
		this.totalResidencyTimes = 0;
		this.evictTimes = 0;
	}
	
	//Compute the next reference word by A, B, and C
	public void nextReference(double A, double B, double C, Scanner random) {
		int randomNum = random.nextInt();
		double quotient = randomNum / (Integer.MAX_VALUE + 1d);
		if (quotient < A) {
			nextWord = (nextWord + 1) % processSize;
		} else if (quotient < A + B) {
			nextWord = (nextWord - 5 + processSize) % processSize;
		} else if (quotient < A + B + C) {
			nextWord = (nextWord + 4) % processSize;
		} else {
			int randomRef = random.nextInt() % processSize;
			nextWord = randomRef;
		}
	}

	//Add total resident time
	public void addResidencyTime(int time) {
		totalResidencyTimes += time;
	}
	
	//Get the next reference word
	public int getNextWord() {
		return nextWord;
	}
	
	//Add page fault, which means, cannot find the page in the frame page time by one
	public void increaseFaultTime() {
		pageFaultTimes++;
	}
	
	// Add eviction time
	public void increaseEvictTime() {
		evictTimes++;
	}
	
	
}
