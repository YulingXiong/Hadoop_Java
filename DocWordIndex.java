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

public class DocWordIndex extends Configured implements Tool {

    public static void main(String args[]) throws Exception {
	int res = ToolRunner.run(new DocWordIndex(), args);
	System.exit(res);
    }

    public int run(String[] args) throws Exception {
	Path inputPath = new Path(args[0]);
	Path outputPath = new Path(args[1]);

	Configuration conf = getConf();
	Job job = new Job(conf, this.getClass().toString());

	FileInputFormat.setInputPaths(job, inputPath);
	FileOutputFormat.setOutputPath(job, outputPath);

	job.setJobName("DocWordIndex");
	job.setJarByClass(DocWordIndex.class);
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
  	private Text word = new Text();

    //private final static IntWritable one = new In;
    //private Text word = new Text();

    @Override
	public void map(LongWritable key, Text value,
			Mapper.Context context) throws IOException, InterruptedException {

		//String filePathString = ((FileSplit) context.getInputSplit()).getPath().toString();

      	//String line = value.toString();
      	//String[] tokens = line.split(" "); //split into words array of string
 
	

	String line = value.toString();
	StringTokenizer tokenizer = new StringTokenizer(line);
	

	int count=0;
	String previousFilePathString = "";

	


		//String previousFilePathString = filePathString;
	while (tokenizer.hasMoreTokens()) {
	String filePathString = ((FileSplit) context.getInputSplit()).getPath().toString();

	if(filePathString.equals(previousFilePathString)){
		count = count + 1;
	} else{
		count = 1;
	}

	    word.set(tokenizer.nextToken());
	    context.write(word, new Text (filePathString+" "+Integer.toString(count)));

	    previousFilePathString = filePathString;
	}
    }
    }
      
    
  

     public static class Reduce extends Reducer<Text, Text, Text, Text> {
    //private IntWritable result = new IntWritable();
   	@Override
	public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
	//int sum = 0;

	HashMap<String, ArrayList<String>> fileMap = new HashMap<String, ArrayList<String>>();
	//ArrayList<String> arrayList = new ArrayList<String>();

	String storeOutput = "";

	for (Text value : values) {
		String[] line = value.toString().split(" ");

		if(fileMap.containsKey(line[0].toString())){

			ArrayList index = fileMap.get(line[0].toString());
			index.add(line[1]);

			fileMap.put(line[0].toString(), index);

		} else {
			ArrayList<String> arrayList = new ArrayList<String>();
			arrayList.add(line[1]);
			fileMap.put(line[0].toString(), arrayList);
		}
	}

	for (String fileName : fileMap.keySet()){
		storeOutput += fileName +" "+fileMap.get(fileName).toString().replace("[", "").replace("]", "") + " ";
	}

	context.write(key, new Text (storeOutput));


    }
    }

}