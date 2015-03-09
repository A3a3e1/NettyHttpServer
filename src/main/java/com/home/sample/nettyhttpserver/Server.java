/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.home.sample.nettyhttpserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author paulvoropaiev
 */
public class Server implements Runnable {

    private static final Logger LOG = Logger.getLogger(Server.class.getName());

    private final int port;
    private final int max_connections=5;
    private final int max_streams=10;

    public Server(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(max_connections);
        EventLoopGroup workerGroup = new NioEventLoopGroup(max_streams);

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new HttpSnoopServerInitializer());

            ChannelFuture ch = bootstrap.bind(port).sync();
            
            LOG.log(Level.INFO, "STARTED ON http://127.0.0.1:8080/");
            
            ch.channel().closeFuture().sync();
        } catch (InterruptedException ex) {
            ; //stop
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public void stop() {
        // !!! IMPLEMENT SHUTTING DOWN IF NEEDED
    }

}
