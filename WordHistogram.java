package cscie55.hw7;
import java.io.IOException;
import java.util.*;

import org.apache.hadoop.conf.*;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.*;
import org.apache.hadoop.mapreduce.lib.output.*;
import org.apache.hadoop.util.*;

public class WordHistogram extends Configured implements Tool {

    public static void main(String args[]) throws Exception {
	int res = ToolRunner.run(new WordHistogram(), args);
	System.exit(res);
    }

    public int run(String[] args) throws Exception {
	Path inputPath = new Path(args[0]);
	Path outputPath = new Path(args[1]);

	Configuration conf = getConf();
	Job job = new Job(conf, this.getClass().toString());

	FileInputFormat.setInputPaths(job, inputPath);
	FileOutputFormat.setOutputPath(job, outputPath);

	job.setJobName("WordHistogram");
	job.setJarByClass(WordHistogram.class);
	job.setInputFormatClass(TextInputFormat.class);
	job.setOutputFormatClass(TextOutputFormat.class);
	job.setMapOutputKeyClass(Text.class);
	job.setMapOutputValueClass(IntWritable.class);
	job.setOutputKeyClass(Text.class);
	job.setOutputValueClass(IntWritable.class);

	job.setMapperClass(Map.class);
	job.setCombinerClass(Reduce.class);
	job.setReducerClass(Reduce.class);

	return job.waitForCompletion(true) ? 0 : 1;
    }

    //Text similar to String
    //IntWritable similar to Integer
    //<inputKey-LineIndex, value, emitted key, value>
    public static class Map extends Mapper<LongWritable, Text, Text, IntWritable> {
	private final static IntWritable one = new IntWritable(1);
	private IntWritable wordLength = new IntWritable();

    @Override
	public void map(LongWritable key, Text value,
			Mapper.Context context) throws IOException, InterruptedException {
	String line = value.toString();
	StringTokenizer tokenizer = new StringTokenizer(line);
	while (tokenizer.hasMoreTokens()) {
	    wordLength.set(tokenizer.nextToken().length());
	    context.write(new Text(wordLength.toString()), one);
	}
    }
    }
    //<input key, value, emitted key, value>
    public static class Reduce extends Reducer<Text, IntWritable, Text, IntWritable> {

    @Override
	public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
	int sum = 0;
	for (IntWritable value : values) {
	    sum += value.get();
	}



	context.write(key, new IntWritable(sum));
    }
    }

}
