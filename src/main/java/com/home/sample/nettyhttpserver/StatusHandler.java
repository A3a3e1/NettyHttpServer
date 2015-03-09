/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.home.sample.nettyhttpserver;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.rendersnake.HtmlAttributesFactory.border;
import org.rendersnake.HtmlCanvas;

/**
 *
 * @author paulvoropaiev
 */
public class StatusHandler {

    String url = "jdbc:mysql://localhost:3306/status";
    String user = "root";
    String password = "12345678";
    int countAll = 0;
    int countUniqueIP = 0, countRedirect = 0;
    Map<String, Integer> uniqueIPMap = new HashMap<>();
    Map<String, String> lastIPtimestamp = new HashMap<>();
    Map<String, Integer> redirectQtyMap = new HashMap<>();
    Set<String> keySetUniqueIP, keySetRedirect;

    Connection myConn;
    Statement myStmt;
    ResultSet resultSet;
    ResultSet last16Set;

    String ip;

    protected StatusHandler(String ip, String uri, String timestamp) {

        try {
            myConn = DriverManager.getConnection(url, user, password);

            myStmt = myConn.createStatement();

            StringBuilder sb = new StringBuilder(ip);
            ip = ip.substring(1, sb.indexOf(":"));
            this.ip = ip;

            //1. ADD A NEW ITEM INTO DB
            String sql = "INSERT INTO status.request_info (src_ip, uri, timestamp, "
                    + "sent_bytes, received_bytes, speed) "
                    + "VALUES ('" + ip + "', '" + uri + "', '" + timestamp
                    + "', '0', '0', '0')";

            myStmt.executeUpdate(sql);

            //2. SELECT ALL QUERIES
            resultSet = myStmt.executeQuery("SELECT * FROM status.request_info");
            while (resultSet.next()) {
                countAll++;
            }

            //3. SELECT UNIQUE IP AND PUT IT INTO MAP WHERE KEY=IP, VALUE=NUMBER OF REQUESTS
            resultSet = myStmt.executeQuery("SELECT DISTINCT (src_ip) FROM status.request_info");

            while (resultSet.next()) {
                countUniqueIP++;
                uniqueIPMap.put(resultSet.getString("src_ip"), 0);
            }

            resultSet = myStmt.executeQuery("SELECT DISTINCT (uri) FROM status.request_info");

            while (resultSet.next()) {
                countRedirect++;
                redirectQtyMap.put(resultSet.getString("uri"), 0);
            }
            
//---------------START___Redirect Qty.

            int eachRedirectCount = 0;
            keySetRedirect = redirectQtyMap.keySet();
            for (String s : keySetRedirect) {
                eachRedirectCount = 0;  
                resultSet = myStmt.executeQuery("SELECT * FROM status.request_info WHERE uri='" + s + "'");
                while (resultSet.next()) {
                    eachRedirectCount++;
                }
                redirectQtyMap.put(s, eachRedirectCount);
            }

//---------------END___Redirect Qty.
            
//---------------START___Unique IP statistics
            
            int eachIPcount = 0;
            String time = "";
            keySetUniqueIP = uniqueIPMap.keySet();
            for (String s : keySetUniqueIP) {
                eachIPcount = 0;
                resultSet = myStmt.executeQuery("SELECT * FROM status.request_info WHERE src_ip='" + s + "'");
                while (resultSet.next()) {
                    eachIPcount++;
                    time = resultSet.getString("timestamp");
                }
                uniqueIPMap.put(s, eachIPcount);
                lastIPtimestamp.put(s, time);
            }

            //4. ADD LAST 16 CONNECTS INTO  LIST last16Connects<String>
            last16Set = myStmt.executeQuery("SELECT * FROM status.request_info ORDER BY id DESC LIMIT 16");

//---------------END___Unique IP statistics
            
        } catch (SQLException ex) {
            Logger.getLogger(StatusHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected void buildHtml() throws IOException, SQLException {

        //RenderSnake 1.8 library
        HtmlCanvas html = new HtmlCanvas();

//---------------START___Unique IP statistics
        html
                .p().i().content("Current IP: " + ip)
                .p().content("Total requests: " + countAll)
                .p().content("Unique IP requests: " + countUniqueIP)
                .p().h3().content("Unique ip detailed:");
        html
                .table(border("1"))
                .tr()
                .th().content("#")
                .th().content("IP")
                .th().content("Requests Qty.")
                .th().content("Time of last request")
                ._tr();

        int i = 0;

        for (String s : keySetUniqueIP) {

            long time = Long.parseLong(lastIPtimestamp.get(s));
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTimeInMillis(time);

            html
                    .tr()
                    .td().content(++i + "  ")
                    .td().content(s)
                    .td().content(uniqueIPMap.get(s))
                    .td().content(calendar.getTime().toString())
                    ._tr();
        }

        html._table()
                .hr();
//---------------END___Unique IP statistics

//---------------START___Redirect Qty.
        html
                .p().h3().content("Redirect statistics:")
                .table(border("1"))
                .tr()
                .th().content("#")
                .th().content("URI")
                .th().content("Redirect Qty.")
                ._tr();

        int j = 0;

        for (String s : keySetRedirect) {

            html
                    .tr()
                    .td().content(++j + "  ")
                    .td().content(s)
                    .td().content(redirectQtyMap.get(s))
                    ._tr();
        }

        html._table()
                .hr();

//---------------END_Redirect Qty.
//---------------START___Last 16 connections
        html
                .p().h3().content("Last 16 connections:");

        html
                .table(border("1"))
                .tr()
                .th().content("#")
                .th().content("src_ip")
                .th().content("uri")
                .th().content("timestamp")
//                .th().content("sent_bytes")
//                .th().content("received_bytes")
//                .th().content("speed")
                ._tr();

        int index = 0;
        while (last16Set.next()) {
            html.tr()
                    .td().content(++index)
                    .td().content(last16Set.getString("src_ip"))
                    .td().content(last16Set.getString("uri"))
                    .td().content(last16Set.getString("timestamp"))
//                    .td().content(last16Set.getString("sent_bytes"))
//                    .td().content(last16Set.getString("received_bytes"))
//                    .td().content(last16Set.getString("speed"))
                    ._tr();
        }

        html._table()
                .hr();
//---------------END_Last 16 connections

        PrintWriter writer = new PrintWriter("./status.html", "UTF-8");
        writer.println(html.toHtml());
        writer.close();
    }

}
