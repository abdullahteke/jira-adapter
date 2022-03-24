package com.abdullahteke.ysd.jiraadapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JiraManager {

	  private URL jiraURL;
	  private String userName;
	  private String userPassword;
	  private String projectKey;
	  HttpURLConnection connection;
	  private String authorizationCode;
	  
	  

	private static JiraManager instance;


	public static JiraManager getInstance(String jiraURL,String userName,String userPassword, String projectKey) {
		
		if (instance==null){
			instance=new JiraManager( jiraURL, userName, userPassword,  projectKey);
		}
		
		return instance;
	}
	
    public JiraManager(String jiraURL,String userName,String userPassword, String projectKey) {
    	
    	this.userName=userName;
    	this.userPassword=userPassword;
    	this.projectKey=projectKey;
    	this.connection=null;
    	this.authorizationCode=getAuthorizationCode();
    	
    	try {
    		
			this.jiraURL = new URL(jiraURL+"/rest/api/2/issue");
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    	
	}
    
    private String getAuthorizationCode() {
    	String retVal="Basic ";
    	 	
		try {
			String decodedPass= new String(Base64.getDecoder().decode(userPassword), "UTF-8");
	    	byte[] encodedPassword = ( userName + ":" + decodedPass).getBytes("UTF-8");
	    	  
	    	retVal=retVal+Base64.getEncoder().encodeToString(encodedPassword);
	    	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	  return retVal;
	}

	public String  createNewIssue(String id, String title, String description, String severity, String category, String subCategory, String component) {
    	String retVal="";
    	String msg=queryMessage(id,title,description,severity,category,subCategory,component);
    	
    	try {
			connection=(HttpURLConnection) jiraURL.openConnection();
	    	connection.setDoOutput(true);
	    	connection.setRequestMethod("POST");
	    	connection.setRequestProperty("Content-Type", "application/json");
	    	connection.addRequestProperty("Authorization", authorizationCode);
	    	
	    	OutputStream os= connection.getOutputStream();
	    	os.write(msg.getBytes());
	    	os.flush();

	    	if (connection.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
	    		throw new RuntimeException("Failed : HTTP error code : "
	    				+ connection.getResponseCode()+" : "+ connection.getResponseMessage());
	    	}
	    	
			BufferedReader br = new BufferedReader(new InputStreamReader((connection.getInputStream())));

		    InputStream is = connection.getInputStream();
		    StringBuffer buf = new StringBuffer();
		    int c;
		    while( ( c = is.read() ) != -1 ) {
		      buf.append( (char) c );
		    }
			
		    retVal=buf.toString();
		    
		    String regexPattern = ".*\"key\":\"(SKOP.*)\",.*";
		    Pattern pattern = Pattern.compile(regexPattern);
		    Matcher matcher=pattern.matcher(buf.toString());
		       
		    if (matcher.matches()) {
		    	retVal=matcher.group(1);
		    }
		       
			connection.disconnect();

	    	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	System.out.println(retVal);
    	return retVal;
    	
    	
    }
    
    
    public String queryMessage(String id, String title, String description, String severity, String category, String subCategory, String component){
    	String retVal="{";
    	retVal=retVal+"\n\t\"fields\":{";
    	retVal=retVal+"\n\t\t\"project\":";
    	retVal=retVal+"\n\t\t{";
    	retVal=retVal+"\n\t\t\t\"key\":\""+projectKey+"\"";
    	retVal=retVal+"\n\t\t},";
    	retVal=retVal+"\n\t\t\"summary\":\""+title+"\",";
    	retVal=retVal+"\n\t\t\"description\":\""+description+"\",";
    	retVal=retVal+"\n\t\t\"issuetype\":";
    	retVal=retVal+"\n\t\t{";
    	retVal=retVal+"\n\t\t\t\"name\":\"Task\"";
    	retVal=retVal+"\n\t\t},";
    	retVal=retVal+"\n\t\t\"components\":";
    	retVal=retVal+"\n\t\t[{";
    	retVal=retVal+"\n\t\t\t\"name\":\""+component+"\"";
    	retVal=retVal+"\n\t\t}],";
    	retVal=retVal+"\n\t\t\"priority\":";
    	retVal=retVal+"\n\t\t{";
    	retVal=retVal+"\n\t\t\t\"name\":\"Critical\"";
    	retVal=retVal+"\n\t\t}";
    	retVal=retVal+"\n\t}";
    	retVal=retVal+"\n}";
    	
    	System.out.println(retVal);
    	return retVal;
    }
    
	
}
