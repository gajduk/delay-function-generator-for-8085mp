package ukim.finki.mps.delay_fucntion_generator;
import java.util.StringTokenizer;

/**
 * used to represent the instructions constituting ones instructions set
 * Completely describes one instruction by specifying the following information:
 * 
 * 1) instruction code as seen in assembler compiler
 * 2) duration in T states
 * 3) size in bytes
 * 4) REGS affected
 * 5) number of memory locations needed 0, or 2
 * 6) is the stack pointer read or changed, and/or is the stack contents changed
 * 7) is this instruction free to be used from the auto-generating code builder
 *
 * the information 4-6 will be represented by a single object of the class Elements
 * 
 * *IMPORTANT NOTICE: this is different then the Instruction class that represents an abstract formulation of an executable 
 * command in an assembler compiler.
 * @author Andrej Gajduk
 *
 */
public class InstructionData {
	
	/**
	 * instruction code as seen in assembler compiler
	 */
	private String instruction_code;
	
	/**
	 * duration in T states of the mP
	 */
	private int duration;
	
	/**
	 * size in bytes
	 */
	private int size;
	
	/**
	 * 4) REGS affected
	 * 5) number of memory locations needed 0, or 2
	 * 6) is the stack pointer read or changed, and/or is the stack contents changed
	 */
	private Elements elements_affected;
	
	/**
	 * is this instruction free to be used from the auto-generating code builder
	 */
	private boolean to_use;
	
	/**
	 * use this constructor to make an InstructionMetadata object from the description read from a text file
	 * the format of the data represented is given in the text file
	 * @param instruction_description - the one-line description of an instruction in specified format
	 */
	public InstructionData ( String instruction_description ) {
		StringTokenizer tkr = new StringTokenizer(instruction_description,";");
		instruction_code = tkr.nextToken().trim();
		duration = Integer.parseInt(tkr.nextToken().trim());
		size = Integer.parseInt(tkr.nextToken().trim());
		elements_affected = new Elements(tkr.nextToken().trim(), tkr.nextToken().trim(), tkr.nextToken().trim());
		to_use = tkr.nextToken().trim().equals("-")?false:true;
	}

	@Override
	public String toString() {
		/**
		 * use this for a short overview of the instruction
		 */
		return  instruction_code;
		/**
		 * use this for a full description
		 */
		/*
		return  "Instruction: "+instruction_code + "\n"+
		"Length in T-states: "+length + "\n"+
		"Size in bytes: "+size+ "\n"+
		"Elements_affected: "+elements_affected.toString() + "\n";
		*/
	}
	
	/**
	 * returns an Executable object representing this instruction,
	 * usually this will be a single instruction
	 * if there is some immediate data we need to replace, replace them we shall with random values
	 * @return an executable - that corresponds to this instruction metadata
	 */
	public Executable getExecutable () {
		Executable exec = null;
		if ( instruction_code.contains("8b") ) {
			exec = getExecutable("8b", Integer.toString((int)(Math.random()*(1<<8))));
		} else if ( instruction_code.contains("16b") ) {
			exec = getExecutable("16b", Integer.toString((int)(Math.random()*(1<<8))));
		}
		else {
			exec = new Instruction(instruction_code,duration);
		}
		return exec;
	}
	
	/**
	 * returns an Executable object representing this instruction,
	 * usually this will be a single instruction,
	 * @param value - the int value to be set as the data
	 * @return an executable - that corresponds to this instruction metadata
	 * *IMPORTANT NOTICE: this method should be used for all instructions where we have some imidiate
	 * data that needs to be set in the instruction code itself
	 */
	public Executable getExecutable ( String data_regex ,String value ) {
		return new Instruction(instruction_code.replaceAll(data_regex,value),duration);
	}

	/**
	 * a function used to separate available instructions from the whole instruction set,
	 * it checks whether the given comparator object contains all the resources required for a given instruction to execute
	 * @param comparator - an object describing the available resources
	 * @return - boolean value - can we use this instruction, with the given resource 
	 */
	public boolean isAvailable ( Elements comparator ) {
		return elements_affected.isAvailable(comparator);
	}

	
	/**
	 * @return the instruction_code
	 */
	public String getInstruction_code() {
		return instruction_code;
	}
	

	/**
	 * @param instruction_code the instruction_code to set
	 */
	public void setInstruction_code(String instruction_code) {
		this.instruction_code = instruction_code;
	}
	

	/**
	 * @return the duration
	 */
	public int getDuration() {
		return duration;
	}
	

	/**
	 * @param duration the duration to set
	 */
	public void setDuration(int duration) {
		this.duration = duration;
	}
	

	/**
	 * @return the size
	 */
	public int getSize() {
		return size;
	}
	

	/**
	 * @param size the size to set
	 */
	public void setSize(int size) {
		this.size = size;
	}
	

	/**
	 * @return the elements_affected
	 */
	public Elements getElements_affected() {
		return elements_affected;
	}
	

	/**
	 * @param elements_affected the elements_affected to set
	 */
	public void setElements_affected(Elements elements_affected) {
		this.elements_affected = elements_affected;
	}
	

	/**
	 * @return the to_use
	 */
	public boolean isTo_use() {
		return to_use;
	}
	

	/**
	 * @param to_use the to_use to set
	 */
	public void setTo_use(boolean to_use) {
		this.to_use = to_use;
	}

}
