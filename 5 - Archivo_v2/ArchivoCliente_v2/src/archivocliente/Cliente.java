package archivocliente;

import java.awt.Desktop;
import java.net.*;
import java.io.*;
import java.util.Scanner;

/**
 * Clase Cliente. En esta clase se manejan todos los Clientes vistos en el
 * parcial.
 * @author Alexander Gámez Urías
 * @version 1.0
 */
public class Cliente {

    private String direccion;
    private int puerto;
    private String mensaje;
    private Socket socket;
    private final String[] arreglo;
    private BufferedReader lector;
    private PrintWriter escritor;
    private FileOutputStream destino;
    private DataInputStream datosRecibidos;
    private BufferedOutputStream salidaDatos;
    private BufferedInputStream entradaDatos;
    private String salida;
    private boolean respuesta;
    private long tam;
    private int tam2;
    private String valorFinal;
    private String archivo;
    private String nombreArchivo;
    private String directorio;
    private String nombreArchivos;
    private String rutaArchivoCliente;
    private final int KB = 1024;
    private final int MB = 1048576;
    private final int GB = 1073741824;
    private final long TB = 1099511627776L;

    /**
     * Aquí se inicia se agarran los datos del Array de String del main.
     * @param arreglo El array de String del main.
     */
    public Cliente(String[] arreglo) {
        this.entradaDatos = null;
        this.salidaDatos = null;
        this.destino = null;
        this.arreglo = arreglo;
    }

    /**
     * Al abrir el Cliente Simple se debe meter una dirección, un puerto y un 
     * mensaje.
     * Ejemplo: -jar "C:\Programación Ambientada a Cliente-Servidor\1 - 
     * Cliente/Servidor Simple\ClienteSimple\dist\ClienteSimple.jar" 127.0.0.1 
     * 2001 "Este es un mensaje"
     */
    public void iniciarClienteSimple() {
        validarSimple(arreglo.length);
        validarDireccion(arreglo[0]);
        validarPuerto(arreglo[1]);
        mensaje = arreglo[2];
        crearSocket();
        mandarMensaje();
        cerrarSocket();
    }

    /**
     * Al abrir el Cliente Bidireccional se debe meter una dirección y 
     * un puerto.
     * Ejemplo: -jar "C:\Programación Ambientada a Cliente-Servidor\2 - 
     * Cliente/Servidor Bidireccional\ClienteBidireccional\dist\
     * ClienteBidireccional.jar" 127.0.0.1 2001
     */
    public void iniciarClienteBidireccional() {
        validarBidireccional(arreglo.length);
        validarDireccion(arreglo[0]);
        validarPuerto(arreglo[1]);
        Inicializar();
    }

    /**
     * Al abrir el Cliente Archivo se debe meter una dirección, un puerto y la 
     * ruta de un Archivo.
     * Ejemplo: -jar "C:\Programación Ambientada a Cliente-Servidor\3 - 
     * Cliente/Servidor Archivos\ArchivoCliente\dist\ArchivoCliente.jar" 
     * 127.0.0.1 2001 "C:\Prueba.exe"
     */
    public void iniciarClienteArchivo() {
        validarArchivoCliente(arreglo.length);
        validarDireccion(arreglo[0]);
        validarPuerto(arreglo[1]);
        archivo = arreglo[2];
        crearSocket();
        mandarRuta();
        recibirClienteArchivo();
        cerrarSocket();
    }

    /**
     * Al abrir el Cliente Directorio se debe meter una dirección, un puerto y 
     * la ruta de un Directorio.
     * Ejemplo: -jar "C:\Programación Ambientada a Cliente-Servidor\4 - 
     * Cliente/Servidor Directorios\DirectorioCliente\dist\DirectorioCliente.jar" 
     * 127.0.0.1 2001 "C:\Windows"
     */
    public void iniciarClienteDirectorio() {
        validarCDirectorio(arreglo.length);
        validarDireccion(arreglo[0]);
        validarPuerto(arreglo[1]);
        directorio = arreglo[2];
        crearSocket();
        mandarDirectorio();
        recibirDatos();
        cerrarSocket();
    }

    /**
     * Al abrir el Cliente Archivo_v2 se debe meter una dirección, un puerto y 
     * la ruta de un Archivo.
     * Ejemplo: -jar "C:\Programación Ambientada a Cliente-Servidor\5 - 
     * Cliente/Servidor Archivos_v2\ArchivoCliente\dist\ArchivoCliente.jar" 
     * 127.0.0.1 2001 "C:\Prueba.exe"
     */
    public void iniciarClienteArchivov2() {
        validarArchivoCliente(arreglo.length);
        validarDireccion(arreglo[0]);
        validarPuerto(arreglo[1]);
        archivo = arreglo[2];
        crearSocket();
        mandarRuta();
        recibirClienteArchivov2();
        cerrarSocket();
    }
    
    //Métodos usados en todos los Clientes
    
    private int convertirInt(String valor) {
        try {
            return Integer.valueOf(valor);
        } catch (Exception e) {
            System.err.println("Debes de poner una dirección válida");
            System.exit(0);
        }
        return -1;
    }

    private void validarDireccion(String valor) {
        if(valor.equals("localhost")){
            direccion = "127.0.0.1";
            return;
        }
        char[] algo = valor.toCharArray();
        int cont = 0;
        for (int i = 0; i < algo.length; i++) {
            if (algo[i] == '.') {
                cont++;
            }
        }
        String[] dir = valor.split("\\.");
        boolean bol = (convertirInt(dir[0]) <= 255 && convertirInt(dir[1]) <= 255 && convertirInt(dir[2]) <= 255 && convertirInt(dir[3]) <= 254);
        if (cont == 3 && bol) {
            direccion = valor;
        } else {
            System.err.println("Debes de poner una dirección válida");
            System.exit(0);
        }
    }

    private void validarPuerto(String valor) {
        try {
            puerto = Integer.parseInt(valor);
        } catch (Exception e) {
            System.err.println("Debes de poner un puerto válido");
            System.exit(0);
        }
    }
    
    private void crearSocket() {
        try {
            socket = new Socket(direccion, puerto);
        } catch (Exception e) {
            System.err.println("Error al crear el socket, " + e.toString());
            System.exit(1);
        }
    }
    
    private void cerrarSocket() {
        try {
            socket.close();
        } catch (Exception e) {
            System.err.println("Error al cerrar el socket, " + e.toString());
            System.exit(3);
        }
    }
    
    //Métodos usados en el Cliente Simple

    private void validarSimple(int tam) {
        if (tam == 0 || tam < 3 || tam >= 4) {
            System.err.println("Debes de poner valores válidos");
            System.exit(0);
        }
    }
    
    private void mandarMensaje() {
        try {
            PrintWriter escritor2 = new PrintWriter(socket.getOutputStream(), true);
            escritor2.println(mensaje);
        } catch (Exception e) {
            System.err.println("Error al mandar el mensaje, " + e.toString());
            System.exit(2);
        }
    }
    
    //Métodos usados en el Cliente Bidireccional
    
    private void validarBidireccional(int tam) {
        if (tam == 0 || tam < 2 || tam >= 3) {
            System.err.println("Debes de poner valores válidos");
            System.exit(0);
        }
    }
    
    private void Inicializar() {
        crearSocket();
        crearLector();
        crearEscritor();
        String entrada = "";
        Scanner scanner = new Scanner(System.in);
        while (true) {
            try {
                entrada = scanner.nextLine();
            } catch (Exception e) {
                System.out.println("Cerrando cliente.");
                System.exit(0);
            }
            if (!entrada.isEmpty() || !entrada.equals("")) {
                if (entrada.equals("fin")) {
                    System.out.println("Cerrando cliente");
                    escritor.println(entrada);
                    cerrarSocket();
                    System.exit(0);
                }
                escritor.println(entrada);
                leerLinea();
                System.out.println(salida);
            }
        }
    }
    
    private void crearLector() {
        try {
            lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (Exception e) {
            System.out.println("Error al crear el lector: " + e);
            System.exit(0);
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
    
    private void leerLinea() {
        try {
            salida = lector.readLine();
        } catch (Exception e) {
            System.out.println("Error al leer línea: " + e);
            System.exit(0);
        }
    }
    
    //Métodos usados en el Cliente de Directorios
    
    private void validarCDirectorio(int tam) {
        if (tam == 0 || tam < 3 || tam >= 4) {
            System.err.println("Debes de poner valores válidos");
            System.exit(0);
        }
    }
    
    private void mandarDirectorio() {
        try {
            PrintWriter escritor2 = new PrintWriter(socket.getOutputStream(), true);
            escritor2.println(directorio);
        } catch (Exception e) {
            System.err.println("Error al mandar la información, " + e.toString());
            System.exit(2);
        }
    }
    
    private void recibirDatos() {
        crearRecepcion();
        respuesta = leerBol();
        if (respuesta) {
            nombreArchivos = leerString();
            System.out.println("Datos en el directorio " + directorio + ": \n" + nombreArchivos);
            cerrarSocket();
            System.exit(0);
        } else {
            System.out.println("No existe el Directorio en el Servidor");
            cerrarSocket();
            System.exit(0);
        }
    }
    
    //Métodos usados en el Cliente de Archivos (se toman en cuenta ambos Clientes, con barra de progreso y sin ella).
    
    private void validarArchivoCliente(int tam) {
        if (tam == 0 || tam < 3 || tam >= 4) {
            System.err.println("Debes de poner valores válidos");
            System.exit(0);
        }
    }
    
    private void crearRuta() {
        try {
            rutaArchivoCliente = new File(".").getCanonicalPath() + "\\Downloads\\";
            System.out.println("El Archivo se guardará en: "+rutaArchivoCliente);
        } catch (Exception e) {
            System.out.println("Error al crear ruta: " + e);
        }
    }

    //Usado en el primer Cliente de Archivos, sin la barra de progreso
    private void recibirClienteArchivo() {
        crearRuta();
        crearRecepcion();
        respuesta = leerBol();
        if (respuesta) {
            tam = leerLong();
            tam2 = leerInt();
            nombreArchivo = leerString();
            crearSalida();
            salidaDatos = new BufferedOutputStream(destino);
            crearEntrada();
            byte[] archivo2 = new byte[tam2];
            int num;
            long num2 = 0;
            while ((num2 += (num = leerEntrada(archivo2))) <= tam) {
                salida(archivo2, num);
                if (num2 == tam) {
                    break;
                }
            }
            comp();
            String algo;
            Scanner scanner2 = new Scanner(System.in);
            boolean si = false;
            do {
                System.out.println("\n¿Deseas abrir el archivo? Si/No");
                algo = scanner2.nextLine();
                if (algo.equalsIgnoreCase("Si")) {
                    si = true;
                }
            } while (!algo.equalsIgnoreCase("Si") && !algo.equalsIgnoreCase("No"));
            if (si) {
                abrirArchivo();
            }
            cerrarES();
            cerrarSocket();
            System.exit(0);
        } else {
            System.out.println("No existe el archivo en el Servidor");
            cerrarSocket();
            System.exit(0);
        }
    }

    //Usado en el segundo Cliente de Archivos, con la barra de progreso
    private void recibirClienteArchivov2() {
        crearRuta();
        crearRecepcion();
        respuesta = leerBol();
        if (respuesta) {
            tam = leerLong();
            tam2 = leerInt();
            nombreArchivo = leerString();
            crearSalida();
            salidaDatos = new BufferedOutputStream(destino);
            crearEntrada();
            byte[] archivo2 = new byte[tam2];
            int num;
            long num2 = 0;
            float num3;
            valorFinal = convertirBytes(tam);
            while ((num2 += (num = leerEntrada(archivo2))) <= tam) {
                salida(archivo2, num);
                num3 = ((float)num2 / tam);
                num3 *= 100;
                barraProgreso(num2, tam, Math.round(num3));
                if (num2 == tam) {
                    break;
                }
            }
            comp();
            String algo;
            Scanner scanner2 = new Scanner(System.in);
            boolean si = false;
            do {
                System.out.println("\n¿Deseas abrir el archivo? Si/No");
                algo = scanner2.nextLine();
                if (algo.equalsIgnoreCase("Si")) {
                    si = true;
                }
            } while (!algo.equalsIgnoreCase("Si") && !algo.equalsIgnoreCase("No"));
            if (si) {
                abrirArchivo();
            }
            cerrarES();
            cerrarSocket();
            System.exit(0);
        } else {
            System.out.println("No existe el archivo en el Servidor");
            cerrarSocket();
            System.exit(0);
        }
    }
    
    private void comp() {
        try {
            salidaDatos.flush();
        } catch (Exception e) {
            System.err.println("Error al sacar datos: " + e);
            System.exit(0);
        }
    }
    
    public String convertirBytes(long tam) {
        String num;
        double kb = tam / 1024;
        double mb = kb / 1024;
        double gb = mb / 1024;
        if(tam < 1024) {
            num = tam + " Bytes";
        } else if(tam >= KB && tam < MB) {
            num =  String.format("%.2f", kb) + " KB";
        } else if(tam >= MB && tam < GB) {
            num = String.format("%.2f", mb) + " MB";
        } else if(tam >= GB && tam < TB) {
            num = String.format("%.2f", gb) + " GB";
        } else {
            return null;
        }
        return num;
    }

    private void barraProgreso(long suma, long total, int por) {
        int porcentaje = (int) ((suma * 100) / total) / 10;
        char caracterFalta = ' ';
        String caracterLleva = "█";
        String barra = new String(new char[10]).replace('\0', caracterFalta) + "|";
        StringBuilder barra2 = new StringBuilder();
        barra2.append("|");
        for (int i = 0; i < porcentaje; i++) {
            barra2.append(caracterLleva);
        }
        String remanente = barra.substring(porcentaje, barra.length());
        System.out.print("\r                                                          ");
        System.out.print("\rRecibiendo... ("+ por + "%) " + barra2 + remanente + "  " + convertirBytes(suma) + " de " + valorFinal);
        if (suma == total) {
            System.out.print("\n");
        }
    }
    
    private void cerrarES() {
        try {
            entradaDatos.close();
            salidaDatos.close();
        } catch (IOException e) {
            System.err.println("Error al cerrar la E/S: " + e);
            System.exit(0);
        }
    }

    private int leerEntrada(byte[] arreglo) {
        try {
            return entradaDatos.read(arreglo);
        } catch (IOException e) {
            System.err.println("Error al leer datos de la entrada: " + e);
            System.exit(0);
        }
        return -1;
    }

    private void crearEntrada() {
        try {
            entradaDatos = new BufferedInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.err.println("Error al crear la entrada de datos: " + e);
            System.exit(0);
        }
    }

    private void crearSalida() {
        try {
            destino = new FileOutputStream(rutaArchivoCliente + nombreArchivo);
        } catch (IOException e) {
            System.err.println("Error al sacar datos: " + e);
            System.exit(0);
        }
    }

    private void salida(byte[] dato, int num) {
        try {
            salidaDatos.write(dato, 0, num);
        } catch (IOException e) {
            System.err.println("Error al pasar datos: " + e);
            System.exit(0);
        }
    }

    private long leerLong() {
        try {
            return datosRecibidos.readLong();
        } catch (IOException e) {
            System.out.println("Error leer datos: " + e);
            System.exit(0);
        }
        return -1;
    }

    private int leerInt() {
        try {
            return datosRecibidos.readInt();
        } catch (IOException e) {
            System.out.println("Error leer datos: " + e);
            System.exit(0);
        }
        return -1;
    }

    private void abrirArchivo() {
        try {
            File archivo2 = new File(rutaArchivoCliente + nombreArchivo);
            Desktop.getDesktop().open(archivo2);
        } catch (IOException e) {
            System.out.println("Error al abrir archivo: " + e);
            System.exit(0);
        }
    }

    private void mandarRuta() {
        try {
            PrintWriter escritor2 = new PrintWriter(socket.getOutputStream(), true);
            escritor2.println(archivo);
        } catch (IOException e) {
            System.err.println("Error al mandar la información, " + e.toString());
            System.exit(2);
        }
    }
    
    //Métodos usados en dos o más Clientes
    
    //Usado en el Cliente de Directorios y los dos de Archivos
    private void crearRecepcion() {
        try {
            datosRecibidos = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.err.println("Error al poner datos: " + e);
            System.exit(0);
        }
    }
    
    //Usado en el Cliente de Directorios y los dos de Archivos
    private String leerString() {
        try {
            return datosRecibidos.readUTF();
        } catch (IOException e) {
            System.out.println("Error leer datos: " + e);
            System.exit(0);
        }
        return null;
    }
    
    //Usado en el Cliente de Directorios y los dos de Archivos
    private boolean leerBol() {
        try {
            return datosRecibidos.readBoolean();
        } catch (IOException e) {
            System.out.println("Error leer datos: " + e);
            System.exit(0);
        }
        return false;
    }
}
