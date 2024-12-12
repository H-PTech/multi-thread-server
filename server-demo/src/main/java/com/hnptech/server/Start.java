package com.hnptech.server;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import com.hnptech.database.ConnectionPool;
import com.hnptech.server.thread.MultiThread;
import com.hnptech.server.thread.ShutdownServer;

public class Start {

    // 서버 호스트 정보
    public static String HOST, SQL_URL, SQL_USER, SQL_PASSWORD;
    public static int PORT;

    static {
        try{
            // 정적 파일로부터 데이터베이스 정보 로딩
            FileInputStream db = new FileInputStream("server-demo/config/dbhost.properties");
            Properties dbProbs = new Properties();
            dbProbs.load(db);
            db.close();

            HOST = new String(dbProbs.getProperty("serverIp"));
            SQL_URL = new String(dbProbs.getProperty("url"));
            SQL_USER = new String(dbProbs.getProperty("user"));
            SQL_PASSWORD = new String(dbProbs.getProperty("password"));
            PORT = 8080;

            Runtime.getRuntime().addShutdownHook(new Thread(()->{
                System.out.println("[SYSTEM] 서버가 종료됩니다.");
                ShutdownServer.getInstance().run();
                ShutdownServer.getInstance().run();
                System.exit(0);
            }));
        } catch(FileNotFoundException e){
            e.printStackTrace();
        } catch(IOException e){
            System.out.println(e);
        }
    }
    public static final Start instance = new Start();
    
    public void run() {
        System.out.println("[SYSTEM] 커넥션 풀 로드");
        ConnectionPool conn = null;
        try {
            conn = ConnectionPool.create(SQL_URL, SQL_USER, SQL_PASSWORD);
        } catch (SQLException e) {
            System.err.println("[SYSTEM] 커넥션 풀 로드 실패 "); ;
        }

        MultiThread.startServer();
        
    }

    public static class Shutdown implements Runnable{
        @Override
        public void run(){
            ShutdownServer.getInstance().run();
            ShutdownServer.getInstance().run();
        }
    }
    
    public static void main(String[] args) {
        instance.run();
    }
}