package org.mornsun.info.server;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timeout;
import org.jboss.netty.util.Timer;
import org.jboss.netty.util.TimerTask;
import org.mornsun.info.api.InfoMgr;
import org.mornsun.info.api.InfoReqData;
import org.mornsun.info.api.InfoResData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.mornsun.info.protocol.InfoProtocol.InfoReqPack;
import org.mornsun.info.protocol.InfoProtocol.InfoResPack;

public class InfoChannelHandler extends SimpleChannelHandler
{
    private static final Logger log = LoggerFactory.getLogger(InfoChannelHandler.class);
    private String m_addr;
    private String m_uid;
    private String m_ip;

    /**
     * 
     * @param c
     * @param proto
     * @param msgID
     */
    private void processResponse(Channel ch, InfoResData resdata, InfoReqPack proto)
    {
        InfoResPack res_pack;
        try {
            InfoResPack.Builder builder = InfoResPack.newBuilder();
            builder.addAllSids(resdata.getSids());
            if (resdata.getLocation() != null) {
                String isp_ = resdata.getLocation().getIsp();
                String nation_ = resdata.getLocation().getNation();
                String province_ = resdata.getLocation().getProvince();
                String city_ = resdata.getLocation().getCity();
                if (null != isp_ && !"".equals(isp_))
                    builder.setIsp(isp_);
                if (null != nation_ && !"".equals(nation_))
                    builder.setNation(nation_);
                if (null != province_ && !"".equals(province_))
                    builder.setProvince(province_);
                if (null != city_ && !"".equals(city_))
                    builder.setCity(city_);
            }
            res_pack = builder.build();
            ChannelFuture res_ch = ch.write(res_pack);
            if (null == res_ch || !res_ch.isSuccess()) {
                res_pack = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            res_pack = null;
        }

        if (null != res_pack) {
            log.info(this.m_addr + " Q[vs:" + proto.getVersion() + " uid:" + this.m_uid + " ip:"
                    + this.m_ip + " app:" + proto.getApp() + " apv:" + proto.getAppVer() + " os:"
                    + proto.getOs() + " osv:" + proto.getOsVer() + " ch:" + proto.getChannel()
                    + " isp:" + proto.getIsp() + " n:" + proto.getNation() + " p:"
                    + proto.getProvince() + " c:" + proto.getCity() + "]" + " R[vs:"
                    + res_pack.getVersion() + " sids:" + intArray2String(res_pack.getSidsList())
                    + " isp:" + res_pack.getIsp() + " n:" + res_pack.getNation() + " p:"
                    + res_pack.getProvince() + " c:" + res_pack.getCity() + "]");
        } else {
            log.info(this.m_addr + " Q[vs:" + proto.getVersion() + " uid:" + this.m_uid + " ip:"
                    + this.m_ip + " app:" + proto.getApp() + " apv:" + proto.getAppVer() + " os:"
                    + proto.getOs() + " osv:" + proto.getOsVer() + " ch:" + proto.getChannel()
                    + " isp:" + proto.getIsp() + " n:" + proto.getNation() + " p:"
                    + proto.getProvince() + " c:" + proto.getCity() + "]");
        }
    }

    /**
     * 
     * @param iarray
     * @return
     */
    protected static final <E> String intArray2String(List<E> iarray)
    {
        if (null == iarray) {
            return "null";
        }
        Iterator<E> i = iarray.iterator();
        if (!i.hasNext())
            return "";

        StringBuilder sb = new StringBuilder();
        for (;;) {
            E e = i.next();
            sb.append(e == iarray ? "(this)" : e);
            if (!i.hasNext())
                return sb.toString();
            sb.append(",");
        }
    }

    // TEST: for timeout
    private static final Timer g_timer = new HashedWheelTimer();

    /**
     * 
     * @author Chauncey
     *
     */
    private final class TimeoutTask implements TimerTask
    {
        private final Channel m_ch;
        private final InfoResData m_resdata;
        private final InfoReqPack m_proto;

        TimeoutTask(Channel ch, InfoResData resdata, InfoReqPack proto)
        {
            m_ch = ch;
            m_resdata = resdata;
            m_proto = proto;
        }

        public void run(Timeout timeout) throws Exception
        {
            if (timeout.isCancelled()) {
                return;
            }
            if (!m_ch.isOpen()) {
                return;
            }
            fireTimedOut();
            timeout.cancel();
        }

        private void fireTimedOut() throws Exception
        {
            m_ch.getPipeline().execute(new Runnable()
            {
                public void run()
                {
                    processResponse(m_ch, m_resdata, m_proto);
                }
            });
        }
    }

    // TEST: end

    /**
     * 
     * @param ctx
     * @param event
     * @return
     */
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent event) throws Exception
    {
        InfoReqPack proto = (InfoReqPack) event.getMessage();
        Channel ch = event.getChannel();
        // InetSocketAddress socketAddress = (InetSocketAddress)event.getRemoteAddress();
        // System.out.println(socketAddress.getHostName());
        this.m_addr = event.getRemoteAddress().toString().substring(1); // trim the leader character '/'
        this.m_uid = proto.getUid();
        this.m_ip = proto.getIp();

        // Object value = reqdata.getField(InfoReqPack.getDescriptor().findFieldByName(key));
        log.trace("REQ " + this.m_addr + " [vs:" + proto.getVersion() + " uid:" + this.m_uid
                + " ip:" + this.m_ip + " app:" + proto.getApp() + " apv:" + proto.getAppVer()
                + " os:" + proto.getOs() + " osv:" + proto.getOsVer() + " ch:" + proto.getChannel()
                + " isp:" + proto.getIsp() + " n:" + proto.getNation() + " p:"
                + proto.getProvince() + " c:" + proto.getCity() + "]");

        InfoMgr infoMgr = InfoMgr.getInstance();
        InfoReqData reqdata = new InfoReqData();
        reqdata.setBasic(proto.getUid(), proto.getIp(), proto.getChannel());
        reqdata.setTerminal(proto.getApp(), proto.getAppVer(), proto.getOs(), proto.getOsVer());
        reqdata.setLocation(proto.getIsp(), proto.getNation(), proto.getProvince(),
                proto.getCity(), proto.getLocSwitch());
        InfoResData resdata = infoMgr.execute(reqdata);

        // TEST: for timeout
        if (proto.getIsp().startsWith("000000")) {
            // System.out.println("Hello test:" + proto.getIsp());
            int sleep_time = Integer.parseInt(proto.getIsp());
            g_timer.newTimeout(new TimeoutTask(ch, resdata, proto), sleep_time,
                    TimeUnit.MILLISECONDS);
            return;
        }
        // System.out.println("Hello world:" + proto.getIsp());
        // TEST: end
        processResponse(ch, resdata, proto);
        // res_ch = ch.close();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jboss.netty.channel.SimpleChannelHandler#channelDisconnected(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.ChannelStateEvent)
     */
    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e)
            throws Exception
    {
        log.debug("Discn " + this.m_addr + "  [uid:" + this.m_uid + " ip:" + this.m_ip + "]");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jboss.netty.channel.SimpleChannelHandler#exceptionCaught(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.ExceptionEvent)
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception
    {
        log.warn("Excpt " + this.m_addr + "  [uid:" + this.m_uid + " ip:" + this.m_ip + "]",
                e.getCause());
    }
}
