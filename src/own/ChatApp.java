package own;

public class ChatApp {

    public static void main(String[] args) {

        ServerGUI server = new ServerGUI();
        ClientGUI client1 = new ClientGUI(server);
        ClientGUI client2 = new ClientGUI(server);



    }
}
