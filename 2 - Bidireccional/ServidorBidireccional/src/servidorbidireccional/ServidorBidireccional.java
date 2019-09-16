package servidorbidireccional;

import servidor.Servidor;

public class ServidorBidireccional {
    
    public static void main(String[] args) {
        Servidor servidor = new Servidor(args);
        servidor.iniciarServidorBidireccional();
    }
    
}
