package com.hnptech.server;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import com.hnptech.database.ConnectionPool;
import com.hnptech.server.thread.ManageService;
import com.hnptech.server.thread.ShutdownServer;

public class Start {

    // 서버 호스트 정보
    public static String HOST;
    public static String SQL_URL;
    public static String SQL_USER;
    public static String SQL_PASSWORD;
    public static int PORT = 8080; // 기본 포트 번호 설정

    // 서버 시작 인스턴스
    private static final Start instance = new Start();

    // 데이터베이스 설정 로드
    private static void loadDatabaseConfig() {
        try (FileInputStream db = new FileInputStream("server-demo/config/dbhost.properties")) {
            Properties dbProps = new Properties();
            dbProps.load(db);

            HOST = dbProps.getProperty("serverIp");
            SQL_URL = dbProps.getProperty("url");
            SQL_USER = dbProps.getProperty("user");
            SQL_PASSWORD = dbProps.getProperty("password");

            System.out.println("[SYSTEM] 데이터베이스 설정이 로드되었습니다.");
        } catch (IOException e) {
            System.err.println("[ERROR] 데이터베이스 설정 로드 실패: " + e.getMessage());
            System.exit(1); // 설정 로드 실패 시 프로그램 종료
        }
    }

    // 서버 종료 시 자원 정리
    private static void setupShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("[SYSTEM] 서버가 종료됩니다.");
            ShutdownServer.getInstance().run(); // 자원 정리
        }));
    }

    // 서버 실행
    public void run() {
        System.out.println("[SYSTEM] 커넥션 풀 초기화 중...");
        ConnectionPool connectionPool = ConnectionPool.getInstance();

        System.out.println("[SYSTEM] 서버 실행 중...");
        ManageService.start(PORT); // ManageService를 통해 서버 시작
    }

    public static void main(String[] args) {
        loadDatabaseConfig(); // 데이터베이스 설정 로드
        setupShutdownHook();  // 종료 후크 설정
        instance.run();       // 서버 실행
    }
}
