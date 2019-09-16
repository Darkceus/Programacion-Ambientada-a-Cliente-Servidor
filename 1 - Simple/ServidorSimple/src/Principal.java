import servidor.Servidor;

public class Principal {
    
    public static void main(String[] args){
        Servidor servidor = new Servidor(args);
        servidor.iniciarServidorSimple();
    }
}
