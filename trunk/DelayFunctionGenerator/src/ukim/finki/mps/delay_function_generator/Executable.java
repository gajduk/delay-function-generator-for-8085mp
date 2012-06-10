package ukim.finki.mps.delay_function_generator;

/**
 * defines the interface for any instruction or group of instructions
 * @author Andrej Gajduk
 *
 */
public abstract class Executable {
	
	/**
	 * elements this instruction affects such as registers or memory locations
	 * they should not be used by any other instruction in the delay function, 
	 * and should be declared available for the delay function,
	 * in the case of registers explicitly condition-less,
	 * in the case of memory the function should prompt the user, for the addresses and amount
	 */
	private Element used_elements;
	
	/**
	 * how long will this take to fully execute
	 * @return time in T- number of states of the mP
	 */
	public abstract long time();

	/**
	 * how many instructions are in this executable
	 * @return number of instructions
	 */
	public abstract int length();
	

	
}
