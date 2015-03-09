package com.home.sample.nettyhttpserver;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

/**
 *
 * @author paulvoropaiev
 */
public class HttpSnoopServerInitializer extends ChannelInitializer<SocketChannel> {
  
      @Override
      public void initChannel(SocketChannel ch) {
          ChannelPipeline p = ch.pipeline();
          
          p.addLast(new HttpRequestDecoder());
          p.addLast(new HttpResponseEncoder());
          p.addLast(new HttpSimpleHandler());
//          Uncomment next line for including message size anylizer
//          p.addLast(new MessageSizeHandler());
          
      }
  }
