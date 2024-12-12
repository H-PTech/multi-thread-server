package com.hnptech;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    public static void main(String[] args) {
        String serverIp = "127.0.0.1"; // 서버 IP 주소 (localhost)
        int port = 8080; // 서버 포트 번호

        try (Socket socket = new Socket(serverIp, port)) {
            System.out.println("[CLIENT] 서버에 연결 성공!");

            // 서버로 메시지 전송
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in))) {

                System.out.println("서버와 통신을 시작합니다. 종료하려면 'exit' 입력");

                while (true) {
                    System.out.print("보낼 메시지: ");
                    String message = userInput.readLine();

                    out.println(message); // 서버 전송

                    String response = in.readLine(); // 서버 응답
                    System.out.println(response);


                    if ("exit".equalsIgnoreCase(message)) {
                        System.out.println("[CLIENT] 연결 종료");
                        break;
                    }
                }
            }
        } catch (UnknownHostException e) {
            System.err.println("[CLIENT] 서버를 찾을 수 없습니다: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("[CLIENT] 서버와의 통신 중 오류 발생: " + e.getMessage());
        }
    }
}
