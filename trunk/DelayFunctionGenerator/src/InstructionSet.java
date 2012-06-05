import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;


/**
 * object containing all the instructions and their definitions, as defined in the manual
 * @author Andrej Gajduk
 *
 */
public class InstructionSet {
	private static InstructionSet instruction_set;
	
	
	/**
	 * all the instructions that make up this instruction set,
	 * fully described as stated in the InstructionMetadata class
	 */
	private ArrayList<InstructionMetadata> instructions;

	
	/**
	 * private constructor
	 * only to be invoked from the getInstance method for the first initialization of the instruction set 
	 */
	protected InstructionSet() {
		instructions = new ArrayList<InstructionMetadata>();
		FileInputStream reader = null;
		try {
			reader = new FileInputStream("Instruction Metadata.txt");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		Scanner in = new Scanner(reader);
		boolean instruction_flag = false;
		
		//read the entire flag
		while ( in.hasNext() ) {
			String line = in.nextLine();
			if ( ! instruction_flag ) {
				//a flag for where the data begins
				if ( line.equals("BEGIN") ) {
					instruction_flag = true;
				}
			}
			else {
				//a flag for where the data ends
				if ( line.equals("END") ) {
					instruction_flag = false;
				}
				
				//just making sure it is not an empty line
				if ( line.length() > 3 ) {
					instructions.add(new InstructionMetadata(line));
				}
			}			
		}
		
	}
	
	/**
	 * get an instance of the instruction set, singleton pattern, global data entry point
	 * use only a single instruction set object
	 * @return instance of the instruction set
	 */
	public static InstructionSet getInstance() {
		if ( instruction_set == null ) {
			instruction_set = new InstructionSet();
		}
		return instruction_set;
	}
	
	/**
	 * used only representing the instruction data, for debugging purposes
	 */
	public String getAllInstructionDescription () {
		String res = "";
		for ( InstructionMetadata instr : instructions ) {
			res += instr.toString()+" \n";
		}
		return res;
	}
	
	public static void main(String[] args) {
		InstructionSet set = InstructionSet.getInstance();
		System.out.println(set.getAllInstructionDescription());
	}
	
	/**
	 * determine the time needed to execute a specific instruction according to the instruction manual
	 * @param description - of the instruction we are trying to evaluate duration of
	 * @return time in T-number of states of the mP, -1 if the instruction parameter could not be found in the instruction set
	 */
	public int getTime ( String description ) {
		//To do read form map or something
		return 0;
	}
	
	/**
	 * used to determine the longest possible execution time I can achieve with a number of commands
	 * the longest command is not always, used this is just a method to determine do-ability e.g.
	 * 	I can use max up to 5 instructions, can I make them last 5 seconds, 
	 * 	if the value returned by this function is > 1s than yes, and I try to create one such
	 *  else I conclude that no such function exists or try with loops  
	 * @param elements_usable - elements that can be used by instructions
	 * @return - the time of execution of  longest instruction that can be executed with only the usable_elements
	 */
	public int getMaxTime( Element elements_usable[] ) {
		//iterate throw all the instructions and get the longest one that can be executed with only affecting the usable_elements
		return -1;
	}
	
	/**
	 * returns the set specific initialization instruction code segment
	 * e.g. for 8085 
	 * 	MOV R,init_value
	 * @param init_value - the value that the instructions utilize to init the element needed to form the loop
	 * @param element_usable - the element that should be initialized and later used for loop logic control
	 * @return a code segment used to init the elements for a loop
	 */
	public Executable getInitSegment( int init_value , Element element_usable ) {
		//give back the set specific initialization instruction code segment
		return null;
	}
	
	/**
	 * returns the set specific decrement instruction code segment
	 * e.g. for 8085 
	 * 	DCR R 
	 * @param element_usable - the element used for loop logic control
	 * @return a code segment used to decrement the elements for a loop
	 * *NOTICE:always should this segment be right before the condition-checking segment
	 */
	public Executable getDecSegment( Element element_usable ) {
		//give back the set specific decrementing instruction code segment
		return null;
	}
	
	/**
	 * returns the set specific condition-checking instruction code segment
	 * e.g. for 8085 
	 * 	JNZ loop_start_label
	 * @param element_usable - the element used for loop logic control
	 * @param loop_start_label - the label declaring where to jump to continue with the loop
	 * @return a code segment used to condition-checking the elements for a loop
	 * *NOTICE:always should this segment be right after the decrement segment
	 */
	public Executable getCondSegment( Element element_usable , String loop_start_label ) {
		//give back the set specific condition-checking instruction code segment
		return null;
	}
	
	/**
	 * returns the set specific return instruction
	 * e.g. for 8085 
	 * 	RET
	 * @return a code segment used to return to the main routine and declaring an end to the delay function
	 * *NOTICE:always should this segment be last in a function
	 */
	public Executable getReturnSegment(  ) {
		//give back the set specific condition-checking instruction code segment
		return null;
	}

	/**
	 * simply the highest integer value this mP can support
	 * @return - the longest possible loop length 
	 */
	public int getMaxInitValue() {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
