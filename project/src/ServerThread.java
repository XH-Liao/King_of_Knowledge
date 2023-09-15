import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

class ServerThread extends Thread implements Runnable {
    private Socket socket;
    private Hashtable ht;

    //格式：題目, 四個選項, 答案
    private Set<Integer> questions;

    public ServerThread(Socket socket, Hashtable ht) {
        this.socket = socket;
        this.ht = ht;
        this.questions = new HashSet<>();
    }

    public void run() {
        DataInputStream inputStream;

        try {
            inputStream = new DataInputStream(socket.getInputStream());

            while (true) {
                String message = inputStream.readUTF();

                System.out.println("Message: " + message);
                if (message.equals("ready to start")) {             //如果收到訊息"ready to start"表示有玩家要參賽
                    Server.players++;
                    synchronized (ht) {
                        DataOutputStream outstream = new DataOutputStream(socket.getOutputStream());
                        outstream.writeUTF("Player " + Server.players);     //傳送player ID
                    }

                    if (Server.players % 3 == 0) {                 //Start條件，傳送題目
                        //outstream.writeUTF("Start!");
                        Server.players++;

                        //隨機挑選題目
                        questions.clear();
                        while (questions.size() < Server.questions_amount) {
                            questions.add((int) (Math.random() * (Server.QA.length)));    //隨機產生0~4的數字
                        }
                        System.out.println("questions: " + questions);

                        synchronized (ht) {
                            for (Enumeration e = ht.elements(); e.hasMoreElements(); ) {
                                DataOutputStream outstream = (DataOutputStream) e.nextElement();
                                //網路傳送題目給Client
                                for (Integer i : questions) {
                                    for (int j = 0; j < 6; j++) {
                                        outstream.writeUTF(Server.QA[i][j]);
                                    }
                                }
                            }
                        }
                    }
                }else {
                    synchronized (ht) {
                        for (Enumeration e = ht.elements(); e.hasMoreElements(); ) {
                            DataOutputStream outstream = (DataOutputStream) e.nextElement();

                            try {
                                    outstream.writeUTF(message);
                                    System.out.println(message);
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                }

            }
        } catch (IOException ex) {
        } finally {
            synchronized (ht) {
                System.out.println("Remove connection: " + socket);
                ht.remove(socket);
                try {
                    socket.close();
                } catch (IOException ex) {
                }
            }
        }
    }
}