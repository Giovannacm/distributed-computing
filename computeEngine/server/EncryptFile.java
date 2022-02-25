package server;

import compute.Task;
import java.io.Serializable;

import java.io.File;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileWriter;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

//Trabalho desenvolvido por: Giovanna Carreira Marinho

/* Classe que realiza a implementacao do serviço computeEncryptFile. 
*  Esse serviço, por meio do nome da imagem de origem, realiza a criptografia SHA-1
*  do conteúdo de um arquivo texto.
*  O arquivo de origem e destino é lido/escrito na pasta resources.
*  Essa classe implementa Serializable para que um objeto do seu tipo possa ser passado 
*  pela rede (nesse caso, passado para ComputeEngine) e a interface Task 
*  (local, que define o método execute). O metodo execute é invocado no 
*  ComputeEngine para realizacao dos calculos, cujo resultado sera retornado ao Servidor.
*/

public class EncryptFile implements Task<Integer>, Serializable {
    private static final long serialVersionUID = 227L;
    private String file_source_name;
   
    public EncryptFile(String file_source_name) { //Construtor
        this.file_source_name = file_source_name;        
    }

    public Integer execute() { //Método da interface local Task
        System.out.println("Enviando a resposta ao servidor");
        return computeEncryptFile(this.file_source_name); //Retornando o resultado do servico
    }

    public static Integer computeEncryptFile(String file_source_name) { //Implementação do servico
        try {
            //ler arquivo
            File file = new File("./resources/" + file_source_name);
            Scanner fileReader = new Scanner(file); //Leitura do arquivo na pasta resources

            String fileContent = "";

            while(fileReader.hasNextLine()) //Capturando o conteudo do arquivo e armazenando em uma string
                fileContent += fileReader.nextLine();
            
            fileReader.close(); //Fechando o arquivo

            //criptografar (SHA)
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-1"); //Instanciando o objeto para criptografia
                
                byte[] messageDigest = md.digest(fileContent.getBytes()); //fazendo o "digest" do conteudo do arquivo
                
                BigInteger no = new BigInteger(1, messageDigest); //Conversao de tipos: matriz de bytes->signum
                
                String hashtext = no.toString(16); //Convertendo em hexadecimal

                while (hashtext.length() < 32) //Loop para deixar em 32 bit
                    hashtext = "0" + hashtext;

                //salvar arquivo
                try {
                    FileWriter fileWriter = new FileWriter("./resources/" + "result_" + file_source_name);

                    fileWriter.write(hashtext); //Escrevendo o conteudo criptografado no arquivo de resultado

                    fileWriter.close(); //Fechando o arquivo

                    return(1); //Sucesso na operacao
                } catch (IOException e) {
                    System.out.println("Erro na criacao do resultado na pasta resources | " + e);
                    return(-2);
                }

            } catch (NoSuchAlgorithmException e) {
                System.out.println("Erro no algoritmo SHA | " + e);
                return(-3);
            }

        } catch (FileNotFoundException e) {
            System.out.println("Erro: arquivo solicitado nao encontrado | " + e);
            return(-1);
        }
    }   
}