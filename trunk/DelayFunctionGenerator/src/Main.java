
public class Main {


	public static void main(String[] args) {
		CodeBuilder c = new CodeBuilder();
		Element elements_usable[] = {   new Element(Element.STACK,"25642"),
		
		};
		//c.precomputeAllPossibleDurations("8085", elements_usable, 10);
		long start = System.currentTimeMillis();
		System.out.println(c.buildDelayFunction(90000900,100000100, "8085", elements_usable));
		long end = System.currentTimeMillis();
		System.out.println("Time taken: "+(end-start)/1000+"."+(end-start)%1000+" s.");
	}

}
