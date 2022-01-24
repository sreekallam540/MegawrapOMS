/**
 * 
 */
package com.propellum.oms.entities;

/**
 * @author viral.sejpal
 *
 */
public class OrderModificationEntry {
	
	private int orderModificationEntryId;
	private int orderModificationId;
	private String field;
	private String oldValue;
	private String newValue;
	
	
	public int getOrderModificationEntryId() {
		return orderModificationEntryId;
	}
	public void setOrderModificationEntryId(int orderModificationEntryId) {
		this.orderModificationEntryId = orderModificationEntryId;
	}
	public int getOrderModificationId() {
		return orderModificationId;
	}
	public void setOrderModificationId(int orderModificationId) {
		this.orderModificationId = orderModificationId;
	}
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public String getOldValue() {
		return oldValue;
	}
	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}
	public String getNewValue() {
		return newValue;
	}
	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}
	
	
	
	

}
