import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.log4j.Logger;

/**
 * 
 * @author Dhvani
 *
 */
public class Driver {
    
    private static Logger log = Logger.getLogger(Driver.class);
    
    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        if (args.length < 5) {
            System.err.println("Usage: neighborhood score <in> "
                + "[<in>...] <out>");
            System.exit(2);
        }
        
        // create and run the first mapreduce job for calculating 
        // character count
        Job job = Job.getInstance(conf, "character count");
        job.setJarByClass(Driver.class);
        job.setMapperClass(CharCountMapper.class);
        job.setCombinerClass(CharCountReducer.class);
        job.setReducerClass(CharCountReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        job.setNumReduceTasks(1);
        final int noOfNeighborhood = Integer.parseInt(args[0]);
        FileInputFormat.addInputPath(job, new Path(args[1]));
        FileOutputFormat.setOutputPath(job, new Path(args[2]));
        job.waitForCompletion(true);
        
        conf.setInt("neighborhood", noOfNeighborhood);
        setCharScore(new Path(args[2] + "/part-r-00000"), conf);
        
        // create and run the second mapreduce job for getting neighborhoods and
        // calculating neighborhood score
        Job job2 = Job.getInstance(conf, "neighborhood score");
        job2.setJarByClass(Driver.class);
        job2.setMapperClass(GetNeighborhoodMapper.class);
        job2.setReducerClass(GetNeighborhoodReducer.class);
        job2.setMapOutputKeyClass(Text.class);
        job2.setMapOutputValueClass(Text.class);
        job2.setInputFormatClass(NonSplittableTextInputFormat.class);
        job2.setOutputKeyClass(Text.class);
        job2.setOutputValueClass(DoubleWritable.class);
        FileInputFormat.addInputPath(job2, new Path(args[1]));
        FileOutputFormat.setOutputPath(job2, new Path(args[3]));
        job2.waitForCompletion(true);
        
        // create and run the third mapreduce job for calculating the median
        // neighborhood score
        Job job3 = Job.getInstance(conf, "median neighborhood score");
        job3.setJarByClass(Driver.class);
        job3.setMapperClass(MedianMapper.class);
        job3.setCombinerClass(MedianReducer.class);
        job3.setReducerClass(MedianReducer.class);
        job3.setMapOutputKeyClass(Text.class);
        job3.setMapOutputValueClass(DoubleWritable.class);
        job3.setInputFormatClass(TextInputFormat.class);
        job3.setOutputKeyClass(Text.class);
        job3.setOutputValueClass(DoubleWritable.class);
        job3.getConfiguration().set("mapreduce.output.textoutputformat.separator", ",");
        FileInputFormat.addInputPath(job3, new Path(args[3]));
        FileOutputFormat.setOutputPath(job3, new Path(args[4]));
        job3.waitForCompletion(true);
    }
    
    // read the character count from the output of first mapreduce job.
    // calculate character score and set it to configuration
    private static void setCharScore(Path p, Configuration config) throws IOException {
        FileSystem fs = FileSystem.get(config);
        BufferedReader br = new BufferedReader(new InputStreamReader(fs.open(p)));
        String line;
        line = br.readLine();
        String charCount[] = new String[2];
        int totalCharacters = 0;
        Map<Character, Integer> charFreq = new HashMap<>();
        Map<Character, Integer> charScore = new HashMap<>();
        while (line != null) {
            charCount = line.split("\\t");
            charFreq.put(charCount[0].charAt(0), Integer.parseInt(charCount[1]));
            totalCharacters = totalCharacters + Integer.parseInt(charCount[1]);
            line = br.readLine();
        }
        charScore = CalcCharWordScore.calculateCharScore(charFreq, 
            totalCharacters);
        for (Map.Entry<Character, Integer> entry : charScore.entrySet()) {
            config.setInt(entry.getKey().toString(), entry.getValue());
        }
    }
}
