package sortable.matcher;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public abstract class LineParser<T> {
	public ArrayList<T> parse(String filename) {
		ArrayList<T> items = new ArrayList<>();
		BufferedReader reader;
		try {
			reader = Files.newBufferedReader(Paths.get(filename), Charset.forName("UTF-8"));
		} catch (IOException e) {
			return null;
		}
		
		try {
			for(;;) {
				String line;
				try {
					line = reader.readLine();
				} catch (IOException e) {
					continue;
				}
				
				if (line == null) {
					break;
				}
				T item = parseLine(line);
				if (item == null) {
					continue;
				}
				items.add(item);
			}
		} finally {
			try {
				reader.close();
			} catch (IOException e) {}
		}
		return items;
	}
	
	public abstract T parseLine(String line);
}
