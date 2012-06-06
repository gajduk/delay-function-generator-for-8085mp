import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
	
	/**
	 * out of all the instructions in the instruction set only a subset are available concerning the elements of the mP our delay
	 * function is allowed to use, this array list contains only those instructions, they are the instructions that will be used
	 * when generating out delay function
	 */
	private ArrayList<InstructionMetadata> instructions_available;
	
	/**
	 * an array that contains the instruction codes for all possible build-able lengths of code
	 * possible_length[i] == null if the length i can not be achieved by the available instructions
	 * otherwise possible_lengths[i] will contain an array list of instructions that last exactly i-states
	 */
	private ArrayList<InstructionMetadata>[] possible_lengths;
	
	/**
	 * the maximum possible length of code segments we see if we can create,
	 * depends on 2 factors, the maximum number of instructions and the maximum length of a single instruction
	 * maximum number of instructions < 50 
	 * && maximum length of a single instruction (for 8085) is 16
	 * =>  maximum possible length =~ 800
	 * still we allow for some added length, just in case
	 */
	private int max_possible_length = 1000;
	
	
	/**
	 * this function will take all the arguments, separate the instructions that we can use out of the whole instruction set of the mP
	 * with respect to the elements_usable, and will then calculate all possible length of instructions segments of maximum instructions
	 * to instruction limit, and store that information in the possible_lengths array 
	 * @param mP - string description for the microprocessor which instruction set should be used
	 * @param elements_usable - descriptors for all the elements that are available to the function to operate with,
	 * 		  such as registers and memory locations
	 * @param instruction_limit - the maximum number of instructions that should be used to achieve the required duration,
	 * 		  use -1 if the limit is not specified, and 20 as the default value
	 * *further notice: the default instruction limit should be dependent on the duration of the delay function required
	 */
	public void precomputeAllPossibleLengths ( String mP , Element elements_usable[] , int instruction_limit  ) {
		setInstructionSet(mP);
		instructions_available = getAllAvailableInstructions(instruction_set,elements_usable);
		//all are null in start
		possible_lengths = new ArrayList[max_possible_length];
		// no instructions are needed for length of 0
		possible_lengths[0] = new ArrayList<InstructionMetadata>();
		
		//logic for generating, we must use at max 'instruction_limit' instructions
		for ( int k = 0 ; k < instruction_limit ; ++k ) {
			for ( int i = max_possible_length-1 ; i >= 0 ; --i ) {
				for ( int w = 0 ; w  < instructions_available.size() ; ++w ) {
					if ( possible_lengths[i] != null ) {
						if ( i+instructions_available.get(w).getLength() < max_possible_length && 
							possible_lengths[i+instructions_available.get(w).getLength()] == null ) {
							possible_lengths[i+instructions_available.get(w).getLength()] = new ArrayList<InstructionMetadata>();
							for ( InstructionMetadata is : possible_lengths[i] ) {
								possible_lengths[i+instructions_available.get(w).getLength()].add(is);
							}
							possible_lengths[i+instructions_available.get(w).getLength()].add(instructions_available.get(w));
						}
					}
				}
			}
		}
		
		/**
		 * un-comment this if you want to see all possible length, debugging only
		 */
		/*
		for ( int i = 0 ; i < 100 ; ++i ) {
			System.out.print("Of length "+i+" :");
			if ( possible_lengths[i] == null ) {
				System.out.println(" NOT POSSIBLE");
			}
			else {
				System.out.println(possible_lengths[i]);
			}
		}
		*/
	}
	
	
	/**
	 * this function will take all the arguments, separate the instructions that we can use out of the whole instruction set of the mP
	 * with respect to the elements_usable
	 * @param instruction_set - an object describing the instruction set of a given mP, it contains all the instructions fully described
	 * @param elements_usable - descriptors for all the elements that are available to the function to operate with,
	 * 		  such as registers and memory locations
	 * @return
	 */
	private ArrayList<InstructionMetadata> getAllAvailableInstructions(
			InstructionSet instruction_set, Element[] elements_usable) {
		// TODO Auto-generated method stub
		return instruction_set.getInstructions();
	}

	/**
	 * @return the instructions_available
	 */
	public ArrayList<InstructionMetadata> getInstructions_available() {
		return instructions_available;
	}

	/**
	 * @param instructions_available the instructions_available to set
	 */
	public void setInstructions_available(
			ArrayList<InstructionMetadata> instructions_available) {
		this.instructions_available = instructions_available;
	}

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
