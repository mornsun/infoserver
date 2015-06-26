/**
 * 
 */
package org.mornsun.info.api;

import org.mornsun.info.geoip.GeoIPEntry;

/**
 * Location relevant information
 * 
 * @author Chauncey
 *
 */
public class InfoLocation
{
    private String m_isp; // operator
    private String m_nation; // country
    private String m_province; // province
    private String m_city; // city

    /**
     * 
     */
    public InfoLocation()
    {
        init(null, null, null, null);
    }

    /**
     * @param m_isp
     * @param m_nation
     * @param m_province
     * @param m_city
     */
    public InfoLocation(String isp, String nation, String province, String city)
    {
        init(isp, nation, province, city);
    }

    /**
     * 
     * @param isp
     * @param nation
     * @param province
     * @param city
     */
    public void init(String isp, String nation, String province, String city)
    {
        this.m_isp = isp;
        this.m_nation = nation;
        this.m_province = province;
        this.m_city = city;
    }

    /**
     * @return the isp
     */
    public String getIsp()
    {
        return m_isp;
    }

    /**
     * @return the nation
     */
    public String getNation()
    {
        return m_nation;
    }

    /**
     * @return the province
     */
    public String getProvince()
    {
        return m_province;
    }

    /**
     * @return the city
     */
    public String getCity()
    {
        return m_city;
    }

    /**
     * merge another specified infoLoc
     */
    public void merge(GeoIPEntry ipEntry)
    {
        // In this structure, there are two parts of info, the ISP and the location
        // Generally, an ISP is determined by the informations interpreted from an IP
        // Therefore, merge respectively ISP and location information
        if (m_isp == null || "".equals(m_isp)) {
            m_isp = ipEntry.getIsp();
        }
        if ((m_nation == null || "".equals(m_nation))
                && (m_province == null || "".equals(m_province))
                && (m_city == null || "".equals(m_city))) { // If none of all the own location informations exists, the local info is unavailable
            m_nation = ipEntry.getNation();
            m_province = ipEntry.getState();
            m_city = ipEntry.getCity();
        }
        // Else regards the local informations as available and confident
    }

    /**
     * Judge whether the location information of this instance is complete and confident enough,
     * if not return true which denote that this instance do not need merge.
     * 
     * @return true: denote do not need merge
     */
    public boolean needMerge()
    {
        // In this class, there are two parts of info, the ISP and the location
        // Generally, an ISP is determined by the information interpreted from an IP
        // And thus merge respectively ISP and location information
        if (m_isp == null || "".equals(m_isp)) {
            return true;
        }
        if ((m_nation == null || "".equals(m_nation))
                && (m_province == null || "".equals(m_province))
                && (m_city == null || "".equals(m_city))) { // If none of all the own location informations exists, the local info is unavailable
            return true;
        }
        // Else regards the local informations as available and confident
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "InfoLocation [m_isp=" + m_isp + ", m_nation=" + m_nation + ", m_province="
                + m_province + ", m_city=" + m_city + "]";
    }

}
