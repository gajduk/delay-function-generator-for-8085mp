
public class Main {


	public static void main(String[] args) {
		CodeBuilder c = new CodeBuilder();
		Element elements_usable[] = { new Element(Element.REGISTER,"A"),new Element(Element.REGISTER,"B"),new Element(Element.REGISTER,"C"),new Element(Element.REGISTER,"D")};
		//c.precomputeAllPossibleDurations("8085", elements_usable, 10);
		System.out.println(c.buildDelayFunction(150000, 151000, "8085", elements_usable, 10));
	}

}
