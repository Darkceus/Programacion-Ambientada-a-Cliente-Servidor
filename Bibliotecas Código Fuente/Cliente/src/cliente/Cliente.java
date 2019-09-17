package cliente;

import java.awt.Desktop;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author Alexander Gámez Urías
 */

public class Cliente
{
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
  
  public Cliente(String[] arreglo)
  {
    this.entradaDatos = null;
    this.salidaDatos = null;
    this.destino = null;
    this.arreglo = arreglo;
  }
  
  public void iniciarClienteSimple()
  {
    validarSimple(this.arreglo.length);
    validarDireccion(this.arreglo[0]);
    validarPuerto(this.arreglo[1]);
    this.mensaje = this.arreglo[2];
    crearSocket();
    mandarMensaje();
    cerrarSocket();
  }
  
  public void iniciarClienteBidireccional()
  {
    validarBidireccional(this.arreglo.length);
    validarDireccion(this.arreglo[0]);
    validarPuerto(this.arreglo[1]);
    Inicializar();
  }
  
  public void iniciarClienteArchivo()
  {
    validarArchivoCliente(this.arreglo.length);
    validarDireccion(this.arreglo[0]);
    validarPuerto(this.arreglo[1]);
    this.archivo = this.arreglo[2];
    crearSocket();
    mandarRuta();
    recibirClienteArchivo();
    cerrarSocket();
  }
  
  public void iniciarClienteDirectorio()
  {
    validarCDirectorio(this.arreglo.length);
    validarDireccion(this.arreglo[0]);
    validarPuerto(this.arreglo[1]);
    this.directorio = this.arreglo[2];
    crearSocket();
    mandarDirectorio();
    recibirDatos();
    cerrarSocket();
  }
  
  public void iniciarClienteArchivov2()
  {
    validarArchivoCliente(this.arreglo.length);
    validarDireccion(this.arreglo[0]);
    validarPuerto(this.arreglo[1]);
    this.archivo = this.arreglo[2];
    crearSocket();
    mandarRuta();
    recibirClienteArchivov2();
    cerrarSocket();
  }
  
  private int convertirInt(String valor)
  {
    try
    {
      return Integer.valueOf(valor);
    }
    catch (Exception e)
    {
      System.err.println("Debes de poner una dirección válida");
      System.exit(0);
    }
    return -1;
  }
  
  private void validarDireccion(String valor)
  {
    if (valor.equals("localhost"))
    {
      this.direccion = "127.0.0.1";
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
    boolean bol = (convertirInt(dir[0]) <= 255) && (convertirInt(dir[1]) <= 255) && (convertirInt(dir[2]) <= 255) && (convertirInt(dir[3]) <= 254);
    if ((cont == 3) && (bol))
    {
      this.direccion = valor;
    }
    else
    {
      System.err.println("Debes de poner una dirección válida");
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
      System.err.println("Debes de poner un puerto válido");
      System.exit(0);
    }
  }
  
  private void crearSocket()
  {
    try
    {
      this.socket = new Socket(this.direccion, this.puerto);
    }
    catch (Exception e)
    {
      System.err.println("Error al crear el socket, " + e.toString());
      System.exit(1);
    }
  }
  
  private void cerrarSocket()
  {
    try
    {
      this.socket.close();
    }
    catch (Exception e)
    {
      System.err.println("Error al cerrar el socket, " + e.toString());
      System.exit(3);
    }
  }
  
  private void validarSimple(int tam)
  {
    if ((tam == 0) || (tam < 3) || (tam >= 4))
    {
      System.err.println("Debes de poner valores válidos");
      System.exit(0);
    }
  }
  
  private void mandarMensaje()
  {
    try
    {
      PrintWriter escritor2 = new PrintWriter(this.socket.getOutputStream(), true);
      escritor2.println(this.mensaje);
    }
    catch (Exception e)
    {
      System.err.println("Error al mandar el mensaje, " + e.toString());
      System.exit(2);
    }
  }
  
  private void validarBidireccional(int tam)
  {
    if ((tam == 0) || (tam < 2) || (tam >= 3))
    {
      System.err.println("Debes de poner valores válidos");
      System.exit(0);
    }
  }
  
  private void Inicializar()
  {
    crearSocket();
    crearLector();
    crearEscritor();
    String entrada = "";
    Scanner scanner = new Scanner(System.in);
    for (;;)
    {
      try
      {
        entrada = scanner.nextLine();
      }
      catch (Exception e)
      {
        System.out.println("Cerrando cliente.");
        System.exit(0);
      }
      if ((!entrada.isEmpty()) || (!entrada.equals("")))
      {
        if (entrada.equals("fin"))
        {
          System.out.println("Cerrando cliente");
          this.escritor.println(entrada);
          cerrarSocket();
          System.exit(0);
        }
        this.escritor.println(entrada);
        leerLinea();
        System.out.println(this.salida);
      }
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
      System.out.println("Error al crear el lector: " + e);
      System.exit(0);
    }
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
  
  private void leerLinea()
  {
    try
    {
      this.salida = this.lector.readLine();
    }
    catch (Exception e)
    {
      System.out.println("Error al leer línea: " + e);
      System.exit(0);
    }
  }
  
  private void validarCDirectorio(int tam)
  {
    if ((tam == 0) || (tam < 3) || (tam >= 4))
    {
      System.err.println("Debes de poner valores válidos");
      System.exit(0);
    }
  }
  
  private void mandarDirectorio()
  {
    try
    {
      PrintWriter escritor2 = new PrintWriter(this.socket.getOutputStream(), true);
      escritor2.println(this.directorio);
    }
    catch (Exception e)
    {
      System.err.println("Error al mandar la información, " + e.toString());
      System.exit(2);
    }
  }
  
  private void recibirDatos()
  {
    crearRecepcion();
    this.respuesta = leerBol();
    if (this.respuesta)
    {
      this.nombreArchivos = leerString();
      System.out.println("Datos en el directorio " + this.directorio + ": \n" + this.nombreArchivos);
      cerrarSocket();
      System.exit(0);
    }
    else
    {
      System.out.println("No existe el Directorio en el Servidor");
      cerrarSocket();
      System.exit(0);
    }
  }
  
  private void validarArchivoCliente(int tam)
  {
    if ((tam == 0) || (tam < 3) || (tam >= 4))
    {
      System.err.println("Debes de poner valores válidos");
      System.exit(0);
    }
  }
  
  private void crearRuta()
  {
    try
    {
      this.rutaArchivoCliente = (new File(".").getCanonicalPath() + "\\Downloads\\");
      System.out.println("El Archivo se guardará en: " + this.rutaArchivoCliente);
    }
    catch (Exception e)
    {
      System.out.println("Error al crear ruta: " + e);
    }
  }
  
  private void recibirClienteArchivo()
  {
    crearRuta();
    crearRecepcion();
    this.respuesta = leerBol();
    if (this.respuesta)
    {
      this.tam = leerLong();
      this.tam2 = leerInt();
      this.nombreArchivo = leerString();
      crearSalida();
      this.salidaDatos = new BufferedOutputStream(this.destino);
      crearEntrada();
      byte[] archivo2 = new byte[this.tam2];
      
      long num2 = 0L;
      int num;
      while ((num2 += (num = leerEntrada(archivo2))) <= this.tam)
      {
        salida(archivo2, num);
        if (num2 == this.tam) {
          break;
        }
      }
      comp();
      
      Scanner scanner2 = new Scanner(System.in);
      boolean si = false;
      String algo;
      do
      {
        System.out.println("\n¿Deseas abrir el archivo? Si/No");
        algo = scanner2.nextLine();
        if (algo.equalsIgnoreCase("Si")) {
          si = true;
        }
      } while ((!algo.equalsIgnoreCase("Si")) && (!algo.equalsIgnoreCase("No")));
      if (si) {
        abrirArchivo();
      }
      cerrarES();
      cerrarSocket();
      System.exit(0);
    }
    else
    {
      System.out.println("No existe el archivo en el Servidor");
      cerrarSocket();
      System.exit(0);
    }
  }
  
  private void recibirClienteArchivov2()
  {
    crearRuta();
    crearRecepcion();
    this.respuesta = leerBol();
    if (this.respuesta)
    {
      this.tam = leerLong();
      this.tam2 = leerInt();
      this.nombreArchivo = leerString();
      crearSalida();
      this.salidaDatos = new BufferedOutputStream(this.destino);
      crearEntrada();
      byte[] archivo2 = new byte[this.tam2];
      
      long num2 = 0L;
      
      this.valorFinal = convertirBytes(this.tam);
      int num;
      while ((num2 += (num = leerEntrada(archivo2))) <= this.tam)
      {
        salida(archivo2, num);
        float num3 = (float)num2 / (float)this.tam;
        num3 *= 100.0F;
        barraProgreso(num2, this.tam, Math.round(num3));
        if (num2 == this.tam) {
          break;
        }
      }
      comp();
      
      Scanner scanner2 = new Scanner(System.in);
      boolean si = false;
      String algo;
      do
      {
        System.out.println("\n¿Deseas abrir el archivo? Si/No");
        algo = scanner2.nextLine();
        if (algo.equalsIgnoreCase("Si")) {
          si = true;
        }
      } while ((!algo.equalsIgnoreCase("Si")) && (!algo.equalsIgnoreCase("No")));
      if (si) {
        abrirArchivo();
      }
      cerrarES();
      cerrarSocket();
      System.exit(0);
    }
    else
    {
      System.out.println("No existe el archivo en el Servidor");
      cerrarSocket();
      System.exit(0);
    }
  }
  
  private void comp()
  {
    try
    {
      this.salidaDatos.flush();
    }
    catch (Exception e)
    {
      System.err.println("Error al sacar datos: " + e);
      System.exit(0);
    }
  }
  
  public String convertirBytes(long tam)
  {
      double kb = tam / 1024;
      double mb = kb / 1024;
      double gb = mb / 1024;
      String num;
      if (tam < KB) {
          num = tam + " Bytes";
      } else if ((tam >= KB) && (tam < MB)) {
          num = String.format("%.2f", kb) + " KB";
      } else if ((tam >= MB) && (tam < GB)) {
          num = String.format("%.2f", mb) + " MB";
      } else if ((tam >= GB) && (tam < TB)) {
          num = String.format("%.2f", gb) + " GB";
      } else {
          return null;
      }
      return num;
  }
  
  private void barraProgreso(long suma, long total, int por)
  {
    int porcentaje = (int)((suma * 100) / total) / 10;
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
    System.out.print("\rRecibiendo... (" + por + "%) " + barra2 + remanente + "  " + convertirBytes(suma) + " de " + this.valorFinal);
    if (suma == total) {
      System.out.print("\n");
    }
  }
  
  private void cerrarES()
  {
    try
    {
      this.entradaDatos.close();
      this.salidaDatos.close();
    }
    catch (Exception e)
    {
      System.err.println("Error al cerrar la E/S: " + e);
      System.exit(0);
    }
  }
  
  private int leerEntrada(byte[] arreglo)
  {
    try
    {
      return this.entradaDatos.read(arreglo);
    }
    catch (Exception e)
    {
      System.err.println("Error al leer datos de la entrada: " + e);
      System.exit(0);
    }
    return -1;
  }
  
  private void crearEntrada()
  {
    try
    {
      this.entradaDatos = new BufferedInputStream(this.socket.getInputStream());
    }
    catch (Exception e)
    {
      System.err.println("Error al crear la entrada de datos: " + e);
      System.exit(0);
    }
  }
  
  private void crearSalida()
  {
    try
    {
      this.destino = new FileOutputStream(this.rutaArchivoCliente + this.nombreArchivo);
    }
    catch (Exception e)
    {
      System.err.println("Error al sacar datos: " + e);
      System.exit(0);
    }
  }
  
  private void salida(byte[] dato, int num)
  {
    try
    {
      this.salidaDatos.write(dato, 0, num);
    }
    catch (Exception e)
    {
      System.err.println("Error al pasar datos: " + e);
      System.exit(0);
    }
  }
  
  private long leerLong()
  {
    try
    {
      return this.datosRecibidos.readLong();
    }
    catch (Exception e)
    {
      System.out.println("Error leer datos: " + e);
      System.exit(0);
    }
    return -1L;
  }
  
  private int leerInt()
  {
    try
    {
      return this.datosRecibidos.readInt();
    }
    catch (Exception e)
    {
      System.out.println("Error leer datos: " + e);
      System.exit(0);
    }
    return -1;
  }
  
  private void abrirArchivo()
  {
    try
    {
      File archivo2 = new File(this.rutaArchivoCliente + this.nombreArchivo);
      Desktop.getDesktop().open(archivo2);
    }
    catch (Exception e)
    {
      System.out.println("Error al abrir archivo: " + e);
      System.exit(0);
    }
  }
  
  private void mandarRuta()
  {
    try
    {
      PrintWriter escritor2 = new PrintWriter(this.socket.getOutputStream(), true);
      escritor2.println(this.archivo);
    }
    catch (Exception e)
    {
      System.err.println("Error al mandar la información, " + e.toString());
      System.exit(2);
    }
  }
  
  private void crearRecepcion()
  {
    try
    {
      this.datosRecibidos = new DataInputStream(this.socket.getInputStream());
    }
    catch (Exception e)
    {
      System.err.println("Error al poner datos: " + e);
      System.exit(0);
    }
  }
  
  private String leerString()
  {
    try
    {
      return this.datosRecibidos.readUTF();
    }
    catch (Exception e)
    {
      System.out.println("Error leer datos: " + e);
      System.exit(0);
    }
    return null;
  }
  
  private boolean leerBol()
  {
    try
    {
      return this.datosRecibidos.readBoolean();
    }
    catch (Exception e)
    {
      System.out.println("Error leer datos: " + e);
      System.exit(0);
    }
    return false;
  }
}

