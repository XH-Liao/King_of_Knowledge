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

    //�榡�G�D��, �|�ӿﶵ, ����
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
                if (message.equals("ready to start")) {             //�p�G����T��"ready to start"��ܦ����a�n����
                    Server.players++;
                    synchronized (ht) {
                        DataOutputStream outstream = new DataOutputStream(socket.getOutputStream());
                        outstream.writeUTF("Player " + Server.players);     //�ǰeplayer ID
                    }

                    if (Server.players % 3 == 0) {                 //Start����A�ǰe�D��
                        //outstream.writeUTF("Start!");
                        Server.players++;

                        //�H���D���D��
                        questions.clear();
                        while (questions.size() < Server.questions_amount) {
                            questions.add((int) (Math.random() * (Server.QA.length)));    //�H������0~4���Ʀr
                        }
                        System.out.println("questions: " + questions);

                        synchronized (ht) {
                            for (Enumeration e = ht.elements(); e.hasMoreElements(); ) {
                                DataOutputStream outstream = (DataOutputStream) e.nextElement();
                                //�����ǰe�D�ص�Client
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