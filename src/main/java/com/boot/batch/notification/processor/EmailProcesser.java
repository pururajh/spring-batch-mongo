package com.boot.batch.notification.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailProcesser {
	
	@Autowired
	private JavaMailSender sender;
	
	
	public String sendMail(String emailBody, String to) {
		String output;
		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setTo(to);
			message.setText(emailBody);
			message.setSubject("Customer details alert");
			message.setFrom("puruimit@yahoo.co.in");
			sender.send(message);
			output = "message send successfully";
		} catch (MailException e) {
			output="There is error in sending the message";
			e.printStackTrace();
			return output;
		}
		return output;
	}

}
