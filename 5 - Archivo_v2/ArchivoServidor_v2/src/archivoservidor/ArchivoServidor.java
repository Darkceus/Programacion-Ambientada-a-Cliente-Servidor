package archivoservidor;

import servidor.Servidor;

public class ArchivoServidor {
    
    public static void main(String[] args){
        Servidor servidor = new Servidor(args);
        servidor.iniciarServidorArchivo();
    }
}
