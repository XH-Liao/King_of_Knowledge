import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.sql.*;

public class Server {
    static int players;                    //�s�W���a�BStart��+1�A���a�� = (players/3)*2 + (player%3 -1)
    static int questions_amount;

    
    /*static String[][] QA = {{"Q1�G�U�C��̤��O�{���y���H", "C++", "Apple", "C", "Java", "B"},
            {"Q2�G8�ର2�i��O�H", "0111", "1001", "0100", "1000", "D"},
            {"Q3�G�U�C�ɶ������צ�̨̳ΡH", "O(n^2)", "O(n)", "O(log n)", "O(n log n)", "C"},
            {"Q4�G�H�U��̻y���P�s�@�����̤������H", "Chinese", "ASP.NET CORE", "PHP", "HTML", "A"},
            {"Q5�G�H�U��̵{���y���̦��Q�}�o�H", "FORTRAN", "LISP", "C", "Prolog", "A"}};
    */
    static String[][] QA;
    private static ServerSocket SSocket;
    private static int port;
    private Hashtable ht = new Hashtable();
    Socket socket;

    public Server() throws IOException {
        //Ū��DB�����D��
        try {
            String driver = "com.mysql.cj.jdbc.Driver";
            //�q�t�m�ѼƤ�����ƾڮwurl
            String url = "jdbc:mysql://localhost:3306/qa";
            //�q�t�m�ѼƤ�����Τ�W
            String user = "root";
            //�q�t�m�ѼƤ�����K�X
            String pass = "";

            //���U�X��
            Class.forName(driver);
            //����ƾڮw�s��
            Connection conn = DriverManager.getConnection(url, user, pass);
            //�Ы�Statement��H
            Statement stmt = conn.createStatement();
            //����d��
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM all_questions");
            rs.next();
            QA = new String[Integer.parseInt(rs.getString("COUNT(*)"))][6];

            rs = stmt.executeQuery("SELECT * FROM all_questions");
            int questions = 0;

            System.out.println("QA_____________________\n");
            while (rs.next()) {
                QA[questions][0] = rs.getString("Question");
                QA[questions][1] = rs.getString("A");
                QA[questions][2] = rs.getString("B");
                QA[questions][3] = rs.getString("C");
                QA[questions][4] = rs.getString("D");
                QA[questions][5] = rs.getString("Ans");

                /*
                System.out.println("Q"+questions+": "+QA[questions][0]);
                System.out.println("(A)"+questions+" "+QA[questions][1]);
                System.out.println("(B)"+questions+" "+QA[questions][2]);
                System.out.println("(C)"+questions+" "+QA[questions][3]);
                System.out.println("(D)"+questions+" "+QA[questions][4]);
                System.out.println("Ans. "+questions+": "+QA[questions][5]);
                 */

                questions++;
                /*
                System.out.println(rs.getString("Question") + " (A)" + rs.getString("A")
                        + " (B)" + rs.getString("B")+ " (C)" + rs.getString("C")
                        + " (D)" + rs.getString("D")+ " Ans " + rs.getString("Ans"));
                 */
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            //�إ�Server
            SSocket = new ServerSocket(port);
            System.out.println("Server created.");
            System.out.println("waiting for client to connect...");

            players = 1;

            //����client�s�u
            while (true) {
                socket = SSocket.accept();
                System.out.println("connected from Client " +
                        socket.getInetAddress().getHostAddress());

                DataOutputStream outstream = new DataOutputStream(socket.getOutputStream());
                ht.put(socket, outstream);
                Thread thread = new Thread(new ServerThread(socket, ht));
                thread.start();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        port = 5678;
        questions_amount = 5;
        Server ServerStart = new Server();
    }
}

