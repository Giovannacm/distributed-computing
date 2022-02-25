/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 

package engine;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import compute.Compute;
import compute.Task;

//Trabalho desenvolvido por: Giovanna Carreira Marinho

/* Classe do Executor/ComputeEngine que possui instancias de objetos remotos, 
*  aqueles que oferecem servicos, e registra o nome de um servico por meio do 
*  metodo rebind. O Executor precisa conhecer a interface e a implementacao 
*  dos metodos remotos. É responsável por fazer o registro do servico e informa 
*  qual sera o objeto que atendera a esse servico, atraves do metodo rebind. 
*  Alem da interce remota, ele precisa ter a implementacao dos metodos remotos.
*  O rmi.registry, importado acima, contem uma identificacao de um servico e o 
*  objeto que prove esse servico, essas informacoes sao passadas por ele por meio
*  do metodo rebind.
*/

public class ComputeEngine implements Compute { 

    public ComputeEngine() { //Construtor da classe
        super();
    }

    public <T> T executeTask(Task<T> t) { //Implementacao do método da interface remota
        System.out.println("\nRecebida a solicitacao do servidor");
        return t.execute(); //que irá retornar o resultado do método da interface local (Task) da tarefa
    }

    public static void main(String[] args) { //Argumentos> RedeRMI PortaRMI
        if (System.getSecurityManager() == null) { //Verificação de segurança
            System.setSecurityManager(new SecurityManager());
        }
        try {
            String name = "Compute";
            Compute engine = new ComputeEngine(); //Instanciando um objeto da classe
            Compute stub = (Compute) UnicastRemoteObject.exportObject(engine, 0); //Instanciacao do objeto que ira prover o servico Compute
            Registry registry = LocateRegistry.getRegistry(args[0], Integer.parseInt(args[1])); //Buscando uma referencia ao rmi.Registry (indicando a rede e a porta, passados como argumento durante a execução)
            registry.rebind(name, stub); //Registrando o servico informando, o id e o objeto que prove o servico
            System.out.println("\nComputeEngine bound");
        } catch (Exception e) { //Tratamento de erro
            System.err.println("ComputeEngine exception:");
            e.printStackTrace();
        }
    }
}
