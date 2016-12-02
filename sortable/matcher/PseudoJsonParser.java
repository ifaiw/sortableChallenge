package sortable.matcher;

import java.util.HashMap;

// Normally I wouldn't write my own parser, but since even the Oracle json parser
// for Java requires a separate jar, I wasn't sure if that would be acceptable for
// this challenge
public class PseudoJsonParser {
	private int _index;
	private String _line;
	private HashMap<String, String> _values;
	
	public HashMap<String, String> parseLine(String line) {
		if (line == null || line.length() < 2 || line.charAt(0) != '{') {
			return null;
		}
		
		_index = 1;
		_line = line;
		_values = new HashMap<>();
		if (_line.equals("{}")) {
			return _values;
		}
		for(;;) {
			boolean result = readPair();
			if (!result) {
				return null;
			}
			
			if (_line.charAt(_index) == '}') {
				return _values;
			}
			if (_line.charAt(_index) != ',') {
				return null;
			}
			_index++;
		}
	}
	
	private boolean readPair() {
		String name = readQuotedString();
		if (name == null) {
			return false;
		}
		if (_index > _line.length() - 2 || _line.charAt(_index) != ':') {
			return false;
		}
		_index++;
		String value = readQuotedString();
		if (value == null) {
			return false;
		}
		if (_index > _line.length() - 1) {
			return false;
		}
		
		_values.put(name,  value);
		return true;
	}
	
	private String readQuotedString() {
		if (_line.charAt(_index) != '"') {
			return null;
		}
		int start = _index + 1;
		int end = findUnescaped(_line, '"', _index + 1);
		if (end == -1) {
			return null;
		}
		_index = end + 1;
		return _line.substring(start, end);
	}
	
	// Doesn't handle c='\\', okay with that
	public static int findUnescaped(String s, char c, int start) {
		int index = start;
		boolean escaped = false;
		for(;;) {
			if (index > s.length() - 1) {
				return -1;
			}
			
			if (escaped) {
				index++;
				escaped = false;
				continue;
			}
			
			if (s.charAt(index) == c) {
				return index;
			}
			
			if (s.charAt(index) == '\\') {
				escaped = true;
			}
			index++;
		}
	}
}
