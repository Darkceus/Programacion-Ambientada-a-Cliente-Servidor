package clientebidireccional;

import cliente.Cliente;

public class ClienteBidireccional {
    
    public static void main(String[] args) {
        Cliente cliente = new Cliente(args);
        cliente.iniciarClienteBidireccional();
    }
}
