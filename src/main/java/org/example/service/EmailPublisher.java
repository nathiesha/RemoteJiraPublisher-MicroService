/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.example.service;

import com.sun.mail.smtp.SMTPTransport;
//import com.sun.net.ssl.internal.ssl.Provider;

import java.io.IOException;
//import java.security.Security;
import java.util.Date;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class EmailPublisher{



	private Properties props;
	private Session session;
	private String username;
	private String password;
	private String recEmail;
	private String title;
	private String message;
	
	public EmailPublisher(String username, String password, String recEmail, String title, String message) {
		super();
		this.username = username;
		this.password = password;
		this.recEmail = recEmail;
		this.title = title;
		this.message = message;
	}

	private static final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";

    void init () throws IOException{
        //init : read preferences for SMTP connection params
    	//Security.addProvider(new Provider());

		// Get a Properties object
		props = System.getProperties();
		props.setProperty("mail.smtps.host", "smtp.gmail.com");
		props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
		props.setProperty("mail.smtp.socketFactory.fallback", "false");
		props.setProperty("mail.smtp.port", "465");
		props.setProperty("mail.smtp.socketFactory.port", "465");
		props.setProperty("mail.smtps.auth", "true");

		props.put("mail.smtps.quitwait", "false");

		session = Session.getInstance(props, null);
      }
    
     // implement publish method 
     public String publish() throws IOException, MessagingException{
         init();
        //send mail
 		// -- Create a new message --
 		final MimeMessage msg = new MimeMessage(session);

 		// -- Set the FROM and TO fields --
 		msg.setFrom(new InternetAddress());
 		//
 		msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recEmail, false));



 		msg.setSubject(title);
 		msg.setText(message, "utf-8");
 		msg.setSentDate(new Date());

 		SMTPTransport t = (SMTPTransport) session.getTransport("smtps");

 		t.connect("smtp.gmail.com", username, password);
 		t.sendMessage(msg, msg.getAllRecipients());
 		t.close();
 		
 		return "ok";
     }



}
