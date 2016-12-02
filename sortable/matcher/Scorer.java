package sortable.matcher;

public class Scorer {
	public static Score score(Product product, Listing listing) {
		// The basic algorithm is this:
		// We're checking the listing "squash" to see if it contains
		// various fields from the product.
		// The "squash" has all the relevant fields from the product, appended
		// together, converted to lower case, and whitespace removed.
		// So the squash is sort of a bag of words for the listing
		// As a bare minimum to be considered a match, the family (if present in the product),
		// manufacturer, and model must be found in the squash.
		// If all are found, then we compute the number of points in the match, which is used
		// to compare with matched scores between this listing and other products.
		// The points magnitude is dominated by the fields used for matching, but also
		// considers how the product name matches the listing squash. The points awarded
		// to strings matching is based on the length of the strings, as longer strings are
		// considered more distinguishing.
		
		int points = 0;
		boolean match = true;
		
		if (product.family == null) {
			points += 50;
		} else {
			if (listing.squash.contains(product.family)) {
				points += 100 + product.family.length();
			} else {
				match = false;
			}
		}
		if (listing.squash.contains(product.manufacturer)) {
			points += 100 + product.manufacturer.length();
		} else {
			match = false;
		}
		if (listing.squash.contains(product.model)) {
			points += 100 + product.model.length();
		} else {
			match = false;
		}
		
		// For each "piece" of the product name, score if it's present in the listing squash 
		for (String namePiece : product.namePieces) {
			if (listing.squash.contains(namePiece)) {
				points += namePiece.length();
			}
		}
		
		
		// See how many characters of the product name squash appears in the
		// listing squash, in the order they appear in the product name squash.
		int productIndex = 0;
		int listingIndex = 0;
		while (productIndex < product.nameSquash.length() && listingIndex < listing.squash.length()) {
			if (product.nameSquash.charAt(productIndex) == listing.squash.charAt(listingIndex)) {
				productIndex++;
			}
			listingIndex++;
		}
		points += productIndex;
		
		Score score = new Score(match, points);
		
		return score;
	}
	
	public static class Score {
		public final boolean match;
		public final int points;
		
		public Score(boolean match_, int score_) {
			match = match_;
			points = score_;
		}
	}
}
