package cscie55.hw8;
//import Link.*;
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

public class TagUnion extends Configured implements Tool {

    public static void main(String args[]) throws Exception {
	int res = ToolRunner.run(new TagUnion(), args);
	System.exit(res);
    }

    public int run(String[] args) throws Exception {
	Path inputPath = new Path(args[0]);
	Path outputPath = new Path(args[1]);

	Configuration conf = getConf();
	Job job = new Job(conf, this.getClass().toString());

	FileInputFormat.setInputPaths(job, inputPath);
	FileOutputFormat.setOutputPath(job, outputPath);

	job.setJobName("TagUnion");
	job.setJarByClass(TagUnion.class);
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

    @Override
	public void map(LongWritable key, Text value,
			Mapper.Context context) throws IOException, InterruptedException {
 

	String line = value.toString();
	Link myLink = Link.parse(line);
	word.set(myLink.url());
	List<String> tags = myLink.tags();

	for(String tag:tags){

	context.write(word, new Text (tag));

	}
    }
}
    
      
    
  

     public static class Reduce extends Reducer<Text, Text, Text, Text> {
    //private IntWritable result = new IntWritable();
   	@Override
	public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {

	//HashMap<String, HashSet<String>> fileMap = new HashMap<String, HashSet<String>>();
	HashSet<String> tagsCol = new HashSet<String>();
	//ArrayList<String> arrayList = new ArrayList<String>();
	String storeOutput = "";

	for (Text value : values) {
		tagsCol.add(value.toString());	
	}

	//fileMap.put(key, tagsCol);

	for(String tag: tagsCol){
		storeOutput += tag + ",";
	}

	context.write(key, new Text (storeOutput));


    }
    }

}