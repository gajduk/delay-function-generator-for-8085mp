import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

/**
 * used to generate single instructions,
 * code segments and loops
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
	 * an array that contains the instruction codes for all possible build-able durations of code
	 * possible_duration[i] == null if the duration i can not be achieved by the available instructions
	 * otherwise possible_durations[i] will contain an array list of instructions that last exactly i-states
	 */
	private Executable[] possible_durations;
	
	/**
	 * the maximum possible duration of code segments we see if we can create,
	 * depends on 2 factors, the maximum number of instructions and the maximum duration of a single instruction
	 * maximum number of instructions < 50 
	 * && maximum duration of a single instruction (for 8085) is 16
	 * =>  maximum possible duration =~ 800
	 * still we allow for some added duration, just in case
	 */
	private int max_possible_duration = 1000;
	
	/**
	 * this function will take all the arguments, separate the instructions that we can use out of the whole instruction set of the mP
	 * with respect to the elements_usable, and will then calculate all possible duration of instructions segments of maximum instructions
	 * to instruction limit, and store that information in the possible_durations array 
	 * @param mP - string description for the microprocessor which instruction set should be used
	 * @param elements_usable - descriptors for all the elements that are available to the function to operate with,
	 * 		  such as registers and memory locations
	 * @param instruction_limit - the maximum number of instructions that should be used to achieve the required duration,
	 * 		  use -1 if the limit is not specified, and 20 as the default value
	 * *further notice: the default instruction limit should be dependent on the duration of the delay function required
	 */
	public void precomputeAllPossibleDurations ( String mP , Element elements_usable[] , int instruction_limit  ) {
		setInstructionSet(mP);
		precomputeAllPossibleDurations(elements_usable,instruction_limit);
		
	}
	
	/**
	 * for this function to work the precomputeAllPossibledurations to have been called before
	 * @return - an array list of Instruction descriptions that have duration in the interval [min_time,max_time]
	 */
	public Executable getInstructionsForDuration ( int min_time , int max_time ) {
		for ( int i = min_time ; i <= max_time ; ++i ) {
			if ( possible_durations[i] != null ) return possible_durations[i];
		}
		return null;
	}
	
	/**
	 * this function will take all the arguments, separate the instructions that we can use out of the whole instruction set of the mP
	 * with respect to the elements_usable
	 * @param instruction_set - an object describing the instruction set of a given mP, it contains all the instructions fully described
	 * @param elements_usable - descriptors for all the elements that are available to the function to operate with,
	 * 		  such as registers and memory locations
	 * @return - array list of instructions descriptions
	 */
	private ArrayList<InstructionMetadata> getAllAvailableInstructions(
			InstructionSet instruction_set, Element[] elements_usable) {
		ArrayList<InstructionMetadata> res = new ArrayList<InstructionMetadata>();
		Elements comp = new Elements(elements_usable);
		for ( InstructionMetadata i : instruction_set.getInstructions() ) {
			if ( i.isAvailable(comp) ) res.add(i);
		}
		return res;
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
	
	private void setInstructionSet(String mP) {
		if ( mP.equals("8085") ) {
			instruction_set = InstructionSet8085.getInstance();
		}
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
		DelayFunction result = new DelayFunction();
		//we have to have a return statement so that time is unavoidable, and instruction
		setInstructionSet(mP);
		int return_instruction_duration = instruction_set.getInstruction("RET").getDuration();
		min_time -= return_instruction_duration;
		max_time -= return_instruction_duration;
		int return_instruction_length = 1;
		instruction_limit -= return_instruction_length;
		precomputeAllPossibleDurations(mP, elements_usable,instruction_limit);	
		result.return_instruction = new Instruction(instruction_set.getInstruction("RET").toString());
		//first determine whether we need a loop
		if ( min_time > getMaximumPossibleCreatedduration() ) {
			//we need a loop
			Executable exec = buildLoop(min_time,max_time, elements_usable, instruction_limit,0);
			if ( exec != null ) {
				result.main_instructions = exec;
			}
			else {
				//we can't create such a function and we need to quit
				System.out.println("SORRY NO CAN DO");
			}
		}
		else {
			//we don't need a loop just generate a loop-less code segment and finish
			Executable exec = getInstructionsForDuration(min_time,max_time);
			if ( exec != null ) {
				result.main_instructions = exec;
			}
			else {
				//we can't create such a function and we need to quit
				System.out.println("SORRY NO CAN DO");
			}
			
		}
		return result;
	}
	
	private int maximum_created_duration;
	private final static int maximum_created_duration_not_initialized = -1;
	private int getMaximumPossibleCreatedduration() {
		if ( maximum_created_duration == maximum_created_duration_not_initialized ) {
			for ( int i = max_possible_duration-1 ; i >= 0 ; --i ) {
				if ( possible_durations[i] != null )  {
					maximum_created_duration = i; break;
				}
			}
		}
		return maximum_created_duration;
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
	 * **IMPORTANT NOTICE --
 	 * *** WARNING!!!: this functions breaks the proposed architecture and deals only with 8085 loop structure
	 * this should be dealt with in some other manner in the future, perhaps migrate it to the InstructionSet somehow
	 * or create an AbstractBuilder
	 */
	public Executable buildLoop ( int min_time, int max_time, Element elements_usable[] , int instruction_limit , int nested_loops_counter ) {
		/*
		 * 8085 manual reference:
		 * there will be two loop structures:
		 * 
		 * the first one is:
		 * 
		 *  LXI Rpair,16b
		 *  label: OTHER_INSTRUCTION
		 *  	   OTHER_INSTRUCTION
		 *         ...
		 *         DCX Rpair
		 *         JNZ label
		 *         
		 *   Rpair can be any of the following pairs of registers BC, DE , HL
		 *   both registers need to be available for this to work
		 *   it can execute for maximum of 2^16-1 iteration
		 *   
		 * the second one is:
		 * 
		 *  MVI R,8b
		 *  label: OTHER_INSTRUCTION
		 *  	   OTHER_INSTRUCTION
		 *         ...
		 *         DCR R
		 *         JNZ label
		 *         
		 *   R can be any of the following registers A , B , C , D , E , F , H , L
		 *   the register needs to be available for this to work
		 *   it can execute for maximum of 2^8-1 iteration  
		 *   
		 *   in both cases the number of OTHER_INSTRUCTIONS is irrelevant can be 0 or any other positive number
		 */
		
		
		//first determine if we have enough instructions to actually build the loop
		if ( instruction_limit < 3 ) {
			System.out.println("ERROR. I can't build a loop with just "+instruction_limit+" instructions.");
			return null;
		}
		
		String label = "loop"+nested_loops_counter;
		//check if we have the required resources for building loops
		boolean case_one = false;
		boolean case_two = false;
		Elements el = new Elements(elements_usable);
		int bc_pair = 2+4;
		int de_pair = 8+16;
		int hl_pair = 64+128;
		if ( (bc_pair&el.getE()) == bc_pair ||
			 (de_pair&el.getE()) == de_pair ||
			 (hl_pair&el.getE()) == hl_pair	) {
			case_one = true;
		}
		if ( (el.getE()&((1<<8)-1)) > 0 ) {
			case_two = true;
		}
		//memorize the parametars, cause we might change them
		int temp_min_time = min_time;
		int temp_max_time = max_time;
		Element temp_elements_usable[] = Arrays.copyOf(elements_usable,0);
		int temp_instruction_limit = instruction_limit;
		int temp_nestd_loops_counter = nested_loops_counter;
		
		if ( case_one ) {
			//we have the necessary resources for building a type 1 loop structure
			//first choose which register pair we should use, if possible choose BC or DE before HL
			String reg_pair = "";
			if ( (bc_pair&el.getE()) == bc_pair ) reg_pair = "B"; else 
			if ( (de_pair&el.getE()) == de_pair ) reg_pair = "D"; 
			else reg_pair = "H"; 
			//the loops structure instructions are a must so we need to subtract them
			instruction_limit -= 3;
			int init_instruction_duratioin = instruction_set.getInstruction("LXI B,16b").getDuration();
			min_time -= init_instruction_duratioin;
			max_time -= init_instruction_duratioin;
			int min_iterations = 2;
			int max_iterations = (1<<16)-1;
			int loop_logic_instructions_duration =instruction_set.getInstruction("DCX B").getDuration()
						+ instruction_set.getInstruction("JNZ 16b").getDuration();
			//we have already decided which elements to use to get the loop logic so remove them from the list of available elements
			int counter = 0;
			String reg_pairs[] = new String[200];
			reg_pairs['B'] = "C";reg_pairs['D'] = "E";reg_pairs['H'] = "L";
			for ( int i = 0 ; i < elements_usable.length ; ++i ) {
				if ( elements_usable[i].type == Element.REGISTER ) {
					if ( elements_usable[i].description.equals(reg_pair) ||
							elements_usable[i].description.equals(reg_pairs[reg_pair.charAt(0)]) ) 
						elements_usable[i] = null;
					else ++counter;
				}
			}
			Element next_elements_usable[] = new Element[counter];
			counter = 0;
			for ( int i = 0 ; i < elements_usable.length ; ++i ) {
				if ( elements_usable[i]  != null ) next_elements_usable[counter++] = elements_usable[i];
			}

			for ( int num_iterations = min_iterations ; num_iterations < max_iterations ; ++num_iterations ) {
				//we need to check for round-off errors in duration, can we afford them
				int next_min_time = min_time/num_iterations+((min_time%num_iterations)==0?0:1);
				int next_max_time = max_time/num_iterations;
				if ( next_min_time <= next_max_time ) {
					//it just may work so go for it, first by subtracting the loop_logic_instructions_duration 
					next_min_time -= loop_logic_instructions_duration;
					next_max_time -= loop_logic_instructions_duration;
					if ( next_max_time < 0 ) {
						//we can't do it
						break;
					}
					precomputeAllPossibleDurations(next_elements_usable,instruction_limit);	
					if ( next_min_time > getMaximumPossibleCreatedduration() ) {
						//we need loop nesting
						Executable exec = buildLoop(next_min_time,next_max_time,next_elements_usable,instruction_limit,nested_loops_counter+1);
						if ( exec != null ) {
							//we got it
							
							//WARNING!!!: Code repetition, please, in the name of all that is holy, put it in a function!
							Loop res = new Loop();
							res.init_instructions = new InstructionGroup(
									new Instruction(instruction_set.getInstruction("LXI "+reg_pair+",16b").
											        getInstruction_code().
											           replaceAll("16b",Integer.toString(num_iterations))));
							res.other_instructions = exec;
							res.dcr_instructions = new InstructionGroup(
								new Instruction(instruction_set.getInstruction("DCX "+reg_pair).
										        getInstruction_code()));
							res.cond_instructions = new InstructionGroup(
								new Instruction(instruction_set.getInstruction("JNZ 16b").
										        getInstruction_code().
										           replaceAll("16b",label)));
							res.label = label;
							return res;
						}
						else {
							//better luck next time
						}
					}
					else {
						Executable exec = getInstructionsForDuration(next_min_time<0?0:next_min_time, next_max_time);
						
						if ( exec != null ) {
							//we got it
							Loop res = new Loop();
							res.init_instructions = new InstructionGroup(
									new Instruction(instruction_set.getInstruction("LXI "+reg_pair+",16b").
											        getInstruction_code().
											           replaceAll("16b",Integer.toString(num_iterations))));
							res.other_instructions = exec;
							res.dcr_instructions = new InstructionGroup(
								new Instruction(instruction_set.getInstruction("DCX "+reg_pair).
										        getInstruction_code()));
							res.cond_instructions = new InstructionGroup(
								new Instruction(instruction_set.getInstruction("JNZ 16b").
										        getInstruction_code().
										           replaceAll("16b",label)));
							res.label = label;
							return res;
						}
						else {
							//better luck next time
						}
					}
					
					
				}
				//sorry guys, perhaps you can try with a different number of iteration
			}
		}
		//only try case two if we have failed with case one,just to remind you we are looking for A solution
		//not the best one, not the shortest one, just A solution
		//return the parametars old values
		min_time = temp_min_time;
		max_time = temp_max_time;
		elements_usable = Arrays.copyOf(temp_elements_usable,0);
		instruction_limit = temp_instruction_limit;
		nested_loops_counter = temp_nestd_loops_counter;
		
		if ( case_two ) {
			//we have the necessary resources for building a type 2 loop structure
			//first choose which register we should use, if possible choose in next order of priority B,C,D,E,A,H,L
			String reg = "";
			if ( (2&el.getE()) == 2 ) reg = "B"; else 
			if ( (4&el.getE()) == 4 ) reg = "C"; else 
			if ( (8&el.getE()) == 8 ) reg = "D"; else 
			if ( (16&el.getE()) == 16 ) reg = "E"; else 	 			
			if ( (1&el.getE()) == 1 ) reg = "A"; else 
			if ( (64&el.getE()) == 64 ) reg = "H";		
			else reg = "L";	
			//the loops structure instructions are a must so we need to subtract them
			instruction_limit -= 3;
			int init_instruction_duratioin = instruction_set.getInstruction("MVI B,8b").getDuration();
			min_time -= init_instruction_duratioin;
			max_time -= init_instruction_duratioin;
			int min_iterations = 2;
			int max_iterations = (1<<8)-1;
			int loop_logic_instructions_duration = instruction_set.getInstruction("DCR B").getDuration()
						+ instruction_set.getInstruction("JNZ 16b").getDuration();
			//we have already decided which elements to use to get the loop logic so remove them from the list of available elements
			int counter = 0;
			for ( int i = 0 ; i < elements_usable.length ; ++i ) {
				if ( elements_usable[i].type == Element.REGISTER ) {
					if ( elements_usable[i].description.equals(reg) ) 
						elements_usable[i] = null;
					else ++counter;
				}
			}
			Element next_elements_usable[] = new Element[counter];
			counter = 0;
			for ( int i = 0 ; i < elements_usable.length ; ++i ) {
				if ( elements_usable[i]  != null ) next_elements_usable[counter++] = elements_usable[i];
			}

			for ( int num_iterations = min_iterations ; num_iterations < max_iterations ; ++num_iterations ) {
				//we need to check for round-off errors in duration, can we afford them
				int next_min_time = min_time/num_iterations+((min_time%num_iterations)==0?0:1);
				int next_max_time = max_time/num_iterations;
				if ( next_min_time <= next_max_time ) {
					//it just may work so go for it, first by subtracting the loop_logic_instructions_duration 
					next_min_time -= loop_logic_instructions_duration;
					next_max_time -= loop_logic_instructions_duration;
					if ( next_max_time < 0 ) {
						//we can't do it
						break;
					}
					precomputeAllPossibleDurations(next_elements_usable,instruction_limit);	
					if ( next_min_time > getMaximumPossibleCreatedduration() ) {
						//we need loop nesting
						Executable exec = buildLoop(next_min_time,next_max_time,next_elements_usable,instruction_limit,nested_loops_counter+1);
						if ( exec != null ) {
							//we got it
							
							//WARNING!!!: Code repetition, please, in the name of all that is holy, put it in a function!
							Loop res = new Loop();
							res.init_instructions = new InstructionGroup(
									new Instruction(instruction_set.getInstruction("MVI "+reg+",8b").
											        getInstruction_code().
											           replaceAll("8b",Integer.toString(num_iterations))));
							res.other_instructions = exec;
							res.dcr_instructions = new InstructionGroup(
								new Instruction(instruction_set.getInstruction("DCR "+reg).
										        getInstruction_code()));
							res.cond_instructions = new InstructionGroup(
								new Instruction(instruction_set.getInstruction("JNZ 16b").
										        getInstruction_code().
										           replaceAll("16b",label)));
							res.label = label;
							return res;
						}
						else {
							//better luck next time
						}
					}
					else {
						Executable exec = getInstructionsForDuration(next_min_time<0?0:next_min_time, next_max_time);
						
						if ( exec != null ) {
							//we got it
							Loop res = new Loop();
							res.init_instructions = new InstructionGroup(
									new Instruction(InstructionSet.getInstance().getInstruction("MVI "+reg+",8b").
											        getInstruction_code().
											           replaceAll("8b",Integer.toString(num_iterations))));
							res.other_instructions = exec;
							res.dcr_instructions = new InstructionGroup(
								new Instruction(InstructionSet.getInstance().getInstruction("DCR "+reg).
										        getInstruction_code()));
							res.cond_instructions = new InstructionGroup(
								new Instruction(InstructionSet.getInstance().getInstruction("JNZ 16b").
										        getInstruction_code().
										           replaceAll("16b",label)));
							res.label = label;
							return res;
						}
						else {
							//better luck next time
						}
					}
					
					
				}
				//sorry guys, perhaps you can try with a different number of iteration
			}
			
		}
		
		
		return null;
		
		
		
		
		/*
		 * useless code from before
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
		for ( int i = 0 ; i < temp.length ; ++i ) temp[i] = elements_usable[i+1];
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
		*/
	}

	
	/**
	 * this function will take all the arguments, separate the instructions that we can use out of the whole instruction set of the mP
	 * with respect to the elements_usable, and will then calculate all possible duration of instructions segments of maximum instructions
	 * to instruction limit, and store that information in the possible_durations array 
	 
	 * @param elements_usable - descriptors for all the elements that are available to the function to operate with,
	 * 		  such as registers and memory locations
	 * @param instruction_limit - the maximum number of instructions that should be used to achieve the required duration,
	 * 		  use -1 if the limit is not specified, and 20 as the default value
	 * *notice: the mP that we will use is already set
	 * *further notice: the default instruction limit should be dependent on the duration of the delay function required
	 */
	public void precomputeAllPossibleDurations(Element[] elements_usable,
			int instruction_limit) {
		instructions_available = getAllAvailableInstructions(instruction_set,elements_usable);
		maximum_created_duration = maximum_created_duration_not_initialized;
		//all are null in start
		ArrayList<InstructionMetadata>[] possible_durations_descr = new ArrayList[max_possible_duration];
		// no instructions are needed for duration of 0
		possible_durations_descr[0] = new ArrayList<InstructionMetadata>();
		
		//logic for generating, we must use at max 'instruction_limit' instructions
		//reference on algorithm used, here -> http://www.seeingwithc.org/topic1html.html
		for ( int k = 0 ; k < instruction_limit ; ++k ) {
			for ( int i = max_possible_duration-1 ; i >= 0 ; --i ) {
				for ( int w = 0 ; w  < instructions_available.size() ; ++w ) {
					if ( possible_durations_descr[i] != null ) {
						if ( i+instructions_available.get(w).getDuration() < max_possible_duration ) {
							if ( possible_durations_descr[i+instructions_available.get(w).getDuration()] == null ) {
								possible_durations_descr[i+instructions_available.get(w).getDuration()] = new ArrayList<InstructionMetadata>(possible_durations_descr[i].size());
								for ( InstructionMetadata is : possible_durations_descr[i] ) {
									possible_durations_descr[i+instructions_available.get(w).getDuration()].add(is);
								}
								possible_durations_descr[i+instructions_available.get(w).getDuration()].add(instructions_available.get(w));
							}
								
						}
						else {
							System.out.println("YOU need to increase the maximum_possible_duration current is "+max_possible_duration);
						}
					}
				}
			}
		}
		
		/**
		 * un-comment this if you want to see all do-able duration, debugging only
		 */
		/*
		for ( int i = 0 ; i < 100 ; ++i ) {
			System.out.print("Of duration "+i+" :");
			if ( possible_durations[i] == null ) {
				System.out.println(" NOT POSSIBLE");
			}
			else {
				System.out.println(possible_durations[i]);
			}
		}
		*/
		
		//now transform this code-segments from InstructionMetadata (descriptions)
		//to real executables, that can be used directly for creating delay functions
		possible_durations = new Executable[max_possible_duration];
		possible_durations[0] = new InstructionGroup();
		for (int i = 1 ; i < possible_durations.length; i++) {
			if ( possible_durations_descr[i] != null ) {
				possible_durations[i] = new InstructionGroup(possible_durations_descr[i]);
			}
//			System.out.println("Of duration "+i+" :"+possible_durations[i]);
		}
		
		
		
	}

}
