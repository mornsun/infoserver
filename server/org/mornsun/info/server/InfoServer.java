package org.mornsun.info.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Chauncey
 *
 */
public class InfoServer
{
    private static final Logger log = LoggerFactory.getLogger(InfoServer.class);

    private InfoChannel frontend;

    /**
     * 
     * @param localPort
     */
    public InfoServer(int localPort)
    {
        frontend = new InfoChannel(localPort);
        frontend.init();
    }

    /**
     * 
     * @throws Exception
     */
    public void start() throws Exception
    {
        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    shutdown();
                }
                catch (Exception e)
                {
                }
            }
        });

        setup();
    }
    
    /**
     * 
     * @throws Exception
     */
    protected void setup() throws Exception
    {
        frontend.start();
    }

    /**
     * 
     * @throws Exception
     */
    protected void shutdown() throws Exception
    {
        log.warn("InfoServer Shutdown");
    }
}
