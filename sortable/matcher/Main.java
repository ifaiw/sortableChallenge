package sortable.matcher;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import sortable.matcher.Scorer.Score;

public class Main {
	public static void main(String[] arg) {
		// Read in all the products at once
		ProductParser productParser = new ProductParser();
		ArrayList<Product> products = productParser.parse(arg[0]);
		if (products == null) {
			System.out.println("Products file not found: " + arg[0]);
			System.exit(-1);
		}
		
		// Here is where we'll track all listings that match each product
		String[] productsToListings = new String[products.size()];
		
		BufferedReader reader = null;
		try {
			reader = Files.newBufferedReader(Paths.get(arg[1]), Charset.forName("UTF-8"));
		} catch (IOException e) {
			System.out.println("Listings file not found: " + arg[1]);
			System.exit(-1);
		}
		
		// Do the real work
		try {
			processListings(reader, products, productsToListings);
		} finally {
			try {
				reader.close();
			} catch (IOException e) {}
		}
		
		// Write out our results
		String outputFilename = "output.txt";
		BufferedWriter writer = null;
		try { 
			writer = Files.newBufferedWriter(Paths.get(outputFilename), Charset.forName("UTF-8"));
		} catch (IOException e) {
			System.out.println("Unable to open output file " + outputFilename);
			System.exit(-1);
		}
		try {
			writeResults(writer, products, productsToListings);
		} catch (IOException e) {
			System.out.println("Error writing results to " + outputFilename + ": " + e);
			e.printStackTrace(System.out);
		} finally {
			try {
				writer.close();
			} catch (IOException e) {}
		}
	}
	
	private static void processListings(BufferedReader reader, ArrayList<Product> products, String[] productsToListings) {
		ListingParser listingParser = new ListingParser();
		
		// So, we're going to effectively check every listing against every product, which is
		// O(j * k) where j is the number of products and listings.
		// This runtime is not ideal if the number of products is considered to grow
		// with the size of the problem. However, the relative sizes of the listings.txt
		// and products.txt files provided suggests to me that the products are a relatively
		// constant list, and the problem input is a continuous stream of listings.
		// If the product list could also be arbitrarily large or growing, then a different
		// algorithm that runs in closer to O(j + k) might be necessary. To that end we'd 
		// probably want to index all the products based on the name, manufacturer, model, 
		// and family fields first. Then for each listing keywords could be extracted from
		// the title and manufacturer fields, and looked up in the product indices rather quickly.
		// The trick with such an approach is figuring out how to extract relevant terms from the
		// listing titles, which can vary quite a bit in how they're written.
		for (;;) {
			String line = null;
			try {
				line = reader.readLine();
			} catch (IOException e) {
				// If an error happens reading a line, just skip it
				continue;
			}
			if (line == null) {
				break;
			}
			
			Listing listing = listingParser.parseLine(line);
			if (listing == null) {
				continue;
			}
			
			Score bestMatch = null;
			int matchProductIndex = -1;
			int numMatches = 0;

			// Loop through all the products, looking for matches.
			// To ensure precision, we only accept a product if it's the only product that matches,
			// or if it has the higher points of two products that match.
			// If there are more than two products that match, or two products match but they have 
			// the same number of points, don't accept any product.
			for (int productIndex = 0; productIndex < products.size(); ++productIndex) {
				Score score = Scorer.score(products.get(productIndex), listing);
				if (score.match) {
					numMatches++;
					
					if (numMatches > 2) {
						bestMatch = null;
						break;
					}
					
					if (numMatches == 2 && bestMatch.points == score.points) {
						bestMatch = null;
						break;
					}
					
					if (numMatches == 1 || score.points > bestMatch.points) {
						bestMatch = score;
						matchProductIndex = productIndex;
					}
				}
			}
			
			if (bestMatch != null) {
				if (productsToListings[matchProductIndex] == null) {
					productsToListings[matchProductIndex] = listing.line;
				} else {
					productsToListings[matchProductIndex] += ',' + listing.line;
				}
			}
		}
	}
	
	private static void writeResults(BufferedWriter writer, ArrayList<Product> products, String[] productsToListings)
		throws IOException
	{
		for (int i = 0; i < products.size(); ++i) {
			String listings = productsToListings[i] == null ? "" : productsToListings[i];
			writer.write("{\"product_name\":\"" + products.get(i).name + "\",\"listings\":[" + listings + "]}\n");
		}
	}
	
	private static class ProductParser extends LineParser<Product> {
		PseudoJsonParser _jParser = new PseudoJsonParser();
		
		@Override
		public Product parseLine(String line) {
			HashMap<String, String> values = _jParser.parseLine(line);
			if (values == null) {
				return null;
			}
			return Product.createFromMap(line, values);
		}
	}
	
	private static class ListingParser extends LineParser<Listing> {
		PseudoJsonParser _jParser = new PseudoJsonParser();
		
		@Override
		public Listing parseLine(String line) {
			HashMap<String, String> values = _jParser.parseLine(line);
			if (values == null) {
				return null;
			}
			return Listing.createFromMap(line, values);
		}
	}

}
