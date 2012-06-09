package ukim.finki.mps.delay_fucntion_generator;

/**
 * class that contains the data which instructions uses which resources of a mP system such as registers, memory and stack
 * This class is specialized for 8085, since the last update in specification stated that the project is not general but specific and native to 8085
 * @author Andrej Gajduk
 */
public class Elements {
	
	/**
	 * all the elements that are affected by this instruction
	 * the integer is coded as follows
	 * 
	 * bits 0-7 represent the 8 REGs in the following order
	 * bit|register
	 *  0 | A
	 *  1 | B
	 *  2 | C
	 *  3 | D
	 *  4 | E
	 *  5 | H
	 *  6 | L
	 *  
	 *  bits 8-10 represent the number of memory locations required to be allowed access to by the instruction
	 *  bit|number of memory location
	 *   8 | 0
	 *   9 | 1
	 *  10 | 2
	 *  
	 *  bit 11 states whether or not the instruction affects or need the stack to be operational
	 *  
	 *  in all cases the value '0' stands for NO, it is NOT needed and/or it is NOT affected
	 *  while the value '1' stands for the opposite YES, it IS needed and/or it IS affected 
	 *  
	 *  bits are order form lowest significance in ascending order meaning the bit 0 is the least significant one,
	 *  bit 1 is the next least significant and so on.
	 *  
	 *  Sample encoding
	 *  instruction         -  DAD SP
	 *  registers affected  -  HL   
	 *  memory locations    -  0
	 *  needed 
	 *  stack required      -  1
	 *  
	 *  HL => bits 6 & 7  are set all other bits 0-5 are clear
	 *  memory location needed=1 => bit 8 is set, bits 9&10 are clear
	 *  stack required => bit 11 is set 
	 *  the value of 'e' will be :
	 *  100111000000 in binary
	 *  2496 normal
	 *  
	 *  *IMPORTANT NOTICE: unlike the registers who are only included here if their contents (value) is changed
	 *  the memory locations are included even if their value is not changed i.e. 
	 *  a memory location is included if it is being accessed.
	 *  This is to prevent the code from attempts at accessing inexistent memory locations or memory locations that have a I/O device mapped to it.
	 *  concerning the stack:is included if the instructions reads/changes the value of the SP(stack pointer) register,
	 *  or/and perform push or pop operations on the stack.
	 *  
	 */
	private int e;
	
	/*
	 * the next three integer arrays are used to encrypt within a single integer all the information provided about a given instructions interaction with the elements of the 8085 system
	 * such as registers, memory locations and stack,
	 * only for internal use, as showed in the first constructor
	 * 	 */
	
	/**
	 * registers encoding as seen
	 */
	//                                               A   B   C   D   E    F    G   H    I   J   K   L  
	private static final int[] register_encoding = { 1 , 2 , 4 , 8 , 16 , 0 , 0 , 32 , 0 , 0 , 0 , 64 };
	
	/**
	 *   memory locations needed                    0     1     2
	 */
	private static final int[] memory_encoding = { 256 , 512 , 1024 };
	
	/**
	 *  is stack needed 0 = no , 1 = yes          0   1
	 */
	private static final int[] stack_encoding = { 0 , 2048 };
	
	/**
	 * a constructor for creating elements objects from string descriptors of instruction read from files
	 * @param registers - string descriptor of the available registers
	 * @param memory_locations - string descriptor of the available memory locations
	 * @param stack - string descriptor of the available stack resource
	 */
	public Elements ( String registers , String memory_locations , String stack ) {
		e = 0;
		for ( int i = 0 ; i < registers.length() ; ++i ) {
			char c = registers.charAt(i);
			e += encodeRegister(c,e);
		}
		e += memory_encoding[Integer.parseInt(memory_locations)];
		e += stack_encoding[Integer.parseInt(stack)];
	}
	
	/**
	 * constructor for when we want to create a stub-comparison object for determining whether or not we can use a specific instruction
	 * with the usable elements, used when we want to separate the available instructions out of the whole instruction set
	 * @param elements_usable - a descriptions of all the elements that needs to be encrypted in a single integer, as an instance of this class
	 */
	public Elements ( Element elements_usable[] ) {
		e = 0;
		int memory_locations_counter = 0;
		e += memory_encoding[memory_locations_counter++];
		if ( elements_usable == null || elements_usable.length == 0 ) return;
		
		for ( Element el  : elements_usable ) {
			if ( el == null ) continue;
			if ( el.type == Element.REGISTER ) {
				if ( el.status.equals(Element.available) ) {
					char c = el.description.charAt(0);
					e += encodeRegister(c,e);
				}
			}
			else {
			if ( el.type == Element.MEMORY ) {
				if ( memory_locations_counter < 3 ) {
					if ( el.status.equals(Element.available) ) {
						e += memory_encoding[memory_locations_counter++];
					}
				}
			}
			else {
			if ( el.type == Element.STACK ) {
				if ( el.description.equals("not valid stack") ) {
					if ( (e&stack_encoding[1]) == 0 ) {
						if ( el.status.equals(Element.available) ) {
							e += stack_encoding[1];
						}
					}
				}
			}
			else {
				System.err.println("UNKNOWN TYPE");
			}
			}}
			
		}
	}
	
	/**
	 * to be used internally as a method that returns the integer value that needs to be added to the description encoding,
	 * so that it states availability of the register described by its char
	 * @param register - char representing the register (A,B .. H,L .. )
	 * @param e - current encoding - check if we already have that register encoded as available
	 * @return
	 */
	private int encodeRegister ( char register , int e ) {
		if ( (register >= 'A' && register <='H' && register != 'G') || ( register == 'L' ) ) {
			if ( (e&register_encoding[register-'A']) == 0 ) return register_encoding[register-'A'];
		}
		return 0;
	}

	/**
	 * @return the e
	 */
	public int getE() {
		return e;
	}

	/**
	 * @param e the e to set
	 */
	
	public void setE(int e) {
		this.e = e;
	}
	

	/**
	 * gives a nice detailed description of the resources used by an instruction
	 */
	@Override
	public String toString() {
		String res = "REGISTERS: ";
		boolean flag = true;
		for ( int i = 0 ; i < register_encoding.length ; ++i ) {
			if ( register_encoding[i] != 0  && (e&register_encoding[i]) != 0 ) {
				res += (char)('A'+i)+" , ";
				flag = false;
			}
		}
		if ( flag ) res += " NONE ,";
		res += "MEMORY: ";
		for ( int i = 0 ; i < memory_encoding.length ; ++i ) {
			if ( (e&memory_encoding[i]) != 0  ) {
				res += i+" locations required , "; break;
			}
		}
		res += "STACK: ";
		if ( (e&stack_encoding[1]) == 0 ) res += " NOT ";
		res += " REQUIRED";
		return res;
	}
	
	/**
	 * a function used to separate available instructions from the whole instruction set,
	 * it checks whether the given comparator object contains all the resources required for a given instruction to execute
	 * @param comparator - an object describing the available resources
	 * @return - boolean value - can we use this instruction, with the given resource 
	 */
	public boolean isAvailable ( Elements comparator ) {
		return (comparator.e&e) == e;
	}

	/**
	 * reverse mapping on which bit does which register corresponds
	 */
	private static final String registers[] = { "A" , "B" , "C" , "D" , "E" , "H" , "L" };
	
	/**
	 * a function to determine which register corresponds to the bit-index in the e-integer
	 * @param index - of a bit in the e-integer
	 * @return - the register the bit at position index corresponds
	 */
	public String getRegister(int index) {
		if ( index >= 0 && index < registers.length )return registers[index];
		return null;
	}

}
