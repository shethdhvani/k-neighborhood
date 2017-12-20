import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * 
 * @author Dhvani
 * Calculate the median neighborhood score for each word
 */
public class MedianReducer extends Reducer<Text,
    DoubleWritable, Text, DoubleWritable> {
    @Override
    public void reduce(Text key, Iterable<DoubleWritable> values, 
        Context context) 
        throws IOException, InterruptedException {
        DoubleWritable result = new DoubleWritable();
        List<Double> scores = new ArrayList<>();
        // get the scores and add it to a list
        for (DoubleWritable v : values) {
            scores.add(v.get());
        }
        // sort the list
        Collections.sort(scores);
        // get the median
        double median = 0;
        double pos1 = Math.floor((scores.size() - 1.0) / 2.0);
        double pos2 = Math.ceil((scores.size() - 1.0) / 2.0);
        if (pos1 == pos2 ) {
            median = scores.get((int)pos1);
        } else {
            median = (scores.get((int)pos1) + scores.get((int)pos2)) / 2.0 ;
        }
        result.set(median);
        context.write(key, result);
    }
}
