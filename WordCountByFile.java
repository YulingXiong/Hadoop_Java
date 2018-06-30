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

public class WordCountByFile extends Configured implements Tool {

    public static void main(String args[]) throws Exception {
	int res = ToolRunner.run(new WordCountByFile(), args);
	System.exit(res);
    }

    public int run(String[] args) throws Exception {
	Path inputPath = new Path(args[0]);
	Path outputPath = new Path(args[1]);

	Configuration conf = getConf();
	Job job = new Job(conf, this.getClass().toString());

	FileInputFormat.setInputPaths(job, inputPath);
	FileOutputFormat.setOutputPath(job, outputPath);

	job.setJobName("WordCountByFile");
	job.setJarByClass(WordCountByFile.class);
	job.setInputFormatClass(TextInputFormat.class);
	job.setOutputFormatClass(TextOutputFormat.class);
	job.setMapOutputKeyClass(Text.class);
	job.setMapOutputValueClass(Text.class);
	job.setOutputKeyClass(Text.class);
	job.setOutputValueClass(Text.class);

	job.setMapperClass(Map.class);
	//job.setCombinerClass(Reduce.class);
	job.setReducerClass(Reduce.class);

	return job.waitForCompletion(true) ? 0 : 1;
    }

    public static class Map extends Mapper<LongWritable, Text, Text, Text> {
	//private final static IntWritable one = new IntWritable(1);
	private Text word = new Text();


    @Override
	public void map(LongWritable key, Text value,
			Mapper.Context context) throws IOException, InterruptedException {
	String line = value.toString();
	StringTokenizer tokenizer = new StringTokenizer(line);
	String filePathString = ((FileSplit) context.getInputSplit()).getPath().toString();


	while (tokenizer.hasMoreTokens()) {
	    word.set(tokenizer.nextToken());
	    context.write(word, new Text (filePathString));
	}
    }
    }

    public static class Reduce extends Reducer<Text, Text, Text, Text> {

    @Override
	public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
	//int sum = 0;
	//fileName, 1; for each key
	HashMap<String, Integer> fileMap = new HashMap<String, Integer>();
	String storeOutput = "";

	for (Text value : values) {
		if(fileMap.containsKey(value.toString())){
			Integer count = fileMap.get(value.toString());
			count++;
			fileMap.put(value.toString(), count);
		} else {
			fileMap.put(value.toString(), 1);
		}
	}

	for (String fileName : fileMap.keySet()){
		storeOutput += fileName +" "+Integer.toString(fileMap.get(fileName)) + " ";
	}

	context.write(key, new Text (storeOutput));


    }
    }

}


//Java string minipulation, rm wordcountbyfile.jar from input folder
// if anything above the first space is equal, then concate the string later