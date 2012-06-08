package ukim.finki.mps.delay_fucntion_generator;

public class Main {


	public static void main(String[] args) {
		CodeBuilder c = new CodeBuilder();
		Element elements_usable[] = {   new Element(Element.REGISTER,"A"),
				new Element(Element.REGISTER,"B"),
				new Element(Element.REGISTER,"C"),
		};
		//c.precomputeAllPossibleDurations("8085", elements_usable, 10);
		long start = System.currentTimeMillis();
		DelayFunction df = c.buildDelayFunction(99000000,100000000, "8085", elements_usable);
		System.out.println(df.toString());
		System.out.println("Duration of delay function: "+df.time()+" states of the mP.");
		long end = System.currentTimeMillis();
		System.out.println("Time taken: "+(end-start)/1000+"."+(end-start)%1000+" s.");
	}

}
