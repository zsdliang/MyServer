import java.io.*;
import java.net.Socket;

class BattleService implements Runnable {
    private Socket socket1;
    private Socket socket2;
    private ObjectInputStream in = null;
    private String score1;
    private String score2;
    private PrintWriter writer1;
    private PrintWriter writer2;
    private BufferedReader in1;
    private BufferedReader in2;

    public BattleService(Socket socket1, Socket socket2) throws IOException {
        this.socket1 = socket1;
        this.socket2 = socket2;
        writer1 = new PrintWriter(new BufferedWriter(
                new OutputStreamWriter(
                        socket1.getOutputStream(), "UTF-8")), true);
        writer2 = new PrintWriter(new BufferedWriter(
                new OutputStreamWriter(
                        socket2.getOutputStream(), "UTF-8")), true);
        in1 = new BufferedReader(new InputStreamReader(socket1.getInputStream()));
        in2 = new BufferedReader(new InputStreamReader(socket2.getInputStream()));
    }
    @Override
    public void run() {
        if((socket1 != null) && (socket2 != null)) {
            try {
                while(true) {
                    score1 = in1.readLine();
                    System.out.println("score1: "+score1);
                    score2 = in2.readLine();
                    System.out.println("score2: "+score2);
                    writer1.println(score2);
                    writer2.println(score1);
                    if((score1 != null && score2 != null) && (score1.equals("end") || score2.equals("end"))){
                        break;
                    }
                }
                socket1.close();
                socket2.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}