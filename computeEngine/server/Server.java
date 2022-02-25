package server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.math.BigDecimal;
import compute.Compute;

import java.net.*;
import java.io.*;

//Trabalho desenvolvido por: Giovanna Carreira Marinho

/* Classe do Servidor que fica rodando na porta definida durante a 
*  execução e aguarda o estabelecimento de conexões. Para cada conexão 
*  feita, é instanciado um objeto da classe Connection, onde a 
*  implementação RMI é realizada.
*/

public class Server {
    public static void main(String args[]) { //Argumentos> PortaServidor RedeRMI PortaRMI
        try{
            int serverPort = Integer.parseInt(args[0]);
            ServerSocket listenSocket = new ServerSocket(serverPort); //Instanciando um socket de acordo com a PortaServidor
            
            System.out.println("\nRodando na porta " + serverPort);

            while(true) { //Loop para estabelecimento de conexoes
                Socket clientSocket = listenSocket.accept(); //por meio do método accept uma solicitação de conexão é aceita e é instanciado um objeto da classe Socket
                Connection c = new Connection(clientSocket, args); //esse socket é passado como argumento para a classe Connection, além dos argumentos (para o RMI)
            }
        } catch(IOException e) {
            System.out.println("Listen socket:"+e.getMessage());
        }
    }    
}

class Connection extends Thread { //Classe para cada conexao estabelecida com o servidor
    DataInputStream in;
    DataOutputStream out;
    Socket clientSocket;
    String args[];

    public Connection (Socket aClientSocket, String args[]) { //Construtor da classe Connection
        try {
            clientSocket = aClientSocket;
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
            this.args = args;
            this.start(); //execucao do método run (uso de Thread)
        } catch(IOException e) {System.out.println("Connection:"+e.getMessage());}
    }

    public void run(){ //método onde a lógica da aplicação RMI acontece para cada conexao 
        System.out.println("\nConexao estabelecida com o Cliente | Socket " + clientSocket);

        if (System.getSecurityManager() == null) { //Verificacao de seguranca
            System.setSecurityManager(new SecurityManager());
        }
        try {
            String name = "Compute";
            Registry registry = LocateRegistry.getRegistry(args[1],Integer.parseInt(args[2])); //Buscando uma referencia ao rmi.Registry (indicando a rede e a porta, passados por parâmetro durante a execução)
            Compute comp = (Compute) registry.lookup(name); //Buscando uma referencia ao objeto remoto
            String message;
            String[] data;

            do {
                message = in.readUTF(); //Leitura da mensagem enviada pelo cliente
                System.out.println("\nMensagem recebida: " + message);
                data = message.split(" "); //Dividindo a string para capturar os dados

                if(data[0].equals("ComputeEncryptFile")) { //Verificando o servico solicitado
                    EncryptFile task = new EncryptFile(data[1]); //Instanciando um objeto da classe que oferece a tarefa

                    System.out.println("    Enviando um objeto da classe EncryptFile que tem o servico ComputeEncryptFile");
                    Integer result = comp.executeTask(task); //Chamando a execução do método remoto
                    System.out.println("    Recebida a resposta (" + result +") do servico ComputeEncryptFile e enviando ao Cliente");

                    if(result == -3) //Analisando a resposta recebida e enviando o resultado ao cliente
                        out.writeUTF("Erro no algoritmo SHA");
                    else if(result == -2)
                        out.writeUTF("Erro na criacao do resultado na pasta resources");
                    else if(result == -1)
                        out.writeUTF("Erro: arquivo solicitado nao encontrado");
                    else //result == 1
                        out.writeUTF("Arquivo de resultado criado na pasta resources");
                }
                else if(data[0].equals("ComputeSortFile")) {
                    SortFile task = new SortFile(data[1]); //Instanciando um objeto da classe que oferece a tarefa

                    System.out.println("    Enviando um objeto da classe SortFile que tem o servico ComputeSortFile");
                    Integer result = comp.executeTask(task); //Chamando a execução do método remoto
                    System.out.println("    Recebida a resposta (" + result +") do servico ComputeSortFile e enviando ao Cliente");

                    if(result == -2)  //Analisando a resposta recebida e enviando o resultado ao cliente
                        out.writeUTF("Erro na criacao do resultado na pasta resources");
                    else if(result == -1)
                        out.writeUTF("Erro: arquivo solicitado nao encontrado");
                    else //result == 1
                        out.writeUTF("Arquivo de resultado criado na pasta resources");
                }
                else if(data[0].equals("ComputeFaceDetection")) {
                    FaceDetection task = new FaceDetection(data[1]); //Instanciando um objeto da classe que oferece a tarefa

                    System.out.println("    Enviando um objeto da classe FaceDetection que tem o servico ComputeFaceDetection");
                    Integer result = comp.executeTask(task); //Chamando a execução do método remoto
                    System.out.println("    Recebida a resposta (" + result +") do servico ComputeFaceDetection e enviando ao Cliente");

                    if(result == -2) //Analisando a resposta recebida e enviando o resultado ao cliente
                        out.writeUTF("Erro no classificador");
                    else if(result == -1)
                        out.writeUTF("Erro: arquivo solicitado nao encontrado");
                    else //result == 1
                        out.writeUTF(result.toString() + " faces detectadas");
                }
                else if(data[0].equals("Exit")) { //Cliente quer sair
                    System.out.println("    Cliente solicitou sair da aplicacao!");
                    clientSocket.close(); //Fecha a conexao
                }
                else {
                    out.writeUTF("Servico solicitado invalido!");
                }

            } while(!data[0].equals("Exit"));
            
        } catch (Exception e) {
            System.err.println("Server exception:");
            e.printStackTrace();
        }
    }
}