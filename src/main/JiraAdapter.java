package main;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

import com.abdullahteke.ysd.jiraadapter.JiraManager;

public class JiraAdapter {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		
		  String jiraURL="https://jiraurl.com.tr";
		  String userName="jira_user";
		  String userPassword="jira_password";
		  String projectKey="project_key";
		  
		String id=""; 
		String title="Deneme : Mersin"; 
		String description="Mersin deneme";
		String severity="Critical";
		String category="category"; 
		String subCategory="asdfasdf";
		String components="VeriTabani_KritikAlarm_Sorumlusu";
		
		String  password= "0719SK_Bilkent_OpsBridge";
		
		String encoded= Base64.getEncoder().encodeToString(password.getBytes());
		System.out.println(encoded);
		
		String decodedPass = null;
		try {
			decodedPass = new String(Base64.getDecoder().decode(userPassword), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(decodedPass);
		
		
	String ticketNumber=JiraManager
			.getInstance(jiraURL, userName, userPassword, projectKey)
			.createNewIssue(id,title,description,severity,category,subCategory,components);
	
	System.out.println(ticketNumber+ " is opened!");

   
	}
    
}
