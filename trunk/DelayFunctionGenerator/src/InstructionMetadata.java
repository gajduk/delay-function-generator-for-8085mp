import java.util.StringTokenizer;



/**
 * used to represent the instruction constituting ones instructions set
 * Completely describes one instruction by specifying the following information:
 * 
 * 1) instruction code as seen in assembler compiler
 * 2) duration in T states
 * 3) size in bytes
 * 4) REGS affected
 * 5) number of memory locations needed 0, or 2
 * 6) is the stack pointer read or changed, and/or is the stack contents changed
 *
 * the information 4-6 will be represented by a single object of the class Elements
 * 
 * *IMPORTANT NOTICE: this is different then the Instruction class that represents an abstract formulation of an executable 
 * command in an assembler compiler.
 * @author Andrej Gajduk
 *
 */
public class InstructionMetadata {
	
	private String instruction_code;
	
	private int duration;
	
	private int size;
	
	private Elements elements_affected;
	
	private boolean to_use;
	
	public InstructionMetadata () {
		
	}
	
	/**
	 * use this constructor to make an InstructionMetadata object from the description read from a text file
	 * the format of the data represented is given in the text file
	 * @param instruction_description - the one-line description of an instruction in specified format
	 */
	public InstructionMetadata ( String instruction_description ) {
		StringTokenizer tkr = new StringTokenizer(instruction_description,";");
		instruction_code = tkr.nextToken().trim();
		duration = Integer.parseInt(tkr.nextToken().trim());
		size = Integer.parseInt(tkr.nextToken().trim());
		elements_affected = new Elements(tkr.nextToken().trim(), tkr.nextToken().trim(), tkr.nextToken().trim());
		to_use = tkr.nextToken().trim().equals("-")?false:true;
	}

	public InstructionMetadata(String instruction_code, int duration, int size,
			Elements elements_affected , boolean to_use ) {
		this.instruction_code = instruction_code;
		this.duration = duration;
		this.size = size;
		this.elements_affected = elements_affected;
		this.to_use = to_use;
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
	 * getters and setters
	 */
	
	
	
	public String getInstruction_code() {
		return instruction_code;
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

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public Elements getElements_affected() {
		return elements_affected;
	}

	public void setElements_affected(Elements elements_affected) {
		this.elements_affected = elements_affected;
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

}
