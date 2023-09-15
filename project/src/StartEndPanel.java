import javax.swing.*;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.FontUIResource;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.awt.*;
import java.util.Enumeration;

public class StartEndPanel extends JPanel {
    private JFrame frame;
    private final JLabel label, labelP1, labelP2;
    private final JButton button_start;

    Socket socket;
    static String servername;
    static int port;
    DataOutputStream outstream;
    DataInputStream instream;
    String[][] QA;              //接收Server傳送的題目

    static int questions_amount = 5;

    public StartEndPanel(JFrame frame, int scoreP1, int scoreP2) {
        //super("知識王");
        super.setLayout(null);
        //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.frame=frame;

        label = new JLabel();

        label.setBounds(150, 42, 100, 200);
        add(label);

        labelP1 = new JLabel();
        labelP1.setBounds(20, 30, 60, 200);
        add(labelP1);

        labelP2 = new JLabel();
        labelP2.setBounds(350, 30, 60, 200);
        add(labelP2);

        button_start = new JButton();
        if(scoreP1<0 || scoreP2<0){
            label.setText("知識王");

            label.setForeground(Color.white);
            Font myFont1 = new Font("微軟正黑體", Font.BOLD, 30);
            label.setFont(myFont1);

            button_start.setText("Start");
            Font myFont2 = new Font("微軟正黑體", Font.BOLD, 20);
            button_start.setFont(myFont2);
            button_start.setBackground(Color.YELLOW);
            button_start.setFocusPainted(false);
            button_start.setBorder(BorderFactory.createMatteBorder(4,5,3,3,Color.white));


        }else{
            if (scoreP1 > scoreP2){
                label.setText("挑戰成功");
                label.setForeground(Color.white);
                Font myFont3 = new Font("微軟正黑體", Font.BOLD, 25);
                label.setFont(myFont3);}
            else if (scoreP1 == scoreP2){
                label.setText("勢均力敵");
                label.setForeground(Color.white);
                Font myFont3 = new Font("微軟正黑體", Font.BOLD, 25);
                label.setFont(myFont3);
            }
            else {
                label.setText("挑戰失敗");
                label.setForeground(Color.white);
                Font myFont3 = new Font("微軟正黑體", Font.BOLD, 25);
                label.setFont(myFont3);
            }
            labelP1.setText("<html>P1<br>" + scoreP1 + "</html>");
            labelP1.setForeground(Color.white);
            Font myFont3 = new Font("微軟正黑體", Font.BOLD, 25);
            labelP1.setFont(myFont3);

            labelP2.setText("<html>P2<br>" + scoreP2 + "</html>");
            labelP2.setForeground(Color.white);
            Font myFont4 = new Font("微軟正黑體", Font.BOLD, 25);
            labelP2.setFont(myFont4);

            button_start.setText("Restart");
            Font myFont2 = new Font("微軟正黑體", Font.BOLD, 25);
            button_start.setFont(myFont2);
            button_start.setBackground(Color.orange);
            button_start.setFocusPainted(false);
            button_start.setBorder(BorderFactory.createMatteBorder(4,5,3,3,Color.white));
        }

        button_start.setBounds(100, 300, 200, 55);
        button_start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {            	
            	String file = "button.wav";
                music musicObject = new music();
                musicObject.playMusic(file);
                
                
                try {
                    servername = "localhost";
                    port = 5678;
                    socket = new Socket(InetAddress.getByName(servername), port);
                    outstream = new DataOutputStream(socket.getOutputStream());
                    instream = new DataInputStream(socket.getInputStream());

                    outstream.writeUTF("ready to start");
                    String player = instream.readUTF();                     //接收player ID
                    System.out.println(player);

                    //讀入Server傳送的題目
                    QA = new String[questions_amount][6];
                    for (int i = 0; i < questions_amount; i++) {
                        for (int j = 0; j < 6; j++) {
                            QA[i][j] = instream.readUTF();
                            System.out.println(QA[i][j]);
                        }
                    }

                    setVisible(false);                          //隱藏當前面板
                    Client client1 = new Client(frame, socket, outstream, instream, QA, player);      //建立主面板
                    client1.setBounds(0, 0, 400, 600);
                    client1.setVisible(true);

                    frame.remove(StartEndPanel.this);
                    frame.add(client1);
                } catch (Exception f) {
                    f.printStackTrace();
                }
            }
        });
        add(button_start);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("知識王");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 600);
        frame.setLocation(300,150);
        frame.setResizable(false);

        setUIFont(new FontUIResource("微軟正黑體",0,14)); //所有字體大小

        StartEndPanel endFrame = new StartEndPanel(frame, -1, -1);
        endFrame.setBounds(0, 0, 400, 600);

        frame.add(endFrame);
        frame.setVisible(true);
        
        
        String filepath = "bgm.wav";
        music musicObject = new music();
        musicObject.playMusic_bgm(filepath);
        

    }

    public static void setUIFont (FontUIResource fui){
        Enumeration keys=UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key=keys.nextElement();
            Object value=UIManager.get(key);
            if (value != null && value instanceof FontUIResource) {
                UIManager.put(key, fui);
            }
        }
    }
    public void paintComponent(Graphics g)
    {
        int x=0,y=0;
        java.net.URL imgURL=getClass().getResource("background.jpg");

        ImageIcon icon=new ImageIcon(imgURL);
        g.drawImage(icon.getImage(),x,y,getSize().width,getSize().height,this);
        while(true)
        {
            g.drawImage(icon.getImage(),x,y,this);
            if(x>getSize().width && y>getSize().height)
                break;
            if(x>getSize().width)
            {
                x=0;
                y+=icon.getIconHeight();
            }
            else
                x+=icon.getIconWidth();
        }
    }
}


