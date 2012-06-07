

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
	
	public final static String available = "available";
	public final static String not_available = "not_available";
	
	/**
	 * determines the type of the element which can be one of the following:
	 * register, memory location, stack 
	 */
	int type;
	
	
	/**
	 * defines to be used when determining the type of element in question
	 */
	public final static int REGISTER = 0;
	public final static int MEMORY = 1;
	public final static int STACK = 2;
	
	/**
	 * a constructor from GUI data
	 * default status = available
	 */
	public Element ( int type , String desc ) {
		this.type = type;
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
	
	
}
