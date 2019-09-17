package servidor;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author Alexander Gámez Urías
 */

public class Servidor
{
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
  private long tam = 0L;
  private Scanner scanner;
  private String salida;
  private String entrada;
  private String lista;
  private String rutaDirectorio;
  private final String[] arreglo;
  
  public Servidor(String[] arreglo)
  {
    this.rutaDirectorio = "";
    this.rutaArchivoServidor = "";
    this.salidaCliente = null;
    this.arreglo = arreglo;
  }
  
  public void iniciarServidorSimple()
  {
    this.entro = false;
    validar(this.arreglo.length);
    validarPuerto(this.arreglo[0]);
    while(true)
    {
      crearServidor();
      crearLector();
      leerMensaje();
      cerrarSocket();
    }
  }
  
  public void iniciarServidorBidireccional()
  {
    validar(this.arreglo.length);
    validarPuerto(this.arreglo[0]);
    todoBidireccional();
  }
  
  public void iniciarServidorDirectorio()
  {
    validar(this.arreglo.length);
    validarPuerto(this.arreglo[0]);
    while(true)
    {
      crearServidor();
      crearLector();
      AccionDir();
      cerrarSocket();
    }
  }
  
  public void iniciarServidorArchivo()
  {
    validar(this.arreglo.length);
    validarPuerto(this.arreglo[0]);
    while(true)
    {
      crearServidor();
      crearLector();
      Accion();
      cerrarSocket();
    }
  }
  
  private void validar(int tam)
  {
    if ((tam == 0) || (tam >= 2))
    {
      System.err.println("Debes de poner valores válidos");
      System.exit(0);
    }
  }
  
  private void validarPuerto(String valor)
  {
    try
    {
      this.puerto = Integer.parseInt(valor);
    }
    catch (Exception e)
    {
      System.err.println("Debes de poner un puerto válido, " + e);
      System.exit(0);
    }
  }
  
  private void crearServidor()
  {
    try
    {
      this.socketServidor = new ServerSocket(this.puerto);
      if (!this.entro)
      {
        System.out.println("El servidor está funcionando");
        this.entro = true;
      }
      this.socket = this.socketServidor.accept();
    }
    catch (Exception e)
    {
      System.err.println("Error al crear el servidor, " + e);
      System.exit(0);
    }
  }
  
  private void crearLector()
  {
    try
    {
      this.lector = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
    }
    catch (Exception e)
    {
      System.err.println("Error al recibir el mensaje, " + e.toString());
      System.exit(0);
    }
  }
  
  private void cerrarSocket()
  {
    try
    {
      this.socket.close();
      this.socketServidor.close();
    }
    catch (Exception e) 
    {
      System.err.println("Error al cerrar los Sockets, " + e.toString());
      System.exit(0);
    }
  }
  
  private void leerMensaje()
  {
    try
    {
      String entrada = "";
      while ((entrada = this.lector.readLine()) != null) {
        System.out.println("Entrada: " + entrada);
      }
    }
    catch (Exception e)
    {
      System.err.println("Error al leer el mensaje, " + e.toString());
      System.exit(0);
    }
  }
  
  private void todoBidireccional()
  {
    this.entro = false;
    crearServidor();
    crearLector();
    crearEscritor();
    crearScanner();
    do
    {
      leerLinea();
      checar();
      System.out.println(this.entrada);
      if (this.entrada.equalsIgnoreCase("fin"))
      {
        System.out.println("Cerrando servidor");
        cerrarSocket();
        System.exit(0);
      }
      this.salida = this.scanner.nextLine();
      this.escritor.println(this.salida);
    } while (!this.entrada.equalsIgnoreCase("fin"));
  }
  
  private void leerLinea()
  {
    try
    {
      this.entrada = this.lector.readLine();
    }
    catch (Exception e)
    {
      System.out.println("Error al leer línea: " + e);
      System.exit(0);
    }
  }
  
  private void checar()
  {
    try
    {
      if (this.entrada == null)
      {
        System.out.println("No se permiten espacios vacíos");
        this.socket.close();
        this.socketServidor.close();
        System.exit(0);
      }
    }
    catch (Exception e) {}
  }
  
  private void crearEscritor()
  {
    try
    {
      this.escritor = new PrintWriter(this.socket.getOutputStream(), true);
    }
    catch (Exception e)
    {
      System.out.println("Error al crear el escritor: " + e);
      System.exit(0);
    }
  }
  
  private void crearScanner()
  {
    try
    {
      this.scanner = new Scanner(System.in);
    }
    catch (Exception e)
    {
      System.out.println("Error al crear el Scanner: " + e);
      System.exit(0);
    }
  }
  
  private void AccionDir()
  {
    leerLineaDir();
    enviarListado();
  }
  
  private void leerLineaDir()
  {
    try
    {
      if (((this.entrada = this.lector.readLine()) != null) && (checarDirectorio())) {
        this.rutaDirectorio = this.entrada;
      }
    }
    catch (Exception e)
    {
      System.out.println("Error al leer línea: " + e);
      System.exit(0);
    }
  }
  
  private boolean checarDirectorio()
  {
    File archivo2 = new File(this.entrada);
    return (archivo2.exists()) && (archivo2.isDirectory());
  }
  
  private boolean validarDirectorio()
  {
    this.archivo = new File(this.rutaDirectorio);
    return (this.archivo.exists()) && (this.archivo.isDirectory());
  }
  
  private void crearListado()
  {
    File[] contenido = this.archivo.listFiles();
    String lista1 = "";
    String lista2 = "";
    int cont1 = 0;
    int cont2 = 0;
    this.lista = "\nDirectorios: \n";
    for (File cosa : contenido) {
      if (cosa.isDirectory())
      {
        cont1++;
        lista1 = lista1 + cont1 + " - " + cosa.getName() + "\n";
      }
      else if (cosa.isFile())
      {
        cont2++;
        lista2 = lista2 + cont2 + " - " + cosa.getName() + "\n";
      }
    }
    this.lista = (this.lista + "Número de Directorios encontrados: " + cont1 + "\n\n");
    this.lista += lista1;
    this.lista += "\nArchivos: \n";
    this.lista = (this.lista + "Número de Archivos encontrados: " + cont2 + "\n\n");
    this.lista += lista2;
  }
  
  private void enviarListado()
  {
    crearSalida();
    if (validarDirectorio())
    {
      crearListado();
      escribirDatosDir();
    }
    else
    {
      escribirDatos2();
    }
  }
  
  private void escribirDatosDir()
  {
    try
    {
      this.salidaCliente.writeBoolean(true);
      this.salidaCliente.writeUTF(this.lista);
    }
    catch (Exception e)
    {
      System.err.println("Error al escribir datos: " + e);
    }
  }
  
  private void Accion()
  {
    leerLineav2();
    enviarArchivo();
  }
  
  private void leerLineav2()
  {
    try
    {
      if (((this.entrada = this.lector.readLine()) != null) && (checarArchivo())) {
        this.rutaArchivoServidor = this.entrada;
      }
    }
    catch (Exception e)
    {
      System.out.println("Error al leer línea: " + e);
      System.exit(0);
    }
  }
  
  private boolean checarArchivo()
  {
    File archivo2 = new File(this.entrada);
    return (archivo2.exists()) && (archivo2.isFile());
  }
  
  private boolean validarArchivo()
  {
    this.archivo = new File(this.rutaArchivoServidor);
    if ((this.archivo.exists()) && (this.archivo.isFile()))
    {
      this.nombre = this.archivo.getName();
      return true;
    }
    return false;
  }
  
  private void enviarArchivo()
  {
    crearSalida();
    if (validarArchivo())
    {
      this.tam = this.archivo.length();
      this.cantidad = 1048576;
      escribirDatos();
      entrada();
      this.leerArchivo = new BufferedInputStream(this.entrada2);
      salida();
      byte[] arreglo2 = new byte[this.cantidad];
      if ((arreglo2 != null) && (arreglo2.length > 0))
      {
        long j = 0L;
        int i;
        while ((j += (i = leer(arreglo2))) <= this.tam)
        {
          escribir(arreglo2, i);
          if (j == this.tam) {
            break;
          }
        }
      }
      System.out.println("Archivo enviado.");
      cerrarTodo();
    }
    else
    {
      escribirDatos2();
    }
  }
  
  private void escribir(byte[] i, int num)
  {
    try
    {
      this.salida2.write(i, 0, num);
    } 
    catch (Exception e)
    {
      System.err.println("Error al escribir arreglo: " + e);
      System.exit(0);
    }
  }
  
  private int leer(byte[] arreglo)
  {
    try
    {
      return this.leerArchivo.read(arreglo);
    }
    catch (Exception e)
    {
      System.err.println("Error al leer arreglo: " + e);
      System.exit(0);
    }
    return -1;
  }
  
  private void cerrarTodo()
  {
    try
    {
      this.salida2.flush();
      this.salida2.flush();
      this.salida2.close();
      this.entrada2.close();
    }
    catch (Exception e)
    {
      System.err.println("Error al cerrar la E/S: " + e);
    }
  }
  
  private void salida()
  {
    try
    {
      this.salida2 = new BufferedOutputStream(this.socket.getOutputStream());
    }
    catch (Exception e)
    {
      System.err.println("Error al crear salida de datos: " + e);
    }
  }
  
  private void entrada()
  {
    try
    {
      this.entrada2 = new FileInputStream(this.archivo);
    }
    catch (Exception e)
    {
      System.err.println("Error al crear entrada de datos: " + e);
    }
  }
  
  private void escribirDatos()
  {
    try
    {
      this.salidaCliente.writeBoolean(true);
      this.salidaCliente.writeLong(this.tam);
      this.salidaCliente.writeInt(this.cantidad);
      this.salidaCliente.writeUTF(this.nombre);
    }
    catch (Exception e)
    {
      System.err.println("Error al escribir datos: " + e);
    }
  }
  
  private void escribirDatos2()
  {
    try
    {
      this.salidaCliente.writeBoolean(false);
      this.salidaCliente.writeUTF("No existe el archivo en el Servidor");
    }
    catch (Exception e)
    {
      System.err.println("Error al crear salida de datos: " + e);
    }
  }
  
  private void crearSalida()
  {
    try
    {
      this.salidaCliente = new DataOutputStream(this.socket.getOutputStream());
    }
    catch (Exception e)
    {
      System.err.println("Error al crear salida de datos: " + e);
    }
  }
}

