package com.hnptech.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.hnptech.server.Start;

public class ConnectionPool {
    private List<Connection> availableConnections = new ArrayList<>();
    private List<Connection> usedConnections = new ArrayList<>();
    private static final int INITIAL_POOL_SIZE = 10;

    private String url = null;
    private String user = null;
    private String password = null;

    private static ConnectionPool instance;
    
    public static ConnectionPool getInstance(){

        if(instance == null){
            try {
                instance = new ConnectionPool();
            } catch (SQLException e) {
                System.err.println("[SYSTEM] 데이터베이스 접속 실패 - " + e.getMessage());
            }
        }
        return instance;
    }

    private ConnectionPool() throws SQLException{
        this.url = Start.SQL_URL;
        this.user = Start.SQL_USER;
        this.password = Start.SQL_PASSWORD;

        for(int i = 0; i < INITIAL_POOL_SIZE; i++){
            availableConnections.add(createConnection());
        }
    }

    // 커넥션 생성
    private Connection createConnection() throws SQLException{
        return DriverManager.getConnection(url, user, password);
    }

    // 커넥션 가져오기
    public synchronized Connection getConnection(){
        if(availableConnections.isEmpty()){
            throw new RuntimeException("커넥션 풀에 이용 가능한 커넥션이 없습니다.");
        }
        // 사용가능한 -> 사용중인으로 옮김
        Connection connection = availableConnections.remove(availableConnections.size()-1);
        usedConnections.add(connection);
        return connection;
    }

    // 커넥션 반환
    public synchronized void releaseConnection(Connection connection){
        if(usedConnections.remove(connection)){
            availableConnections.add(connection);
        } else {
            throw new IllegalArgumentException("커넥션 풀이 아닙니다.");
        }
    }

    public int getAvailableConnectionsCount(){
        return availableConnections.size();
    }

    // 모든 커넥션 종료
    public void shutdown(){
        System.out.println("[SYSTEM] 커넥션 풀 종료");
        for(Connection connection : availableConnections){
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        for(Connection connection : usedConnections){
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
