package sortable.matcher;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Product {
	public final String line;
	public final String name;
	public final String manufacturer;
	public final String model;
	public final String family;
	public final Date announced;
	public final ArrayList<String> namePieces;
	public final String nameSquash;
	
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	public Product(String line_, String name_, String manufacturer_, String model_, String family_, String announced_) 
		throws ParseException
	{
		line = line_;
		
		name = name_;
		manufacturer = manufacturer_.toLowerCase();
		model = model_.toLowerCase();
		family = family_ != null ? family_.toLowerCase() : null;
		announced = dateFormat.parse(announced_);
		
		String nameLower = name.toLowerCase();
		namePieces = WordTools.split(nameLower);
		nameSquash = WordTools.squish(nameLower);
	}
	
	public static Product createFromMap(String line, HashMap<String, String> values) {
		String name = values.get("product_name");
		if (name == null) return null;
		
		String manufacturer = values.get("manufacturer");
		if (manufacturer == null) return null;
		
		String model = values.get("model");
		if (model == null) return null;
		
		String family = values.get("family");
		
		String date = values.get("announced-date");
		if (date == null) return null;
		if (date.length() < 10) return null;
		date = date.substring(0, 10);
		
		try {
			return new Product(line, name, manufacturer, model, family, date);
		} catch (ParseException e) {
			return null;
		}
	}
}
