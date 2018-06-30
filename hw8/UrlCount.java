package cscie55.hw8;
//import Link.*;
import java.io.IOException;
import java.util.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;

import org.apache.hadoop.conf.*;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.*;
import org.apache.hadoop.mapreduce.lib.output.*;
import org.apache.hadoop.util.*;


public class UrlCount extends Configured implements Tool {
	//Long startmilliseconds, endmilliseconds;

    public static void main(String args[]) throws Exception {
	int res = ToolRunner.run(new Configuration(),new UrlCount(), args);
	System.exit(res);
    }

    public int run(String[] args) throws Exception {
	Path inputPath = new Path(args[0]);
	Path outputPath = new Path(args[1]);
	
	//String startDate = "Novermber 8, 2009";   08-09-2009
    //String endDate = "Novermber 9, 2009";  09-09-2009

	//Configuration conf = this.getConf();


	//conf.set(startmilliseconds.toString(), endmilliseconds.toString());
	//Job job = new Job(conf, this.getClass().toString());
	//Job job = new Job(conf,"UrlCount");


	Configuration conf = new Configuration();
	conf.set("startDate", args[2]);
	conf.set("endDate", args[3]);
	Job job = new Job(conf);

	FileInputFormat.setInputPaths(job, inputPath);
	FileOutputFormat.setOutputPath(job, outputPath);


	job.setJobName("UrlCount");
	job.setJarByClass(UrlCount.class);
	job.setInputFormatClass(TextInputFormat.class);
	job.setOutputFormatClass(TextOutputFormat.class);
	job.setMapOutputKeyClass(Text.class);
	job.setMapOutputValueClass(IntWritable.class);
	job.setOutputKeyClass(Text.class);
	job.setOutputValueClass(IntWritable.class);

	job.setMapperClass(Map.class);
	//job.setCombinerClass(Reduce.class);
	job.setReducerClass(Reduce.class);

	return job.waitForCompletion(true) ? 0 : 1;
    }





    public static class Map extends Mapper<LongWritable, Text, Text, IntWritable> {
  	private final static IntWritable one = new IntWritable(1);
  	private final static Text word = new Text();




    @Override
	public void map(LongWritable key, Text value,
			Mapper.Context context) throws IOException, InterruptedException {
 
	Configuration config = context.getConfiguration();
	String startDateString = config.get("startDate");
  	String endDateString = config.get("endDate");
	String line = value.toString();
	Link myLink = Link.parse(line);


	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
	simpleDateFormat.setTimeZone(TimeZone.getTimeZone("EST"));
	//Configuration conf = this.getConf();
	//System.out.println(conf.get("startDate"));
	try{
	Date startDate = simpleDateFormat.parse(startDateString);
	Date endDate = simpleDateFormat.parse(endDateString);
	Long startmilliseconds = startDate.getTime()/1000;
	Long endmilliseconds = endDate.getTime()/1000;


	if(myLink.timestamp()>=startmilliseconds && myLink.timestamp()<endmilliseconds){
		word.set(myLink.url());
		context.write(word,one);
		}

	} catch(ParseException e){
		e.printStackTrace();
	}

	}	

    }

    
    
     public static class Reduce extends Reducer<Text, IntWritable, Text, IntWritable> {
    //private IntWritable result = new IntWritable();
   	@Override
	public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {

	int sum = 0;
	for(IntWritable value:values){
		sum += value.get();
	}


	context.write(key, new IntWritable (sum));


    }
    }

}