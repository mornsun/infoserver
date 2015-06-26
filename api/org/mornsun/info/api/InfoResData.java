/**
 * 
 */
package org.mornsun.info.api;

import java.util.ArrayList;

/**
 * @author Chauncey
 *
 */
public class InfoResData
{
	private ArrayList<Integer> m_sids; //抽样id
	private InfoLocation m_location;  //位置相关
	
	/**
	 * @param sids
	 * @param isp
	 * @param nation
	 * @param province
	 * @param city
	 */
	public InfoResData(ArrayList<Integer> sids, InfoLocation location)
	{
		this.m_sids = sids;
		this.m_location = location;
	}
	
	/**
	 * @return the sids
	 */
	public ArrayList<Integer> getSids()
	{
		return m_sids;
	}

	/**
	 * @return the location
	 */
	public InfoLocation getLocation()
	{
		return m_location;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "InfoResData [m_sids=" + m_sids
				+ ", m_location=" + m_location + "]";
	}

}
