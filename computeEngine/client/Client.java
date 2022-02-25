package client;

import java.net.*;
import java.io.*;
import java.util.Scanner;

//Trabalho desenvolvido por: Giovanna Carreira Marinho

/* Classe do Cliente que, uma vez estabelecida a conexão com o Servidor, 
*  fica em loop até que seja solicitada a saída da aplicação (mensagem Exit). 
*  Dentro do loop é solicitada a mensagem a ser enviada ao Servidor e, analisado 
*  o serviço, é enviada a mensagem ao Servidor. Em seguida, a partir da leitura 
*  da resposta enviada pelo Servidor, o resultado é exibido no console.
*/

public class Client {
	public static void main (String args[]) { //Argumentos> HostServidor PortaServidor
		Socket clientSocket = null;

		try {
			String serverHostName = args[0];
			int serverPort = Integer.parseInt(args[1]);

			clientSocket = new Socket(serverHostName, serverPort); //Instanciando um socket para o cliente na rede e porta passados como argumento

			//Variaveis para funcionamento da comunicação cliente/servidor e da aplicação
			DataInputStream in = new DataInputStream(clientSocket.getInputStream());
			DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
			Scanner scan = new Scanner(System.in);
			String message;
			String[] data;

			menu(serverHostName, serverPort);

			do {
				System.out.println("\n>");
				message = scan.nextLine(); //Leitura do comando
				data = message.split(" "); //Dividindo a string pelos espacos para capturar os dados

                if(data[0].equals("ComputeEncryptFile")) { //Verificando qual servico foi solicitado
                	if(data.length != 2) { //Erro na escrita do comando
                		System.out.println("	Comando invalido, digite >ComputeEncryptFile nomeArquivo.extansao");
                		continue;
                	}

                	System.out.println("	Solicitado o servico ComputeEncryptFile ao servidor");
                    out.writeUTF(message); //Enviando o comando ao servidor

                    String result = in.readUTF(); //Leitura do retorno
					System.out.println("	Resultado do servidor: " + result);
                }
                else if(data[0].equals("ComputeSortFile")) {
                	if(data.length != 2) { //Erro na escrita do comando
                		System.out.println("	Comando invalido, digite >ComputeSortFile nomeArquivo.extansao");
                		continue;
                	}

                	System.out.println("	Solicitado o servico ComputeSortFile ao servidor");
                    out.writeUTF(message); //Enviando o comando ao servidor

                    String result = in.readUTF(); //Leitura do retorno
					System.out.println("	Resultado do servidor: " + result);
                }
                else if(data[0].equals("ComputeFaceDetection")) {
                	if(data.length != 2) { //Erro na escrita do comando
                		System.out.println("	Comando invalido, digite >ComputeFaceDetection nomeArquivo.extansao");
                		continue;
                	}

                	System.out.println("	Solicitado o servico ComputeFaceDetection ao servidor");
                    out.writeUTF(message); //Enviando o comando ao servidor

                    String result = in.readUTF(); //Leitura do retorno
					System.out.println("	Resultado do servidor: " + result);
                }
                else if(data[0].equals("Exit")) { //Opção de sair
                	out.writeUTF(message);
                	System.out.println("	Voce escolheu sair da aplicacao!");
                }
                else {
                	System.out.println("	Servico invalido");
                }
			
			} while(!message.equals("Exit")); //Saindo do loop ao digitar Exit

		} catch (UnknownHostException e) {
			System.out.println("Socket: " + e.getMessage());
		} catch (EOFException e) {
			System.out.println("EOF: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("Readline: " + e.getMessage());
		} finally { //Fechando a conexao
			if(clientSocket != null) 
				try {
					clientSocket.close();
				} catch (IOException e) {
					System.out.println("Close: " + e.getMessage());
				}
		}
    }

    public static void menu(String serverHostName, int serverPort) {
     	System.out.println("\nTrabalho Computacao Distribuida e Paralela");
		System.out.println("Giovanna Carreira Marinho \n"); 
		System.out.println("Conexao estabelecida com o Servidor | Host: " + serverHostName + " | Porta: " + serverPort);
		System.out.println("Solicite os servicos:");
		System.out.println("	ComputeEncryptFile nomeArquivo.txt");
		System.out.println("	ComputeSortFile nomeArquivo.txt");
		System.out.println("	ComputeFaceDetection nomeArquivo.extansao");
		System.out.println("Exit para sair");
		System.out.println("Resultados salvos na pasta >resources \n");
    }
}
