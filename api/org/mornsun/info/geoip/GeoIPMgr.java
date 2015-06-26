/**
 * 
 */
package org.mornsun.info.geoip;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.mornsun.info.util.IUpdatable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Chauncey
 * 
 */
public class GeoIPMgr implements IUpdatable
{
    private static final Logger log = LoggerFactory.getLogger(GeoIPMgr.class);
    private static final Object updateLock = new Object();
    private static GeoIPMgr INSTANCE = null;
    private static long m_lastUpdateTime = -1;

    /**
     * Atomic: if you read or write it, you must assure that it is an atomic operation
     */
    ArrayList<GeoIPEntry> m_entries = new ArrayList<GeoIPEntry>(100000);

    /**
     * Retrieve the singleton instance NOTE: It is strongly recommended to invoke this at the period of loading configurations or initialization.
     * 
     * @return
     */
    public static GeoIPMgr getInstance()
    {
        if (INSTANCE == null) {
            synchronized (GeoIPMgr.class) {
                if (INSTANCE == null) {
                    GeoIPMgr instance = new GeoIPMgr();
                    instance.init();
                    INSTANCE = instance;
                }
            }
        }
        return INSTANCE;
    }

    /**
     * When it is necessary, update the configuration structure with the mechanism like double buffers
     * 
     * @return true: updated
     */
    public boolean update()
    {
        // sample configuration
        String cfgFolder = System.getProperty("config", "config");
        String cfgFn = cfgFolder + File.separator + "ss-geoip.utf8";// InfoServerConfig.getInstance().getGeoIPInfoFile();
        File cfgFile = new File(cfgFn);
        long time = cfgFile.lastModified();
        try {
            if (time != 0 && time != m_lastUpdateTime) { // the configuration file exists and has been modified
                boolean bNeedUpdate = false;
                synchronized (updateLock) {
                    if (time != 0 && time != m_lastUpdateTime) { // the configuration file exists and has been modified
                        m_lastUpdateTime = time; // If set m_lastUpdateTime after successfully reconfigure, a incorrect configuration will be loaded repeatedly
                        bNeedUpdate = true;
                    }
                }
                if (bNeedUpdate) {
                    // update the configuration structure with the mechanism like double buffers
                    // lock free to update the configuration
                    ArrayList<GeoIPEntry> entries = this.load(cfgFile);
                    m_entries = entries; // it is atomic
                    m_lastUpdateTime = time;
                    log.warn("updated " + cfgFile);
                    return true;
                }
            }
            if (0 == time) {
                log.error("the configuration file does not be found: " + cfgFile);
            } else { // time == m_lastUpdateTime && time != 0
                log.debug("the configuration file does not modified: " + cfgFile);
            }
        } catch (FileNotFoundException e) { // will not enter this block in normal case because of the anterior assertion of time
            log.error("init Exception: config file error: file not found" + cfgFile);
        } catch (Exception e) {
            log.error("init Exception: config file error: " + cfgFile);
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 
     * @param map
     * @param key
     * @return
     */
    protected static final <T> T getReusableInstance(Map<T, T> map, T key)
    {
        T key_tmp;
        if ((key_tmp = map.get(key)) == null) {
            map.put(key, key);
        } else {
            key = key_tmp;
        }
        return key;
    }

    /**
     * 
     * @param infoFile
     */
    protected ArrayList<GeoIPEntry> load(File file) throws FileNotFoundException, IOException
    {
        HashMap<String, String> nationSet = new HashMap<String, String>(1024); // use history set for economize the RAM
        HashMap<String, String> stateSet = new HashMap<String, String>(1024);
        HashMap<String, String> citySet = new HashMap<String, String>(1024);
        HashMap<String, String> ispSet = new HashMap<String, String>(1024);

        String line;// 一次读入一行，直到读入null为文件结束
        int iLine = 1;
        BufferedReader reader = null;

        ArrayList<GeoIPEntry> entries = new ArrayList<GeoIPEntry>(100000);
        reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
        while ((line = reader.readLine()) != null) {
            if ('#' == line.charAt(0))
                continue;
            String cols[] = line.split("\t");
            if (cols.length != 11) {
                log.warn("Line ignored: the format of line[" + iLine + "] in " + file.getName()
                        + " is illegal: cols_num=" + cols.length);
                continue;
            }
            long ip_begin = GeoIPEntry.string2ip(cols[5]);
            long ip_end = GeoIPEntry.string2ip(cols[6]);
            String city = getReusableInstance(citySet, cols[7].trim());
            String state = getReusableInstance(stateSet, cols[8].trim());
            String isp = getReusableInstance(ispSet, cols[9].trim());
            String nation = getReusableInstance(nationSet, cols[10].trim());
            GeoIPEntry entry = new GeoIPEntry(ip_begin, ip_end, nation, state, city, isp);
            entries.add(entry);
            ++iLine;
        }
        reader.close();
        return entries;
    }

    /**
     * Classic binary search
     * 
     * @param ip
     * @return
     */
    public GeoIPEntry find0(long ip)
    {
        ArrayList<GeoIPEntry> entries = getEntries();
        int low = 0;
        int high = entries.size() - 1;
        while (low <= high) {
            int middle = low + ((high - low) >> 1);
            GeoIPEntry entry = entries.get(middle);
            int resComp = entry.compareTo(ip);
            if (0 == resComp) {
                return entry;
            } else if (resComp < 0) {
                high = middle - 1;
            } else {
                low = middle + 1;
            }
        }
        return null;
    }

    /**
     * Binary search of better performance for large data
     * This is approximately 1/6 quicker than the classic binary search for random input
     * 
     * @param ip
     * @return
     */
    public GeoIPEntry find(long ip)
    {
        ArrayList<GeoIPEntry> entries = getEntries();
        int low = 0;
        int high = entries.size() - 1;
        while (low < high) {
            int middle = low + ((high - low) >> 1);
            GeoIPEntry entry = entries.get(middle);
            int resComp = entry.compareTo(ip);
            if (resComp > 0) {
                low = middle + 1;
            } else {
                high = middle;
            }
        }
        GeoIPEntry entry = entries.get(low);
        if ((high == low) && 0 == entry.compareTo(ip))
            return entry;
        return null;
    }

    /**
     * 
     * @param strIp
     * @return
     */
    public GeoIPEntry find(String strIp)
    {
        long ip = GeoIPEntry.string2ip(strIp);
        return this.find(ip);
    }

    /**
	 * 
	 */
    protected GeoIPMgr()
    {
        // TODO Auto-generated constructor stub
    }

    /**
     * Initialize this instance
     */
    protected void init()
    {
        boolean res = this.update();
        if (false == res) { // at the 1st time, it must be successful
            throw new RuntimeException("fail to init the configuration");
        }
    }

    /**
     * @return the m_entries
     */
    public ArrayList<GeoIPEntry> getEntries()
    {
        return m_entries;
    }

    /**
     * Only for testing
     * 
     * @param args
     */
    public static void main(String[] args)
    {
        // TODO Auto-generated method stub
        GeoIPMgr mgr = GeoIPMgr.getInstance();
        GeoIPEntry entry = mgr.find("111.206.115.203");
        if (null != entry)
            System.out.println(entry.toString());
        entry = mgr.find("1.206.115.0");
        if (null != entry)
            System.out.println(entry.toString());
        entry = mgr.find("223.255.255.255");
        if (null != entry)
            System.out.println(entry.toString());

        for (int i = 0; i < 5; ++i) {
            long start_time = System.currentTimeMillis();
            boolean fromEnd = false;
            for (long k = 1; k < 100000000; ++k) {
                long ip;
                if (fromEnd) {
                    ip = 100000000 - k;
                } else {
                    ip = k;
                }
                fromEnd = !fromEnd;
                mgr.find(ip);
                // System.out.println(ip);
            }
            System.out.println("Send time elapsed:" + (System.currentTimeMillis() - start_time));
        }
    }

}
