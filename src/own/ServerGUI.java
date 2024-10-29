package own;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;


public class ServerGUI extends JFrame {
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private int WINDOW_POSX =(screenSize.width - WINDOW_WIDTH) / 2;
    private int WINDOW_POSY = 200;
    private static final int WINDOW_WIDTH = 400;
    private static final int WINDOW_HEIGHT = 300;
    private static final String START_BTN_TEXT = "ПУСК";
    private static final String STOP_BTN_TEXT = "СТОП";
    private static final String WINDOW_TITLE = "Чат-сервер";


    private boolean serverWork;
    private File historyFile = new File("sem1/src/own/log.txt");
    private List<String> msgHistory = new ArrayList<>();
    private List<ClientGUI> userList = new ArrayList<>();

    private DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    private DateFormat dateFormat1 = new SimpleDateFormat("dd.MM.yyyy");


    JButton startBtn = new JButton(START_BTN_TEXT);
    JButton stopBtn = new JButton(STOP_BTN_TEXT);
    JTextArea logConsole = new JTextArea();


    public ServerGUI() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocation(WINDOW_POSX, WINDOW_POSY);
        setTitle(WINDOW_TITLE);
        setResizable(false);
        sendStatusServer();
        JPanel buttonsPnl = new JPanel();

        logConsole.setEditable(false);
        logConsole.setLineWrap(true);
        logConsole.setWrapStyleWord(true);


        JScrollPane scrollPane = new JScrollPane(logConsole);


        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);


        startBtn.addActionListener(e -> {
            if (!isServerWork()){
                serverWork = true;
                consolePrint("Server started successfully.");
                consolePrint("Waiting for clients...");
            }
            else {
                consolePrint("Server already run.");
            }


        });


        stopBtn.addActionListener(e -> {
            if (isServerWork()){
                serverWork = false;
                consolePrint("Server stopped.");
                sendToAll("Сервер завершил свою работу");
                userList.clear();
                saveChatHistory();

            }
        });

        buttonsPnl.setLayout(new GridLayout(1, 2));
        buttonsPnl.add(startBtn);
        buttonsPnl.add(stopBtn);
        add(buttonsPnl, BorderLayout.SOUTH);

        getContentPane().add(scrollPane, BorderLayout.CENTER);


        setVisible(true);
    }



    public boolean isServerWork() {
        return serverWork;
    }



    private void saveChatHistory(){
        if (!historyFile.exists()){
            try {
                historyFile.createNewFile();
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }

        if (historyFile.canWrite()){
            try (FileWriter fw = new FileWriter(historyFile, false)){
                for(String item: msgHistory){
                    fw.write(item + "\n");
                }
            }
            catch (IOException e){
                System.out.println(e.getMessage());
            }
        }
    }



    private List<String> loadChatHistory (){
        if (!historyFile.exists()){
            try {
                historyFile.createNewFile();
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }

        if (historyFile.canRead()){
            try (Scanner scan = new Scanner(historyFile)){
                msgHistory.clear();
                while (scan.hasNext()){
                    msgHistory.add(scan.nextLine());
                }
                return msgHistory;
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
        return null;
    }



    private void consolePrint(String msg){
        logConsole.append(dateFormat.format(new Date()) + ": " + msg + "\n");
    }


    public boolean addUser(ClientGUI user){
        userList.add(user);
        consolePrint("User " + user.getLogin() + " connected to server.");
        sendToCurrentUser(user, "Соединение установлено. Добро пожаловать, " + user.getLogin() + "!");
        sendToCurrentUser(user, "Сегодня " + dateFormat1.format(new Date()) + " г.");
        sendToAll("Пользователь " + user.getLogin() + " вошёл в чат.");
        String history = loadChatHistory().toString().replaceAll("\\[\\]","");
        history = history.replaceAll("[\\[\\]]","");
        history = history.replaceAll(", ", "\n");
        user.consolePrintMsg(
                "История сообщений ============\n"
                + history +
                " =====================================");
        return true;
    }



    public void removeUser(ClientGUI user) {
        if (userList.contains(user)) {
            userList.remove(user);
            consolePrint("User " + user.getLogin() + " disconnected.");
            sendToAll("Пользователь " + user.getLogin() + " покинул чат.");
        }
        else {
            consolePrint("Such login (" + user.getLogin() + ") is absent in user list.");
        }
    }


    private void sendToAll(String msg){
        if (!userList.isEmpty()) {
            for (ClientGUI user : userList) {
                user.consolePrintMsg(msg);
            }
        }
    }



    private void sendToCurrentUser(ClientGUI user, String msg){
         user.consolePrintMsg(msg);
    }



    public void getUserMessage(ClientGUI user, String msgText) {
        consolePrint(user.getLogin() + ": " + msgText);
        sendToAll(user.getLogin() + ": " + msgText);
        msgHistory.add("(" + dateFormat.format(new Date()) + ") " + user.getLogin() + ": " + msgText);
        saveChatHistory();
    }


    public void sendStatusServer(){
        if (!userList.isEmpty()) {
            for (ClientGUI user : userList) {
                if (isServerWork()){
                    user.setServStatusText("Server ON");
                    user.repaint();
                }else user.setServStatusText("Server OFF");
                user.repaint();
            }
        }

    }
}
