
package com.propellum.oms.entities;

import java.util.Calendar;
import java.util.List;

public class Order
{

	private int						orderId;
	private String					orderName;
	private String					orderUrl;
	private String					crawlUrl;
	private OrderStatus				orderStatus;
	private String					userId;

	private Calendar				creationDate;
	private Calendar				liveDate;
	private Calendar				expiredDate;
	private Calendar				lastModified;

	private String					startDate;
	private String					endDate;

	private String					remarks;

	private List<OrderModification>	orderModificationHistory;

	private String					companyType;
	private String					companyDescription;
	private String					companyIcon;
	private String					companySite;
	private String					clientSuccesAnalyst;
	private String					spareField1;
	private String					spareField2;
	private String					spareField3;
	private String					spareField4;
	private String					spareField5;
	private String					spareField6;
	private String					spareField7;
	private String					spareField8;

	public Order()
	{}

	public String getCompanyType()
	{
		return companyType;
	}

	public void setCompanyType(String companyType)
	{
		this.companyType = companyType;
	}

	public String getCompanyDescription()
	{
		return companyDescription;
	}

	public void setCompanyDescription(String companyDescription)
	{
		this.companyDescription = companyDescription;
	}

	public String getCompanyIcon()
	{
		return companyIcon;
	}

	public void setCompanyIcon(String companyIcon)
	{
		this.companyIcon = companyIcon;
	}

	public String getCompanySite()
	{
		return companySite;
	}

	public void setCompanySite(String companySite)
	{
		this.companySite = companySite;
	}

	public String getClientSuccesAnalyst()
	{
		return clientSuccesAnalyst;
	}

	public void setClientSuccesAnalyst(String clientSuccesAnalyst)
	{
		this.clientSuccesAnalyst = clientSuccesAnalyst;
	}

	public String getSpareField1()
	{
		return spareField1;
	}

	public void setSpareField1(String spareField1)
	{
		this.spareField1 = spareField1;
	}

	public String getSpareField2()
	{
		return spareField2;
	}

	public void setSpareField2(String spareField2)
	{
		this.spareField2 = spareField2;
	}

	public String getSpareField3()
	{
		return spareField3;
	}

	public void setSpareField3(String spareField3)
	{
		this.spareField3 = spareField3;
	}

	public String getSpareField4()
	{
		return spareField4;
	}

	public void setSpareField4(String spareField4)
	{
		this.spareField4 = spareField4;
	}

	public String getSpareField5()
	{
		return spareField5;
	}

	public void setSpareField5(String spareField5)
	{
		this.spareField5 = spareField5;
	}

	public String getSpareField6()
	{
		return spareField6;
	}

	public void setSpareField6(String spareField6)
	{
		this.spareField6 = spareField6;
	}

	public String getSpareField7()
	{
		return spareField7;
	}

	public void setSpareField7(String spareField7)
	{
		this.spareField7 = spareField7;
	}

	public String getSpareField8()
	{
		return spareField8;
	}

	public void setSpareField8(String spareField8)
	{
		this.spareField8 = spareField8;
	}

	public Calendar getCreationDate()
	{
		return creationDate;
	}

	public void setCreationDate(Calendar creationDate)
	{
		this.creationDate = creationDate;
	}

	public Calendar getLiveDate()
	{
		return liveDate;
	}

	public void setLiveDate(Calendar liveDate)
	{
		this.liveDate = liveDate;
	}

	public Calendar getLastModified()
	{
		return lastModified;
	}

	public void setLastModified(Calendar lastModified)
	{
		this.lastModified = lastModified;
	}

	public Calendar getExpiredDate()
	{
		return expiredDate;
	}

	public void setExpiredDate(Calendar expiredDate)
	{
		this.expiredDate = expiredDate;
	}

	public String getStartDate()
	{
		return startDate;
	}

	public void setStartDate(String startDate)
	{
		this.startDate = startDate;
	}

	public String getEndDate()
	{
		return endDate;
	}

	public void setEndDate(String endDate)
	{
		this.endDate = endDate;
	}

	public int getOrderId()
	{
		return orderId;
	}

	public void setOrderId(int orderId)
	{
		this.orderId = orderId;
	}

	public String getOrderName()
	{
		return orderName;
	}

	public void setOrderName(String orderName)
	{
		this.orderName = orderName;
	}

	public String getOrderUrl()
	{
		return orderUrl;
	}

	public void setOrderUrl(String orderUrl)
	{
		this.orderUrl = orderUrl;
	}

	public String getCrawlUrl()
	{
		return crawlUrl;
	}

	public void setCrawlUrl(String crawlUrl)
	{
		this.crawlUrl = crawlUrl;
	}

	public OrderStatus getOrderStatus()
	{
		return orderStatus;
	}

	public void setOrderStatus(OrderStatus orderStatus)
	{
		this.orderStatus = orderStatus;
	}

	public String getUserId()
	{
		return userId;
	}

	public void setUserId(String userId)
	{
		this.userId = userId;
	}

	public String getRemarks()
	{
		return remarks;
	}

	public void setRemarks(String remarks)
	{
		this.remarks = remarks;
	}

	public List<OrderModification> getOrderModificationHistory()
	{
		return orderModificationHistory;
	}

	public void setOrderModificationHistory(List<OrderModification> orderModificationHistory)
	{
		this.orderModificationHistory = orderModificationHistory;
	}

	@Override
	public String toString()
	{
		return "Order [orderId=" + orderId + ", orderName=" + orderName + ", orderUrl=" + orderUrl + ", crawlUrl=" + crawlUrl + ", orderStatus=" + orderStatus + ", creationDate=" + creationDate + ", liveDate=" + liveDate + ", expiredDate=" + expiredDate
				+ ", lastModified=" + lastModified + ", remarks=" + remarks + ", orderModificationHistory=" + orderModificationHistory + ", companyType=" + companyType + ", companyDescription=" + companyDescription + ", companyIcon=" + companyIcon
				+ ", companySite=" + companySite + ", startDate=" + startDate + ", endDate=" + endDate + ", spareField1=" + spareField1 + ", spareField2=" + spareField2 + ", spareField3=" + spareField3 + ", spareField4=" + spareField4 + ", spareField5="
				+ spareField5 + ", spareField6=" + spareField6 + ", spareField7=" + spareField7 + ", spareField8=" + spareField8 + "]";
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub

	}

}
