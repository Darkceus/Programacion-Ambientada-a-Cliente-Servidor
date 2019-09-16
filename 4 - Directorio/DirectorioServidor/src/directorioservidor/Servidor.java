package directorioservidor;

import java.net.*;
import java.io.*;
import java.util.Scanner;

/**
 *
 * @author ITLM
 */
public class Servidor {
    
    private int puerto;
    private ServerSocket socketServidor;
    private Socket socket;
    private BufferedReader lector;
    private ObjectInputStream entradaA = null;
    private ObjectOutputStream salidaA = null;
    private DataInputStream entradaCliente = null;
    private DataOutputStream salidaCliente = null;
    private BufferedInputStream leerArchivo;
    private boolean entro;
    private PrintWriter escritor;
    private File archivo = null;
    private String nombre;
    private String rutaArchivoServidor = "";
    private FileInputStream entrada2;
    private BufferedOutputStream salida2;
    private int cantidad = 0;
    private long tam = 0;
    private Scanner scanner;
    private String salida;
    private String entrada;
    private String lista;
    private String rutaDirectorio = "";
    private final String[] arreglo;
    
    public Servidor(String[] arreglo){
        this.arreglo = arreglo;
    }
    
    public void iniciarServidorSimple(){
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
    
    public void iniciarServidorBidireccional(){
        validar(arreglo.length);
        validarPuerto(arreglo[0]);
        todoBidireccional();
    }
    
    public void iniciarServidorDirectorio(){
        validar(arreglo.length);
        validarPuerto(arreglo[0]);
        while(true){
            crearServidor();
            crearLector();
            AccionDir();
            cerrarSocket();
        }
    }
    
    public void iniciarServidorArchivo(){
        validar(arreglo.length);
        validarPuerto(arreglo[0]);
        while (true) {
            crearServidor();
            crearLector();
            Accion();
            cerrarSocket();
        }
    }
    
    private void validar(int tam){
        if(tam == 0 || tam >= 2){
            System.err.println("Debes de poner valores válidos");
            System.exit(0);
        }
    }
    
    private void validarPuerto(String valor) {
        try {
            puerto = Integer.parseInt(valor);
        } catch (Exception e) {
            System.err.println("Debes de poner un puerto válido, "+e);
            System.exit(0);
        }
    }

    private void crearServidor() {
        try {
            if (!entro) {
                System.out.println("El servidor está funcionando");
                entro = true;
            }
            socketServidor = new ServerSocket(puerto);
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
    
    private void Accion(){
        leerLineav2();
        enviarArchivo();
    }
    
    private void AccionDir(){
        leerLineaDir();
        enviarListado();
    }
    
    private void leerLineaDir(){
        try{
            if ((entrada = lector.readLine()) != null) {
                if (checarDirectorio()) {
                    rutaDirectorio = entrada;
                }
            }
        }catch(IOException e){
            System.out.println("Error al leer línea: "+e);
            System.exit(0);
        }
    }
    
    private boolean checarDirectorio(){
        File archivo2 = new File(entrada);
        return (archivo2.exists() && archivo2.isDirectory());
    }
    
    private boolean validarDirectorio() {
        archivo = new File(rutaDirectorio);
        return archivo.exists() && archivo.isDirectory();
    }
    
    private void crearListado(){
        File[] contenido = archivo.listFiles();
        String lista1 = "";
        String lista2 = "";
        int cont1 = 0;
        int cont2 = 0;
        lista = "\nDirectorios: \n";
        for(File cosa : contenido){
            if(cosa.isDirectory()){
                cont1++;
                lista1 += cont1 + " - " + cosa.getName() + "\n";
            }else if(cosa.isFile()){
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
    
    private void escribirDatosDir(){
        try{
            salidaCliente.writeBoolean(true);
            salidaCliente.writeUTF(lista);
        }catch(IOException e){
            System.err.println("Error al escribir datos: "+e);
        }
    }
    
    private void leerLineav2(){
        try{
            if ((entrada = lector.readLine()) != null) {
                if (checarArchivo()) {
                    rutaArchivoServidor = entrada;
                }
            }
        }catch(IOException | NullPointerException e){
            System.out.println("Error al leer línea: "+e);
            System.exit(0);
        }
    }
    
    private boolean checarArchivo(){
        File archivo2 = new File(entrada);
        return (archivo2.exists() && archivo2.isFile());
    }
    
    private boolean validarArchivo() {
        archivo = new File(rutaArchivoServidor);
        if(archivo.exists() && archivo.isFile()){
            nombre = archivo.getName();
            return true;
        }else{
            return false;
        }
    }
    
    private void enviarArchivo(){
        crearSalida();
        if (validarArchivo()) {
            tam = archivo.length();
            cantidad = 1048576;
            escribirDatos();
            entrada();
            leerArchivo = new BufferedInputStream(entrada2);
            salida();
            byte[] arreglo = new byte[cantidad];
            if (arreglo != null && arreglo.length > 0) {
                int i = 0;
                long j = 0;
                while ((j += (i = leer(arreglo))) <= tam) {
                    escribir(arreglo, i);
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
    
    private void escribir(byte[] i, int num){
        try{
            try{
                salida2.write(i, 0, num);
            }catch(ArrayIndexOutOfBoundsException e){}
        }catch(IOException e){
            System.err.println("Error al escribir arreglo: "+e);
            System.exit(0);
        }
    }
    
    private int leer(byte[] arreglo){
        try {
            return leerArchivo.read(arreglo);
        } catch (IOException e) {
            System.err.println("Error al leer arreglo: " + e);
            System.exit(0);
        }
        return -1;
    }
    
    private void cerrarTodo(){
        try{
            salida2.flush();
            salida2.flush();
            salida2.close();
            entrada2.close();
        }catch(IOException e){
            System.err.println("Error al cerrar la E/S: "+e);
        }
    }
    
    private void escribirDatos2(){
        try{
            salidaCliente.writeBoolean(false);
            salidaCliente.writeUTF("No existe el archivo en el Servidor");
        }catch(IOException e){
            System.err.println("Error al crear salida de datos: "+e);
        }
    }
    
    private void salida(){
        try{
            salida2 = new BufferedOutputStream(socket.getOutputStream());
        }catch(IOException e){
            System.err.println("Error al crear salida de datos: "+e);
        }
    }
    
    private void entrada(){
        try{
            entrada2 = new FileInputStream(archivo);
        }catch(IOException e){
            System.err.println("Error al crear entrada de datos: "+e);
        }
    }
    
    private void escribirDatos(){
        try{
            salidaCliente.writeBoolean(true);
            salidaCliente.writeLong(tam);
            salidaCliente.writeInt(cantidad);
            salidaCliente.writeUTF(nombre);
        }catch(IOException e){
            System.err.println("Error al escribir datos: "+e);
        }
    }
    
    private void crearSalida(){
        try{
            salidaCliente = new DataOutputStream(socket.getOutputStream());
        }catch(IOException e){
            System.err.println("Error al crear salida de datos: "+e);
        }
    }
    
    private void todoBidireccional(){
        entro = false;
        abrir();
        do{
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
    
    private void leerLinea(){
        try{
            entrada = lector.readLine();
        }catch(IOException e){
            System.out.println("Error al leer línea: "+e);
        }
    }
    
    private void checar(){
        try{
            if (entrada == null) {
                System.out.println("No se permiten espacios vacíos");
                socket.close();
                socketServidor.close();
                System.exit(0);
            }
        }catch(IOException e){
            
        }
    }
    
    private void abrir(){
        try{
            socketServidor = new ServerSocket(puerto);
            if (!entro) {
                System.out.println("Se ha iniciado el servidor");
                entro = true;
            }
            socket = socketServidor.accept();
            lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            escritor = new PrintWriter(socket.getOutputStream(), true);
            scanner = new Scanner(System.in);
        }catch(IOException e){
            System.out.println("Error al abrir el servidor: "+e);
            System.exit(0);
        }
    }

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
    
    private void cerrarSocket(){
        try{
            socket.close();
            socketServidor.close();
        }catch(IOException e){}
    }

    private void cerrarConexion() {
        try {
            socketServidor.close();
        } catch (Exception e) {
            System.err.println("Error al cerrar la conexión, " + e.toString());
            System.exit(0);
        }
    }
    
}
