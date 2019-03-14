package com.redhat.management.approval;

public class NotificationEmail {
	public String getUrl() {
		return "<url>";
	}
	
	public String getHeaders() {
		/*
		 x-rh-clientid: <id>
		 x-rh-apitoken: <token>
		 x-rh-insights-env: <env>
		 
		 key1=val1;key2=v2;key3=v3
		 */
		return null;
	}
	
	public String getBody() {
		/*
		{
			  "emails": [
			    {
			      "subject": "Here's an Email!",
			      "body": "<body><p>This is the email.</p></body>",
			      "bodyType": "html",
			      "recipients": [
			        "\"Bill\" bilwei@redhat.com"
			      ]
			    }
			  ]
			}
		*/
		return null;
	}

}
