package org.example.service;


import javax.ws.rs.core.MediaType;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import javax.mail.MessagingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import org.apache.commons.codec.binary.Base64;

@Path("/publish")
public class PublisherService {

	private static final String TARGET_URL = "https://wso2.org/jira/rest/api/2/issue";
	private HttpURLConnection connection;

	@POST
	@Path("/jira")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public String publishJira(@FormParam("urlParams") String urlParams) {

		try {
			String username = "devstudiouser@gmail.com";
			String password = "devUser123";
			String userCredentials = username + ":" + password;

			URL url = new URL(TARGET_URL);
			connection = (HttpURLConnection) url.openConnection();
			String basicAuth = "Basic "
					+ new String(
							new Base64().encode(userCredentials.getBytes()));
			connection.setRequestProperty("Authorization", basicAuth);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Content-Length",
					Integer.toString(urlParams.getBytes().length));
			connection.setRequestProperty("Content-Language", "en-US");

			connection.setUseCaches(false);
			connection.setDoOutput(true);
			System.out.println("InsidePublish");
			DataOutputStream wr = new DataOutputStream(
					connection.getOutputStream());
			wr.writeBytes(urlParams);
			wr.close();

			// Get Response
			InputStream is;

			if (connection.getResponseCode() >= 400) {
				is = connection.getErrorStream();
			} else {
				is = connection.getInputStream();
			}

			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			StringBuilder response = new StringBuilder(); 

			String line;

			while ((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}
			rd.close();
			System.out.println(response);
			return response.toString();
			
		} catch (Exception e) {
			e.printStackTrace();
			//return null;

		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
			return "Success";
	}

	
	@POST
	@Path("/email")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public String publishEmail(@FormParam("recmail") String recEmail,@FormParam("body") String body) {

		String username="devstudiouser";
		String password="devUser123";
		String title="Test Email";
		EmailPublisher emailPub=new EmailPublisher(username, password, recEmail, title, body);
		try {
			return emailPub.publish();
		} catch (IOException | MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "failed";
		}

	
		}
	
	@GET
	@Path("/env")
	@Produces(MediaType.TEXT_PLAIN)

	public String getEnv() {
		
	 String last="";
	 Map<String, String> env = System.getenv();
     for (String envName : env.keySet()) {
         System.out.format("%s=%s%n",
                           envName,
                           env.get(envName));
         last=envName+" "+env.get(envName);
     }
         
         return last;
	}
    
     
}