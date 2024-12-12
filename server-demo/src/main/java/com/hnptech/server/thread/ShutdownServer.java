package com.hnptech.server.thread;

public class ShutdownServer {
    public static ShutdownServer instance = new ShutdownServer();
    private int step = 0;
    public static ShutdownServer getInstance(){
        return instance;
    }

    public void run(){

        // 종료 단계 분할
        if(step == 0){
            System.out.println("[SYSTEM] 서버 소켓 닫음");
            MultiThread.stopServer();
            step++;
        }else if(step == 1){
            System.out.println("[shutdown step 2]");
        }
        
    }
}
