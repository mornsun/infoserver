package org.mornsun.info.server;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.protobuf.ProtobufDecoder;
import org.jboss.netty.handler.codec.protobuf.ProtobufEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ExtensionRegistry;
import org.mornsun.info.protobuf.ProtobufVarint64FrameDecoder;
import org.mornsun.info.protobuf.ProtobufVarint64LengthFieldPrepender;
import org.mornsun.info.protocol.InfoProtocol;

/**
 * 
 * @author Chauncey
 *
 */
public class InfoChannelPipelineFactory implements ChannelPipelineFactory
{
    private static final Logger log = LoggerFactory.getLogger(InfoChannelPipelineFactory.class);

    /**
     * 
     */
    public ChannelPipeline getPipeline() throws Exception
    {
        log.debug("getPipeline");
        ChannelPipeline p = Channels.pipeline();
        p.addLast("frameDecoder", new ProtobufVarint64FrameDecoder());
        p.addLast("frameEncoder", new ProtobufVarint64LengthFieldPrepender());
        ExtensionRegistry registry = ExtensionRegistry.newInstance();
        InfoProtocol.registerAllExtensions(registry);
        p.addLast("protobufDecoder",
                new ProtobufDecoder(InfoProtocol.InfoReqPack.getDefaultInstance(), registry));
        p.addLast("protobufEncoder", new ProtobufEncoder());
        p.addLast("handler", new InfoChannelHandler());
        return p;
    }
}
