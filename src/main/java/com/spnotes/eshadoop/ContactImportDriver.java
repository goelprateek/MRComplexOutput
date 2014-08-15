package com.spnotes.eshadoop;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.codehaus.jackson.map.ObjectMapper;
import org.elasticsearch.hadoop.mr.EsOutputFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ContactImportDriver extends Configured implements Tool{
	
    Logger logger = LoggerFactory.getLogger(ContactImportDriver.class);

	public static class ContactImportMapper extends Mapper<LongWritable, Text, NullWritable, Text>{
	    Logger logger = LoggerFactory.getLogger(ContactImportMapper.class);
	    private ObjectMapper jsonMapper;
	    private SimpleDateFormat dateFormatter =
	    		 new SimpleDateFormat("dd-MMM-yyyy");
	    private Text contactText;

		@Override
		protected void map(LongWritable key, Text value,
				Mapper<LongWritable, Text, NullWritable, Text>.Context context)
				throws IOException, InterruptedException {
			logger.debug("Entering ContactImportMapper.map()");
			try {
				String contactContent = value.toString();
				System.out.println("Value of contactContent " + contactContent);
				String[] contactDataList= contactContent.split(",");
				
				System.out.println("ContactDataList "  + Arrays.toString(contactDataList) +" length -> " + contactDataList.length);
				
				SimpleDateFormat df = new SimpleDateFormat("");
				
				if(contactDataList.length == 6){
					String firstName = contactDataList[0];
					String lastName = contactDataList[1];
					Date dateOfBirth = dateFormatter.parse(contactDataList[2]);
					
					String addressLine1 = contactDataList[3];
					String city = contactDataList[4];
					String country = contactDataList[5];
					Address address = new Address(addressLine1, city, country);
					List<Address> addressList = new ArrayList<Address>();
					addressList.add(address);;
					Contact contact = new Contact(firstName, lastName,
							dateOfBirth, addressList);

					String contactJSON = jsonMapper.writeValueAsString(contact);
					contactText.set(contactJSON);

					context.write(NullWritable.get(), contactText);
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			logger.debug("Exiting ContactImportMapper.map()");
		}

		@Override
		protected void setup(
				Mapper<LongWritable, Text, NullWritable, Text>.Context context)
				throws IOException, InterruptedException {
			jsonMapper = new ObjectMapper();
			contactText = new Text();
		}
		
	}
	
	public int run(String[] args) throws Exception {
		if (args.length != 2) {
            System.err.printf("Usage: %s [generic options] <input> <output>\n",
                    getClass().getSimpleName());
            ToolRunner.printGenericCommandUsage(System.err);
            return -1;
        }

        Job job = new Job();
        job.setJarByClass(ContactImportDriver.class);
        job.setJobName("ContactImporter");
        
        job.getConfiguration().set("es.input.json", "yes");
        logger.info("Input path " + args[0]);
        logger.info("Oupput path " + args[1]);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        Configuration configuration = job.getConfiguration();
        configuration.set("es.nodes","localhost:9200");
        configuration.set("es.resource",args[1]);
        job.setOutputFormatClass(EsOutputFormat.class);
        job.setNumReduceTasks(0);
        
        job.setMapOutputKeyClass(NullWritable.class);
        job.setMapOutputValueClass(Text.class);
        job.setOutputKeyClass(NullWritable.class);
        job.setOutputValueClass(Text.class);
        
        job.setMapperClass(ContactImportMapper.class);

        int returnValue = job.waitForCompletion(true) ? 0:1;
        System.out.println("job.isSuccessful " + job.isSuccessful());
        return returnValue;
	}

	public static void main(final  String[] args) throws Exception{
		int exitCode = ToolRunner.run(new ContactImportDriver(), args);
        System.exit(exitCode);
	}

}
