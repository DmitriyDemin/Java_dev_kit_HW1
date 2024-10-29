package own;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class ClientGUI extends JFrame {

    private static final int WINDOW_WIDTH = 400;
    private static final int WINDOW_HEIGHT = 300;
    private static final String WINDOW_TITLE = "Чат-клиент";
    private static final String LOGIN_BTN_TEXT = "Enter chat";
    private static final String SENDMSG_BTN_TEXT = "Send >>";


    private String serverIp = "127.0.0.1";
    private String serverSocket = "12345";
    private String login = "user" + new Random().nextInt(100,1000);
    private String password = "********";
    private DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");



    public String servStatusText = "defalt";

    private static int counter;
    private int WINDOW_POSX = 150 + counter * (WINDOW_WIDTH + 50);



    private JTextArea chatConsole = new JTextArea();
    private JTextField serverIpTF = new JTextField(serverIp);
    private JTextField serverSocketTF = new JTextField(serverSocket);
    private JTextField loginTF = new JTextField(login);
    private JTextField passwordTF = new JTextField(password);
    private JButton loginBtn = new JButton(LOGIN_BTN_TEXT);
    private JTextField messageTF = new JTextField();
    private JButton sendMsgBtn = new JButton(SENDMSG_BTN_TEXT);
    private JLabel statusServer = new JLabel(servStatusText);



    private ServerGUI serv;

    private JPanel loginPnl = new JPanel(new GridLayout(2 , 3));

    public ClientGUI(ServerGUI serverGUI) {
        this.serv = serverGUI;
        counter ++;

        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocation(WINDOW_POSX, 500);
        setTitle(WINDOW_TITLE);
        setResizable(false);

        chatConsole.addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {
                if (!serv.isServerWork()){
                    loginPnl.setVisible(true);
                }
            }
        });


        JPanel messagePnl = new JPanel(new BorderLayout());

        loginBtn.addActionListener(e-> {
            if (loginTF.getText().isEmpty()) {
                loginTF.requestFocus(true);
            } else if (passwordTF.getText().isEmpty()) {
                passwordTF.requestFocus(true);
            } else {
                connectToServer();
                messageTF.requestFocus(true);
            }
        });


        sendMsgBtn.addActionListener(e -> {
            if (isMsgValid()) sendMsg();
        });


        messageTF.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && isMsgValid()) {
                    sendMsg();
                }
            }
        });



        loginPnl.add(serverIpTF);
        loginPnl.add(serverSocketTF);
        loginPnl.add(statusServer);//не обновляется?
        loginPnl.add(loginTF);
        loginPnl.add(passwordTF);
        loginPnl.add(loginBtn);
        add(loginPnl, BorderLayout.NORTH);



        chatConsole.setEditable(false);

        chatConsole.setLineWrap(true);
        chatConsole.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(chatConsole);

        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        getContentPane().add(scrollPane, BorderLayout.CENTER);


        messagePnl.add(messageTF, BorderLayout.CENTER);
        messagePnl.add(sendMsgBtn, BorderLayout.EAST);
        add(messagePnl, BorderLayout.SOUTH);

        setVisible(true);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                disconnectFromServer();
                setVisible(false);
            }
        });
    }

    private boolean disconnectFromServer() {
        serv.removeUser(this);
        return true;
    }



    public boolean connectToServer(){
        login = loginTF.getText();
        password = passwordTF.getText();
        if (serv.isServerWork()){
            serv.addUser(this);
            loginPnl.setVisible(false);
            return true;
        } else {
            consolePrintMsg("Нет соединения с сервером. Попробуйте ещё раз.");
        }
        return false;
    }


    public void consolePrintMsg(String sysMsgText){
        chatConsole.append("(" + timeFormat.format(new Date()) + ") " + sysMsgText + "\n");
    }

    private boolean isMsgValid(){
        String message = messageTF.getText();
        return  (!message.isEmpty() && message.length() < 255);
    }

    public void sendMsg(){
        serv.getUserMessage(this, messageTF.getText());
        messageTF.setText("");
    }

    public String getLogin() {
        return login;
    }

    public void setServStatusText(String servStatusText) {
        this.servStatusText = servStatusText;
    }


}
