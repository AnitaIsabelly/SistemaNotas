import java.net.*;
import java.io.*;

public class Servidor{
	private static DataOutputStream dataOutputStream = null;
    private static DataInputStream dataInputStream = null;
	public static void main(String args[]){
		ServerSocket server = null;
		try{
			server = new ServerSocket(32000);
			server.setReuseAddress(true);
			while(true){
				Socket client = server.accept();
				System.out.println("Novo cliente conectado "+ client.getInetAddress().getHostAddress());
				ClientHandler clientSock = new ClientHandler(client);
				new Thread(clientSock).start();
			}
		} catch(IOException e){
			e.printStackTrace();
		} finally {
			if (server != null){
				try{
					server.close();
				}catch(IOException e){
					e.printStackTrace();
				}
			}
		}
	}
	private static class ClientHandler implements Runnable{
		private final Socket clientSocket;

		public ClientHandler(Socket socket){
			this.clientSocket = socket;
		}

		@Override
		public void run(){
			int bytes=0, acerto=0, erro=0;
			File gabarito = new File("gabarito.txt");
			
			PrintWriter out = null;
			BufferedReader in = null;
			try{
				OutputStream Output = new FileOutputStream("arquivo.txt");
				out = new PrintWriter(clientSocket.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				dataInputStream = new DataInputStream(clientSocket.getInputStream());
            	dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
				long size = dataInputStream.readLong();
        		byte[] buffer = new byte[4*1024];
        		while (size > 0 && (bytes = dataInputStream.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1) {
            		Output.write(buffer,0,bytes);
            		size -= bytes;
        		}
        		Output.close();
				File arq = new File("arquivo.txt");
				
				BufferedReader correcao = new BufferedReader (new FileReader(arq));
         		BufferedReader compara  = new BufferedReader (new FileReader(gabarito));

				String nome = correcao.readLine();
				String linhaArq = correcao.readLine();
       			String linhaGab = compara.readLine();
        		String questoesArq = linhaArq.replaceAll("[^0-9]", "");
        		String questoesGab = linhaGab.replaceAll("[^0-9]", ""); 

				while((linhaArq != null) && (linhaGab != null)){
          			if(questoesArq.equals(questoesGab)){
            			if(linhaArq.equals(linhaGab)){
              				acerto++;
            			}else{
              				erro++;
            			}            
            			linhaArq = correcao.readLine();
            			linhaGab = compara.readLine();
          			}else{
            			linhaArq = correcao.readLine();
          			}      
        		}
				System.out.println("Nome do aluno: <" + nome + "> \n" + "Acertos: <" + acerto + "> \n" + "Erros: <" + erro + ">\n");
			}catch(IOException e){
				e.printStackTrace();
			} finally{
				try{
					if(out != null){
						out.close();
					}
					if (in != null){
						in.close();
					}
					clientSocket.close();
				}catch (IOException e){
					e.printStackTrace();
				}
			}
		}
	}
}