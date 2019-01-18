package com.boot.batch.notification.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
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

@Configuration
@EnableBatchProcessing
public class ApplicationConfig {
	
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
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
	@Bean
	public StaxEventItemWriter<CustomerProfile> writer(){
		StaxEventItemWriter<CustomerProfile> writer = new StaxEventItemWriter<CustomerProfile>();
		writer.setRootTagName("CustomerProfiles");
		writer.setResource(new FileSystemResource("xml/profile.xml"));
		writer.setMarshaller(marshaller());
		return writer;
	}
	
	private XStreamMarshaller marshaller() {
		XStreamMarshaller marshaller = new XStreamMarshaller();
		Map<String, Class> map = new HashMap<String, Class>();
		map.put("cprofile", CustomerProfile.class);
		marshaller.setAliases(map);
		return marshaller;
	}
	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step1").<CustomerProfile, CustomerProfile>chunk(5).reader(reader()).writer(writer()).build();
	}
	@Bean
	public Job runJob() {
		return jobBuilderFactory.get("report generation").flow(step1()).end().build();
	}

}
