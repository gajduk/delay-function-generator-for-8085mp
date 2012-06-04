
public class DelayFunction extends Executable {
	
	/**
	 * instructions used to do some initializing such as storing some values of elements on stack so they
	 *  may be recovered at later times, while the instructions get to utilize those required elements
	 */
	Executable init_instructions;
	
	/**
	 * the main body of the delay functions, these instructions are the main contributors to the time of execution of the function
	 */
	Executable main_instructions;
	
	/**
	 * instructions used to wrap up the processing in the delay function, usually by restoring the state
	 *  of the system elements as they were before invoking the delay functions
	 */
	Executable finalize_instrucions;
	
	/**
	 * RET instruction or its equivalent, for returning to the main program routine
	 */
	Executable return_instruction;
	
	

	/**
	 * how long will this function take to fully execute
	 * the sum of the times for all instruction groups and loop in the function
	 * @return time in T - number of states of the mP
	 */
	@Override
	public int time() {
		int result = 0;
		result += init_instructions != null?init_instructions.time():0;
		result += main_instructions != null?main_instructions.time():0;
		result += finalize_instrucions != null?finalize_instrucions.time():0;
		result += return_instruction != null?return_instruction.time():0;
		return result;
	}

}
