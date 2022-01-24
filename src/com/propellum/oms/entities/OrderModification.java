package com.propellum.oms.entities;

import java.util.Calendar;
import java.util.List;

public class OrderModification {
	
	
	private int orderModificationId;
	private Calendar orderModificationDate;
	private int orderId;
	private String userId;
	private String remark;
		
	private List<OrderModificationEntry> orderModificationEntries;
	

	public int getOrderModificationId() {
		return orderModificationId;
	}

	public void setOrderModificationId(int orderModificationId) {
		this.orderModificationId = orderModificationId;
	}

	public Calendar getOrderModificationDate() {
		return orderModificationDate;
	}

	public void setOrderModificationDate(Calendar orderModificationDate) {
		this.orderModificationDate = orderModificationDate;
	}

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public List<OrderModificationEntry> getOrderModificationEntries() {
		return orderModificationEntries;
	}

	public void setOrderModificationEntries(
			List<OrderModificationEntry> orderModificationEntries) {
		this.orderModificationEntries = orderModificationEntries;
	}
	
	
	
		
	
	

}
