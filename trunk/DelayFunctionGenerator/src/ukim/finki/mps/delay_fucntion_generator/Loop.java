package ukim.finki.mps.delay_fucntion_generator;

/**
 * a loop is defined:
 * 
 * 					 init_instructions
 * loop_start_label: other_instructions
 * 					 dcr_instructions
 * 					 cond_instruction
 * 
 * 
 * for the purpose of reducing the probability of endless loops,
 *  we constraint the possible instructions in each group
 *  
 * all loops should follow the next structure
 * 
 * 					 MOV R,value
 * loop_start_label: other_instructions
 * 				     DCR R
 * 				     JNZ loop_start_label
 * 
 * loop_end_label: some other code
 * 
 * *IMPORTANT NOTICE: the minimum size of the loop is 3 instructions
 * **IMPORTANT NOTICE: the A register must be available for the loop, otherwise the date needs to be previously - not relevant 
 *  stored somewhere else, so that we can recreate the state of the program as it was before the loop got invoked
 * *Notice: the MOV A,R instruction is to be used always even when the R register used is the A register,
 * **Further Notice: the previous notice may need to be changed in cases 
 * when we have problems with the number of instructions or registers availability
 * but for now it stays that way.
 * 
 * @author Andrej Gajduk
 *
 */
public class Loop extends Executable {
	
	/**
	 * a group of instructions used to initialize a variable/register,
	 *  this determines the iterations done over the other instruction groups
	 *  does not influence significantly the time to execute the loop directly but by value,
	 *  *IMPORTANT NOTICE: must be present for the loop to function properly
	 */
	public Executable init_instructions;
	
	/**
	 * the instruction used to decrement a variable/register,
	 * always use the DCR or DCX functions to limit the probability to end up with an endless loop
	 * *IMPORTANT NOTICE: must be present for the loop to function properly
	 */
	public Executable dcr_instructions;
	
	/**
	 * the instruction that will determine the end of the loop
	 * always consisting of three commands, get the value you need to check in the A register
	 * compare the value to some other value (always 0 for now) by SUI value
	 * then use the JNZ loop_start to get back in the loop for further processing 
	 * *IMPORTANT NOTICE: must be present for the loop to function properly
	 */
	public Executable cond_instructions;
	
	/**
	 * other instruction used only to provide more time for the loop iterations
	 * so we don't have too many nested loops,
	 * use this as a better solution whenever possible
	 */
	public Executable other_instructions;

	/**
	 * just a label for where the loop starts
	 */
	public String label;
	
	/**
	 * the number of iterations this loops is supposed to process through before exiting,
	 * this value is the same as the init value in the init_instrucion
	 */
	public int iterations;
	
	/**
	 * the time it takes this loop to fully execute
	 * by constraining the instructions used in each instruction group
	 * @return time in T-states of the mP
	 */
	@Override
	public long time() {
		long result = 0;
		result += init_instructions!=null?init_instructions.time():0;
		result += iterations*singleIterationTime();
		return result-3;
	}
	
	/**
	 * the time it takes for a single iteration to execute
	 * @return time in T-states of the mP
	 */
	public long singleIterationTime() {
		long result = 0;
		result += other_instructions!=null?other_instructions.time():0;
		result += dcr_instructions!=null?dcr_instructions.time():0;
		result += cond_instructions!=null?cond_instructions.time():0;
		return result;
	}

	/**
	 * how many instructions are there in this group
	 * the sum of the times for all instructions in this group
	 * @return the number of instructions
	 */
	@Override
	public int length() {
		int result = 0;
		result += init_instructions!=null?init_instructions.length():0;
		result += other_instructions!=null?other_instructions.length():0;
		result += dcr_instructions!=null?dcr_instructions.length():0;
		result += cond_instructions!=null?cond_instructions.length():0;
		return result;
	}
	
	@Override
	public String toString() {
		String result = "";
		result += init_instructions!=null?init_instructions.toString()+"\n":"";
		result += label+":";
		result += other_instructions!=null?other_instructions.toString()+"\n":"";
		result += dcr_instructions!=null?dcr_instructions.toString()+"\n":"";
		result += cond_instructions!=null?cond_instructions.toString()+"\n":"";
		return result;
	}
	
}
