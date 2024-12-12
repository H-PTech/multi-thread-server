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
    
    public static void startServer(){
        int port = Start.PORT;
        try(ServerSocket serverSocket = new ServerSocket(port)){
            System.out.println("[SERVER] 대기 중... ");

            while(true){
                Socket clientSocket = serverSocket.accept();
                System.out.println("[SERVER] 클라이언트 연결됨 : " + clientSocket.getInetAddress());
                threadPool.execute(new ClientHandler(clientSocket));
            }
        } catch(IOException e){
            System.err.println("[SERVER] 서버 소켓 초기화 실패");
        }
    }
}