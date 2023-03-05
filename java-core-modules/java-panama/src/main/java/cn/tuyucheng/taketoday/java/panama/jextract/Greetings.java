package cn.tuyucheng.taketoday.java.panama.jextract;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.MemorySession;

// Generate JExtract bindings before uncommenting
// import static foreign.c.stdio_h.printf;
public class Greetings {

	public static void main(String[] args) {
		String greeting = "Hello World from Project Panama Tuyucheng Article, using JExtract!";

		try (MemorySession memorySession = MemorySession.openConfined()) {
			MemorySegment greetingSegment = memorySession.allocateUtf8String(greeting);
			// Generate JExtract bingings before uncommenting
			//  printf(greetingSegment);
		}
	}
}