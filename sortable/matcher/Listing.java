package sortable.matcher;

import java.util.HashMap;

public class Listing {
	public final String line;
	public final String lineLower;
	public final String squash;
	public final String title;
	public final String manufacturer;
	public final String currency;
	public final double price;
	
	// In here we'll precompute a few useful strings for matching later
	// Also, convert everything to lower case, to make text matching easier and faster
	public Listing(String line_, String title_, String manufacturer_, String currency_, String price_) 
	{
		line = line_;
		lineLower = line.toLowerCase();
		
		title = title_.toLowerCase();
		manufacturer = manufacturer_.toLowerCase();
		currency = currency_;
		price = Double.parseDouble(price_);
		
		// The squash is a pseudo-bag of words, which various Product fields can be matched against
		int firstSpace = title.indexOf(' ');
		if (firstSpace > -1 && !manufacturer.equals(title.substring(0, firstSpace))) {
			// If the title doesn't start with the manufacturer, prepend it
			squash = WordTools.squish(manufacturer, title, manufacturer);
		} else {
			squash = WordTools.squish(title, manufacturer);
		}
	}
	
	public static Listing createFromMap(String line, HashMap<String, String> values) {
		String title = values.get("title");
		if (title == null) return null;
		
		String manufacturer = values.get("manufacturer");
		if (manufacturer == null) return null;
		
		String currency = values.get("currency");
		if (currency == null) return null;
		
		String price = values.get("price");
		if (price == null) return null;
		
		try {
			return new Listing(line, title, manufacturer, currency, price);
		} catch (NumberFormatException e) {
			return null;
		}
	}
}
