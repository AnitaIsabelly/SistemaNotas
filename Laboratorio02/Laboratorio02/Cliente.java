import java.net.*;
import java.io.*;
import java.util.Scanner;

public class Cliente{
	private static DataOutputStream dataOutputStream = null;
    private static DataInputStream dataInputStream = null;
	public static void main(String args[]){
		String host = "127.0.0.1";
		int port = 32000;
		int bytes = 0;
		
		try (Socket socket = new Socket(host, port)){
			Scanner scan = new Scanner(System.in);
      		System.out.println("Insira o nome do arquivo: ");
     		String nomeArq = scan.nextLine();
			File arq = new File(nomeArq);
			dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
			FileInputStream fileInputStream = new FileInputStream(arq);
			dataOutputStream.writeLong(arq.length());  
			byte[] buffer = new byte[4*1024];
			while ((bytes=fileInputStream.read(buffer))!=-1){
				dataOutputStream.write(buffer,0,bytes);
				dataOutputStream.flush();
			}
			fileInputStream.close();
			dataInputStream.close();
            dataInputStream.close();
		} catch (IOException e){
			e.printStackTrace();
		}
	}
}