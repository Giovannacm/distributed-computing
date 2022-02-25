package server;

import compute.Task;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.io.File;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileWriter;

//Trabalho desenvolvido por: Giovanna Carreira Marinho

/* Classe que realiza a implementacao do serviço computeSortFile. 
*  Esse serviço, por meio do nome da imagem de origem, realiza a ordenação dos
*  valores inteiros presentes num arquivo texto.
*  O arquivo de origem e destino é lido/escrito na pasta resources.
*  Essa classe implementa Serializable para que um objeto do seu tipo possa ser passado 
*  pela rede (nesse caso, passado para ComputeEngine) e a interface Task 
*  (local, que define o método execute). O metodo execute é invocado no 
*  ComputeEngine para realizacao dos calculos, cujo resultado sera retornado ao Servidor.
*/

public class SortFile implements Task<Integer>, Serializable {
    private static final long serialVersionUID = 227L;
    private String file_source_name;
   
    public SortFile(String file_source_name) { //Construtor
        this.file_source_name = file_source_name;        
    }

    public Integer execute() { //Método da interface local Task
        System.out.println("Enviando a resposta ao servidor");
        return computeSortFile(this.file_source_name); //Retornando o resultado do servico
    }

    public static Integer computeSortFile(String file_source_name) {
        try {
            //ler arquivo
            File file = new File("./resources/" + file_source_name);
            Scanner fileReader = new Scanner(file); //Leitura do arquivo na pasta resources

            String fileContent = "";

            while(fileReader.hasNextLine()) //Capturando o conteudo do arquivo e armazenando em uma string
                fileContent += fileReader.nextLine();
            
            fileReader.close(); //Fechando o arquivo

            String[] fileContentList = fileContent.split(" "); //Separando o conteudo do arquivo por espaços para capturar os valores e armazenando numa lista
            ArrayList<Integer> values = new ArrayList<Integer>();

            for(String data : fileContentList) //para cada valor no formato de string, convertendo para inteiro e inserindo num arraylist
                values.add(Integer.parseInt(data));

            //ordenar
            Collections.sort(values); //ordenacao por meio do método sort do pacote Collections

            //salvar arquivo
            try {
                FileWriter fileWriter = new FileWriter("./resources/" + "result_" + file_source_name);

                for(Integer value : values) //Escrevendo o conteudo criptografado no arquivo de resultado
                    fileWriter.write(value.toString() + " ");

                fileWriter.close(); //Fechando o arquivo

                return(1); //Sucesso na operação
            } catch (IOException e) {
                System.out.println("Erro na criacao do resultado na pasta resources | " + e);
                return(-2);
            }

        } catch (FileNotFoundException e) {
            System.out.println("Erro: arquivo solicitado nao encontrado | " + e);
            return(-1);
        }
    }   
}