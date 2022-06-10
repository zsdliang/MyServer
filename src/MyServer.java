import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MyServer {
    private String content = "";
    private List<Score> scores = new ArrayList<Score>();
    private List<Score> dataFromClient = new ArrayList<Score>();
    private File file = new File("data.ser");
    public static void main(String args[]){
        new MyServer();
    }
    public  MyServer(){
        try{
            InetAddress addr = InetAddress.getLocalHost();
            System.out.println("local host:" + addr);

            //创建server socket
            ServerSocket serverSocket = new ServerSocket(10581);
            System.out.println("listen port "+serverSocket.getLocalPort());

            while(true){
                System.out.println("waiting client connect");
                Socket socket = serverSocket.accept();
                System.out.println("accept client connect" + socket);
                new Thread(new syncScoreService(socket)).start();
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    class syncScoreService implements Runnable{
        private Socket socket;
        private ObjectInputStream in = null;


        public syncScoreService(Socket socket){
            this.socket = socket;

        }

        @Override
        public void run() {
            synchronized (file) {
                try {
                    in = new ObjectInputStream(socket.getInputStream());
                    dataFromClient = (List<Score>) in.readObject();
                    fileRead();

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                synchronized (dataFromClient) {
                    if(dataFromClient != null) {
                        addScore();
                    }
                }


                try {
                    sendMessge(socket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    fileWrite();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        public void sendMessge(Socket socket) throws IOException {
            ObjectOutputStream objOut = new ObjectOutputStream(socket.getOutputStream());
            objOut.writeObject(scores);
        }
    }

    class battleService implements Runnable {
        private Socket socket;
        private ObjectInputStream in = null;

        public battleService(Socket socket){
            this.socket = socket;

        }

        @Override
        public void run() {

        }
    }

    private void fileWrite() throws IOException {
        if (file.exists()) {
            file.delete();
        }
        FileOutputStream fileout = new FileOutputStream(file);
        ObjectOutputStream out = new ObjectOutputStream(fileout);
        out.writeObject(scores);
        fileout.close();
    }

    private void fileRead() throws IOException {
        FileInputStream filein = null;
        try {
            if (file.exists()) {
                filein = new FileInputStream(file);
                ObjectInputStream in = new ObjectInputStream(filein);
                scores = (List<Score>) in.readObject();
            }
        } catch (IOException | ClassNotFoundException i) {
            i.printStackTrace();
        }

        if (scores == null) {
            scores = new ArrayList<Score>();
        }

        if (filein != null) {
            filein.close();
        }
    }

    private void addScore() {
        for(int i = scores.size()-1;i >=0;i--) {
            for(int j = dataFromClient.size()-1;j>=0;j--) {
                if(scores.get(i).uid.equals(dataFromClient.get(j).uid)) {
                    dataFromClient.remove(dataFromClient.get(j));
                    System.out.println(dataFromClient);
                }
            }
        }

        scores.addAll(dataFromClient);
        scores.sort((o1, o2) -> {
            if (o1.userscore >= o2.userscore) {
                return -1;
            } else {
                return 1;
            }
        });
    }
}