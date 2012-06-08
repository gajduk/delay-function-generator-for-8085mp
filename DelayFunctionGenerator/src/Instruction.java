
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
	
	/**
	 * the duration in T cycles of the instruction
	 */
	private int duration;
	private static final int duration_not_set = -1;
	
	/**
	 * a constructor that only sets the instruction op-code,
	 * use when you don't know the duration, 
	 * or the duration doesn't matter
	 * @param instruction_code - the instruction code as seen in assembler compiler
	 */
	public Instruction(String instruction_code) {
		this.instruction_code = instruction_code;
		duration = duration_not_set;
	}
	
	/**
	 * a full all-fields constructor
	 * @param instruction_code - the instruction code as seen in assembler compiler
	 * @param duration - in T states of the mP
	 */
	public Instruction(String instruction_code , int duration) {
		this.instruction_code = instruction_code;
		this.duration = duration;
	}


	/**
	 * how long will this instruction take to fully execute,
	 * because it's a single instruction just return the duration
	 * @return time in T - number of states of the mP
	 */
	@Override
	public int time() {
		if ( duration == duration_not_set ) {
			System.out.println("You just read an unset duration of the instruction "+instruction_code+" any further calculations is wrong");
		}
		return duration;
	}

	@Override
	public String toString() {
		return instruction_code;
	}


	@Override
	public int length() {
		return 1;
	}
	
	@Override
	public boolean equals(Object obj) {
		if ( obj.getClass().equals(this.getClass() ) ) {
			return instruction_code.equals(((Instruction) obj).instruction_code);
		}
		return false;
	}
	

}
