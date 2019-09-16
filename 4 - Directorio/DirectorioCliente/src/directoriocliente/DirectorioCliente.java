package directoriocliente;

import cliente.Cliente;

public class DirectorioCliente {
    
    public static void main(String[] args) {
        Cliente cliente = new Cliente(args);
        cliente.iniciarClienteDirectorio();
    }
}
