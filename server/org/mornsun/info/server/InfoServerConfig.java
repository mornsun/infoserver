package org.mornsun.info.server;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.log4j.PropertyConfigurator;
import org.ho.yaml.Yaml;
import org.mornsun.info.api.InfoMgr;
import org.mornsun.info.util.IPostprocessable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Chauncey
 *
 */
public class InfoServerConfig implements IPostprocessable
{
    private static final Logger log = LoggerFactory.getLogger(InfoServerConfig.class);
    private static InfoServerConfig INSTANCE = null;

    protected static final int DEFAULT_UPDATE_PERIOD = 60; // in second
    protected static final String DEFAULT_GEOIP_INFO_FILE = "ss-geoip.utf8";
    protected static final int DEFAULT_iNFO_SERVER_PORT = 39088;

    /**
     * Retrieve the singleton instance
     * NOTE: It is strongly recommended to invoke this at the period of loading configurations or initialization.
     * 
     * @return
     */
    public static InfoServerConfig getInstance()
    {
        if (INSTANCE == null) {
            synchronized (InfoServerConfig.class) {
                if (INSTANCE == null) {
                    String configFolder = System.getProperty("config", "./config/");
                    System.setProperty("infoserver.dir", configFolder + "../");
                    // log configuration
                    String log4jConfig = configFolder + File.separator + "log4j.properties";
                    System.out.println("log4j: " + log4jConfig);
                     PropertyConfigurator.configure(log4jConfig);

                    // sample configuration
                    String svrCfgFn = configFolder + File.separator + "infoserver.yaml";
                    System.out.println("cfgFile: " + svrCfgFn);
                    File svrCfgFile = new File(svrCfgFn);
                    try {
                        INSTANCE = Yaml.loadType(svrCfgFile, InfoServerConfig.class);
                    } catch (FileNotFoundException e) {
                        log.error("init Exception: YAML config file is not found: " + svrCfgFile);
                        throw new IllegalArgumentException(e);
                    }
                    // experiment configuration
                    InfoMgr info_mgr = InfoMgr.getInstance();
                    info_mgr.startAutoUpdate(INSTANCE.getUpdatePeriod());
                }
            }
        }
        return INSTANCE;
    }

    public int infoServerTCPPort;

    /**
     * 
     * @return
     */
    public int getInfoServerTCPPort()
    {
        return infoServerTCPPort;
    }

    public String geoIPInfoFile;

    /**
     * 
     * @return
     */
    public String getGeoIPInfoFile()
    {
        return geoIPInfoFile;
    }

    public int updatePeriod;

    /**
     * 
     * @return
     */
    public int getUpdatePeriod()
    {
        return updatePeriod;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.mornsun.info.util.IPostprocessable#postprocess()
     */
    @Override
    public void postprocess() throws IllegalArgumentException
    {
        // TODO Auto-generated method stub
        if (0 == infoServerTCPPort) {
            infoServerTCPPort = DEFAULT_iNFO_SERVER_PORT;
        }
        if (0 == updatePeriod) {
            updatePeriod = DEFAULT_UPDATE_PERIOD;
        }
        if (null == geoIPInfoFile || "".equals(geoIPInfoFile)) {
            geoIPInfoFile = DEFAULT_GEOIP_INFO_FILE;
        }
    }
}
