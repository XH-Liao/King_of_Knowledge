import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class Client extends JPanel implements Runnable {
    JFrame frame;
    //boolean start;          //�{�b���A�O playing or not
    boolean selected;       //�������a�O�_�w�@��
    int q_now;              //�{�b�i����n�D
    int times;              //����Server�s�������2����A�i�J�U�@�D
    int scoreP1, scoreP2;     //A�BB������

    Socket socket;
    static String servername;
    static int port;
    static String user;
    DataOutputStream outstream;
    DataInputStream instream;

    String[][] QA;              //����Server�ǰe���D��
    String player;              //player ID (��Server�ǰe)
    String msg;                 //Server�s�������

    //����start-----------------------------------------------------------------------
    private final JLabel labelQ, labelA, labelMsgP1, labelMsgP2, labelT, labelP1, labelP2;
    //private final JTextField textField;
    private final JButton buttonA, buttonB, buttonC, buttonD;
    Timer timer;
    int t = 10;   //�˼�10��
    //����end-------------------------------------------------------------------------

    //�I��(A/B/C/D)��A�P�_��/���A�ǰe���Ƶ�Server
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
            //�ǰe��server�GP1���D���ﶵ
            try {
                labelA.setText("Ans (" + QA[q_now][5] + ")");
                labelA.setForeground(Color.white);
                Font myFont6 = new Font("�L�n������", Font.BOLD, 13);
                labelA.setFont(myFont6);

                if (choice.equals(QA[q_now][5])) {
                	                	
                	String file = "button.wav";
                    music musicObject = new music();
                    musicObject.playMusic(file);
                    
                    labelMsgP1.setText("����");
                    labelMsgP1.setForeground(Color.white);
                    Font myFont1 = new Font("�L�n������", Font.BOLD, 20);
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
                    
                    labelMsgP1.setText("����");
                    labelMsgP1.setForeground(Color.white);
                    Font myFont1 = new Font("�L�n������", Font.BOLD, 20);
                    labelMsgP1.setFont(myFont1);
                }
                outstream.writeUTF(player + "'s choice: " + choice);
            } catch (Exception f) {
                f.printStackTrace();
            }
        }
    }

    //�˼ƭp��
    ActionListener taskPerformer = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (t >= 0) {
                labelT.setText("<html>�˼�<br> " + t+"</html>");
                t--;
            } else {
                if (selected == false) {
                    buttonA.setEnabled(false);
                    buttonB.setEnabled(false);
                    buttonC.setEnabled(false);
                    buttonD.setEnabled(false);
                    labelMsgP1.setText("No answer");
                    labelMsgP1.setForeground(Color.white);
                    Font myFont1 = new Font("�L�n������", Font.BOLD, 13);
                    labelMsgP1.setFont(myFont1);

                    labelA.setText("Ans (" + QA[q_now][5] + ")");
                    labelA.setForeground(Color.white);
                    Font myFont2 = new Font("�L�n������", Font.BOLD, 13);
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
        //super("���Ѥ�");
        super.setLayout(null);
        //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.frame=frame;
        this.socket = socket;
        this.outstream = outstream;
        this.instream = instream;
        this.QA = QA;
        this.player = player;
        //����___start

        labelT = new JLabel("�˼�");
        labelT.setBounds(182, 30, 100, 40);
        labelT.setForeground(Color.black);
        Font myFont1 = new Font("�L�n������", Font.BOLD, 18);
        labelT.setFont(myFont1);
        add(labelT);

        labelP1 = new JLabel("<html>P1<br>" + scoreP1 + "</html>");
        labelP1.setBounds(20, 20, 80, 60);
        labelP1.setForeground(Color.white);
        Font myFont3 = new Font("�L�n������", Font.BOLD, 20);
        labelP1.setFont(myFont3);
        add(labelP1);

        labelP2 = new JLabel("<html>P2<br>" + scoreP2 + "</html>");
        labelP2.setBounds(350, 20, 80, 60);
        labelP2.setForeground(Color.white);
        Font myFont4 = new Font("�L�n������", Font.BOLD, 20);
        labelP2.setFont(myFont4);
        add(labelP2);

        labelQ = new JLabel("�D��");
        labelQ.setBounds(50, 85, 300, 140);
        labelQ.setOpaque(true);
        labelQ.setBackground(Color.white);
        labelQ.setForeground(Color.black);
        Font myFont7 = new Font("�L�n������", Font.BOLD, 15);
        labelQ.setFont(myFont7);
        add(labelQ);

        labelA = new JLabel("");
        labelA.setBounds(180, 260, 50, 20);
        add(labelA);

        /*
        labelMsg = new JLabel();    //Msg: �@������...
        labelMsg.setBounds(175, 270, 200, 20);
        add(labelMsg);
         */

        labelMsgP1 = new JLabel();    //Msg: P1 ����/�����B�@������
        labelMsgP1.setBounds(10, 260, 80, 20);
        add(labelMsgP1);

        labelMsgP2 = new JLabel("",SwingConstants.RIGHT);    //Msg: P2 ����/�����B�@������
        labelMsgP2.setBounds(300, 260, 80, 20);
        labelMsgP2.setForeground(Color.white);
        Font myFont5 = new Font("�L�n������", Font.BOLD, 20);
        labelMsgP2.setFont(myFont5);
        add(labelMsgP2);



        buttonA = new JButton("(A)");
        buttonA.setBounds(50, 280, 300, 63);
        buttonA.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //�@������A�A�i�J�U�D�e�A���i�A���@��
                after_selected("A");
            }
        });
        add(buttonA);

        buttonB = new JButton("(B)");
        buttonB.setBounds(50, 350, 300, 63);
        buttonB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //�@������A�A�i�J�U�D�e�A���i�A���@��
                after_selected("B");
            }
        });
        add(buttonB);

        buttonC = new JButton("(C)");
        buttonC.setBounds(50, 420, 300, 63);
        buttonC.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //�@������A�A�i�J�U�D�e�A���i�A���@��
                //�@������A�A�i�J�U�D�e�A���i�A���@��
                after_selected("C");
            }
        });
        add(buttonC);

        buttonD = new JButton("(D)");
        buttonD.setBounds(50, 490, 300, 63);
        buttonD.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //�@������A�A�i�J�U�D�e�A���i�A���@��
                after_selected("D");
            }
        });
        add(buttonD);

        timer = new Timer(1000, taskPerformer);
        //����___end

        //Start �Ѽƪ�l�]�w
        q_now = 0;
        selected = false;
        t = 15;
        timer.start();
        scoreP1 = 0;
        scoreP2 = 0;
        labelMsgP1.setText("");
        labelMsgP2.setText("");

        //��s��ܪ��D�ءB�ﶵ
        labelQ.setText("<html>" + QA[q_now][0] + "</html>");

        buttonA.setText("<html>(A) " + QA[q_now][1] + "</html>");
        Font myFont8 = new Font("�L�n������", Font.BOLD, 12);
        buttonA.setFont(myFont8);
        buttonA.setBackground(Color.white);
        buttonA.setFocusPainted(false);
        buttonA.setBorder(BorderFactory.createMatteBorder(2,2,2,2,Color.black));
        buttonB.setText("<html>(B) " + QA[q_now][2] + "</html>");
        Font myFont9 = new Font("�L�n������", Font.BOLD, 12);
        buttonB.setFont(myFont9);
        buttonB.setBackground(Color.white);
        buttonB.setFocusPainted(false);
        buttonB.setBorder(BorderFactory.createMatteBorder(2,2,2,2,Color.black));
        buttonC.setText("<html>(C) " + QA[q_now][3] + "</html>");
        Font myFont10 = new Font("�L�n������", Font.BOLD, 12);
        buttonC.setFont(myFont10);
        buttonC.setBackground(Color.white);
        buttonC.setFocusPainted(false);
        buttonC.setBorder(BorderFactory.createMatteBorder(2,2,2,2,Color.black));
        buttonD.setText("<html>(D) " + QA[q_now][4] + "</html>");
        Font myFont11 = new Font("�L�n������", Font.BOLD, 12);
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
                msg = instream.readUTF();               //����server�s�����T��
                System.out.println(msg);

                //��sP2��score
                int choice_index = msg.indexOf("choice");
                if (choice_index!=-1 && !msg.substring(0, choice_index - 3).equals(player)) {
                    String choice_P2 = msg.substring(choice_index + 8);
                    if(choice_P2.equals(QA[q_now][5])){     //����
                     
                        
                        labelMsgP2.setText("����");
                        labelMsgP2.setForeground(Color.white);
                        Font myFont1 = new Font("�L�n������", Font.BOLD, 20);
                        labelMsgP2.setFont(myFont1);
                        
                        if(q_now != 4) {
                        	scoreP2 += (10*t);
                        }
                        else if (q_now == 4) {
                        	scoreP2 += (10*t)*2;
                        }
                        labelP2.setText("<html>P2<br>" + scoreP2 + "</html>");
                    }else{
                        labelMsgP2.setText("����");
                        labelMsgP2.setForeground(Color.white);
                        Font myFont1 = new Font("�L�n������", Font.BOLD, 20);
                        labelMsgP2.setFont(myFont1);
                    }
                }

                times++;                                //�C����@�� => ++
                if (times == 2) {                       //����2�� => ��H�������s�F => Start or next Question
                    times = 0;

                    //��H�ҧ@����A�Ytimer�٦brunning�A�Nstop��
                    if (timer.isRunning()) {
                        timer.stop();
                    }

                    //next Question
                    Thread.sleep(1000);                 //���Ȱ��@��A�ݥ��D���G
                    q_now++;
                    if (q_now >= StartEndPanel.questions_amount) {                       //End (�Ҧ��D�ا@������)
                        socket.close();
                        setVisible(false);
                        StartEndPanel endPanel = new StartEndPanel(frame, scoreP1, scoreP2);
                        endPanel.setSize(400, 600);
                        endPanel.setVisible(true);

                        frame.remove(Client.this);
                        frame.add(endPanel);

                        return;
                    }

                    //����i��
                    t = 15;
                    timer.start();

                    //�ﶵ���s��_�i��
                    selected = false;
                    buttonA.setEnabled(true);
                    buttonB.setEnabled(true);
                    buttonC.setEnabled(true);
                    buttonD.setEnabled(true);
                    labelMsgP1.setText("");
                    labelMsgP2.setText("");
                    labelA.setText("");

                    //��s��ܪ��D�ءB�ﶵ
                    labelQ.setText("<html>" + QA[q_now][0] + "</html>");
                    buttonA.setText("<html>(A) " + QA[q_now][1] + "</html>");
                    Font myFont1 = new Font("�L�n������", Font.BOLD, 12);
                    buttonA.setFont(myFont1);
                    buttonB.setText("<html>(B) " + QA[q_now][2] + "</html>");
                    Font myFont2 = new Font("�L�n������", Font.BOLD, 12);
                    buttonB.setFont(myFont2);
                    buttonC.setText("<html>(C) " + QA[q_now][3] + "</html>");
                    Font myFont3 = new Font("�L�n������", Font.BOLD, 12);
                    buttonC.setFont(myFont3);
                    buttonD.setText("<html>(D) " + QA[q_now][4] + "</html>");
                    Font myFont4 = new Font("�L�n������", Font.BOLD, 12);
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

        //test.jpg�O���չϤ��A�PDemo.java��b�P�@�ؿ��U
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
