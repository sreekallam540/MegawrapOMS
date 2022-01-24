
package com.propellum.oms.entities;

import java.util.List;

public class OrderDetailsAPI
{

	private int						oid;
	private String					companyname;
	private String					employerid;
	private String					joburl;
	private String					refreshdays;
	private String					refreshfrequency;
	private String					startdate;
	private String					enddate;
	private String					industry;
	private String					orderstatus;
	private String					autoimport;
	private String					combinewrapfiles;
	private String					remarks;
	private String					additionalnotes;

	private List<OrderModification>	orderModificationHistory;

	/**
	 * @return the oid
	 */
	public int getOid()
	{
		return oid;
	}

	/**
	 * @param oid
	 *            the oid to set
	 */
	public void setOid(int oid)
	{
		this.oid = oid;
	}

	/**
	 * @return the companyname
	 */
	public String getCompanyname()
	{
		return companyname;
	}

	/**
	 * @param companyname
	 *            the companyname to set
	 */
	public void setCompanyname(String companyname)
	{
		this.companyname = companyname;
	}

	/**
	 * @return the employerid
	 */
	public String getEmployerid()
	{
		return employerid;
	}

	/**
	 * @param employerid
	 *            the employerid to set
	 */
	public void setEmployerid(String employerid)
	{
		this.employerid = employerid;
	}

	/**
	 * @return the joburl
	 */
	public String getJoburl()
	{
		return joburl;
	}

	/**
	 * @param joburl
	 *            the joburl to set
	 */
	public void setJoburl(String joburl)
	{
		this.joburl = joburl;
	}

	/**
	 * @return the refreshdays
	 */
	public String getRefreshdays()
	{
		return refreshdays;
	}

	/**
	 * @param refreshdays
	 *            the refreshdays to set
	 */
	public void setRefreshdays(String refreshdays)
	{
		this.refreshdays = refreshdays;
	}

	/**
	 * @return the refreshfrequency
	 */
	public String getRefreshfrequency()
	{
		return refreshfrequency;
	}

	/**
	 * @param refreshfrequency
	 *            the refreshfrequency to set
	 */
	public void setRefreshfrequency(String refreshfrequency)
	{
		this.refreshfrequency = refreshfrequency;
	}

	/**
	 * @return the startdate
	 */
	public String getStartdate()
	{
		return startdate;
	}

	/**
	 * @param startdate
	 *            the startdate to set
	 */
	public void setStartdate(String startdate)
	{
		this.startdate = startdate;
	}

	/**
	 * @return the enddate
	 */
	public String getEnddate()
	{
		return enddate;
	}

	/**
	 * @param enddate
	 *            the enddate to set
	 */
	public void setEnddate(String enddate)
	{
		this.enddate = enddate;
	}

	/**
	 * @return the industry
	 */
	public String getIndustry()
	{
		return industry;
	}

	/**
	 * @param industry
	 *            the industry to set
	 */
	public void setIndustry(String industry)
	{
		this.industry = industry;
	}

	/**
	 * @return the orderstatus
	 */
	public String getOrderstatus()
	{
		return orderstatus;
	}

	/**
	 * @param orderstatus
	 *            the orderstatus to set
	 */
	public void setOrderstatus(String orderstatus)
	{
		this.orderstatus = orderstatus;
	}

	/**
	 * @return the autoimport
	 */
	public String getAutoimport()
	{
		return autoimport;
	}

	/**
	 * @param autoimport
	 *            the autoimport to set
	 */
	public void setAutoimport(String autoimport)
	{
		this.autoimport = autoimport;
	}

	/**
	 * @return the combinewrapfiles
	 */
	public String getCombinewrapfiles()
	{
		return combinewrapfiles;
	}

	/**
	 * @param combinewrapfiles
	 *            the combinewrapfiles to set
	 */
	public void setCombinewrapfiles(String combinewrapfiles)
	{
		this.combinewrapfiles = combinewrapfiles;
	}

	/**
	 * @return the remarks
	 */
	public String getRemarks()
	{
		return remarks;
	}

	/**
	 * @param remarks
	 *            the remarks to set
	 */
	public void setRemarks(String remarks)
	{
		this.remarks = remarks;
	}

	/**
	 * @return the additionalnotes
	 */
	public String getAdditionalnotes()
	{
		return additionalnotes;
	}

	/**
	 * @param additionalnotes
	 *            the additionalnotes to set
	 */
	public void setAdditionalnotes(String additionalnotes)
	{
		this.additionalnotes = additionalnotes;
	}

	/**
	 * @return the orderModificationHistory
	 */
	public List<OrderModification> getOrderModificationHistory()
	{
		return orderModificationHistory;
	}

	/**
	 * @param orderModificationHistory
	 *            the orderModificationHistory to set
	 */
	public void setOrderModificationHistory(List<OrderModification> orderModificationHistory)
	{
		this.orderModificationHistory = orderModificationHistory;
	}

}
