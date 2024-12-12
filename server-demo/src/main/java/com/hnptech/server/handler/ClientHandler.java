package com.hnptech.server.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable{
    private Socket clienSocket;

    public ClientHandler(Socket clienSocket){
        this.clienSocket = clienSocket;
    }

    @Override
    public void run(){
        try(BufferedReader in = new BufferedReader(new InputStreamReader(clienSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clienSocket.getOutputStream(), true)){
            
                String message;
                while((message = in.readLine()) != null){
                    System.out.println("[CLIENT] " + message);

                    // 클라이언트에게 응답
                    out.println("[SERVER RESPONSE]" + message);

                    // 종료 확인
                    if(message.equalsIgnoreCase("exit")){
                        System.out.println("[SERVER] 클라이언트 연결 종료");
                        break;
                    }
                }
                
        } catch(IOException e){
            System.err.println("[SERVER] 클라이언트 처리 중 오류 발생");
        } finally{
            try{
                clienSocket.close();
            } catch(IOException e){
                System.err.println("[SERVER] 클라이언트 소켓 닫기 실패");
            }
        }

    }
}
