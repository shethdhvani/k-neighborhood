
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Dhvani
 * 
 */
public class CalcCharWordScore {
	/**
	 * 
	 * @param charCountMap          map containing each character and it's count
	 * @param totalCharactersCorpus count of total number of characters in the corpus
	 * @return                      map containing each character and it's score
	 */
	static Map<Character, Integer> calculateCharScore(Map<Character, Integer> 
	    charCountMap, float totalCharactersCorpus) {
		// calculate the score of each letter
		Map<Character, Integer> charScoreMap = new HashMap<>();
		for (Map.Entry<Character, Integer> entry : charCountMap.entrySet()) {
		    double charPerc = entry.getValue()/totalCharactersCorpus;
		    int charScore = 0;
			if (charPerc > 0.1)
				charScore = 0;
			else if (charPerc >= 0.08)
				charScore = 1;
			else if (charPerc >= 0.06)
				charScore = 2;
			else if (charPerc >= 0.04)
				charScore = 4;
			else if (charPerc >= 0.02)
				charScore = 8;
			else if (charPerc >= 0.01)
				charScore = 16;
			else
				charScore = 32;
			charScoreMap.put(entry.getKey(), new Integer(charScore));
		}
		return charScoreMap;
	}
}
