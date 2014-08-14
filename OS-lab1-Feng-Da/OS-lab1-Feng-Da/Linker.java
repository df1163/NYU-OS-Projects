import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Vector;
import java.util.regex.*;

public class Linker {
	/**
	 * @param args
	 * @throws IOException 
	 */
	//Storing the defination of a symbol: symbol and its relative address
	private static LinkedHashMap <String,Integer> symbolTable= new LinkedHashMap<String,Integer>();
	//Storing the module numbers in which a symbol stores
	private static LinkedHashMap <String,Integer> symbolDefinedInModule=new LinkedHashMap<String,Integer>();
	//Storing the module numbers in which a symbol was used
	private static LinkedHashMap <String,Integer> symbolUsedInModule=new LinkedHashMap<String,Integer>();
	//Storing iinformation about whether the address contains error
	private static LinkedHashMap <String,Integer> symbolStatus=new LinkedHashMap<String,Integer>();
	//Storing in <integer,string> which integer is the external address and the string is the symbol that will be resolved
	private static LinkedHashMap <Integer,String> useList= new LinkedHashMap<Integer,String>();
	//Storing in <integer,integer>, the Integer is the module# and map stores the status in each of the external address:
	//whether then can be successfully resolved and relocated, symboled by 0 ,1,2
	private static LinkedHashMap <Integer,Integer> useListStatus=new LinkedHashMap<Integer,Integer>();
	//each module number is a index to each module's uselist
	private static LinkedHashMap <Integer,LinkedHashMap> useListModules = new LinkedHashMap<Integer,LinkedHashMap>();
	//each module number is a index to each module's uselist status
	private static LinkedHashMap <Integer,LinkedHashMap> useListStatusModules = new LinkedHashMap<Integer,LinkedHashMap>();
	//storing if one address exceeds module size
	private static LinkedHashMap <String,Integer> useExcceedsModuleSize = new LinkedHashMap();
	//see as the outspace storing program text,cannot process more than one module at the same time
	private static ArrayList programText = new ArrayList();
	//see as the outspace storing program text,cannot process more than one module at the same time,used for resolving
	private static ArrayList baseAddressOfEachModule = new ArrayList();
	private static ArrayList usedDefInModules = new ArrayList();
	private static ArrayList unUsedDefInModules = new ArrayList();
	private static ArrayList symbolList = new ArrayList();
	//worked as a global counter for each time processing one program text
	static int baseAddress=0;
	//read in one line string and the # of module then load into memory for first passing
	private static void getSymbolTable(String symbolTableString,int serialOfModules){		
		String defAndSymbol[]=(symbolTableString.trim()).split("\\s+");
		for(int i=0;i<defAndSymbol.length;i++){
			//if undefined
			int symbolVal = Integer.parseInt(defAndSymbol[i+1]);
			int startAdd = Integer.parseInt((String) baseAddressOfEachModule.get(serialOfModules-1));
			int symbolDef = symbolVal+startAdd;
			//there is no multi define in symbol list then configure different maps: indexed by the symbol
			if(!symbolList.contains(defAndSymbol[i])){
				symbolTable.put(defAndSymbol[i], symbolDef);
				symbolDefinedInModule.put(defAndSymbol[i], serialOfModules);
				symbolStatus.put(defAndSymbol[i], 0);
				symbolList.add(defAndSymbol[i]);
			}
			//if multi defined then add the list 
			else{
				symbolStatus.put(defAndSymbol[i], 1);
			}
			i++;
		}
	}
	//read 2 splitted strings,one is the #of use list the other is use list string and the # of module then load into memory for first passing
	private static void getuseList(String numbersOfUseList,String useListString,int serialOfModules){
		useList.clear();
		useListStatus.clear();
		//split the string by -1
		String useListFactor[]=(useListString.trim()).split("-1");
		int nu=Integer.parseInt(numbersOfUseList);
		for(int j=0;j<nu;j++){
			
			String listArgu[]=(useListFactor[j].trim()).split("\\s+",2);
			String num[]=(listArgu[1].trim()).split("\\s+");
			for(int i=0;i<num.length;i++){
				//contains multiple use in one instruction
				if(useList.containsKey(Integer.parseInt(num[i]))){
					useListStatus.put(Integer.parseInt(num[i]), 1);//Error: Multiple variables used in instruction; all but first ignored.
												//1 means error state
				}
				//contains 
				else{
					useList.put(Integer.parseInt(num[i]), listArgu[0]);//listArgu:symbol
					useListStatus.put(Integer.parseInt(num[i]), 0);
				}
				symbolUsedInModule.put(listArgu[0],serialOfModules);
				usedDefInModules.add(listArgu[0]);
			}
		}
		//put the use list and its status in to two map distincted by the 
		useListModules.put(serialOfModules, new LinkedHashMap<Integer,String>(useList));
		useListStatusModules.put(serialOfModules, new LinkedHashMap<Integer,Integer>(useListStatus));
	}
	//first pass, processing symbol table and use list
	private static void _1stpass(LinkedHashMap<String,Integer> symbolTable,LinkedHashMap<String,Integer> symbolStatus){
		System.out.println("Symbol Table");
		//listing all values in the hashmap by key with map iterator, parsing one by one
		Collection symbolTableEntry = symbolTable.entrySet();
		for(Iterator iterator=symbolTableEntry.iterator(); iterator.hasNext();){
			Object symbolTableItem = iterator.next();
			//split the map key by "=", the left is key symbolizes symbol and right is value symbolizes relative address
			String obj2[]=(symbolTableItem.toString()).split("=");
			//if defination is used 
			if(usedDefInModules.contains(obj2[0])){
				int moduleNum=symbolUsedInModule.get(obj2[0]);
				int moduleSize=Integer.parseInt((String) baseAddressOfEachModule.get(moduleNum)) - Integer.parseInt((String) baseAddressOfEachModule.get(moduleNum-1));
				//if symbol defination exceeds module size then set the status 2
				if(symbolTable.get(obj2[0])>Integer.parseInt((String) (baseAddressOfEachModule.get(baseAddressOfEachModule.size()-1)))){
					symbolTable.put(obj2[0], Integer.parseInt((String) (baseAddressOfEachModule.get(baseAddressOfEachModule.size()-2))));
					symbolStatus.put(obj2[0], 2);
				}
			}
			//if defination is not used set the status 1
			else{
				unUsedDefInModules.add(obj2[0]);
			}
			//Outputting symbol table
			if(symbolStatus.get(obj2[0])==0){
				System.out.println(symbolTableItem);				
			}
			else if(symbolStatus.get(obj2[0])==1){
				System.out.println(symbolTableItem +" Error: This variable is multiply defined; first value used.");
			}
			else{
				System.out.println(symbolTableItem +" Error: Definition exceeds module size; zero used.");
			}
		}
	}
	//first pass, processing program text
	private static void _2ndpass(ArrayList list){
		System.out.println("Memory Map");
		//read in string as the text
		programText=list;
		int NP;
		for(int i=0;i<programText.size();i++){
			int relativeAddress=0;
			//split the NP and text
			String numAndText[]=(((String) programText.get(i)).trim()).split("\\s+",2);
			String Text[]=(numAndText[1].trim()).split("\\s+");
			NP=Integer.parseInt(numAndText[0]);
			//read NP pairs
			for(int j=0;j<2*NP;j++){
				//If an address in a definition exceeds the size of the module
				if(useListModules.containsKey(i+1)){
					//get the use list of this module
					LinkedHashMap <Integer,String> useListInThisModule=useListModules.get(i+1);
					//check the keys of the use list, keys are external address
					Collection useListInThisModuleEntry = useListInThisModule.entrySet();
					for(Iterator iterator=useListInThisModuleEntry.iterator(); iterator.hasNext();){
						Object useListInThisModuleItem = iterator.next();
						String useListAddressAndSymbol[]=useListInThisModuleItem.toString().split("=");
						int useListAddress = Integer.parseInt(useListAddressAndSymbol[0]);
						//if one use exceeds the module size
						if(useListAddress>NP){
							//Put into the useExcceedsModuleSize map for further outputting
							useExcceedsModuleSize.put(useListAddressAndSymbol[1], i+1);
						}
					}
				}
				int absoluteAddress=baseAddress+relativeAddress;
				//resolving words
				//case absolute address
				if(Text[j].equals("A")){
					int words = Integer.parseInt(Text[j+1]);
					//if absolute address exceeds machine size, set 0
					if((words%1000)>200){
						System.out.println(absoluteAddress+":"+(words-words%1000)+" Error: Absolute address exceeds machine size; zero used.");
					}
					else{
						System.out.println(absoluteAddress+":"+Text[j+1]);
					}
				}
				//case internal address
				if(Text[j].equals("I")){
					System.out.println(absoluteAddress+":"+Text[j+1]);
				}
				//case relative address
				if(Text[j].equals("R")){
					int a =Integer.parseInt(Text[j+1]);
					int b = a%1000+baseAddress;
					//if relative address exceeds module size, set value 0
					if(b<Integer.parseInt((String) baseAddressOfEachModule.get(baseAddressOfEachModule.size()-1))){
						System.out.println(absoluteAddress+":"+(Integer.parseInt(Text[j+1])+baseAddress));						
					}
					else{
						System.out.println(absoluteAddress+":"+(a-a%1000)+" Error: Relative address exceeds module size; zero used.");
					}
				}
				//case external address
				if(Text[j].equals("E")){
					int b = Integer.parseInt(Text[j+1]);
					//if symbol is defined
					if(symbolList.contains((CharSequence) useListModules.get(i+1).get(j/2))){
						//find the address by getting the table index through values of uselist in one module
						int symbolTableAddress = symbolTable.get(useListModules.get(i+1).get(j/2));
						int c=b-b%1000+symbolTableAddress;
						//if one instruction contains multiple symbol use the first one
						if((useListStatusModules.get(i+1).get(j/2)).equals(1)){
							System.out.println(absoluteAddress+":"+c+" Error: Multiple variables used in instruction; all but first ignored.");						
						}
						else{
							System.out.println(absoluteAddress+":"+c);
						}
					}
					//if symbol is not defined
					else{
						System.out.println(absoluteAddress+":"+(b-b%1000)+" Error: "+useListModules.get(i+1).get(j/2)+" is not defined; zero used.");		
					}
				}
				j++;
				relativeAddress++;
			}
			baseAddress+=NP;
		}
		finalErrorInput();
	}
	
	private static void finalErrorInput(){
		System.out.println();
		for(int i=0;i<unUsedDefInModules.size();i++){
			System.out.println("Warning:"+ unUsedDefInModules.get(i)+ " was defined in module "+ symbolDefinedInModule.get(unUsedDefInModules.get(i)) +" but never used.");
		}
		if(useExcceedsModuleSize.size()!=0){
			Collection useExcceedsModuleSizeEntry = useExcceedsModuleSize.entrySet();
			for(Iterator iterator = useExcceedsModuleSizeEntry.iterator();iterator.hasNext();){
				Object useExcceedsModuleSizeItem=iterator.next();
				String useExcceedsModuleSizeOutput[]=useExcceedsModuleSizeItem.toString().split("=");
				System.out.println("Use of "+useExcceedsModuleSizeOutput[0]+" in module "+useExcceedsModuleSizeOutput[1]+" exceeds module size; use ignored.");
			}	
		}
	}
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		//# of module
		int serialOfModules=1;
		//# of line number
		int linenum=0;
		baseAddressOfEachModule.add("0");
		//file path:
		System.out.println("Please Input The Input File Path:");
		Scanner input = new Scanner(System.in);
		String path = input.next();
		System.out.println("Successfully read in file, begin processing modules...");
		//read in file
		BufferedReader br = new BufferedReader(new InputStreamReader(   
              new FileInputStream(path)));   
        for (String line = br.readLine(); line != null; line = br.readLine()) {
        	//remove null line
        	if(!line.equals("")){
        		//1st line is defination, 2nd is use list and 3rd is program text. Each module takes 3 lines.
        		switch(linenum%3){
        			case 0: {
        				if((line.trim()).equals("0")){
        				}
        				else{
        					//split into 2 parts, first is the defination number and last string is the defination pair
	        				String symbolTable[]=(line.trim()).split("\\s+", 2);
	        				getSymbolTable(symbolTable[1],serialOfModules);
        				}
        				break;
        			}
        			case 1: {
        				if((line.trim()).equals("0")){
        				}
        				else{
        					//split into 2 parts, first is the use list number and last string is the defination pair
	        				String useListString[]=(line.trim()).split("\\s+", 2);
	        				getuseList(useListString[0].trim(),useListString[1].trim(),serialOfModules);
        				}
        				break;
        			}
        			case 2: {
        				programText.add(line.trim());
        				String addressNum[]=(line.trim()).split("\\s+");
        				int address = Integer.parseInt(addressNum[0]) + Integer.parseInt((String) baseAddressOfEachModule.get(baseAddressOfEachModule.size()-1));
        				String addressString = String.valueOf(address);
        				baseAddressOfEachModule.add(addressString);
        				serialOfModules++;
        				break;
        			}
        		}
        		linenum++;
        	}
        } 
        br.close();
        _1stpass(symbolTable,symbolStatus);
        _2ndpass(programText);
	}       
}