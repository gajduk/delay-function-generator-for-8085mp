import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;


/**
 * a group of instructions defined in the instruction set manual
 * @author Andrej Gajduk
 *
 */
public class InstructionGroup extends Executable {
	
	/**
	 * the group of instructions stored in an array list to provide dynamic adding and removing instructions
	 * the order does not matter and does not have to be maintained
	 */
	private ArrayList<Executable> instuctions;
	
	/**
	 * default constructor, initialize the instructions to an empty array
	 */
	public InstructionGroup () {
		instuctions = new ArrayList<Executable>();
	}
	
	
	/**
	 * default constructor, initialize the instructions to a the instruction array param
	 * @param instuctions - copy this as the instruction group
	 */
	public InstructionGroup ( ArrayList<Executable> instuctions ) {
		this.instuctions = new ArrayList<Executable>(instuctions.size());
		Collections.copy(this.instuctions,instuctions);
	}

	/**
	 * a shortcut constructor for creating a single instruction groups
	 * @param instruction
	 */
	public InstructionGroup(Instruction instruction) {
		this.instuctions = new ArrayList<Executable>(instuctions.size());
		this.instuctions.add(instruction);
	}


	/**
	 * how long will this InstructionGroup take to fully execute
	 * the sum of the times for all instructions in this group
	 * @return time in T- number of states of the mP
	 */
	@Override
	public int time() {
		if ( instuctions == null || instuctions.size() == 0 ) return 0;
		int total_time = 0;
		for ( Executable e : instuctions ) {
			total_time += e.time();
		}
		return total_time;
	}

}
