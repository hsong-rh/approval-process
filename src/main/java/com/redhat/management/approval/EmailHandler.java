package com.redhat.management.approval;

public class EmailHandler implements java.io.Serializable {
    
	static final long serialVersionUID = 1L;

	@org.kie.api.definition.type.Label("Url")
	private java.lang.String url;
	@org.kie.api.definition.type.Label("Headers")
	private java.lang.String headers;
	@org.kie.api.definition.type.Label("Body")
	private java.lang.String body;


	public EmailHandler() {
	}
/*
	public java.lang.String getHeaders() {
		return this.header;
	}
*/
	public void setHeaders(java.lang.String headers) {
		this.headers = headers;
	}
/*
	public java.lang.String getUrl() {
		return this.url;
	}
*/
	public void setUrl(java.lang.String url) {
		this.url = url;
	}
/*
	public java.lang.String getBody() {
		return this.body;
	}
*/
	public void setBody(java.lang.String body) {
		this.body = body;
	}

	public EmailHandler(java.lang.String headers, java.lang.String url,
			java.lang.String body) {
		this.headers = headers;
		this.url = url;
		this.body = body;
	}

	public String getUrl() {
		return "url";
	}
	
	public String getHeaders() {
		/*
		 x-rh-clientid: <id>
		 x-rh-apitoken: <token>
		 x-rh-insights-env: <env>
		 
		 key1=val1;key2=v2;key3=v3
		 */
		return "header";
	}
	
	public String getBody(com.redhat.management.approval.Approver approver, String request_content) {
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
		return "body";
	}

}
