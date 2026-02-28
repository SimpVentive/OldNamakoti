package com.unitol.namakoti.model;

public class CountModel {
	
	public String user_id;
	public String namam_id;
	
	public CountModel(String user_id, String namam_id) {
		super();
		this.user_id = user_id;
		this.namam_id = namam_id;
	}

	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getNamam_id() {
		return namam_id;
	}
	public void setNamam_id(String namam_id) {
		this.namam_id = namam_id;
	}
}
