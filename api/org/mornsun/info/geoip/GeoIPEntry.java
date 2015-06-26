package org.mornsun.info.geoip;

/**
 * 
 * @author Chauncey
 *
 */
public class GeoIPEntry implements Comparable<GeoIPEntry>
{
    private long ip_begin;
    private long ip_end;
    private String nation;
    private String state;
    private String city;
    private String isp;

    /**
     * @param ip_begin
     * @param ip_end
     * @param nation
     * @param state
     * @param city
     * @param isp
     */
    public GeoIPEntry(long ip_begin, long ip_end, String nation, String state, String city,
            String isp)
    {
        this.ip_begin = ip_begin;
        this.ip_end = ip_end;
        this.nation = nation;
        this.state = state;
        this.city = city;
        this.isp = isp;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(GeoIPEntry entry)
    {
        // TODO Auto-generated method stub
        if (entry.getIpBegin() == ip_begin) {
            return 0;
        } else if (entry.getIpBegin() < ip_begin) {
            return -1;
        } else {
            return 1;
        }
    }

    /**
     * 
     * @param ip
     * @return
     */
    public int compareTo(long ip)
    {
        // TODO Auto-generated method stub
        if (ip >= ip_begin && ip <= ip_end) {
            return 0;
        } else if (ip < ip_begin) {
            return -1;
        } else {
            return 1;
        }
    }

    /**
     * 
     * @param ip
     * @return
     */
    public static String ip2string(long ip)
    {
        return (ip >>> 24) + "." + (ip >>> 16 & 0xFF) + "." + (ip >>> 8 & 0xFF) + "." + (ip & 0xFF);
    }

    /**
     * 
     * @param str
     * @return
     */
    public static long string2ip(String str)
    {
        String[] ip = str.split("\\.");
        return (Long.parseLong(ip[0]) << 24) + (Integer.parseInt(ip[1]) << 16)
                + (Integer.parseInt(ip[2]) << 8) + Integer.parseInt(ip[3]);
    }

    /**
     * @return the ip_begin
     */
    public long getIpBegin()
    {
        return ip_begin;
    }

    /**
     * @return the ip_end
     */
    public long getIpEnd()
    {
        return ip_end;
    }

    /**
     * @return the nation
     */
    public String getNation()
    {
        return nation;
    }

    /**
     * @return the state
     */
    public String getState()
    {
        return state;
    }

    /**
     * @return the city
     */
    public String getCity()
    {
        return city;
    }

    /**
     * @return the isp
     */
    public String getIsp()
    {
        return isp;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "GeoIPEntry [ip_begin=" + ip2string(ip_begin) + ", ip_end=" + ip2string(ip_end)
                + ", nation=" + nation + ", state=" + state + ", city=" + city + ", isp=" + isp
                + "]";
    }

}
