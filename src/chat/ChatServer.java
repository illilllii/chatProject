package chat;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Vector;

import protocol.Chat;

public class ChatServer {
	
	private static final String TAG = "ChatServer : ";
	private ServerSocket serverSocket;
	private Vector<ClientInfo> vc; // ����� Ŭ���̾�Ʈ Ŭ����(����)�� ��� �÷���
	private FileWriter file = null;
	private LocalDateTime now;
	
	public ChatServer() {
		try {
			vc = new Vector<>();
			serverSocket = new ServerSocket(10000);
			
			file = new FileWriter("Dialouge.txt", true);
			System.out.println(TAG + "Ŭ���̾�Ʈ ���� �����...");
			// ���ξ������� ����
			while(true) {
				Socket socket = serverSocket.accept(); // Ŭ���̾�Ʈ ���� ���
				ClientInfo clientInfo = new ClientInfo(socket);
				System.out.println("��û ����...");
				clientInfo.start();
				vc.add(clientInfo);
				
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	class ClientInfo extends Thread {
		
		Socket socket;
		BufferedReader reader;
		PrintWriter writer; // BufferedWriter�� �ٸ� ���� �������� �Լ��� ����
		
		public ClientInfo(Socket socket) {
			this.socket = socket;
			try {
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				writer = new PrintWriter(socket.getOutputStream());
			} catch (Exception e) {
				System.out.println("���� ���� ���� : "+e.getMessage());
			}
		}
		
		// ���� : Ŭ���̾�Ʈ�� ���� ���� �޽����� ��� Ŭ���̾�Ʈ���� ������
		@Override
		public void run() {
			String input = null;
			String inputData = null;
			String fileInputData = null;
			try {
				while ((input = reader.readLine()) != null) {
					String gubun[] = input.split(":");
					now = LocalDateTime.now();
					inputData = now.format(DateTimeFormatter.ofPattern("a h:mm ")) + input;
					fileInputData = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd a h:mm ")) + input;
					if(input.equals("����!!!!!")) {
						file.close();
					}
					if(gubun[0].equals(Chat.ALL)) {
						for (int i = 0; i < vc.size(); i++) {
							if (vc.get(i) != this) {
								
								vc.get(i).setName("["+ Integer.toString(i+1) + "�� Ŭ���̾�Ʈ]");
								vc.get(i).writer.println(inputData);
								vc.get(i).writer.flush();
								
								file.append(vc.get(i).getName() + fileInputData);
								file.append("\r\n");
							}						
						}
					}
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	public static void main(String[] args) {
		new ChatServer();
		
	}
	
}
