package ukim.finki.mps.delay_fucntion_generator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
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
	private ArrayList<InstructionData> instructions_available;
	
	/**
	 * an array that contains the instruction codes for all possible build-able durations of code
	 * possible_duration[i] == null if the duration i can not be achieved by the available instructions
	 * otherwise possible_durations[i] will contain an array list of instructions that last exactly i-states
	 */
	private Executable[] possible_durations;

	/**
	 * set the instruction_set to be of the specified mP
	 * @param mP - the mP we will use to set the instruction set
	 */
	public void setInstructionSet(String mP) {
		instruction_set = InstructionSet.getInstance(mP);
	}
	
	/**
	 * the method to be used for generating delay functions 
	 * this method will try to make a function with least possible instructions
	 * specifies all the input parameters needed
	 * @param min_time - lower bound for the duration of the delay function 
	 * @param max_time - upper bound for the duration of the delay function
	 * @param mP - string description for the microprocessor which instruction set should be used
	 * @param elements_usable - descriptors for all the elements that are available to the function to operate with,
	 * 		  such as registers and memory locations
	 * @return a code segment that is a function, with adequate labels ending in a RET or an equivalent instruction
	 * 		   the code segment should have a duration time that belongs in the interval [min_time,max_time]
	 * 		   some small probability for changes in duration should be allowed, The delay function used should not be 
	 * 		   using any other elements other than those explicitly specified by the usable_elemnts array, as an addition
	 * 		   the delay function should not be longer than the maximum instruction limit allowed
	 * *further notice: the default instruction limit should be dependent on the duration of the delay function required
	 * **further notice: the error allowed for the duration of the delay function should be reverse-proportional to the interval size		
	 */
	private DelayFunction buildDelayFunction ( long min_time , long max_time , String mP , Element elements_usable[] , DelayFunction res ) {
		setInstructionSet(mP);
		long n = getMaximumNumberofIterationsWhenNestingLoops(new Elements(elements_usable));
		long k = getMaximumDurationWhenNestingLoops(new Elements(elements_usable));
		int l = (int) ((min_time-k) / (n*4));
		int loop_instructions = (int) ( Math.log(n)/ Math.log(256) ) * 3;
		// do the math if it can't be done tell the user to go f.ck himself
		if ( res == null ) {
		    res = new DelayFunction();
		    res.init_instructions = new InstructionGroup();
		    res.finalize_instrucions = new InstructionGroup();
		    res.return_instruction = instruction_set.getRetInstrcution();
		}
		// l - number of nested instructions to add to get this duration, if this is large > 20~30
		// consider making some more room for the mP loop_logic to operate by releasing some registers
		// store their value temporarily in some memory locations or on top of stack
		
		if ( l > 30 ) {
			//try releasing some registers
			//first find a register that is currently occupied
			Elements els = new Elements(elements_usable);
			int reg = -1;
			for ( int i = 0 ; i < 7 ; ++i ) {
				if ( ((1<<i)&els.getE()) == 0 ) {
					reg = i; break;
				}
			}
			if ( reg == -1 ) {
				//come on this can't be happening
				//warn the user that there will be many instructions, if he is okay with that go on, otherwise quit
				return null;
			}
			String reg_s = els.getRegister(reg);
			int temporarly_location_found = -1;
			for ( int i = 0 ; i < elements_usable.length ; ++i ) {
				if ( elements_usable[i].type == Element.MEMORY ) {
					if ( elements_usable[i].status.equals(Element.available) ) {
						temporarly_location_found = i;
						//if it is not the A register, we are in serious shit, we need first to move an data to the A register then
						//transfer it to memory
						if ( reg != 0 ) {
								((InstructionGroup)res.init_instructions).append(instruction_set.getMoveInstruction("A",reg_s));
						}
						((InstructionGroup)res.init_instructions).append(instruction_set.getStoreInstruction(elements_usable[i].description));
						if (  reg != 0 ) {
							((InstructionGroup)res.finalize_instrucions).insert(instruction_set.getMoveInstruction(reg_s,"A"));
							((InstructionGroup)res.finalize_instrucions).insert(instruction_set.getLoadInstruction(elements_usable[i].description));
						}
						else {	
							((InstructionGroup)res.finalize_instrucions).append(instruction_set.getLoadInstruction(elements_usable[i].description));
						}
						break;
					}
				}
			}
			if ( temporarly_location_found == -1 ) {
				//okay no free memory locations to store some data, try the stack
				for ( int i = 0 ; i < elements_usable.length ; ++i ) {
					if ( elements_usable[i].type == Element.STACK ) {
						if ( elements_usable[i].status.equals(Element.available) ) {
							temporarly_location_found = i;
							//if it is not the A register, we are in serious shit, we need first to move an data to the A register then
							//transfer it to memory
							//we might have already saved register C when we were trying to save register B, no need to push the same data twice
							if ( !((InstructionGroup)res.init_instructions).contains(instruction_set.getPushInstruction(reg_s)) ) {
								((InstructionGroup)res.init_instructions).append(instruction_set.getPushInstruction(reg_s));
							}
							if ( !((InstructionGroup)res.finalize_instrucions).contains(instruction_set.getPopInstruction(reg_s)) ) {
								((InstructionGroup)res.finalize_instrucions).insert(instruction_set.getPopInstruction(reg_s));
							}
							break;
						}
					}
					
				}
				if ( temporarly_location_found == -1 ) {
					//come on this can't be happening
					//warn the user that there will be many instructions, if he is okay with that go on, otherwise quit
					return null;
				}
				temporarly_location_found = -1;
				
			}
			//if we used the stack, add a new Element for the register, the stack element remains
			//if we used a memory location remove it from the elements_usable array, and also add a new element for the register
			if ( temporarly_location_found == -1 ) {
				Element next_elements_usable[] = new Element[elements_usable.length+1];
				for ( int i = 0 ; i < elements_usable.length ; ++i ) next_elements_usable[i] = elements_usable[i];
				next_elements_usable[elements_usable.length] = new Element(Element.REGISTER,reg_s);
				elements_usable = next_elements_usable;
			}
			else {
				elements_usable[temporarly_location_found] = new Element(Element.REGISTER, reg_s);
			}
			return buildDelayFunction(min_time, max_time, mP, elements_usable, res);
		}
		int instruction_limit = l>0?l:0+loop_instructions;
		
		min_time -= res.time();
		max_time -= res.time();
		if ( max_time < 0 ) return null;
		while ( true ) {
			res.main_instructions = buildDelayFunction(min_time, max_time, elements_usable,instruction_limit);
			if ( res.main_instructions != null ) break;
			++instruction_limit;
		}
		return res;
	}
	
	/**
	 * just used to set the default value of res to null
	 */
	public DelayFunction buildDelayFunction ( long min_time , long max_time , String mP , Element elements_usable[] ) {
		return buildDelayFunction(min_time,max_time,mP,elements_usable,null);
	}
		
	/**
	 * for this function to work the precomputeAllPossibledurations to have been called before
	 * @return - an Executable that has duration in the interval [min_time,max_time]
	 */
	private Executable getInstructionsForDuration ( long min_time , long max_time ) {
		if ( max_time < 0 ) return null;
		if ( min_time < 0 ) min_time = 0;
		for ( int i = (int) min_time ; i <= max_time ; ++i ) {
			if ( possible_durations[i] != null ) {
				return possible_durations[i];
			}
		}
		return null;
	}
	
	/**
	 * the method to be used for generating delay functions with specific length of instructions
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
	 */
	private Executable buildDelayFunction ( long min_time , long max_time , Element elements_usable[] , int instruction_limit ) {
		precomputeAllPossibleDurations(elements_usable,instruction_limit);	
		Executable exec = null;
		
		//first determine whether we need a loop
		if ( min_time > getMaximumPossibleCreatedDuration() ) {
			//we need a loop
			exec = buildLoop(min_time,max_time,elements_usable,instruction_limit,1);
		}
		else {
			//we don't need a loop just generate a loop-less code segment and finish
			exec = getInstructionsForDuration(min_time,max_time);
		}
		return exec;
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
	private Executable buildLoop ( long min_time, long max_time, Element elements_usable[] , int instruction_limit , int nested_loops_counter ) {
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
			//System.out.println("ERROR. I can't build a loop with just "+instruction_limit+" instructions.");
			return null;
		}
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
		//memorize some of the parameters, cause we will change them
		long temp_min_time = min_time;
		long temp_max_time = max_time;
		
		//the loops structure instructions are a must so we need to subtract them, conveniently they are the same in both cases so do that here
		instruction_limit -= 3;
		
		if ( case_one ) {
			//we have the necessary resources for building a type 1 loop structure
			//first choose which register pair we should use, if possible choose BC or DE before HL
			String reg_pair = ""; String reg_pair_other_reg = "";
			if ( (bc_pair&el.getE()) == bc_pair ) {reg_pair = "B"; reg_pair_other_reg = "C"; } else 
			if ( (de_pair&el.getE()) == de_pair ) {reg_pair = "D"; reg_pair_other_reg = "E"; }
			else  {reg_pair = "H"; reg_pair_other_reg = "L"; }
			
			int init_instruction_duration = instruction_set.getInitInstructionsDuration(instruction_set.loop_type_16b);
			int loop_logic_instructions_duration = instruction_set.getLoopLogicInstructionsDuration(instruction_set.loop_type_16b);
			min_time -= init_instruction_duration;
			max_time -= init_instruction_duration;
			int min_iterations = 2;
			int max_iterations = (int) ((1<<16)-1>max_time/loop_logic_instructions_duration ? max_time/loop_logic_instructions_duration:(1<<16)-1);
			
			
			//we have already decided which elements to use to get the loop logic so remove them from the list of available elements
			Element next_elements_usable[] = removeUsedRegisters(elements_usable, reg_pair , reg_pair_other_reg);
			precomputeAllPossibleDurations(next_elements_usable,instruction_limit);	
			Loop res = buildLoopByDeterminingInitValue(min_iterations, max_iterations, min_time, max_time, next_elements_usable, 
					   instruction_limit, nested_loops_counter, reg_pair, instruction_set.loop_type_16b);
			if ( res != null ) return res;
			
		}
		//only try case two if we have failed with case one,just to remind you we are looking for A solution
		//not the best one, not the shortest one, just A solution
		//return the parameters old values
		min_time = temp_min_time;
		max_time = temp_max_time;
		
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
			int init_instruction_duration = instruction_set.getInitInstructionsDuration(instruction_set.loop_type_8b);
			int loop_logic_instructions_duration = instruction_set.getLoopLogicInstructionsDuration(instruction_set.loop_type_8b);
			min_time -= init_instruction_duration;
			max_time -= init_instruction_duration;
			int min_iterations = 2;
			int max_iterations = (int) ((1<<8)-1<max_time/loop_logic_instructions_duration ? (1<<8)-1 : max_time/loop_logic_instructions_duration);
			
			
			//we have already decided which elements to use to get the loop logic so remove them from the list of available elements
			Element next_elements_usable[] = removeUsedRegisters(elements_usable,reg);
			
			precomputeAllPossibleDurations(next_elements_usable,instruction_limit);	
			
			Loop res = buildLoopByDeterminingInitValue(min_iterations, max_iterations, min_time, max_time, next_elements_usable, 
							instruction_limit, nested_loops_counter, reg, instruction_set.loop_type_8b);
			if ( res != null ) return res;
		}
		return null;
	}
		
	/**
	 * a method used to create a loop
	 * @param loop_type -  type of loop 8bit or 16bit
	 * @param init_value - this determines the number of iterations
	 * @param label - label used to guide this loop's jumps
	 * @param reg_or_reg_pair - string describing which register is used to implement this loop's logic
	 * @param other_instructions - some instructions meant to give more duration to the loop, they are all nested
	 * @return - a loop with default init, decr and cond segments and other set
	 */
	private Loop makeLoop ( int loop_type , int init_value , String label , String reg_or_reg_pair , Executable other_instructions ) {
		Loop result = new Loop();
		result.iterations = init_value;
		result.init_instructions = instruction_set.getInitInstrcution(loop_type, reg_or_reg_pair, init_value);
		result.other_instructions = other_instructions;
		result.dcr_instructions = instruction_set.getDcrInstrcution(loop_type, reg_or_reg_pair);
		result.cond_instructions = instruction_set.getCondInstruction(loop_type, label);
		result.label = label;
		return result;
	}
	
	/**
	 * this method is used to generate loops
	 * it will try every possible number of iterations in the interval[min_iter,max_iter] 
	 * for this loop's logic in order to get it's execution time in the interval [min_time,max_time],
	 * @param min_iterations 
	 * @param max_iterations
	 * @param min_time 
	 * @param max_time
	 * @param elements_usable - describes all the elements this loop can use for the instruction that are part of the other executable
	 * @param instruction_limit - the maximum number of instructions this loop can use for it's code
	 * @param nested_loops_counter - keeping track of loop nesting in order to give a unique label for each loop
	 * @param reg_or_reg_pair - the register the loop should use to implement it's logic
	 * @param loop_type - the type of the loop we are trying to build - 8 bit or 16 bit
	 * @return
	 */
	private Loop buildLoopByDeterminingInitValue ( int min_iterations , int max_iterations , long min_time , long max_time , 
												  Element elements_usable[] , int instruction_limit , 
												  int nested_loops_counter ,  String reg_or_reg_pair , int loop_type ) {
		String label = "loop"+nested_loops_counter;
		for ( int num_iterations = max_iterations  ; num_iterations >= min_iterations  ; --num_iterations ) {
			//we need to check for round-off errors in duration, can we afford them
			long next_min_time = min_time/num_iterations+((min_time%num_iterations)==0?0:1);
			long next_max_time = max_time/num_iterations;
			if ( next_min_time <= next_max_time ) {
				//it just may work so go for it, first by subtracting the loop_logic_instructions_duration 
				int loop_logic_instructions_duration = instruction_set.getLoopLogicInstructionsDuration(loop_type);
				
				next_min_time -= loop_logic_instructions_duration;
				next_max_time -= loop_logic_instructions_duration;
				if ( next_max_time < 0 ) {
					//we can't do it
					break;
				}
				
				Executable exec = null;
				
				//we need loop nesting
				if ( next_min_time > getMaximumPossibleCreatedDuration() ) {
					exec = buildLoop(next_min_time,next_max_time,elements_usable,instruction_limit,nested_loops_counter+1);
				}
				else {
					//just generate a bunch of instructions
					exec = getInstructionsForDuration(next_min_time, next_max_time);
				}
				if ( exec != null ) {
					//we got it
					return makeLoop(loop_type, num_iterations, label, reg_or_reg_pair, exec);
				}
			}
			//sorry guys, perhaps you can try with a different number of iteration
		}
		return null;
	}
	
	/**
	 * removes the specified register from the array of elements on our disposal, does not change the original array
	 * @param elements_usable - current elements that we can use
	 * @param registers - the String description of the register elements we need to remove from the array
	 * @return a new array of elements that contain all the elements from the original but with the registers removed
	 */
	private Element[] removeUsedRegisters  ( Element elements_usable[] , String... registers ) {
		ArrayList<Element> result = new ArrayList<Element>();
		for ( Element e : elements_usable) {
			boolean to_add = true;
			if ( e.type == Element.REGISTER 
					&& e.status.equals(Element.available) ) {
				for ( int k = 0 ; k < registers.length ; ++k ) {
					if ( e.description.equals(registers[k]) ) {
						to_add = false;	break;
					}
				}
			}
			if ( to_add ) result.add(e);
		}
		return (Element[])result.toArray(new Element[result.size()]);

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
	private void precomputeAllPossibleDurations(Element[] elements_usable,
			int instruction_limit) {
		instructions_available = instruction_set.getAllAvailableInstructions(elements_usable);
		/**
		 * the maximum possible duration of code segments we see if we can create,
		 * depends on 2 factors, the maximum number of instructions and the maximum duration of a single instruction
		 * maximum number of instructions < 50 
		 * && maximum duration of a single instruction (for 8085) is 16
		 * =>  maximum possible duration =~ 800
		 * still we allow for some added duration, just in case
		 * we should compute this for every call of the precomputing separate so we don't do any excess computing
		 */
		int max_possible_duration = instruction_limit*getMaximumInstructionDuration()+1;
		//all are null in start
		ArrayList<InstructionData>[] possible_durations_descr = new ArrayList[max_possible_duration];
		// no instructions are needed for duration of 0
		possible_durations_descr[0] = new ArrayList<InstructionData>();
		//logic for generating, we must use at max 'instruction_limit' instructions
		//reference on algorithm used, here -> http://www.seeingwithc.org/topic1html.html
		
		for ( int k = 0 ; k < instruction_limit ; ++k ) {
			for ( int i = max_possible_duration-1 ; i >= 0 ; --i ) {
				if ( possible_durations_descr[i] != null ) {
					for ( int w = 0 ; w  < instructions_available.size() ; ++w ) {
						if ( i+instructions_available.get(w).getDuration() < max_possible_duration &&
							 possible_durations_descr[i+instructions_available.get(w).getDuration()] == null ) {
								possible_durations_descr[i+instructions_available.get(w).getDuration()] = new ArrayList<InstructionData>(possible_durations_descr[i].size()+1);
								for ( InstructionData is : possible_durations_descr[i] ) {
									possible_durations_descr[i+instructions_available.get(w).getDuration()].add(is);
								}
								possible_durations_descr[i+instructions_available.get(w).getDuration()].add(instructions_available.get(w));
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
		}
		
	}

	/**
	 * returns the longest possible duration with the current available instructions
	 * @return - duration in T-states of the mP of the longest available instruction
	 */
	private int getMaximumInstructionDuration() {
		int res = 4;
		if ( instructions_available == null ) return res;
		for ( InstructionData i : instructions_available ) {
			if ( i.getDuration() > res ) res = i.getDuration();
		}
		return res;
	}
	
	/**
	 * used to roughly estimate the number of loops we need to get a duration
	 * @param elements - elements available for making loops, only the registers are of interest
	 * @return int - the maximum such number - 0 if we can make no loops, known to cause overflow
	 */
	private long getMaximumNumberofIterationsWhenNestingLoops ( Elements elements ) {
		long result = 1;
		for ( int i = 0 ; i < 7/*we have 7 registers in 8085*/ ; ++i ) {
			if ( (1<<i&elements.getE()) > 0 ) {
				result *= 256;
			}
		}
		return result;
	}

	/**
	  * used to roughly estimate the number of loops we need to get a duration
	 * @param elements - elements available for making loops, only the registers are of interest
	 * @return duration in T-states of the mP, the longest we can get by nesting all loops available,0 if no loops
	 */
	private long getMaximumDurationWhenNestingLoops(Elements elements) {
		return instruction_set.getLoopLogicInstructionsDuration(instruction_set.loop_type_8b)*getMaximumNumberofIterationsWhenNestingLoops(elements);
	}
	
	/**
	 * used when we determine do we need or not a loop in our function
	 * @return - duration in T-states of the mP of the longest created loopless instruction sequence
	 * by the method for precomputing the duration , 0 default
	 */
	private int getMaximumPossibleCreatedDuration() {
		for ( int i = possible_durations.length-1 ; i >= 0 ; --i ) {
			if ( possible_durations[i] != null )  return i;
		}
		return 0;
	}
	
}
