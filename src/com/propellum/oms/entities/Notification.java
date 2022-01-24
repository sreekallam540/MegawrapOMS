/**
 * 
 */
package com.propellum.oms.entities;

import java.util.Date;

/**
 * @author viral.sejpal
 *
 */
public class Notification {
	
	private long notificationId;
	private String message;
	private Date when;
	private boolean alreadyRead;
	private int orderId;
	private int userId;
	private User user;
	private String notificationHeader;
	private String orderName;
	
	
	public String getOrderName() {
		return orderName;
	}
	public void setOrderName(String orderName) {
		this.orderName = orderName;
	}
	public String getNotificationHeader() {
		return notificationHeader;
	}
	public void setNotificationHeader(String orderName) {
		this.notificationHeader = orderName;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public long getNotificationId() {
		return notificationId;
	}
	public void setNotificationId(long notificationId) {
		this.notificationId = notificationId;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Date getWhen() {
		return when;
	}
	public void setWhen(Date when) {
		this.when = when;
	}
	public boolean isAlreadyRead() {
		return alreadyRead;
	}
	public void setAlreadyRead(boolean alreadyRead) {
		this.alreadyRead = alreadyRead;
	}
	public int getOrderId() {
		return orderId;
	}
	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}
	
	
	
	
	
}
