package server;

import compute.Task;
import java.io.Serializable;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.objdetect.CascadeClassifier;
import java.util.List;

//Trabalho desenvolvido por: Giovanna Carreira Marinho

/* Classe que realiza a implementacao do serviço computeFaceDetection. 
*  Esse serviço, por meio do nome da imagem de origem, realiza a detecção
*  de faces a partir de um modelo já disponibilizado pela biblioteca OpenCV (versão 3.4.13).
*  A imagem de origem e destino é lida/escrita na pasta resources.
*  Essa classe implementa Serializable para que um objeto do seu tipo possa ser passado 
*  pela rede (nesse caso, passado para ComputeEngine) e a interface Task 
*  (local, que define o método execute). O metodo execute é invocado no 
*  ComputeEngine para realizacao dos calculos, cujo resultado sera retornado ao Servidor.
*/

public class FaceDetection implements Task<Integer>, Serializable {
    private static final long serialVersionUID = 227L;
    private String image_source_name;
   
    public FaceDetection(String image_source_name) { //Construtor
        this.image_source_name = image_source_name;        
    }

    public Integer execute() { //Método da interface local Task
        System.out.println("Enviando a resposta ao servidor");
        return computeFaceDetection(this.image_source_name); //Retornando o resultado do servico
    }

    public static Integer computeFaceDetection(String image_source_name) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME); //OpenCV: carregando a biblioteca

        CascadeClassifier faceCascade = new CascadeClassifier(); //Instanciando o classificador
        faceCascade.load("./lib/" + "haarcascade_frontalface_alt.xml"); //carregando o modelo já treinado (disponibilizado pela biblioteca) presente na pasta lib
        
        if(faceCascade.empty()) //Verificacao de erro
            return(-2);

        Mat source = Imgcodecs.imread("./resources/" + image_source_name, Imgcodecs.IMREAD_COLOR); //Leitura da imagem na pasta resources
        
        if(source.empty()) //Verificacao de erro
            return(-1);

        Mat gray = new Mat(); //Criacao de uma imagem vazia
        Imgproc.cvtColor(source, gray, Imgproc.COLOR_RGB2GRAY); //Convertendo a imagem de origem em tons de cinza e salvando na vazia
        Imgproc.equalizeHist(gray, gray); //Equalizando o histograma da imagem

        MatOfRect faces = new MatOfRect(); //Instanciando um objeto para armazenar os retangulos
        faceCascade.detectMultiScale(gray, faces); //Realizando a deteccao da imagem e armazenando os regangulos em faces

        List<Rect> listOfFaces = faces.toList(); //Convertendo os retangulos em lista
        Integer facesFound = listOfFaces.size(); //Verificando a quantidade de faces detectadas
        
        for(Rect face : listOfFaces) //Percorrendo as faces/retangulos encontrados
            Imgproc.rectangle(source, face.tl(), face.br(), new Scalar(255, 0, 255), 3); //Desenhando um retangulo na imagem de acordo com suas coordenadas
        
        Imgcodecs.imwrite("./resources/" + "result_" + image_source_name, source); //Salvando o resultado em resources
        
        return(facesFound); //Retornando a quantidade de faces encontradas
    }   
}
