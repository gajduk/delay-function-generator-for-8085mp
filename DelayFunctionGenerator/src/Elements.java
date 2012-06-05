
public class Elements {
	
	
	/**
	 * all the elements that are affected by this an instruction
	 * the integer is coded as follows
	 * 
	 * bits 0-7 represent the 8 REGs in the following order
	 * bit|register
	 *  0 | A
	 *  1 | B
	 *  2 | C
	 *  3 | D
	 *  4 | E
	 *  5 | F
	 *  6 | H
	 *  7 | L
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
	
	/**
	 * the next three integer arrays are used to encrypt within a single integer all the information provided about a given instructions interaction with the elements of the 8085 system
	 * such as registers, memory locations and stack,
	 * only for internal use, as showed in the first constructor
	 * 	 */
	
	//                                               A   B   C   D   E    F    G   H    I   J   K   L  
	private static final int[] register_encoding = { 1 , 2 , 4 , 8 , 16 , 32 , 0 , 64 , 0 , 0 , 0 , 128 };
	
	//  memory locations needed                    0     1     2
	private static final int[] memory_encoding = { 256 , 512 , 1024 };
	
	//  is stack needed 0 = no , 1 = yes          0   1
	private static final int[] stack_encoding = { 0 , 2048 };
	
	public Elements ( String registers , String memory_locations , String stack ) {
		e = 0;
		for ( int i = 0 ; i < registers.length() ; ++i ) {
			char c = registers.charAt(i);
			if ( (c >= 'A' && c <='H' && c != 'G') || ( c == 'L' ) ) {
				e += register_encoding[c-'A'];
			}
		}
		e += memory_encoding[Integer.parseInt(memory_locations)];
		e += stack_encoding[Integer.parseInt(stack)];
	}
	
	
	public int getE() {
		return e;
	}

	public void setE(int e) {
		this.e = e;
	}
	
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

}
