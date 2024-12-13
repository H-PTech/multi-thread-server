package com.hnptech.server.handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try (InputStream in = clientSocket.getInputStream();
             OutputStream out = clientSocket.getOutputStream()) {
            // 클라이언트 요청 처리
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                String received = new String(buffer, 0, bytesRead);
                System.out.println("[SERVER] 클라이언트 요청: " + received);

                // 응답 처리
                String response = "서버 응답: " + received;
                out.write(response.getBytes());
                out.flush();
            }
        } catch (IOException e) {
            System.err.println("[CLIENT] 처리 중 오류 발생: " + e.getMessage());
        } finally {
            closeSocket();
        }
    }

    // 소켓 닫기
    private void closeSocket() {
        try {
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
                System.out.println("[CLIENT] 소켓이 닫혔습니다.");
            }
        } catch (IOException e) {
            System.err.println("[CLIENT] 소켓 닫기 실패: " + e.getMessage());
        }
    }
}
