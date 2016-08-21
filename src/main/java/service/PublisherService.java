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

package service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import javax.mail.MessagingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import constants.EnvironmentVariablesConstants;
import publishers.EmailPublisher;
import publishers.JiraPublisher;

@Path("/publish")
public class PublisherService {

	private HttpURLConnection connection;

	@POST
	@Path("/jira")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public String publishJira(@FormParam("urlParams") String urlParams) {

		String response = "";
		try {
			Map<String, String> env = System.getenv();

			String jiraUrl = env.get(EnvironmentVariablesConstants.JIRA_URL);
			String username = env.get(EnvironmentVariablesConstants.JIRA_USERNAME);
			String password = env.get(EnvironmentVariablesConstants.JIRA_PASSWORD);
			String userCredentials = username + ":" + password;

			JiraPublisher jp = new JiraPublisher(urlParams, userCredentials, jiraUrl);
			response = jp.publish();

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
		return response;
	}

	@POST
	@Path("/email")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public String publishEmail(@FormParam("recmail") String recEmail, @FormParam("body") String body) {

		Map<String, String> env = System.getenv();
		String username = env.get(EnvironmentVariablesConstants.GMAIL_USERNAME);
		String password = env.get(EnvironmentVariablesConstants.GMAIL_PASSWORD);

		String title = "Developer Studio - Error Report";
		EmailPublisher emailPub = new EmailPublisher(username, password, recEmail, title, body);
		try {
			return emailPub.publish();
		} catch (IOException | MessagingException e) {
			e.printStackTrace();
			return "failed";
		}

	}

	@POST
	@Path("/status")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public String inquireStatus(@FormParam("id") String id) {

		String output = "";
		String response = "";

		URL url;
		HttpURLConnection connection;
		BufferedReader br;

		try {
			Map<String, String> env = System.getenv();
			String jiraUrl = env.get(EnvironmentVariablesConstants.JIRA_URL);
			String path = env.get(EnvironmentVariablesConstants.STATUS_INQUIRY_PATH);
			String targetUrl = jiraUrl + "/" + id + path;
			
			url = new URL(targetUrl);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Accept", "application/json");
			if (connection.getResponseCode() != 200) {

				response = "Error";
			}

			br = new BufferedReader(new InputStreamReader((connection.getInputStream())));
			while ((output = br.readLine()) != null) {

				response += output;
			}
			connection.disconnect();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return response;
	}

}