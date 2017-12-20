import java.io.IOException;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
/**
 * 
 * @author Dhvani
 * 
 */
public class MedianMapper extends Mapper<Object,
    Text, Text, DoubleWritable> {
    @Override
    public void map(Object key, Text value, Context context) 
        throws IOException, InterruptedException {
        String line[] = value.toString().split("\\t");
        context.write(new Text(line[0]), 
            new DoubleWritable(Double.parseDouble(line[1])));
    }
}
