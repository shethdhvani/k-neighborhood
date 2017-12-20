import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * @author Dhvani
 * Calculate the neighborhood score for a word from each file
 * original code taken from my assignment 2
 */
public class GetNeighborhoodReducer extends Reducer<Text,
    Text, Text, DoubleWritable> {
    private Map<Character, Integer> charScore = new HashMap<>();
    private static final String SEPARATOR = "!";
    private Text outKey;
    private DoubleWritable outValue;
    int k;
    Map<String, Integer> wordScore = new HashMap<>(); 
    @Override
    protected void setup(Context context) throws IOException, 
        InterruptedException {
        // get the character score from context
        for (int i = 97; i < 123; i++) {
            Character c =  (char) i;
            String s = Character.toString(c);
            charScore.put(c, context.getConfiguration().getInt(s, 2));   
        }        
        k = context.getConfiguration().getInt("neighborhood", 2);
    }
    
    @Override
    public void reduce(Text key, Iterable<Text> values, Context context) 
        throws IOException, InterruptedException {
        int totalScore = 0;
        for (Text t : values) {
            if (t.toString().contains(SEPARATOR)) {
                outKey = new Text(t.toString().replaceAll(SEPARATOR, ""));
            } else {
                totalScore = totalScore + storeWordScore(t.toString());
            }
        }
        outValue = new DoubleWritable(totalScore);
        context.write(outKey, outValue);
    }
    
    // calculate the word's score and then store it in a map if not already there. 
    private int storeWordScore(String word) {
        if (wordScore.containsKey(word)) {
            return wordScore.get(word);
        } else {
            int score = 0;
            for (Character c: word.toCharArray()) {
                score = score + charScore.get(c);
            }
            wordScore.put(word, score);
            return score;
        }
    }
}
