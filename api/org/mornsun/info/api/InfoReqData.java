/**
 * 
 */
package org.mornsun.info.api;

import java.util.HashMap;

/**
 * @author Chauncey
 *
 */
public class InfoReqData
{
    private String m_uid; // User id, constituted with arbitrary character
    private String m_ip; // User client IP
    private String m_channel; // The channel where this application was download
    private InfoTerminal m_terminal = new InfoTerminal(); // The terminal information
    private InfoLocation m_location = new InfoLocation(); // The location information
    private boolean m_loc_switch; // If open the location recognition
    private HashMap<String, String> m_datamap = new HashMap<String, String>(32); // Mapping the name and the data of an input

    /**
     * 
     */
    public InfoReqData()
    {
        setBasic(null, null, null);
        setLocation(null, null, null, null, false);
        setTerminal(null, null, null, null);
    }

    /**
     * 
     * @param uid
     * @param ip
     * @param channel
     */
    public void setBasic(String uid, String ip, String channel)
    {
        m_uid = uid;
        m_ip = ip;
        m_channel = channel;
        m_datamap.put("uid", uid);
        m_datamap.put("ip", ip);
        m_datamap.put("channel", channel);
    }

    /**
     * 
     * @param isp
     * @param nation
     * @param province
     * @param city
     * @param is_loc_switch
     */
    public void setLocation(String isp, String nation, String province, String city,
            boolean is_loc_switch)
    {
        m_loc_switch = is_loc_switch;
        m_location.init(isp, nation, province, city);
        m_datamap.put("isp", isp);
        m_datamap.put("nation", nation);
        m_datamap.put("province", province);
        m_datamap.put("city", city);
    }

    /**
     * 
     * @param app
     * @param app_ver
     * @param os
     * @param os_ver
     */
    public void setTerminal(String app, String app_ver, String os, String os_ver)
    {
        m_terminal.init(app, app_ver, os, os_ver);
        m_datamap.put("app", app);
        m_datamap.put("app_ver", app_ver);
        m_datamap.put("os", os);
        m_datamap.put("os_ver", os_ver);
    }

    /**
     * 
     * @param key
     * @return
     */
    public String get(String key)
    {
        return m_datamap.get(key);
    }

    /**
     * @return the uid
     */
    public String getUid()
    {
        return m_uid;
    }

    /**
     * @return the ip
     */
    public String getIp()
    {
        return m_ip;
    }

    /**
     * @return the channel
     */
    public String getChannel()
    {
        return m_channel;
    }

    /**
     * @return the adapter
     */
    public InfoTerminal getAdapter()
    {
        return m_terminal;
    }

    /**
     * @return the location
     */
    public InfoLocation getLocation()
    {
        return m_location;
    }

    /**
     * @return the m_loc_switch
     */
    public boolean isLocSwitch()
    {
        return m_loc_switch;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "InfoReqData [m_uid=" + m_uid + ", m_ip=" + m_ip + ", m_channel=" + m_channel
                + ", m_adapter=" + m_terminal + ", m_location=" + m_location + "]";
    }

}
