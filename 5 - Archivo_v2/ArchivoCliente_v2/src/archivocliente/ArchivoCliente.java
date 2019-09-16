package archivocliente;

import cliente.Cliente;

public class ArchivoCliente {
    
    public static void main(String[] args) {
        Cliente cliente = new Cliente(args);
        cliente.iniciarClienteArchivov2();
    }
}
