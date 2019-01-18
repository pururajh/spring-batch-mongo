package com.boot.batch.notification.data;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Document ( collection = "customerprofile")
public class CustomerProfile {
	@Field("f_name")
	private String fname;
	@Field("l_name")
	private String lname;

}
