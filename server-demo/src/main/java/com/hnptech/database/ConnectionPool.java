package com.hnptech.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ConnectionPool {
    private List<Connection> availableConnections = new ArrayList<>();
    private List<Connection> usedConnections = new ArrayList<>();
    private static final int INITIAL_POOL_SIZE = 10;

    private String url = null;
    private String user = null;
    private String password = null;

    public ConnectionPool(String url, String user, String passowrd) throws SQLException{
        this.url = url;
        this.user = user;
        this.password = passowrd;
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
    public void shutdown() throws SQLException{
        for(Connection connection : availableConnections){
            connection.close();
        }
        for(Connection connection : usedConnections){
            connection.close();
        }
    }

    // 기본 Pool 생성
    public static ConnectionPool create(String url, String user, String password) throws SQLException{
        return new ConnectionPool(url, user, password);
    }
}
