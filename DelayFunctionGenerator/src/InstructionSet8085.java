import javax.activation.CommandInfo;


/**
 * an object for the 8085 instruction set
 * knows all instructions, their execution times and elements they affect
 * @author Andrej Gajduk
 *
 */
public class InstructionSet8085 extends InstructionSet {
	
	private static InstructionSet8085 instance;
	
	private InstructionSet8085 () {
		//initialize the set probably form a file
	}
	
	public static InstructionSet8085 getInstance() {
		if ( instance == null ) {
			instance = new InstructionSet8085();
		}
		return instance;
	}
	
	@Override
	public int getTime(String description) {
		// TODO Auto-generated method stub
		return super.getTime(description);
	}
	
	/**
	 * used to determine the longest possible execution time I can achieve with a number of commands
	 * the longest command is not always, used this is just a method to determine do-ability e.g.
	 * 	I can use max up to 5 instructions, can I make them last 5 seconds, 
	 * 	if the value returned by this function is > 1s than yes, and I try to create one such function
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
		return new Instruction("MOV "+element_usable.toString()+","+init_value);
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
		return new Instruction("DCR "+element_usable.toString());
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
		return new Instruction("JNZ "+loop_start_label);
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
		return new Instruction("RET");
	}
	
	/**
	 * simply the highest integer value this mP can support
	 * @return - the longest possible loop length 
	 */
	public int getMaxInitValue() {
		return 255;
	}
	

}
