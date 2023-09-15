import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.sql.*;

public class Server {
    static int players;                    //新增玩家、Start都+1，玩家數 = (players/3)*2 + (player%3 -1)
    static int questions_amount;

    
    /*static String[][] QA = {{"Q1：下列何者不是程式語言？", "C++", "Apple", "C", "Java", "B"},
            {"Q2：8轉為2進位是？", "0111", "1001", "0100", "1000", "D"},
            {"Q3：下列時間複雜度何者最佳？", "O(n^2)", "O(n)", "O(log n)", "O(n log n)", "C"},
            {"Q4：以下何者語言與製作網頁最不相關？", "Chinese", "ASP.NET CORE", "PHP", "HTML", "A"},
            {"Q5：以下何者程式語言最早被開發？", "FORTRAN", "LISP", "C", "Prolog", "A"}};
    */
    static String[][] QA;
    private static ServerSocket SSocket;
    private static int port;
    private Hashtable ht = new Hashtable();
    Socket socket;

    public Server() throws IOException {
        //讀取DB中的題目
        try {
            String driver = "com.mysql.cj.jdbc.Driver";
            //從配置參數中獲取數據庫url
            String url = "jdbc:mysql://localhost:3306/qa";
            //從配置參數中獲取用戶名
            String user = "root";
            //從配置參數中獲取密碼
            String pass = "";

            //註冊驅動
            Class.forName(driver);
            //獲取數據庫連接
            Connection conn = DriverManager.getConnection(url, user, pass);
            //創建Statement對象
            Statement stmt = conn.createStatement();
            //執行查詢
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
            //建立Server
            SSocket = new ServerSocket(port);
            System.out.println("Server created.");
            System.out.println("waiting for client to connect...");

            players = 1;

            //接收client連線
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

