
/**
 * a single instruction defined in the instruction set manual
 * @author Andrej Gajduk
 *
 */
public class Instruction extends Executable {
	/**
	 * the code that fully describes this instruction
	 */
	private String instruction_code;
	
	
	public Instruction(String string) {
		instruction_code = string;
	}


	/**
	 * how long will this instruction take to fully execute,
	 * because it's a single instruction only read the data from the InstructionSet
	 * @return time in T - number of states of the mP
	 */
	@Override
	public int time() {
		return InstructionSet.getInstance().getTime(instruction_code);
	}

	

}
