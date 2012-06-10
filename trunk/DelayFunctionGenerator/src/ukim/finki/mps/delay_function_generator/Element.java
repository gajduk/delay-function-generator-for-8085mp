package ukim.finki.mps.delay_function_generator;


/**
 * defines usable elements of the mP, registers, memory locations, stack etc
 * @author Andrej Gajduk
 *
 */
public class Element {
	
	/**
	 * a description of the element,
	 * for registers A , B etc
	 * for memory locations their address
	 * for stack NOT APLICABLE
	 */
	String description;
	
	/**
	 * the status of the element
	 * available - we can use it
	 * not available - we can't use it
	 */
	String status;
	
	/**
	 * define to be used when examining the status of an element
	 */
	public final static String available = "available";
	
	/**
	 * define to be used when examining the status of an element
	 */
	public final static String not_available = "not_available";
	
	/**
	 * determines the type of the element which can be one of the following:
	 * register, memory location, stack 
	 */
	int type;
	
	/**
	 * define to be used when determining the type of element in question
	 */
	public final static int REGISTER = 0;
	
	/**
	 * define to be used when determining the type of element in question
	 */
	public final static int MEMORY = 1;
	
	/**
	 * define to be used when determining the type of element in question
	 */
	public final static int STACK = 2;
	
	/**
	 * define for stack when we can do pushes and pops
	 */
	public final static String STACK_VALID = "stack valid";
	
	
	/**
	 * define for stack when we can't do pushes and pops
	 */
	public final static String STACK_NOT_VALID = "stack not valid";
	
	/**
	 * a constructor from GUI data
	 * default status = available
	 */
	public Element ( int type , String desc ) {
		this.type = type;
		if ( type == MEMORY ) {
			try {
				int k = Integer.parseInt(desc);
			}
			catch ( Exception ex ) {
				desc = Integer.toString((int)(Math.random()*65535));
			}
		}
		description = desc;
		status = available;
	}

	/**
	 * a full-field constructor
	 */
	public Element(String description, String status, int type) {
		this.description = description;
		this.status = status;
		this.type = type;
	}
	
	@Override
	public String toString() {
		return description+" "+status+" "+type;
	}
	
	
}
