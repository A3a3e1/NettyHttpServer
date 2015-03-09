/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.home.sample.nettyhttpserver;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringDecoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Logger;

/**
 *
 * @author paulvoropaiev
 */
public class HttpSimpleHandler extends SimpleChannelInboundHandler<HttpRequest> {

    private static final Logger LOG = Logger.getLogger(HttpSimpleHandler.class.getName());

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, HttpRequest req) throws Exception {
        QueryStringDecoder qsd = new QueryStringDecoder(req.getUri());
        System.out.printf("%s %s%n", req.getMethod(), qsd.path());
        
        
        FullHttpResponse resp;
        String ip = ctx.channel().remoteAddress().toString();
        String uri = req.getUri();
        String timestamp = Long.toString(System.currentTimeMillis());
        
        
        //Update the statistics DB with all info about current request
        StatusHandler sh = new StatusHandler(ip, uri, timestamp);
        
        switch (qsd.path().substring(1)) {
            case "hello":
                resp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
                ByteBuf content = resp.content();
//  Return text:
//                content.writeBytes("Hello world".getBytes("UTF-8"));

//  Return file *.html:
                File fileHello = new File("./index.html");
                try (FileInputStream fis = new FileInputStream(fileHello)) {
                    content.writeBytes(fis, (int) fileHello.length());
                } catch (IOException ex) {
//                    LOG.log(Level.SEVERE, null, ex);
                }
                Thread.sleep(10000);
                break;
            case "redirect":
                resp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FOUND);
                //Add null pointer validation (if no params)
                resp.headers().set(HttpHeaders.Names.LOCATION, qsd.parameters().get("url").get(0));
                break;

            case "status":
                
                //Build actual version of html stat file *status.html*
                sh.buildHtml();
                
                resp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
                ByteBuf contentStatus = resp.content();
//                contentStatus.writeBytes("Hello world".getBytes("UTF-8"));

                //Output File with Statistics *status.html*
                File fileStatus = new File("./status.html");
                try (FileInputStream fis = new FileInputStream(fileStatus)) {
                    contentStatus.writeBytes(fis, (int) fileStatus.length());
                } catch (IOException ex) {
//                    LOG.log(Level.SEVERE, null, ex);
                }

                System.out.println("URI: " + req.getUri() + "\n127.0.0.1" + qsd.path()
                        + "\nIP: " + ctx.channel().remoteAddress());

                break;

            default://github.com/netty/netty
                resp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND);
                ByteBuf contentError = resp.content();
                File fileError = new File("./404.html");
                try (FileInputStream fis = new FileInputStream(fileError)) {
                    contentError.writeBytes(fis, (int) fileError.length());
                } catch (IOException ex) {
//                    LOG.log(Level.SEVERE, null, ex);
                }
                break;
        }

        ctx.write(resp);
        ctx.close();
    }

}
