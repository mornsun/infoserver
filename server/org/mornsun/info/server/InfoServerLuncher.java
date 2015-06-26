package org.mornsun.info.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Chauncey
 *
 */
public class InfoServerLuncher
{
    private static final Logger log = LoggerFactory.getLogger(InfoServerLuncher.class);

    /**
     * The main entrance of this server
     * 
     * @param args
     */
    public static void main(String[] args)
    {
        try {
            InfoServerConfig svrCfg = InfoServerConfig.getInstance();
            log.trace("Configuration loaded.");
            log.trace("InfoServer starting...");
            InfoServer infoServer = new InfoServer(svrCfg.getInfoServerTCPPort());
            infoServer.start();
        } catch (Exception e) {
            log.error("Forward Proxy start failed.", e);
            System.exit(-6);
        }
        log.trace("InfoServer started.");
    }
}
