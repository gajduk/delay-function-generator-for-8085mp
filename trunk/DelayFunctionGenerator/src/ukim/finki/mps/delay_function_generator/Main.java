package ukim.finki.mps.delay_function_generator;

import java.util.Arrays;

public class Main {
	
	
	public static void main(String[] args) {
		Main m = new Main();
		m.c = new CodeBuilder();
		Element elements_usable2 []= 
		   { //	new Element(Element.STACK,Element.STACK_NOT_VALID) ,
//				 new Element(Element.MEMORY,"123") , 
//				 new Element(Element.MEMORY,"124") , 
				 new Element(Element.REGISTER, "A") 
//				 new Element(Element.REGISTER, "H"),
//				 new Element(Element.REGISTER, "L"),
//				 new Element(Element.REGISTER, "D"),
				/* new Element(Element.REGISTER, "E")*/ };
//		m.test(16292,16292, elements_usable2, false);
		m.testSmallDuration1();
		m.testSmallDuration2();
		m.testMediumDuration1();
		m.testLargeDuration1();
	}

	
	CodeBuilder c;
	
	/**
	 * use this for testing
	 */
	
	public void test( long min_time, long max_time, Element elements_usable[], boolean ret_is_required ) {
		System.out.println("Trying to make a function with duration in the intervall ["+min_time+","+max_time+"], with the following elements:" +
				Arrays.toString(elements_usable)+" , return "+(ret_is_required?"":"not") +" required");
		DelayFunction d = c.buildDelayFunction(min_time, max_time, "8085", elements_usable, ret_is_required);
		if ( d == null ) {
			System.out.println("We failed in generating");
		}
		else {
			System.out.println("Resulting code:\n"+d.toString());
			System.out.println("Lasting:"+d.time()+" states");
		}
	}
	
	public void testWithStep( long repetition , long start , long step , Element elements_usable[], boolean ret_is_required , long fault_tollerance) {
		for ( long i = 0 ; i < repetition ; ++i ) {
			long time = (long)(Math.random()*step) + start+(i*step);
			long min_time = time-fault_tollerance;
			long max_time = time+fault_tollerance;
			test(min_time,max_time,elements_usable,ret_is_required);
		}
	}
	
	public void testSmallDuration1 () {
		Element elements_usable[] = { /*new Element(Element.MEMORY,"123")*/ };
		System.out.println("Testing on small duration, no elements, no return and no fault tolerance");
		testWithStep(5, 0, 10, elements_usable, false, 0);

		System.out.println("Testing on small duration, no elements, no return and large fault tolerance");
		testWithStep(5, 0, 10, elements_usable, false, 2);
		
		System.out.println("Testing on small duration, no elements, with return and no fault tolerance");
		testWithStep(5, 0, 10, elements_usable, true, 0);
		
		System.out.println("Testing on small duration, no elements, with return and large fault tolerance");
		testWithStep(5, 0, 10, elements_usable, true, 2);
		
		Element elements_usable2 []= 
			   {/* new Element(Element.STACK,Element.STACK_NOT_VALID) , */
				 new Element(Element.MEMORY,"123") , 
				 new Element(Element.MEMORY,"124") , 
				 new Element(Element.REGISTER, "A"), 
				 new Element(Element.REGISTER, "H"),
				 new Element(Element.REGISTER, "L"),
				 new Element(Element.REGISTER, "D"),
				 new Element(Element.REGISTER, "E") };
		System.out.println("Testing on small duration, all possible elements, no return and no fault tolerance");
		testWithStep(5, 0, 10, elements_usable2, false, 0);
		
		System.out.println("Testing on small duration, all possible elements, no return and small fault tolerance");
		testWithStep(5, 0, 10, elements_usable2, false, 1);
	}
	
	public void testSmallDuration2 () {
		Element elements_usable[] = { /*new Element(Element.MEMORY,"123")*/ };
		System.out.println("\nTesting on small duration, no elements, no return and no fault tolerance.\n");
		testWithStep(5, 50, 30, elements_usable, false, 0);

		System.out.println("\nTesting on small duration, no elements, no return and large fault tolerance.\n");
		testWithStep(5, 50, 30, elements_usable, false, 2);
		
		System.out.println("\nTesting on small duration, no elements, with return and no fault tolerance.\n");
		testWithStep(5, 50, 30, elements_usable, true, 0);
		
		System.out.println("\nTesting on small duration, no elements, with return and large fault tolerance.\n");
		testWithStep(5, 50, 30, elements_usable, true, 2);
		
		Element elements_usable2 [] = 
			   { new Element(Element.STACK,Element.STACK_NOT_VALID) , 
				 new Element(Element.MEMORY,"123") , 
				 new Element(Element.MEMORY,"124") , 
				 new Element(Element.REGISTER, "A"), 
				 new Element(Element.REGISTER, "H"),
				 new Element(Element.REGISTER, "L"),
				 new Element(Element.REGISTER, "D"),
				 new Element(Element.REGISTER, "E") };
		
		System.out.println("\nTesting on small duration, all possible elements, no return and no fault tolerance.\n");
		testWithStep(5, 50, 30, elements_usable2, false, 0);
		
		System.out.println("\nTesting on small duration, all possible elements, with return and large fault tolerance.\n");
		testWithStep(5, 50, 30, elements_usable2, true, 2);
	}
	
	public void testMediumDuration1 () {
		long iters = 5;
		long start = 500;
		long step = 4000;
		Element elements_usable[] = { new Element(Element.MEMORY,"123") };
		System.out.println("\nTesting on medium duration, one memory location, no return and small fault tolerance.\n");
		testWithStep(iters, start, step, elements_usable, false, 2);

		Element elements_usable2[] = { new Element(Element.REGISTER,"A") };
		System.out.println("\nTesting on medium duration, one register, no return and small fault tolerance.\n");
		testWithStep(iters, start, step, elements_usable2, false, 2);
		
		Element elements_usable3 [] = 
			   { 
				 new Element(Element.MEMORY,"123") , 
				 new Element(Element.MEMORY,"124") , 
			   };
		System.out.println("\nTesting on medium duration, two memory locations, no return and small fault tolerance.\n");
		testWithStep(iters, start, step, elements_usable3, false, 2);
		
		Element elements_usable4 [] = 
			   { 
				 new Element(Element.REGISTER,"A") , 
				 new Element(Element.REGISTER,"B") , 
			   };
		System.out.println("\nTesting on medium duration, two registers, no return and small fault tolerance.\n");
		testWithStep(iters, start, step, elements_usable4, false, 2);
		
		Element elements_usable5 [] = 
			   { 
				 new Element(Element.REGISTER,"A") , 
				 new Element(Element.REGISTER,"B") , 
				 new Element(Element.REGISTER,"C") ,
				 new Element(Element.REGISTER,"D") ,
				 new Element(Element.REGISTER,"E") ,
			   };
		System.out.println("\nTesting on medium duration, lot of registers and pairs, no return and small fault tolerance.\n");
		testWithStep(iters, start, step, elements_usable5, false, 2);
		
		Element elements_usable6 [] = 
			   { 
				 new Element(Element.REGISTER,"A") , 
				 new Element(Element.REGISTER,"B") ,
				 new Element(Element.REGISTER,"D") ,
				 new Element(Element.REGISTER,"H") ,
			   };
		System.out.println("\nTesting on medium duration, lot of registers but no pairs, no return and small fault tolerance.\n");
		testWithStep(iters, start, step, elements_usable6, false, 2);
		
		Element elements_usable7 [] = 
			   { 
				new Element(Element.STACK,Element.STACK_VALID)  
			   };
		System.out.println("Testing medium small duration, stack only, with return and small fault tolerance");
		testWithStep(iters, start, step, elements_usable7, true, 2);
		
		Element elements_usable8[] = { new Element(Element.MEMORY,"123") };
		System.out.println("\nTesting on medium duration, one memory location, no return and no fault tolerance.\n");
		testWithStep(iters, start, step, elements_usable8, false, 0);

		Element elements_usable9[] = { new Element(Element.REGISTER,"A") };
		System.out.println("\nTesting on medium duration, one register, no return and no fault tolerance.\n");
		testWithStep(iters, start, step, elements_usable9, false, 0);
		
		Element elements_usable10[] = 
			   { 
				 new Element(Element.MEMORY,"123") , 
				 new Element(Element.MEMORY,"124") , 
			   };
		System.out.println("\nTesting on medium duration, two memory locations, no return and no fault tolerance.\n");
		testWithStep(iters, start, step, elements_usable10, false, 0);
		
		Element elements_usable11[] = 
			   { 
				 new Element(Element.REGISTER,"A") , 
				 new Element(Element.REGISTER,"B") , 
			   };
		System.out.println("\nTesting on medium duration, two registers, no return and no fault tolerance.\n");
		testWithStep(iters, start, step, elements_usable11, false, 0);
		
		Element elements_usable12[] = 
			   { 
				 new Element(Element.REGISTER,"A") , 
				 new Element(Element.REGISTER,"B") , 
				 new Element(Element.REGISTER,"C") ,
				 new Element(Element.REGISTER,"D") ,
				 new Element(Element.REGISTER,"E") ,
			   };
		System.out.println("\nTesting on medium duration, lot of registers and pairs, no return and no fault tolerance.\n");
		testWithStep(iters, start, step, elements_usable12, false, 0);
		
		Element elements_usable13[] = 
			   { 
				 new Element(Element.REGISTER,"A") , 
				 new Element(Element.REGISTER,"B") ,
				 new Element(Element.REGISTER,"D") ,
				 new Element(Element.REGISTER,"H") ,
			   };
		System.out.println("\nTesting on medium duration, lot of registers but no pairs, no return and no fault tolerance.\n");
		testWithStep(iters, start, step, elements_usable13, false, 0);
		
		Element elements_usable14[] = 
			   { 
				new Element(Element.STACK,Element.STACK_VALID)  
			   };
		System.out.println("Testing medium small duration, stack only, with return and no fault tolerance");
		testWithStep(iters, start, step, elements_usable14, true, 0);
		
		Element elements_usable15 [] = 
			   { new Element(Element.STACK,Element.STACK_NOT_VALID) , 
				 new Element(Element.MEMORY,"123") , 
				 new Element(Element.MEMORY,"124") , 
				 new Element(Element.REGISTER, "A"), 
				 new Element(Element.REGISTER, "H"),
				 new Element(Element.REGISTER, "L"),
				 new Element(Element.REGISTER, "D"),
				 new Element(Element.REGISTER, "E") };
		

		System.out.println("Testing medium small duration, all possible elements, with return and no fault tolerance");
		testWithStep(iters, start, step, elements_usable15, true, 0);
	}
	
	public void testLargeDuration1 () {
		long iters = 5;
		long start = 100000000L;
		long step = 300000000L;
		Element elements_usable1[] = 
			   { 
				new Element(Element.STACK,Element.STACK_VALID)  
			   };
		/*
		 * known issue, taking really long (30 seconds) when working with stack only
		 */
		System.out.println("Testing large duration, stack only, with return and small fault tolerance");
		testWithStep(2, start, step, elements_usable1, true, 10);
		Element elements_usable2[] = 
			   { 
				 new Element(Element.REGISTER,"A") , 
				 new Element(Element.REGISTER,"B") , 
				 new Element(Element.REGISTER,"C") ,
				 new Element(Element.REGISTER,"D") ,
				 new Element(Element.REGISTER,"E") ,
			   };
		System.out.println("\nTesting on large duration, lot of registers and pairs, no return and small fault tolerance.\n");
		testWithStep(iters, start, step, elements_usable2, false,10);
		
		Element elements_usable3[] = 
			   { 
				 new Element(Element.REGISTER,"A") , 
				 new Element(Element.REGISTER,"B") ,
				 new Element(Element.REGISTER,"D") ,
				 new Element(Element.REGISTER,"H") ,
			   };
		System.out.println("\nTesting on large duration, lot of registers but no pairs, no return and small fault tolerance.\n");
		testWithStep(iters, start, step, elements_usable3, false, 10);
		
		Element elements_usable4[] = 
			   { 
				 new Element(Element.MEMORY,"123") , 
				 new Element(Element.MEMORY,"124") , 
				 new Element(Element.MEMORY,"125") , 
				 new Element(Element.MEMORY,"126") , 
				 new Element(Element.MEMORY,"127") , 
			   };
		System.out.println("\nTesting on large duration, 5 memory locations, no return and small fault tolerance.\n");
		testWithStep(iters, start, step, elements_usable4, false, 10);
		
		Element elements_usable5[] = 
			   { 
				 new Element(Element.MEMORY,"123") , 
				 new Element(Element.MEMORY,"124") , 
				 new Element(Element.MEMORY,"125") , 
				 new Element(Element.MEMORY,"126") , 
				 new Element(Element.REGISTER,"A") , 
				 new Element(Element.REGISTER,"B") ,
				 new Element(Element.REGISTER,"D") ,
				 new Element(Element.REGISTER,"H") ,
				 new Element(Element.REGISTER,"E") ,
			   };
		System.out.println("\nTesting on large duration, lot of registers and some memory locations, no return and small fault tolerance.\n");
		testWithStep(iters, start, step, elements_usable5, false, 10);
		
		Element elements_usable6[] = 
			   { new Element(Element.STACK,Element.STACK_NOT_VALID) , 
				 new Element(Element.MEMORY,"123") , 
				 new Element(Element.MEMORY,"124") , 
				 new Element(Element.REGISTER, "A"), 
				 new Element(Element.REGISTER, "H"),
				 new Element(Element.REGISTER, "L"),
				 new Element(Element.REGISTER, "D"),
				 new Element(Element.REGISTER, "E") };
		

		System.out.println("Testing on large duration, all possible elements, with return and no fault tolerance");
		testWithStep(iters, start, step, elements_usable6, true, 0);
		
	}

}
