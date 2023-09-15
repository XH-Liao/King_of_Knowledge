import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class Client extends JPanel implements Runnable {
    JFrame frame;
    //boolean start;          //現在狀態是 playing or not
    boolean selected;       //偵測玩家是否已作答
    int q_now;              //現在進行到第n題
    int times;              //收到Server廣播的資料2次後，進入下一題
    int scoreP1, scoreP2;     //A、B的分數

    Socket socket;
    static String servername;
    static int port;
    static String user;
    DataOutputStream outstream;
    DataInputStream instream;

    String[][] QA;              //接收Server傳送的題目
    String player;              //player ID (由Server傳送)
    String msg;                 //Server廣播的資料

    //介面start-----------------------------------------------------------------------
    private final JLabel labelQ, labelA, labelMsgP1, labelMsgP2, labelT, labelP1, labelP2;
    //private final JTextField textField;
    private final JButton buttonA, buttonB, buttonC, buttonD;
    Timer timer;
    int t = 10;   //倒數10秒
    //介面end-------------------------------------------------------------------------

    //點選(A/B/C/D)後，判斷對/錯，傳送分數給Server
    private void after_selected(String choice) {
        if (selected == false) {
            selected = true;
            if(choice == "A") {
            	buttonB.setEnabled(false);
            	buttonC.setEnabled(false);
            	buttonD.setEnabled(false);
            }
            else if(choice == "B") {
            	buttonA.setEnabled(false);
            	buttonC.setEnabled(false);
            	buttonD.setEnabled(false);
            }
            else if(choice == "C") {
            	buttonA.setEnabled(false);
            	buttonB.setEnabled(false);
            	buttonD.setEnabled(false);
            }
            else if(choice == "D") {
            	buttonA.setEnabled(false);
            	buttonB.setEnabled(false);
            	buttonC.setEnabled(false);
            }
            //傳送給server：P1答題的選項
            try {
                labelA.setText("Ans (" + QA[q_now][5] + ")");
                labelA.setForeground(Color.white);
                Font myFont6 = new Font("微軟正黑體", Font.BOLD, 13);
                labelA.setFont(myFont6);

                if (choice.equals(QA[q_now][5])) {
                	                	
                	String file = "button.wav";
                    music musicObject = new music();
                    musicObject.playMusic(file);
                    
                    labelMsgP1.setText("答對");
                    labelMsgP1.setForeground(Color.white);
                    Font myFont1 = new Font("微軟正黑體", Font.BOLD, 20);
                    labelMsgP1.setFont(myFont1);
                    
                    if(q_now != 4) {
                    	scoreP1 += (10*t);
                    }
                    else if (q_now == 4) {
                    	scoreP1 += (10*t)*2;
                    }
                    
                    labelP1.setText("<html>P1<br>" + scoreP1 + "</html>");
                } else {               	
                	String file = "die.wav";
                    music musicObject = new music();
                    musicObject.playMusic(file);
                    
                    labelMsgP1.setText("答錯");
                    labelMsgP1.setForeground(Color.white);
                    Font myFont1 = new Font("微軟正黑體", Font.BOLD, 20);
                    labelMsgP1.setFont(myFont1);
                }
                outstream.writeUTF(player + "'s choice: " + choice);
            } catch (Exception f) {
                f.printStackTrace();
            }
        }
    }

    //倒數計時
    ActionListener taskPerformer = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (t >= 0) {
                labelT.setText("<html>倒數<br> " + t+"</html>");
                t--;
            } else {
                if (selected == false) {
                    buttonA.setEnabled(false);
                    buttonB.setEnabled(false);
                    buttonC.setEnabled(false);
                    buttonD.setEnabled(false);
                    labelMsgP1.setText("No answer");
                    labelMsgP1.setForeground(Color.white);
                    Font myFont1 = new Font("微軟正黑體", Font.BOLD, 13);
                    labelMsgP1.setFont(myFont1);

                    labelA.setText("Ans (" + QA[q_now][5] + ")");
                    labelA.setForeground(Color.white);
                    Font myFont2 = new Font("微軟正黑體", Font.BOLD, 13);
                    labelA.setFont(myFont2);
                    try {
                        outstream.writeUTF(player + "'s choice: No answer");
                    } catch (Exception f) {
                        f.printStackTrace();
                    }
                }
            }
        }
    };

    public Client(JFrame frame, Socket socket, DataOutputStream outstream, DataInputStream instream, String[][] QA, String player) {
        //super("知識王");
        super.setLayout(null);
        //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.frame=frame;
        this.socket = socket;
        this.outstream = outstream;
        this.instream = instream;
        this.QA = QA;
        this.player = player;
        //介面___start

        labelT = new JLabel("倒數");
        labelT.setBounds(182, 30, 100, 40);
        labelT.setForeground(Color.black);
        Font myFont1 = new Font("微軟正黑體", Font.BOLD, 18);
        labelT.setFont(myFont1);
        add(labelT);

        labelP1 = new JLabel("<html>P1<br>" + scoreP1 + "</html>");
        labelP1.setBounds(20, 20, 80, 60);
        labelP1.setForeground(Color.white);
        Font myFont3 = new Font("微軟正黑體", Font.BOLD, 20);
        labelP1.setFont(myFont3);
        add(labelP1);

        labelP2 = new JLabel("<html>P2<br>" + scoreP2 + "</html>");
        labelP2.setBounds(350, 20, 80, 60);
        labelP2.setForeground(Color.white);
        Font myFont4 = new Font("微軟正黑體", Font.BOLD, 20);
        labelP2.setFont(myFont4);
        add(labelP2);

        labelQ = new JLabel("題目");
        labelQ.setBounds(50, 85, 300, 140);
        labelQ.setOpaque(true);
        labelQ.setBackground(Color.white);
        labelQ.setForeground(Color.black);
        Font myFont7 = new Font("微軟正黑體", Font.BOLD, 15);
        labelQ.setFont(myFont7);
        add(labelQ);

        labelA = new JLabel("");
        labelA.setBounds(180, 260, 50, 20);
        add(labelA);

        /*
        labelMsg = new JLabel();    //Msg: 作答完畢...
        labelMsg.setBounds(175, 270, 200, 20);
        add(labelMsg);
         */

        labelMsgP1 = new JLabel();    //Msg: P1 答對/答錯、作答完畢
        labelMsgP1.setBounds(10, 260, 80, 20);
        add(labelMsgP1);

        labelMsgP2 = new JLabel("",SwingConstants.RIGHT);    //Msg: P2 答對/答錯、作答完畢
        labelMsgP2.setBounds(300, 260, 80, 20);
        labelMsgP2.setForeground(Color.white);
        Font myFont5 = new Font("微軟正黑體", Font.BOLD, 20);
        labelMsgP2.setFont(myFont5);
        add(labelMsgP2);



        buttonA = new JButton("(A)");
        buttonA.setBounds(50, 280, 300, 63);
        buttonA.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //作答之後，再進入下題前，不可再次作答
                after_selected("A");
            }
        });
        add(buttonA);

        buttonB = new JButton("(B)");
        buttonB.setBounds(50, 350, 300, 63);
        buttonB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //作答之後，再進入下題前，不可再次作答
                after_selected("B");
            }
        });
        add(buttonB);

        buttonC = new JButton("(C)");
        buttonC.setBounds(50, 420, 300, 63);
        buttonC.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //作答之後，再進入下題前，不可再次作答
                //作答之後，再進入下題前，不可再次作答
                after_selected("C");
            }
        });
        add(buttonC);

        buttonD = new JButton("(D)");
        buttonD.setBounds(50, 490, 300, 63);
        buttonD.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //作答之後，再進入下題前，不可再次作答
                after_selected("D");
            }
        });
        add(buttonD);

        timer = new Timer(1000, taskPerformer);
        //介面___end

        //Start 參數初始設定
        q_now = 0;
        selected = false;
        t = 15;
        timer.start();
        scoreP1 = 0;
        scoreP2 = 0;
        labelMsgP1.setText("");
        labelMsgP2.setText("");

        //更新顯示的題目、選項
        labelQ.setText("<html>" + QA[q_now][0] + "</html>");

        buttonA.setText("<html>(A) " + QA[q_now][1] + "</html>");
        Font myFont8 = new Font("微軟正黑體", Font.BOLD, 12);
        buttonA.setFont(myFont8);
        buttonA.setBackground(Color.white);
        buttonA.setFocusPainted(false);
        buttonA.setBorder(BorderFactory.createMatteBorder(2,2,2,2,Color.black));
        buttonB.setText("<html>(B) " + QA[q_now][2] + "</html>");
        Font myFont9 = new Font("微軟正黑體", Font.BOLD, 12);
        buttonB.setFont(myFont9);
        buttonB.setBackground(Color.white);
        buttonB.setFocusPainted(false);
        buttonB.setBorder(BorderFactory.createMatteBorder(2,2,2,2,Color.black));
        buttonC.setText("<html>(C) " + QA[q_now][3] + "</html>");
        Font myFont10 = new Font("微軟正黑體", Font.BOLD, 12);
        buttonC.setFont(myFont10);
        buttonC.setBackground(Color.white);
        buttonC.setFocusPainted(false);
        buttonC.setBorder(BorderFactory.createMatteBorder(2,2,2,2,Color.black));
        buttonD.setText("<html>(D) " + QA[q_now][4] + "</html>");
        Font myFont11 = new Font("微軟正黑體", Font.BOLD, 12);
        buttonD.setFont(myFont11);
        buttonD.setBackground(Color.white);
        buttonD.setFocusPainted(false);
        buttonD.setBorder(BorderFactory.createMatteBorder(2,2,2,2,Color.black));

        try {
            this.outstream = new DataOutputStream(outstream);
            this.instream = new DataInputStream(instream);

            new Thread(this).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (true) {
            try {
                msg = instream.readUTF();               //接收server廣播的訊息
                System.out.println(msg);

                //更新P2的score
                int choice_index = msg.indexOf("choice");
                if (choice_index!=-1 && !msg.substring(0, choice_index - 3).equals(player)) {
                    String choice_P2 = msg.substring(choice_index + 8);
                    if(choice_P2.equals(QA[q_now][5])){     //答對
                     
                        
                        labelMsgP2.setText("答對");
                        labelMsgP2.setForeground(Color.white);
                        Font myFont1 = new Font("微軟正黑體", Font.BOLD, 20);
                        labelMsgP2.setFont(myFont1);
                        
                        if(q_now != 4) {
                        	scoreP2 += (10*t);
                        }
                        else if (q_now == 4) {
                        	scoreP2 += (10*t)*2;
                        }
                        labelP2.setText("<html>P2<br>" + scoreP2 + "</html>");
                    }else{
                        labelMsgP2.setText("答錯");
                        labelMsgP2.setForeground(Color.white);
                        Font myFont1 = new Font("微軟正黑體", Font.BOLD, 20);
                        labelMsgP2.setFont(myFont1);
                    }
                }

                times++;                                //每收到一次 => ++
                if (times == 2) {                       //收到2次 => 兩人都按按鈕了 => Start or next Question
                    times = 0;

                    //兩人皆作答後，若timer還在running，就stop它
                    if (timer.isRunning()) {
                        timer.stop();
                    }

                    //next Question
                    Thread.sleep(1000);                 //先暫停一秒，看本題結果
                    q_now++;
                    if (q_now >= StartEndPanel.questions_amount) {                       //End (所有題目作答完畢)
                        socket.close();
                        setVisible(false);
                        StartEndPanel endPanel = new StartEndPanel(frame, scoreP1, scoreP2);
                        endPanel.setSize(400, 600);
                        endPanel.setVisible(true);

                        frame.remove(Client.this);
                        frame.add(endPanel);

                        return;
                    }

                    //持續進行
                    t = 15;
                    timer.start();

                    //選項按鈕恢復可按
                    selected = false;
                    buttonA.setEnabled(true);
                    buttonB.setEnabled(true);
                    buttonC.setEnabled(true);
                    buttonD.setEnabled(true);
                    labelMsgP1.setText("");
                    labelMsgP2.setText("");
                    labelA.setText("");

                    //更新顯示的題目、選項
                    labelQ.setText("<html>" + QA[q_now][0] + "</html>");
                    buttonA.setText("<html>(A) " + QA[q_now][1] + "</html>");
                    Font myFont1 = new Font("微軟正黑體", Font.BOLD, 12);
                    buttonA.setFont(myFont1);
                    buttonB.setText("<html>(B) " + QA[q_now][2] + "</html>");
                    Font myFont2 = new Font("微軟正黑體", Font.BOLD, 12);
                    buttonB.setFont(myFont2);
                    buttonC.setText("<html>(C) " + QA[q_now][3] + "</html>");
                    Font myFont3 = new Font("微軟正黑體", Font.BOLD, 12);
                    buttonC.setFont(myFont3);
                    buttonD.setText("<html>(D) " + QA[q_now][4] + "</html>");
                    Font myFont4 = new Font("微軟正黑體", Font.BOLD, 12);
                    buttonD.setFont(myFont4);
                }
            } catch (Exception f) {
                f.printStackTrace();
            }
        }
    }
    public void paintComponent(Graphics g)
    {
        int x=0,y=0;
        java.net.URL imgURL=getClass().getResource("background.jpg");

        //test.jpg是測試圖片，與Demo.java放在同一目錄下
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
