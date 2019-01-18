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
	@Field("status")
	private String status;
	@Field("email")
	private String email;
	public String getStatus() {
		return this.status;
	}
	public String getFname() {
		return this.fname;
	}
	public String getLname() {
		return this.lname;
	}
	public String getEmail() {
		return this.email;
	}

}
