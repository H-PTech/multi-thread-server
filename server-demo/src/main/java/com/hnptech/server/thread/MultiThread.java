package com.hnptech.server.thread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.hnptech.server.Start;
import com.hnptech.server.handler.ClientHandler;

public class MultiThread {
    // 스레드 풀 생성
    static ExecutorService threadPool = Executors.newFixedThreadPool(10);
    private static ServerSocket serverSocket;

    public static void startServer() {
        int port = Start.PORT;
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("[SERVER] 대기 중... ");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("[SERVER] 클라이언트 연결됨 : " + clientSocket.getInetAddress());
                threadPool.execute(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            if(e.getMessage().equals("Socket closed")) return;

            System.err.println("[SERVER] 서버 소켓 초기화 실패 - " + e.getMessage());
        }
    }

    // 서버 소켓 닫기
    public static void stopServer() {
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
                System.out.println("[SERVER] 서버 소켓이 닫혔습니다.");
            } catch (IOException e) {
                System.err.println("[SERVER] 서버 소켓 닫기 실패 - " + e.getMessage());
            }
        } else {
            System.out.println("[SERVER] 서버 소켓이 이미 닫혀 있습니다.");
        }
        // 스레드 풀 종료
        threadPool.shutdown();
    }
}
