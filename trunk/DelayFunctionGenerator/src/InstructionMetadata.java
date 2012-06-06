import java.util.StringTokenizer;



/**
 * used to represent the instruction constituting ones instructions set
 * Completely describes one instruction by specifying the following information:
 * 
 * 1) instruction code as seen in assembler compiler
 * 2) length in T states
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
	
	private int length;
	
	private int size;
	
	private Elements elements_affected;
	
	public InstructionMetadata () {
		
	}
	
	/**
	 * use this constructor the make an InstructionMetadata object from the description read from a text file
	 * the format of the data represented is given in the text file
	 * @param instruction_description - the one-line description of an instruction in specified format
	 */
	public InstructionMetadata ( String instruction_description ) {
		StringTokenizer tkr = new StringTokenizer(instruction_description,";");
		instruction_code = tkr.nextToken().trim();
		length = Integer.parseInt(tkr.nextToken().trim());
		size = Integer.parseInt(tkr.nextToken().trim());
		elements_affected = new Elements(tkr.nextToken().trim(), tkr.nextToken().trim(), tkr.nextToken().trim());
	}

	public InstructionMetadata(String instruction_code, int length, int size,
			Elements elements_affected) {
		this.instruction_code = instruction_code;
		this.length = length;
		this.size = size;
		this.elements_affected = elements_affected;
	}

	
	/**
	 * use this for a short overview of the instruction
	 */
	@Override
	public String toString() {
		return  instruction_code;
	}
	
	/**
	 * use this for a full description
	 */
	/*
	@Override
	public String toString() {
		return  "Instruction: "+instruction_code + "\n"+
				"Length in T-states: "+length + "\n"+
				"Size in bytes: "+size+ "\n"+
				"Elements_affected: "+elements_affected.toString() + "\n";
	}
	*/
	
	/**
	 * getters and setters
	 */
	
	public String getInstruction_code() {
		return instruction_code;
	}

	public void setInstruction_code(String instruction_code) {
		this.instruction_code = instruction_code;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
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
	
}
