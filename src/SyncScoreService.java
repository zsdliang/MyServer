import com.aircraftwar.android.application.datahandle.Score;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SyncScoreService implements Runnable{
    private List<Score> scores = new ArrayList<Score>();
    private List<Score> dataFromClient = new ArrayList<Score>();
    private File file = new File("data.ser");
    private Socket socket;
    private ObjectInputStream in = null;


    public SyncScoreService(Socket socket){
        this.socket = socket;

    }

    @Override
    public void run() {
        if(socket != null) {
            synchronized (file) {
                try {
                    in = new ObjectInputStream(socket.getInputStream());
                    dataFromClient = (List<Score>) in.readObject();
                    fileRead();
                    synchronized (dataFromClient) {
                        if(dataFromClient != null) {
                            addScore();
                        }
                    }
                    sendMessge(socket);
                    in.close();
                    fileWrite();

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public void sendMessge(Socket socket) throws IOException {
        ObjectOutputStream objOut = new ObjectOutputStream(socket.getOutputStream());
        objOut.writeObject(scores);
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