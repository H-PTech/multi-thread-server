package com.hnptech.server.thread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.hnptech.server.handler.ClientHandler;

public class ThreadManager {
    private static final int THREAD_POOL_SIZE = 10; // 스레드 풀 크기
    private static final ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    private static ServerSocket serverSocket;

    // 서버 시작
    public static void start(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("[SERVER] 서버가 실행 중입니다. 포트: " + port);

            // 클라이언트 수신 스레드 시작
            startClientListener();

            // 기타 작업 실행 (예: 서버 모니터링)
            startAdditionalTasks();
        } catch (IOException e) {
            System.err.println("[SERVER] 서버 소켓 초기화 실패: " + e.getMessage());
        }
    }

    // 클라이언트 요청 수신 처리
    private static void startClientListener() {
        threadPool.execute(() -> {
            while (!serverSocket.isClosed()) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("[SERVER] 클라이언트 연결됨: " + clientSocket.getInetAddress());
                    threadPool.execute(new ClientHandler(clientSocket)); // 클라이언트 처리
                } catch (IOException e) {
                    if (serverSocket.isClosed()) {
                        System.out.println("[SERVER] 서버 소켓이 닫혔습니다.");
                        break;
                    }
                    System.err.println("[SERVER] 클라이언트 연결 실패: " + e.getMessage());
                }
            }
        });
    }

    // 추가 작업 처리 (예: 정기 작업)
    private static void startAdditionalTasks() {
        threadPool.execute(() -> {
            while (!serverSocket.isClosed()) {
                try {
                    // 서버 상태 점검, 로그 저장 등의 추가 작업 실행
                    System.out.println("[SERVER] 서버 상태 점검 중...");
                    Thread.sleep(5000); // 5초 간격으로 작업 수행
                } catch (InterruptedException e) {
                    System.err.println("[SERVER] 추가 작업 중단됨: " + e.getMessage());
                    Thread.currentThread().interrupt();
                }
            }
        });
    }

    // 서버 종료
    public static void stop() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                System.out.println("[SERVER] 서버 소켓이 닫혔습니다.");
            }
        } catch (IOException e) {
            System.err.println("[SERVER] 서버 소켓 닫기 실패: " + e.getMessage());
        } finally {
            threadPool.shutdown(); // 스레드 풀 종료
            System.out.println("[SERVER] 스레드 풀이 종료되었습니다.");
        }
    }
}
