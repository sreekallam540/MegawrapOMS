
package com.propellum.oms.entities;

public class OrderAPI
{
	private int		oid;
	private String	companyname;
	private String	employerid;
	private String	refreshfrequency;
	private String	refreshdays;
	private String	startdate;
	private String	enddate;
	private String	orderstatus;

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

}
