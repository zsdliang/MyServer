import com.aircraftwar.android.application.datahandle.Score;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MyServer {

    public static void main(String args[]){
        new MyServer();
    }
    public  MyServer(){
        try{
            InetAddress addr = InetAddress.getLocalHost();
            System.out.println("local host:" + addr);

            //创建server socket
            ServerSocket serverSocket1 = new ServerSocket(10086);
            ServerSocket serverSocket2 = new ServerSocket(9999);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            Socket socket1 = serverSocket2.accept();
                            System.out.println("enter the battle thread");
                            Socket socket2 = serverSocket2.accept();
                            new Thread(new BattleService(socket1, socket2)).start();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    while(true) {
                        Socket socket1 = null;
                        try {
                            socket1 = serverSocket1.accept();
                            if(socket1 != null) {
                                System.out.println("connect successfully");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        new Thread(new SyncScoreService(socket1)).start();
                    }
                }
            }).start();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

}