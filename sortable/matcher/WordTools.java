package sortable.matcher;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashSet;

public class WordTools {
	public static HashSet<String> getBagOfWords(String s) {
		HashSet<String> bag = new HashSet<>();
		
		s = Normalizer.normalize(s, Normalizer.Form.NFD).toLowerCase();
		
		int start = 0;
		int end = 0;
		int index = 0;
//		for(;;) {
//			if (index == s.length() || s.charAt(index) == ' ') {
//				
//			}
//		}
		
		return bag;
	}
	
	public static String squish(String... strings) {
		int squishedSize = 0;
		int maxSize = 0;
		for (String s : strings) {
			maxSize += s.length();
		}
		char[] squished = new char[maxSize];
		
		for (String s : strings) {
			for (int i = 0; i < s.length(); ++i) {
				char c = s.charAt(i);
				switch (c) {
				case ' ':
				case '_':
					break;
				default:
					squished[squishedSize] = c;
					++squishedSize;
				}
			}
		}
		return new String(squished, 0, squishedSize);
	}
	
	// Split the string into pieces, throw away any pieces with less than two chars
	public static ArrayList<String> split(String s) {
		ArrayList<String> pieces = new ArrayList<>();
		int start = 0;
		for (int index = 0;index < s.length(); ++index) {
			char c = s.charAt(index);
			switch (c) {
			case ' ':
			case '-':
			case '_':
				if (index - start > 2) {
					pieces.add(s.substring(start, index));
				}
				start = index + 1;
				index = start;
				break;
			}
		}
		if (s.length() - start > 3) {
			pieces.add(s.substring(start, s.length()));
		}
		return pieces;
	}
}
