package archivoservidor;

import java.net.*;
import java.io.*;
import java.util.Scanner;

/**
 * Clase Servidor. En esta clase se manejan todos los Servidores vistos en el
 * parcial.
 * @author Alexander Gámez Urías
 * @version 1.0
 */
public class Servidor {

    private int puerto;
    private ServerSocket socketServidor;
    private Socket socket;
    private BufferedReader lector;
    private DataOutputStream salidaCliente;
    private BufferedInputStream leerArchivo;
    private boolean entro;
    private PrintWriter escritor;
    private File archivo = null;
    private String nombre;
    private String rutaArchivoServidor;
    private FileInputStream entrada2;
    private BufferedOutputStream salida2;
    private int cantidad = 0;
    private long tam = 0;
    private Scanner scanner;
    private String salida;
    private String entrada;
    private String lista;
    private String rutaDirectorio;
    private final String[] arreglo;

    /**
     * Aquí se inicia se agarran los datos del Array de String del main.
     * @param arreglo El array de String del main.
     */
    public Servidor(String[] arreglo) {
        this.rutaDirectorio = "";
        this.rutaArchivoServidor = "";
        this.salidaCliente = null;
        this.arreglo = arreglo;
    }

    /**
     * Al abrir el Servidor Simple se debe meter un puerto.
     * Ejemplo: -jar "C:\Programación Ambientada a Cliente-Servidor\1 - 
     * Cliente/Servidor Simple\ServidorSimple\dist\ServidorSimple.jar" 2001
     */
    public void iniciarServidorSimple() {
        entro = false;
        validar(arreglo.length);
        validarPuerto(arreglo[0]);
        while (true) {
            crearServidor();
            crearLector();
            leerMensaje();
            cerrarSocket();
        }
    }

    /**
     * Al abrir el Servidor Bidireccional se debe meter un puerto.
     * Ejemplo: -jar "C:\Programación Ambientada a Cliente-Servidor\2 - 
     * Cliente/Servidor 
     * Bidireccional\ServidorBidireccional\dist\ServidorBidireccional.jar" 2001
     */
    public void iniciarServidorBidireccional() {
        validar(arreglo.length);
        validarPuerto(arreglo[0]);
        todoBidireccional();
    }

    /**
     * Al abrir el Servidor de Directorios se debe meter un puerto.
     * Ejemplo: -jar "C:\Programación Ambientada a Cliente-Servidor\4 - 
     * Cliente/Servidor 
     * Directorios\DirectorioServidor\dist\DirectorioServidor.jar" 2001
     */
    public void iniciarServidorDirectorio() {
        validar(arreglo.length);
        validarPuerto(arreglo[0]);
        while (true) {
            crearServidor();
            crearLector();
            AccionDir();
            cerrarSocket();
        }
    }

    /**
     * Al abrir el Servidor de Archivos se debe meter un puerto.
     * Ejemplo: -jar "C:\Programación Ambientada a Cliente-Servidor\3 - 
     * Cliente/Servidor 
     * Archivos\ArchivoServidor\dist\ArchivoServidor.jar" 2001
     * ó 
     * -jar "C:\Programación Ambientada a Cliente-Servidor\5 - Cliente/Servidor 
     * Archivos_v2\ArchivoServidor\dist\ArchivoServidor.jar" 2001
     */
    public void iniciarServidorArchivo() {
        validar(arreglo.length);
        validarPuerto(arreglo[0]);
        while (true) {
            crearServidor();
            crearLector();
            Accion();
            cerrarSocket();
        }
    }

    //Métodos usados en todos los Servidores.
    private void validar(int tam) {
        if (tam == 0 || tam >= 2) {
            System.err.println("Debes de poner valores válidos");
            System.exit(0);
        }
    }

    private void validarPuerto(String valor) {
        try {
            puerto = Integer.parseInt(valor);
        } catch (Exception e) {
            System.err.println("Debes de poner un puerto válido, " + e);
            System.exit(0);
        }
    }

    private void crearServidor() {
        try {
            socketServidor = new ServerSocket(puerto);
            if (!entro) {
                System.out.println("El servidor está funcionando");
                entro = true;
            }
            socket = socketServidor.accept();
        } catch (Exception e) {
            System.err.println("Error al crear el servidor, " + e);
            System.exit(0);
        }
    }

    private void crearLector() {
        try {
            lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (Exception e) {
            System.err.println("Error al recibir el mensaje, " + e.toString());
            System.exit(0);
        }
    }

    private void cerrarSocket() {
        try {
            socket.close();
            socketServidor.close();
        } catch (Exception e) {
        }
    }

    //Métodos usados en el Servidor Simple.
    private void leerMensaje() {
        try {
            String entrada = "";
            while ((entrada = lector.readLine()) != null) {
                System.out.println("Entrada: " + entrada);
            }
        } catch (Exception e) {
            System.err.println("Error al leer el mensaje, " + e.toString());
            System.exit(0);
        }
    }

    //Métodos usados en el Servidor Bidireccional.
    private void todoBidireccional() {
        entro = false;
        crearServidor();
        crearLector();
        crearEscritor();
        crearScanner();
        do {
            leerLinea();
            checar();
            System.out.println(entrada);
            if (entrada.equalsIgnoreCase("fin")) {
                System.out.println("Cerrando servidor");
                cerrarSocket();
                System.exit(0);
            }
            salida = scanner.nextLine();
            escritor.println(salida);
        } while (!entrada.equalsIgnoreCase("fin"));
    }

    private void leerLinea() {
        try {
            entrada = lector.readLine();
        } catch (Exception e) {
            System.out.println("Error al leer línea: " + e);
        }
    }

    private void checar() {
        try {
            if (entrada == null) {
                System.out.println("No se permiten espacios vacíos");
                socket.close();
                socketServidor.close();
                System.exit(0);
            }
        } catch (Exception e) {

        }
    }
    
    private void crearEscritor() {
        try {
            escritor = new PrintWriter(socket.getOutputStream(), true);
        } catch (Exception e) {
            System.out.println("Error al crear el escritor: " + e);
            System.exit(0);
        }
    }
    
    private void crearScanner() {
        try {
            scanner = new Scanner(System.in);
        } catch (Exception e) {
            System.out.println("Error al crear el Scanner: " + e);
            System.exit(0);
        }
    }

    //Métodos usados en el Servidor de Directorios.
    private void AccionDir() {
        leerLineaDir();
        enviarListado();
    }

    private void leerLineaDir() {
        try {
            if ((entrada = lector.readLine()) != null) {
                if (checarDirectorio()) {
                    rutaDirectorio = entrada;
                }
            }
        } catch (Exception e) {
            System.out.println("Error al leer línea: " + e);
            System.exit(0);
        }
    }

    private boolean checarDirectorio() {
        File archivo2 = new File(entrada);
        return (archivo2.exists() && archivo2.isDirectory());
    }

    private boolean validarDirectorio() {
        archivo = new File(rutaDirectorio);
        return archivo.exists() && archivo.isDirectory();
    }

    private void crearListado() {
        File[] contenido = archivo.listFiles();
        String lista1 = "";
        String lista2 = "";
        int cont1 = 0;
        int cont2 = 0;
        lista = "\nDirectorios: \n";
        for (File cosa : contenido) {
            if (cosa.isDirectory()) {
                cont1++;
                lista1 += cont1 + " - " + cosa.getName() + "\n";
            } else if (cosa.isFile()) {
                cont2++;
                lista2 += cont2 + " - " + cosa.getName() + "\n";
            }
        }
        lista += "Número de Directorios encontrados: " + cont1 + "\n\n";
        lista += lista1;
        lista += "\nArchivos: \n";
        lista += "Número de Archivos encontrados: " + cont2 + "\n\n";
        lista += lista2;
    }

    private void enviarListado() {
        crearSalida();
        if (validarDirectorio()) {
            crearListado();
            escribirDatosDir();
        } else {
            escribirDatos2();
        }
    }

    private void escribirDatosDir() {
        try {
            salidaCliente.writeBoolean(true);
            salidaCliente.writeUTF(lista);
        } catch (Exception e) {
            System.err.println("Error al escribir datos: " + e);
        }
    }

    //Métodos usados en el Servidor de Archivos (Ambos servidores de archivos incluídos, ya que no hay cambios).
    private void Accion() {
        leerLineav2();
        enviarArchivo();
    }

    private void leerLineav2() {
        try {
            if ((entrada = lector.readLine()) != null) {
                if (checarArchivo()) {
                    rutaArchivoServidor = entrada;
                }
            }
        } catch (Exception e) {
            System.out.println("Error al leer línea: " + e);
            System.exit(0);
        }
    }

    private boolean checarArchivo() {
        File archivo2 = new File(entrada);
        return (archivo2.exists() && archivo2.isFile());
    }

    private boolean validarArchivo() {
        archivo = new File(rutaArchivoServidor);
        if (archivo.exists() && archivo.isFile()) {
            nombre = archivo.getName();
            return true;
        } else {
            return false;
        }
    }

    private void enviarArchivo() {
        crearSalida();
        if (validarArchivo()) {
            tam = archivo.length();
            cantidad = 1048576;
            escribirDatos();
            entrada();
            leerArchivo = new BufferedInputStream(entrada2);
            salida();
            byte[] arreglo2 = new byte[cantidad];
            if (arreglo2 != null && arreglo2.length > 0) {
                int i;
                long j = 0;
                while ((j += (i = leer(arreglo2))) <= tam) {
                    escribir(arreglo2, i);
                    if (j == tam) {
                        break;
                    }
                }
            }
            System.out.println("Archivo enviado.");
            cerrarTodo();
        } else {
            escribirDatos2();
        }
    }

    private void escribir(byte[] i, int num) {
        try {
            try {
                salida2.write(i, 0, num);
            } catch (ArrayIndexOutOfBoundsException e) {
            }
        } catch (Exception e) {
            System.err.println("Error al escribir arreglo: " + e);
            System.exit(0);
        }
    }

    private int leer(byte[] arreglo) {
        try {
            return leerArchivo.read(arreglo);
        } catch (Exception e) {
            System.err.println("Error al leer arreglo: " + e);
            System.exit(0);
        }
        return -1;
    }

    private void cerrarTodo() {
        try {
            salida2.flush();
            salida2.flush();
            salida2.close();
            entrada2.close();
        } catch (Exception e) {
            System.err.println("Error al cerrar la E/S: " + e);
        }
    }

    private void salida() {
        try {
            salida2 = new BufferedOutputStream(socket.getOutputStream());
        } catch (Exception e) {
            System.err.println("Error al crear salida de datos: " + e);
        }
    }

    private void entrada() {
        try {
            entrada2 = new FileInputStream(archivo);
        } catch (Exception e) {
            System.err.println("Error al crear entrada de datos: " + e);
        }
    }

    private void escribirDatos() {
        try {
            salidaCliente.writeBoolean(true);
            salidaCliente.writeLong(tam);
            salidaCliente.writeInt(cantidad);
            salidaCliente.writeUTF(nombre);
        } catch (Exception e) {
            System.err.println("Error al escribir datos: " + e);
        }
    }

    //Métodos usados en dos o más Servidores.
    //Usado en el Servidor de Directorios y en el de Archivos
    private void escribirDatos2() {
        try {
            salidaCliente.writeBoolean(false);
            salidaCliente.writeUTF("No existe el archivo en el Servidor");
        } catch (Exception e) {
            System.err.println("Error al crear salida de datos: " + e);
        }
    }

    //Usado en el Servidor de Directorios y en el de Archivos
    private void crearSalida() {
        try {
            salidaCliente = new DataOutputStream(socket.getOutputStream());
        } catch (Exception e) {
            System.err.println("Error al crear salida de datos: " + e);
        }
    }
}