import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * 
 * @author Dhvani
 * Read the input and for each character in the input, output the number of 
 * occurrences
 *
 */
public class CharCountMapper extends Mapper<Object, 
    Text, Text, IntWritable> {
    public void map(Object key, Text value, Context context) 
        throws IOException, InterruptedException {
        String line = value.toString().toLowerCase();
        // for each character i.e. a to z, count the number of occurrences
        // and output it
        for(int i = 97; i < 123; i++) {
            String s = Character.toString ((char) i); 
            int count = org.apache.commons.lang.StringUtils.countMatches(line, s);
            context.write(new Text(s), new IntWritable(count));
        }
    }
}
