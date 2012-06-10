package ukim.finki.mps.delay_function_generator;
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
	public InstructionGroup ( ArrayList instuctions ) {
		this.instuctions = new ArrayList<Executable>(instuctions.size());
		if ( instuctions == null || instuctions.size() < 0 ) return;
		if ( instuctions.get(0).getClass() == InstructionData.class ) {
			for ( Object is : instuctions ) {
				this.instuctions.add(((InstructionData) is).getExecutable());
			}
		}
		else {
			for ( Object is : instuctions ) {
				this.instuctions.add((Executable) is);
			}
		}
	}	
	
	/**
	 * constructor to be used to specify memory locations for the instructions
	 * , initialize the instructions to a the instruction array param
	 * @param instuctions - copy this as the instruction group
	 */
	public InstructionGroup ( ArrayList<InstructionData> instuctions , Element single , Element pair ) {
		this.instuctions = new ArrayList<Executable>(instuctions.size());
		if ( instuctions == null || instuctions.size() < 0 ) return;
		if ( instuctions.get(0).getClass() == InstructionData.class ) {
			for ( Object is : instuctions ) {
				InstructionData is_data = (InstructionData) is;
				if ( (is_data.getElementsAffected().getE()&(1<<9)) > 0 ) {
					this.instuctions.add(((InstructionData) is).getExecutable("16b",single.description));
				} else {
				if ( (is_data.getElementsAffected().getE()&(1<<10)) > 0 ) {
					this.instuctions.add(((InstructionData) is).getExecutable("16b",pair.description));
				} else {
				 
				this.instuctions.add(((InstructionData) is).getExecutable());}}
			}
		}
	}	

	/**
	 * a shortcut constructor for creating a single instruction groups
	 * @param instruction
	 */
	public InstructionGroup(Executable instruction) {
		this.instuctions = new ArrayList<Executable>();
		this.instuctions.add(instruction);
	}

	/**
	 * how long will this InstructionGroup take to fully execute
	 * the sum of the times for all instructions in this group
	 * @return time in T- number of states of the mP
	 */
	@Override
	public long time() {
		if ( instuctions == null || instuctions.size() == 0 ) return 0;
		long total_time = 0;
		for ( Executable e : instuctions ) {
			total_time += e == null ?0:e.time();
		}
		return total_time;
	}
	
	@Override
	public String toString() {
		if ( instuctions == null ) return "";//"ERROR null value found";
		if ( instuctions.size() == 0 ) return "";//"NO INSTRUCTIONS. EMPTY GROUP";
		String res = "";
		boolean flag = true;
		for ( Executable i : instuctions ) {
			if ( flag ) {
				flag = false;
			}
			else {
				res += "\n";
			}
			res += i.toString() ;
		}
		return res;
	}

	/**
	 * how many instructions are there in this group
	 * @return number of instructions, the sum of all instructions in all 4 executables
	 */
	@Override
	public int length() {
		if ( instuctions == null || instuctions.size() == 0 ) return 0;
		int total_length = 0;
		for ( Executable e : instuctions ) {
			total_length += e == null ?0:e.length();
		}
		return total_length;
	}

	/**
	 * used when we want an executable to be added to this group as the last Executable in the current sequence
	 * @param exec - the executable to be added to this group
	 */
	public Executable append(Executable exec) {
		if ( instuctions == null ) {
			instuctions = new ArrayList<Executable>();
		}
		instuctions.add(exec);	
		return this;
	}
	
	/**
	 * used when we want an executable to be added to this group as the first Executable in the current sequence
	 * @param exec - the executable to be added to this group
	 */
	public Executable insert(Executable exec) {
		if ( instuctions == null ) {
			instuctions = new ArrayList<Executable>();
		}
		instuctions.add(0, exec);
		return this;
	}

	/**
	 * used to determine whether this group contains a specific executable
	 * @param exec - an executable we are checking whether already is in this group
	 * @return - true if the exact same executable is in this group (works only for single instructions, but that is the only reason it is calld for)
	 */
	public boolean contains(Executable exec) {
		return instuctions.contains(exec);
	}

}
