package directorioservidor;

import servidor.Servidor;

public class DirectorioServidor {
    public static void main(String[] args){
        Servidor servidor = new Servidor(args);
        servidor.iniciarServidorDirectorio();
    }
}
