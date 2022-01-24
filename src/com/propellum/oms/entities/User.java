package com.propellum.oms.entities;

import java.util.Calendar;

/**
 * 
 * @author viral.sejpal
 *
 */
public class User {

	private int registrationId;
	private String loginId;
	private String name;
	private String lastName;
	private String accountName;
	private UserType userType;
	private Calendar creationDateTime;
	private boolean active;
	private int notificationCount;
	
	
	
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public int getNotificationCount() {
		return notificationCount;
	}
	public void setNotificationCount(int notificationCount) {
		this.notificationCount = notificationCount;
	}
	public Calendar getCreationDateTime() {
		return creationDateTime;
	}
	public void setCreationDateTime(Calendar creationDateTime) {
		this.creationDateTime = creationDateTime;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public int getRegistrationId() {
		return registrationId;
	}
	public void setRegistrationId(int registrationId) {
		this.registrationId = registrationId;
	}
	public String getLoginId() {
		return loginId;
	}
	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public UserType getUserType() {
		return userType;
	}
	public void setUserType(UserType userType) {
		this.userType = userType;
	}
	
	
	
	
	
}
