package com.redhat.management.approval;

/**
 * This class was automatically generated by the data modeler tool.
 */

public class Internal implements java.io.Serializable {

	static final long serialVersionUID = 1L;

	private java.lang.String org_id;
	private java.lang.String auth_type;
	private java.lang.Integer auth_time;

	public Internal() {
	}

	public java.lang.String getOrg_id() {
		return this.org_id;
	}

	public void setOrg_id(java.lang.String org_id) {
		this.org_id = org_id;
	}

	public java.lang.String getAuth_type() {
		return this.auth_type;
	}

	public void setAuth_type(java.lang.String auth_type) {
		this.auth_type = auth_type;
	}

	public java.lang.Integer getAuth_time() {
		return this.auth_time;
	}

	public void setAuth_time(java.lang.Integer auth_time) {
		this.auth_time = auth_time;
	}

	public Internal(java.lang.String org_id, java.lang.String auth_type,
			java.lang.Integer auth_time) {
		this.org_id = org_id;
		this.auth_type = auth_type;
		this.auth_time = auth_time;
	}

}