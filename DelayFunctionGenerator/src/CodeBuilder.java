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
	 * we should compute this for every call of the precomputing separate so we don;t do any excess computing
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
		if ( max_time < 0 ) return null;
		if ( min_time < 0 ) min_time = 0;
		for ( int i = min_time ; i <= max_time ; ++i ) {
			if ( possible_durations[i] != null ) return possible_durations[i];
		}
		return null;
	}
	
	/**
	 * this function will take all the arguments, separate the instructions that we can use out of the whole instruction set of the mP
	 * with respect to the elements_usable, 
	 * @param instruction_set - an object describing the instruction set of a given mP, it contains all the instructions fully described
	 * @param elements_usable - descriptors for all the elements that are available to the function to operate with,
	 * 		  such as registers and memory locations
	 * @return - array list of instructions descriptions
	 * *IMPORTANT NOTICE: if there are two instruction or  more each of duration 'k' then only one instruction will be taken in consideration
	 * we don;t need any repetition 
	 */
	private ArrayList<InstructionMetadata> getAllAvailableInstructions(
			InstructionSet instruction_set, Element[] elements_usable) {
		ArrayList<InstructionMetadata> res = new ArrayList<InstructionMetadata>();
		Elements comp = new Elements(elements_usable);
		boolean[] already_have = new boolean[20];
		for ( InstructionMetadata i : instruction_set.getInstructions() ) {
			if ( i.isTo_use() && i.isAvailable(comp) && !already_have[i.getDuration()])  {
				res.add(i);
				already_have[i.getDuration()] = true;
			}
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
	 * just used to set the default value of res to null
	 */
	public DelayFunction buildDelayFunction ( int min_time , int max_time , String mP , Element elements_usable[] ) {
		return buildDelayFunction(min_time,max_time,mP,elements_usable,null);
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
	public DelayFunction buildDelayFunction ( int min_time , int max_time , String mP , Element elements_usable[] , DelayFunction res ) {
		setInstructionSet(mP);
		int n = getMaximumNumberofIterationsWhenNestingLoops(new Elements(elements_usable));
		int k = getMaximumDurationWhenNestingLoops(new Elements(elements_usable));
		int l = (min_time-k) / (n*4);
		int loop_instructions = (int) ( Math.log(n)/ Math.log(256) ) * 3;
		// do the math if it can't be done tell the user to go f.ck himself
		if ( res == null ) {
		    res = new DelayFunction();
		    res.init_instructions = new InstructionGroup();
		    res.finalize_instrucions = new InstructionGroup();
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
								((InstructionGroup)res.init_instructions).append(getMoveInstruction("A",reg_s));
						}
						((InstructionGroup)res.init_instructions).append(getStoreInstruction(elements_usable[i].description));
						if (  reg != 0 ) {
							((InstructionGroup)res.finalize_instrucions).insert(getMoveInstruction(reg_s,"A"));
							((InstructionGroup)res.finalize_instrucions).insert(getLoadInstruction(elements_usable[i].description));
						}
						else {
							((InstructionGroup)res.finalize_instrucions).append(getLoadInstruction(elements_usable[i].description));
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
							if ( !((InstructionGroup)res.init_instructions).contains(getPushInstruction(reg_s)) ) {
								((InstructionGroup)res.init_instructions).append(getPushInstruction(reg_s));
							}
							if ( !((InstructionGroup)res.finalize_instrucions).contains(getPopInstruction(reg_s)) ) {
								((InstructionGroup)res.finalize_instrucions).insert(getPopInstruction(reg_s));
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
		while ( true ) {
			res.main_instructions = buildDelayFunction(min_time, max_time, elements_usable,instruction_limit);
			if ( res.main_instructions != null ) break;
			++instruction_limit;
		}
		res.return_instruction = getRetInstrcution();
		return res;
	}
	
	private String getRegPairForPushingPoping( String reg ) {
		if ( reg.equals("B") | reg.equals("C") ) return "B";
		if ( reg.equals("D") | reg.equals("E") ) return "D";
		if ( reg.equals("H") | reg.equals("L") ) return "L";
		if ( reg.equals("A") ) return "PSW";
		return "";
	}

	private Executable getPopInstruction(String reg) {
		return instruction_set.getInstruction("POP "+getRegPairForPushingPoping(reg)).getExecutable();
	}

	private Executable getPushInstruction(String reg) {
		return instruction_set.getInstruction("PUSH "+getRegPairForPushingPoping(reg)).getExecutable();
	}

	private Executable getMoveInstruction(String dest, String src) {
		return instruction_set.getInstruction("MOV "+dest+","+src).getExecutable();
	}

	private Executable getLoadInstruction( String memory_location ) {
		return instruction_set.getInstruction("LDA 16b").getExecutable("16b",memory_location);
	}

	private Executable getStoreInstruction( String memory_location ) {
		return instruction_set.getInstruction("STA 16b").getExecutable("16b",memory_location);
	}

	private int getMaximumDurationWhenNestingLoops(Elements elements) {
		return getLoopLogicInstructionsDuration(loop_type_8b)*getMaximumNumberofIterationsWhenNestingLoops(elements);
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
	public Executable buildDelayFunction ( int min_time , int max_time , Element elements_usable[] , int instruction_limit ) {
		Executable result = new InstructionGroup();
		//we have to have a return statement so that time is unavoidable, and instruction
		
		int return_instruction_duration = getRetInstrcution().time();
		min_time -= return_instruction_duration;
		max_time -= return_instruction_duration;
		int return_instruction_length = 1;
		instruction_limit -= return_instruction_length;
		precomputeAllPossibleDurations(elements_usable,instruction_limit);	
		
		//first determine whether we need a loop
		if ( min_time > getMaximumPossibleCreatedDuration() ) {
			//we need a loop
			Executable exec = buildLoop(min_time,max_time,elements_usable,instruction_limit,0);
			if ( exec != null ) {
				result = exec;
			}
			else {
				//we can't create such a function and we need to quit
				//System.out.println("SORRY NO CAN DO");
				return null;
			}
		}
		else {
			//we don't need a loop just generate a loop-less code segment and finish
			Executable exec = getInstructionsForDuration(min_time,max_time);
			if ( exec != null ) {
				result = exec;
			}
			else {
				//we can't create such a function and we need to quit
				//System.out.println("SORRY NO CAN DO");
				return null;
			}
			
		}
		return result;
	}
	
	private int maximum_created_duration;
	private final static int maximum_created_duration_not_initialized = -1;
	private int getMaximumPossibleCreatedDuration() {
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
		int temp_min_time = min_time;
		int temp_max_time = max_time;
		
		//the loops structure instructions are a must so we need to subtract them, conveniently they are the same in both cases so do that here
		instruction_limit -= 3;
		
		if ( case_one ) {
			//we have the necessary resources for building a type 1 loop structure
			//first choose which register pair we should use, if possible choose BC or DE before HL
			String reg_pair = ""; String reg_pair_other_reg = "";
			if ( (bc_pair&el.getE()) == bc_pair ) {reg_pair = "B"; reg_pair_other_reg = "C"; } else 
			if ( (de_pair&el.getE()) == de_pair ) {reg_pair = "D"; reg_pair_other_reg = "E"; }
			else  {reg_pair = "H"; reg_pair_other_reg = "L"; }
			
			int init_instruction_duratoin = getInitInstructionsDuration(loop_type_16b);
			int loop_logic_instructions_duration = getLoopLogicInstructionsDuration(loop_type_16b);
			min_time -= init_instruction_duratoin;
			max_time -= init_instruction_duratoin;
			int min_iterations = 2;
			int max_iterations = (1<<16)-1>max_time/loop_logic_instructions_duration ? max_time/loop_logic_instructions_duration:(1<<16)-1;
			
			
			//we have already decided which elements to use to get the loop logic so remove them from the list of available elements
			Element next_elements_usable[] = removeUsedRegisters(elements_usable, reg_pair , reg_pair_other_reg);
			precomputeAllPossibleDurations(next_elements_usable,instruction_limit);	
			Loop res = makeLoopByDeterminingInitValue(min_iterations, max_iterations, min_time, max_time, next_elements_usable, 
					loop_logic_instructions_duration, instruction_limit, nested_loops_counter, reg_pair, loop_type_16b);
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
			int init_instruction_duratoin = getInitInstructionsDuration(loop_type_8b);
			int loop_logic_instructions_duration = getLoopLogicInstructionsDuration(loop_type_8b);
			min_time -= init_instruction_duratoin;
			max_time -= init_instruction_duratoin;
			int min_iterations = 2;
			int max_iterations = (1<<8)-1<max_time/loop_logic_instructions_duration ? (1<<8)-1 : max_time/loop_logic_instructions_duration;
			
			
			//we have already decided which elements to use to get the loop logic so remove them from the list of available elements
			Element next_elements_usable[] = removeUsedRegisters(elements_usable,reg);
			
			precomputeAllPossibleDurations(next_elements_usable,instruction_limit);	
			
			Loop res = makeLoopByDeterminingInitValue(min_iterations, max_iterations, min_time, max_time, next_elements_usable, 
							loop_logic_instructions_duration, instruction_limit, nested_loops_counter, reg, loop_type_8b);
			if ( res != null ) return res;
		}
		return null;
	}
		
	private static final int loop_type_16b = 0;
	private static final int loop_type_8b = 1;
	
	private int getInitInstructionsDuration( int loop_type ) {
		if ( loop_type == loop_type_8b  ) return 1; else
		if ( loop_type == loop_type_16b ) return 7; else
		return 0;
	}
	
	private int getLoopLogicInstructionsDuration( int loop_type ) {
		if ( loop_type == loop_type_8b  ) return 14; else
		if ( loop_type == loop_type_16b ) return 16; else
		return 0;
	}

	private Executable getRetInstrcution( ) {
		return instruction_set.getInstruction("RET").getExecutable();
	}
	
	private Executable getInitInstrcution( int loop_type , String reg_or_reg_pair , int init_value ) {
		if ( loop_type == loop_type_8b ) {
			return instruction_set.getInstruction("MVI "+reg_or_reg_pair+",8b").getExecutable("8b",Integer.toString(init_value));
		}
		else if (  loop_type == loop_type_16b )
			return instruction_set.getInstruction("LXI "+reg_or_reg_pair+",16b").getExecutable("16b",Integer.toString(init_value));
		else return null;
	}
	
	private Executable getDcrInstrcution( int loop_type , String reg_or_reg_pair ) {
		if ( loop_type == loop_type_8b ) 
			return instruction_set.getInstruction("DCR "+reg_or_reg_pair).getExecutable();
		else if (  loop_type == loop_type_16b )
			return instruction_set.getInstruction("DCX "+reg_or_reg_pair).getExecutable();
		else return null;
	}
	
	private Executable getCondInstruction ( int loop_type , String location_label ) {
		if ( loop_type == loop_type_8b || loop_type == loop_type_16b ) 
			return instruction_set.getInstruction("JNZ 16b").getExecutable("16b",location_label);
		else return null;
	}
		
	private Loop makeLoop ( int loop_type , int init_value , String label , String reg_or_reg_pair , Executable other_instructions ) {
		Loop result = new Loop();
		result.init_instructions = getInitInstrcution(loop_type, reg_or_reg_pair, init_value);
		result.other_instructions = other_instructions;
		result.dcr_instructions = getDcrInstrcution(loop_type, reg_or_reg_pair);
		result.cond_instructions = getCondInstruction(loop_type, label);
		result.label = label;
		result.iterations = init_value;
		return result;
	}
	
	private Loop makeLoopByDeterminingInitValue ( int min_iterations , int max_iterations , int min_time , int max_time , 
												  Element elements_usable[] , int loop_logic_instructions_duration , int instruction_limit , 
												  int nested_loops_counter ,  String reg_or_reg_pair , int loop_type ) {
		String label = "loop"+nested_loops_counter;
		for ( int num_iterations = max_iterations  ; num_iterations >= min_iterations  ; --num_iterations ) {
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
				
				Executable exec = null;
				
				//we need loop nesting
				if ( next_min_time > getMaximumPossibleCreatedDuration() ) {
					exec = buildLoop(next_min_time,next_max_time,elements_usable,instruction_limit,nested_loops_counter+1);
				}
				else {
					//just generate a bunch of instructions
					exec = getInstructionsForDuration(next_min_time<0?0:next_min_time, next_max_time);
				}
				if ( exec != null ) {
					//we got it
					return makeLoop(loop_type, num_iterations, label, reg_or_reg_pair, exec);
				}
				else {
					//better luck next time
				}
				
				
			}
			//sorry guys, perhaps you can try with a different number of iteration
		}
		return null;
	}
	
	private Element[] removeUsedRegisters  ( Element elements_usable[] , String... registers ) {
		ArrayList<Element> result = new ArrayList<Element>();
		for ( Element e : elements_usable) {
			boolean to_add = true;
			if ( e.type == Element.REGISTER 
					&& e.status.equals(Element.available) ) {
				for ( int k = 0 ; k < registers.length ; ++k ) {
					if ( e.description.equals(registers[k]) ) {
						to_add = false;
						break;
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
	public long call_counter = 0;
	public long iterations = 0;
	public void precomputeAllPossibleDurations(Element[] elements_usable,
			int instruction_limit) {
		long start = 0 ; long end = 0;
		instructions_available = getAllAvailableInstructions(instruction_set,elements_usable);
		max_possible_duration = instruction_limit*getMaximumInstructionDuration()+1;
		maximum_created_duration = maximum_created_duration_not_initialized;
		//all are null in start
		ArrayList<InstructionMetadata>[] possible_durations_descr = new ArrayList[max_possible_duration];
		// no instructions are needed for duration of 0
		possible_durations_descr[0] = new ArrayList<InstructionMetadata>();
		//logic for generating, we must use at max 'instruction_limit' instructions
		//reference on algorithm used, here -> http://www.seeingwithc.org/topic1html.html
		
		for ( int k = 0 ; k < instruction_limit ; ++k ) {
			for ( int i = max_possible_duration-1 ; i >= 0 ; --i ) {
				if ( possible_durations_descr[i] != null ) {
					for ( int w = 0 ; w  < instructions_available.size() ; ++w ) {
						if ( i+instructions_available.get(w).getDuration() < max_possible_duration ) {
							if ( possible_durations_descr[i+instructions_available.get(w).getDuration()] == null ) {
								start = System.currentTimeMillis();
								possible_durations_descr[i+instructions_available.get(w).getDuration()] = new ArrayList<InstructionMetadata>(possible_durations_descr[i].size()+1);
								for ( InstructionMetadata is : possible_durations_descr[i] ) {
									possible_durations_descr[i+instructions_available.get(w).getDuration()].add(is);
								}
								possible_durations_descr[i+instructions_available.get(w).getDuration()].add(instructions_available.get(w));
								 end = System.currentTimeMillis();
								 call_counter += end-start;
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

	private int getMaximumInstructionDuration() {
		int res = 0;
		if ( instructions_available == null ) return res;
		for ( InstructionMetadata i : instructions_available ) {
			if ( i.getDuration() > res ) res = i.getDuration();
		}
		return res;
	}
	
	private int getMaximumNumberofIterationsWhenNestingLoops ( Elements elements ) {
		int result = 1;
		for ( int i = 0 ; i < 7/*we have 7 registers in 8085*/ ; ++i ) {
			if ( (1<<i&elements.getE()) > 0 ) {
				result *= 256;
			}
		}
		return result;
	}

}
