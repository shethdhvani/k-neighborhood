import java.io.IOException;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

/**
 * @author Dhvani
 * For each word in the line of the file, get its neighbors and output it
 * original code taken from my assignment 2
 */
public class GetNeighborhoodMapper extends Mapper<Object,
    Text, Text, Text> {
     /* @author Tom White -- start -- */
    // get the filename
    private String filenameKey;
    private int wordCount = 0;
    private static final String SEPARATOR = "!";
    
    @Override
    protected void setup(Context context) throws IOException,
        InterruptedException {
        InputSplit split = context.getInputSplit();
        Path path = ((FileSplit) split).getPath();
        filenameKey = path.toString();
        k = context.getConfiguration().getInt("neighborhood", 2);
    }
    /* @author Tom White -- end -- */
    
    final static String REGEX_SPACES = "[\\s+]";
    final static String REGEX_NO_ALPHA_NO_SPACE = "[^a-zA-Z\\s]";
    String line;
    String array[];
    Text outKey;
    Text outValue;
    int k;
             
    @Override
    public void map(Object key, Text value, Context context) 
        throws IOException, InterruptedException {     
        // change to lower case and replace everything in the input except
        // letters a-z and space
        line = value.toString().replaceAll(REGEX_NO_ALPHA_NO_SPACE, " ").toLowerCase();
        array = line.split("\\s+");
        // for each word, output the value as this word and key as all the
        // locations of the word for which this word being processed is 
        // a neighbor. The key here is the filename and the position of the
        // word for eg. filename_1 and the value is the word being processed
        // itself. Also, to identify for which word the neighbors are in the
        // reducer, we are appending a separator to the word
        for (String s : array) {
            outKey = new Text(filenameKey + SEPARATOR + wordCount);
            outValue = new Text(SEPARATOR + s);
            context.write(outKey, outValue);                
            int startPos = wordCount - k;
            if (startPos < 0) {
                startPos = 0;
            }
            int endPos = wordCount + k;
            while (startPos <= endPos) {
                if (wordCount != startPos) {
                    outKey = new Text(filenameKey + SEPARATOR + startPos);
                    outValue = new Text(s);
                    context.write(outKey, outValue);
                }
                startPos++;
            }
            wordCount++;
        }
    }
}
