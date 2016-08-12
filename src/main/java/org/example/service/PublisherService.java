/*
 * Copyright (c) 2016, WSO2 Inc. (http://wso2.com) All Rights Reserved.
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

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Map;
import javax.mail.MessagingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/publish")
public class PublisherService {

	private HttpURLConnection connection;

	@POST
	@Path("/jira")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public String publishJira(@FormParam("urlParams") String urlParams) {

		String ret = "";
		try {
			Map<String, String> env = System.getenv();

			String Jurl = env.get("Jiraurl");
			String username = env.get("Jirausername");
			String password = env.get("Jirapassword");
			String userCredentials = username + ":" + password;

			JiraPublisher jp = new JiraPublisher(urlParams, userCredentials, Jurl);
			ret = jp.publish();

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
		return ret;
	}

	@POST
	@Path("/email")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public String publishEmail(@FormParam("recmail") String recEmail, @FormParam("body") String body) {

		Map<String, String> env = System.getenv();
		String username = env.get("Gmailusername");
		String password = env.get("Gmailpassword");

		String title = "Test Email";
		EmailPublisher emailPub = new EmailPublisher(username, password, recEmail, title, body);
		try {
			return emailPub.publish();
		} catch (IOException | MessagingException e) {
			e.printStackTrace();
			return "failed";
		}

	}

}