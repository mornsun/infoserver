package org.mornsun.info.server;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InfoChannel
{
    private static final Logger log = LoggerFactory.getLogger(InfoChannel.class);

    private int port;
    private ServerBootstrap bootstrap;
    private boolean inited;

    /**
     * 
     * @param port
     */
    public InfoChannel(int port)
    {
        this.port = port;
        this.inited = false;
    }

    /**
     * Initialize this instance
     */
    public void init()
    {
        bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(
                Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));
        bootstrap.setPipelineFactory(new InfoChannelPipelineFactory());
        inited = true;
    }

    /**
     * 
     * @throws Exception
     */
    public void start() throws RuntimeException
    {
        if (!inited) {
            log.error("Fail to start ForwardProxy");
            throw new RuntimeException(
                    "Could not start ForwardProxy. Please invoke init() before start().");
        }
        
        bootstrap.setOption("child.keepAlive", true); // for mobiles & our stateful app
        bootstrap.setOption("child.tcpNoDelay", true); // better latency over bandwidth
        bootstrap.setOption("reuseAddress", true); // kernel optimization
        bootstrap.setOption("child.reuseAddress", true); // kernel optimization
        bootstrap.setOption("child.receiveBufferSize", 10485760);
        bootstrap.setOption("localAddress", new InetSocketAddress(port));
        //bootstrap.bind(new InetSocketAddress(port));
        bootstrap.bind();
    }
}
