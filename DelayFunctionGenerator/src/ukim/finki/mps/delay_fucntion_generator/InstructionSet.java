package ukim.finki.mps.delay_fucntion_generator;
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
	
	/**
	 * describes the mP this instruction set refers to
	 */
	String mP;
	
	/**
	 * the only instance of the instruction set that will ever be used
	 */
	private static InstructionSet instruction_set;
	
	/**
	 * all the instructions that make up this instruction set,
	 * fully described as stated in the InstructionMetadata class
	 */
	private ArrayList<InstructionData> instructions;

	/**
	 * @return the instructions
	 */
	public ArrayList<InstructionData> getInstructions() {
		return instructions;
	}

	/**
	 * @param instructions the instructions to set
	 */
	public void setInstructions(ArrayList<InstructionData> instructions) {
		this.instructions = instructions;
	}

	/**
	 * private constructor
	 * only to be invoked from the getInstance method for the first initialization of the instruction set 
	 */
	protected InstructionSet( String mP ) {
		this.mP = mP;
		instructions = new ArrayList<InstructionData>();
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
					instructions.add(new InstructionData(line));
				}
			}			
		}
		
	}
	
	/**
	 * get an instance of the instruction set, global data entry point
	 * use only a single instruction set object
	 * @return instance of the instruction set
	 */
	public static InstructionSet getInstance( String mP ) {
		if ( instruction_set == null || instruction_set.mP == null ||
					instruction_set.mP.equals(mP) ) {
			instruction_set = new InstructionSet(mP);
		}
		return instruction_set;
	}
	
	/**
	 * used only for representing the instruction data, for debugging purposes
	 */
	public String getAllInstructionDescription () {
		String res = "";
		for ( InstructionData instr : instructions ) {
			res += instr.toString()+" \n";
		}
		return res;
	}
	
	/**
	 * given the instruction_code return the InstructionData object that it corresponds to
	 * @param string - instruction code as seen in assembler compiler
	 * @return InstructionData for the given instruction code, null if no such instruction exists
	 * *FURTHER NOTICE:this method is rarely called, otherwise it will have to be accelerated by using a HashMap or a Trie
	 */
	public InstructionData getInstruction(String string) {
		for ( InstructionData ins : instructions ) {
			if ( ins.getInstruction_code().equals(string) )
				return ins;
		}
		return null;
	}

	/**
	 * defines for loop_type
	 *8bit uses: MVI , DCR
	 *16bit uses: LXI , DCX
	 */
	public static final int loop_type_16b = 0;
	/**
	 * defines for loop_type
	 *8bit uses: MVI , DCR
	 *16bit uses: LXI , DCX
	 */
	public static final int loop_type_8b = 1;
	
	/**
	 * returns the set specific initialization instruction duration
	 * @param loop_type - specifies whether we are making a 8bit loop or a 16bit loop
	 * @return duration in T states of the mP
	 */
	public int getInitInstructionsDuration( int loop_type ) {
		if ( loop_type == loop_type_8b  ) return 4; else
		if ( loop_type == loop_type_16b ) return 7; else
		return 0;
	}
	
	/**
	 * returns the set specific loop-logic instruction duration
	 * @param loop_type - specifies whether we are making a 8bit loop or a 16bit loop
	 * @return duration in T states of the mP
	 */
	public int getLoopLogicInstructionsDuration( int loop_type ) {
		if ( loop_type == loop_type_8b  ) return 14; else
		if ( loop_type == loop_type_16b ) return 16; else
		return 0;
	}

	/**
	 * returns the set specific initialization instruction duration
	 * e.g. for 8085 
	 * 	RET
	 * @return executable representation of this set return statement
	 */
	public Executable getRetInstrcution( ) {
		return getInstruction("RET").getExecutable();
	}
	
	/**
	 * returns the set specific initialization instruction code segment
	 * e.g. for 8085 
	 * 	DCR R 
	 * @param loop_type - specifies whether we are making a 8bit loop or a 16bit loop
	 * @param reg_or_reg_pair - the register element which is participating in the loop
	 * @param init_value - this value is to be set to the register specified
	 * @return a code segment used to initialize the elements for  loop-logic
	 */
	public Executable getInitInstrcution( int loop_type , String reg_or_reg_pair , int init_value ) {
		if ( loop_type == loop_type_8b ) {
			return getInstruction("MVI "+reg_or_reg_pair+",8b").getExecutable("8b",Integer.toString(init_value));
		}
		else if (  loop_type == loop_type_16b )
			return getInstruction("LXI "+reg_or_reg_pair+",16b").getExecutable("16b",Integer.toString(init_value));
		else return null;
	}
	
	/**
	 * returns the set specific decrementing instruction code segment
	 * e.g. for 8085 
	 * 	DCR R 
	 * @param loop_type - specifies whether we are making a 8bit loop or a 16bit loop
	 * @return a code segment used to decrement the elements for loop-logic
	 */
	public Executable getDcrInstrcution( int loop_type , String reg_or_reg_pair ) {
		if ( loop_type == loop_type_8b ) 
			return getInstruction("DCR "+reg_or_reg_pair).getExecutable();
		else if (  loop_type == loop_type_16b )
			return getInstruction("DCX "+reg_or_reg_pair).getExecutable();
		else return null;
	}
	
	/**
	 * returns the set specific condition checking instruction code segment
	 * e.g. for 8085 
	 * 	DCR R 
	 * @param loop_type - specifies whether we are making a 8bit loop or a 16bit loop
	 * @return a code segment used to check the condition for  loop-logic
	 */
	public Executable getCondInstruction ( int loop_type , String location_label ) {
		if ( loop_type == loop_type_8b || loop_type == loop_type_16b ) 
			return getInstruction("JNZ 16b").getExecutable("16b",location_label);
		else return null;
	}
	
	private String getRegPairForPushingPoping( String reg ) {
		if ( reg.equals("B") | reg.equals("C") ) return "B";
		if ( reg.equals("D") | reg.equals("E") ) return "D";
		if ( reg.equals("H") | reg.equals("L") ) return "L";
		if ( reg.equals("A") ) return "PSW";
		return "";
	}

	/**
	 * returns the set specific pop instruction code segment
	 * e.g. for 8085 
	 * 	POP R 
	 * @param reg - the register we are trying to pop
	 * @return a code segment used to make a pop from stack to the given reg
	 */
	public Executable getPopInstruction(String reg) {
		return instruction_set.getInstruction("POP "+getRegPairForPushingPoping(reg)).getExecutable();
	}

	/**
	 * returns the set specific push instruction code segment
	 * e.g. for 8085 
	 * 	PUSH R 
	 * @param reg - the register we are trying to push
	 * @return a code segment used to make a push on the stack of the given reg
	 */
	public Executable getPushInstruction(String reg) {
		return instruction_set.getInstruction("PUSH "+getRegPairForPushingPoping(reg)).getExecutable();
	}

	/**
	 * returns the set specific move instruction code segment
	 * e.g. for 8085 
	 * 	MOV Rd,Rs 
	 * @param dest - the register we are copying to
	 * @param src - the register we are copying from
	 * @return a code segment used to copy a value
	 */
	public Executable getMoveInstruction(String dest, String src) {
		return instruction_set.getInstruction("MOV "+dest+","+src).getExecutable();
	}

	/**
	 * returns the set specific instruction code segment for reading from memory
	 * e.g. for 8085 
	 * 	LDA memory
	 * @param memory_location - the memory address we are reading from to
	 * @return a code segment used to read from memory
	 */
	public Executable getLoadInstruction( String memory_location ) {
		return instruction_set.getInstruction("LDA 16b").getExecutable("16b",memory_location);
	}

	/**
	 * returns the set specific instruction code segment for writing to memory
	 * e.g. for 8085 
	 * 	LDA memory
	 * @param memory_location - the memory address we are writing to
	 * @return a code segment used to write to memory
	 */
	public Executable getStoreInstruction( String memory_location ) {
		return instruction_set.getInstruction("STA 16b").getExecutable("16b",memory_location);
	}

	/**
	 * this function will take all the arguments, separate the instructions that we can use out of the whole instruction set of the mP
	 * with respect to the elements_usable, 
	 * @param instruction_set - an object describing the instruction set of a given mP, it contains all the instructions fully described
	 * @param elements_usable - descriptors for all the elements that are available to the function to operate with,
	 * 		  such as registers and memory locations
	 * @return - array list of instructions descriptions
	 * *IMPORTANT NOTICE: if there are two instruction or  more each of duration 'k' then only one instruction will be taken in consideration
	 * we don;t need any repetition 
	 */
	public ArrayList<InstructionData> getAllAvailableInstructions( Element[] elements_usable) {
		ArrayList<InstructionData> res = new ArrayList<InstructionData>();
		Elements comp = new Elements(elements_usable);
		boolean[] already_have = new boolean[20];
		for ( InstructionData i : instruction_set.getInstructions() ) {
			if ( i.isTo_use() && i.isAvailable(comp) && !already_have[i.getDuration()])  {
				res.add(i);
				already_have[i.getDuration()] = true;
			}
		}
		return res;
	}
	

}
