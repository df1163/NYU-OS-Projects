Program files and directory:
Under the root directory there is one folder named src where all source code locates and one read me file. To compile this program you should go to the /src directory first.

This source code includes 8 files:
Main.java: where the program enters, when running the compiling strings will be the argument of this class, according to the algorithm the program will choose a right algorithm to process.

FrameTableInterface.java: an interface defined two methods: has page fault and replace. since they have common methods but different detailed operations on different data storage thus a interface is the best way.

FrameTableOfFIFO.java: as the name shows, frame stores page number, process this page belongs and when this frame is loaded.

FrameTableOfLRU.java: as the name shows, frame stores page number, process this page belongs, least recent time and load time.

FrameTableOfRandom.java:as the name shows, frame stores page number, process this page belongs and load time.

Runner.java: run the process by job mix and A, B, C values. Maintain a memory table and process replace and page fault detecting.

Process.java: processes executed during the program, each process stores necessary information for counting at the end of the algorithm and generate the next word by the random number file.

Random Number.txt: random numbers.

How to compile the program:
for each .java file: javac filename.java.
Having finished all compiling and generated the destination class enter the program by the  main file. e.g: java Main 10 10 20 1 10 lru 0.
The result will then display on the screen.