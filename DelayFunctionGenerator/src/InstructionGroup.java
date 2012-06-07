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
	 *  *Notice: of all the meta-data in the instructions use only the instruction opcode
	 */
	public InstructionGroup ( ArrayList instuctions ) {
		this.instuctions = new ArrayList<Executable>(instuctions.size());
		if ( instuctions.size() < 0 ) return;
		if ( instuctions.get(0).getClass() == InstructionMetadata.class ) {
			for ( Object is : instuctions ) {
				String instruction_code = ((InstructionMetadata) is).getInstruction_code();
				instruction_code = instruction_code.replaceAll("8b", Integer.toString((int)(Math.random()*(1<<8))));
				instruction_code = instruction_code.replaceAll("16b", Integer.toString((int)(Math.random()*(1<<16))));
				this.instuctions.add(new Instruction(instruction_code));
			}
		}
		else {
			for ( Object is : instuctions ) {
				this.instuctions.add((Executable) is);
			}
		}
	}	


	/**
	 * a shortcut constructor for creating a single instruction groups
	 * @param instruction
	 */
	public InstructionGroup(Instruction instruction) {
		this.instuctions = new ArrayList<Executable>();
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
	
	@Override
	public String toString() {
		if ( instuctions == null ) return "ERROR null value found";
		if ( instuctions.size() == 0 ) return "NO INSTRUCTIONS. EMPTY GROUP";
		String res = "";
		for ( Executable i : instuctions ) {
			res += " "+ i.toString() + " ; ";
		}
		return res;
	}


	@Override
	public int length() {
		return instuctions == null ? 0 : instuctions.size();
	}

}
