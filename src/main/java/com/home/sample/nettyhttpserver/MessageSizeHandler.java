/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.home.sample.nettyhttpserver;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.MessageList;


/**
 *
 * @author paulvoropaiev
 */
public class MessageSizeHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageList<Object> msgs) throws Exception {
        
        //TODO message size anylizer
        System.out.printf("Messages: %s%n", msgs.size());
        super.messageReceived(ctx, msgs);
    }
    
}
