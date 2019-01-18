package com.boot.batch.notification.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.MongoItemReader;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;

import com.boot.batch.notification.data.CustomerProfile;
import com.boot.batch.notification.processor.EmailProcesser;

@Configuration
@EnableBatchProcessing
public class ApplicationConfig {
	
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Autowired
	private EmailProcesser emailProcessor;
	
	//Reader-3rd call
	@Bean
	public MongoItemReader<CustomerProfile> reader(){
		MongoItemReader<CustomerProfile> reader = new MongoItemReader<CustomerProfile>();
		reader.setTemplate(mongoTemplate);
		reader.setQuery("{}");
		reader.setTargetType(CustomerProfile.class);
		reader.setSort(new HashMap<String, Sort.Direction>(){{
			put("_custId", Direction.DESC);
		}
		});
		return reader;
		
	}
	//Writer - 5th call
	@Bean
	public StaxEventItemWriter<CustomerProfile> writer(){
		StaxEventItemWriter<CustomerProfile> writer = new StaxEventItemWriter<CustomerProfile>();
		writer.setRootTagName("CustomerProfiles");
		writer.setResource(new FileSystemResource("xml/customerprofile.xml"));
		writer.setMarshaller(marshaller());
		return writer;
	}
	
	private XStreamMarshaller marshaller() {
		XStreamMarshaller marshaller = new XStreamMarshaller();
		Map<String, Class> map = new HashMap<String, Class>();
		map.put("CustomerProfile", CustomerProfile.class);
		marshaller.setAliases(map);
		return marshaller;
	}
	//2nd call
	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step1").<CustomerProfile, CustomerProfile>chunk(5).reader(reader()).processor(process()).writer(writer()).build();
	}
	//1st call
	@Bean
	public Job runJob() {
		return jobBuilderFactory.get("report generation").flow(step1()).end().build();
	}
	
	//4th call
	@Bean	
	public ItemProcessor<CustomerProfile, CustomerProfile> process(){
		ItemProcessor< CustomerProfile, CustomerProfile> itemProcessor = new ItemProcessor<CustomerProfile, CustomerProfile>() {

			@Override
			public CustomerProfile process(CustomerProfile customerProfile) throws Exception {
				if(customerProfile.getStatus().equalsIgnoreCase("inactive")){
					String message=emailProcessor.sendMail(buildEmailBody(customerProfile), customerProfile.getEmail());
					System.out.println("Message from mailer:"+message);
				}
				return customerProfile;
			}

			private String buildEmailBody(CustomerProfile profile) {
				String mailBody = "Dear "+profile.getFname()+" , "+ "\n"+"Please activate your profile in the system to get furhter benefits"+"\n"+"Thanks, "+"\n"+"Admin Team";
				return mailBody;
			}
			
		};
		return itemProcessor;
		
	}

}
