import java.util.Arrays;
import java.util.UUID;



/**
 * used to generate single instructions,
 * code segments and loops
 * implements Abstract Factory pattern
 * @author Andrej Gajduk
 */
public class CodeBuilder {
	/**
	 * specifies the instruction set, i.e. the mP for the delay function
	 */
	private InstructionSet instruction_set;
	
	public void setInstructionSet ( InstructionSet instruction_set ) {
		this.instruction_set = instruction_set;
	}
	
	public InstructionSet  getInstructionSet ( ) {
		return instruction_set;
	}
	
	/**
	 * the method stub to be used for generating delay functions,
	 * specifies all the input parameters needed
	 * @param min_time - lower bound for the duration of the delay function 
	 * @param max_time - upper bound for the duration of the delay function
	 * @param mP - string description for the microprocessor which instruction set should be used
	 * @param elements_usable - descriptors for all the elements that are available to the function to operate with,
	 * 		  such as registers and memory locations
	 * @param instruction_limit - the maximum number of instructions that should be used to achieve the required duration,
	 * 		  use -1 if the limit is not specified, and 20 as the default value
	 * @return a code segment that is a function, with adequate labels ending in a RET or an equivalent instruction
	 * 		   the code segment should have a duration time that belongs in the interval [min_time,max_time]
	 * 		   some small probability for changes in duration should be allowed, The delay function used should not be 
	 * 		   using any other elements other than those explicitly specified by the usable_elemnts array, as an addition
	 * 		   the delay function should not be longer than the maximum instruction limit allowed
	 * *further notice: the default instruction limit should be dependent on the duration of the delay function required
	 * **further notice: the error allowed for the duration of the delay function should be reverse-proportional to the interval size
	 * 			
	 */
	public DelayFunction buildDelayFunction ( int min_time , int max_time , String mP , Element elements_usable[] , int instruction_limit ) {
		setInstructionSet(mP);
		int longest_instruction_time = instruction_set.getMaxTime(elements_usable);
		DelayFunction result = new DelayFunction();
		//first determine whether we need a loop
		if ( (min_time / longest_instruction_time)+(min_time%longest_instruction_time==0?0:1) < instruction_limit ) {
			//we don't need a loop just generate a loop-less code segment and finish
			result.main_instructions = buildLooplessInstruciontGroup(min_time, max_time, elements_usable, instruction_limit-1);
			result.return_instruction = instruction_set.getReturnSegment();
			return result;
		}
		//we need to form a loop or several
		result.main_instructions = buildLoop(min_time, max_time, elements_usable, instruction_limit);
		result.return_instruction = instruction_set.getReturnSegment();
		return result;
	}
	
	private void setInstructionSet(String mP) {
		if ( mP.equals("8085") ) {
			instruction_set = InstructionSet8085.getInstance();
		}
	}

	/**
	 * the method stub to be used for generating a loopless code segment
	 * specifies all the input parameters needed
	 * @param min_time - lower bound for the duration of the code segment
	 * @param max_time - upper bound for the duration of the code segment
	 * @param mP - string description for the microprocessor which instruction set should be used
	 * @param elements_usable - descriptors for all the elements that are available to the instructions to operate with,
	 * 		  such as registers and memory locations
	 * @param instruction_limit - the maximum number of instructions that should be used to achieve the required duration,
	 * 		  use -1 if the limit is not specified
	 * @return a code segment with an execution time that falls in the interval [min_time,max_time]
	 * 			
	 */
	public Executable buildLooplessInstruciontGroup ( int min_time, int max_time , Element elements_usable[] , int instruction_limit  ) {
		//TO DO
		return null;
	}
	
	/**
	 * the method stub to be used for generating a loop
	 * specifies all the input parameters needed
	 * @param min_time - lower bound for the duration of the loop
	 * @param max_time - upper bound for the duration of the loop
	 * @param elements_usable - descriptors for all the elements that are available to the loop to operate with,
	 * 		  such as registers and memory locations
	 * @param instruction_limit - the maximum number of instructions that should be used to achieve the required duration,
	 * 		  use -1 if the limit is not specified
	 * @return a loop with an execution time that falls in the interval [min_time,max_time]
	 * *IMPORTANT NOTICE: this function can and will return nested loop structures when required
	 */
	public Executable buildLoop ( int min_time, int max_time, Element elements_usable[] , int instruction_limit ) {
		//first determine if we have enough instructions to actually build the loop
		if ( instruction_limit < 3 ) {
			System.out.println("ERROR. I can't build a loop with just "+instruction_limit+" instructions.");
			return null;
		}
		Loop loop = new Loop();
		//determine which element should be used for the loop background logic control, and remove it for the list of usable elements
		int element_used_index = 0;
		Element element_used = elements_usable[element_used_index];
		Element temp[] = new Element[elements_usable.length-1];
		for ( int i = 0 ; i < temp.length ; ++i ) temp[i] = elements_usable[i-1];
		elements_usable = temp;
		//create the basic loop structure
		loop.init_instructions = instruction_set.getInitSegment(1, element_used);
		loop.dcr_instructions = instruction_set.getDecSegment(element_used);
		loop.cond_instructions = instruction_set.getCondSegment(element_used,UUID.randomUUID().toString());
		
		instruction_limit -= 3;
		//determine if we need to nest in more loops
		int single_iteration_time = loop.singleIterationTime();
		int single_iteration_max_time = single_iteration_time+instruction_set.getMaxTime(elements_usable)*instruction_limit;
		int min_iterating_time = min_time-loop.init_instructions.time();
		int min_num_iterations = (min_iterating_time/single_iteration_max_time)+(min_iterating_time%single_iteration_max_time==0?0:1);
		if ( min_num_iterations < instruction_set.getMaxInitValue() ) {
			//we don't need no more loops this will do just fine
			//we need now to determine how many iterations we need and what should the other instructions in the loop be if any
			
			//do we need to have other instructions 
			if ( min_iterating_time%single_iteration_time == 0 ) {
				
			}
			//brute force try every possible number of iterations
			int max_num_iterations = Math.min(instruction_set.getMaxInitValue(),min_iterating_time/single_iteration_time)+(min_iterating_time%single_iteration_time==0?0:1);
			for ( int num_iterations = min_num_iterations ; num_iterations < max_num_iterations ; ++num_iterations ) {
				
			}
			
		}
		return null;
	}
}
