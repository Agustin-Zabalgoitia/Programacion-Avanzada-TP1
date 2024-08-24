package imagen;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.IOException;
import java.io.PrintWriter;

public class Imagen {
	
	//Información sobre el formato PGM
	//https://netpbm.sourceforge.net/doc/pgm.html
	//En este código se le dará soporte a "Plain PGM"
	
	static final String CODIFICACION_ARCHIVO_SALIDA = "UTF-8";	//Codificación en la que se guardará la imagen modificada
	static final String EXTENSION = ".pgm";			//Extensión en la que se guardará la imagen de salida
	static final String INICIO_COMENTARIO = "#";	//Caracter a buscar en el archivo de entrada para poder ignorar los comentarios
	static final String MAGIC_NUMBER = "P2";		//Especifica el tipo de archivo PGM
	static final String MENSAJE_ERROR_ESCRITURA_ARCHIVO = "Ocurrió un error escribiendo el archivo";	//Mensaje a mostrar por consola en caso de que haya un error al cargar la imagen
	static final String MENSAJE_ERROR_LECTURA_ARCHIVO = "Ocurrió un error leyendo el archivo";	//Mensaje a mostrar por consola en caso de que haya un error al cargar la imagen
	static final int MENOR_VALOR_ESCALA_GRISES = 0;	//Color negro
	static final int MENOR_VALOR_MATRIZ = 0;		//El tamaño mínimo que puede tener una matriz
	static final String REGEX_ESPACIO_BLANCO = "\s+";	//Regex que busca espacios en blanco
	static final String REGEX_PUNTO = "\\.(?=[^\\.]*$)";	//Regex utilizado para buscar el punto en el nombre de la imagen cargada para renombrarla
	static final String SUFIJO_ARCHIVO_SALIDA = " NUEVO";	//Texto a añadir al nombre de la imagen de salida
	
	String nombreArchivo;	//nombre del archivo, sin incluir extensión
	String rutaArchivo;		//directorio donde está guardado la imagen
	int maxValEscala = MENOR_VALOR_MATRIZ;	//El valor máximo de la imagen cargada actualmente, será el color blanco
	int ancho = MENOR_VALOR_MATRIZ;	//Ancho de la imagen 
	int alto = MENOR_VALOR_MATRIZ;	//Alto de la imagen
	int imagen[][];	//Matriz que contendrá los números que representan la imagen
	
	public Imagen(String rutaArchivo, String nombreArchivo) {
		this.nombreArchivo = nombreArchivo;
		this.rutaArchivo = rutaArchivo + "/";
		this.cargar();
	}

	private void cargar() {
		try {
	      File archivo = new File(this.rutaArchivo + this.nombreArchivo);
	      
	      Scanner lector = new Scanner(archivo);
	      
	      String linea = lector.nextLine(); //Tipo de archivo PGM
	      System.out.println("Tipo de archivo: " + linea);
	      
	      linea = lector.nextLine();
	      if(linea.contains(INICIO_COMENTARIO))
	    	  linea = lector.nextLine(); //Comentarios, los ignoramos. Esto asume que los comentarios siempre van a estar en la segunda línea
	   
	      String vectorDatos[] = linea.split(REGEX_ESPACIO_BLANCO); //Obtengo un array con todos los valores de la línea
	      this.ancho = Integer.parseInt(vectorDatos[0]);
	      this.alto = Integer.parseInt(vectorDatos[1]);
	      this.imagen = new int[this.alto][this.ancho]; //Declaro la matriz donde se guardará la imagen, acorde con el tamaño indicado por el archivo de entrada
	      System.out.println("Alto: " + this.alto + "  Ancho: " + this.ancho);
	      
	      linea = lector.nextLine(); //Máximo valor de la escala de grises
	      this.maxValEscala = Integer.parseInt(linea);
	      System.out.println("Escala de grises: " + linea);
	      System.out.println();
	      
	      int i = MENOR_VALOR_MATRIZ;
	      int j = MENOR_VALOR_MATRIZ;
	      
	      while (lector.hasNextInt()) {
	    	  int dato = lector.nextInt();
	    	  this.imagen[i][j] = dato;
	    	  j++;
	    	  if(j >= this.ancho)
	    	  {
	    		  j=MENOR_VALOR_MATRIZ;
	    		  i++;
	    	  }
	      }
	      
	      lector.close();
	      
	    } catch (FileNotFoundException e) {
	      System.out.println(MENSAJE_ERROR_LECTURA_ARCHIVO);
	      e.printStackTrace();
	    }
	}
	
	public void guardar() {
		this.guardar(this.nombreArchivo.split(REGEX_PUNTO)[0] + SUFIJO_ARCHIVO_SALIDA);
	}
	
	public void guardar(String nombreArchivoSalida) {
		try {
			String nuevoNombreArchivo = this.rutaArchivo + nombreArchivoSalida + EXTENSION;
			PrintWriter writer = new PrintWriter(nuevoNombreArchivo, CODIFICACION_ARCHIVO_SALIDA);
			writer.println(MAGIC_NUMBER);
			writer.println(this.ancho + " " + this.alto);
			writer.println(this.maxValEscala);
			for(int i = MENOR_VALOR_MATRIZ ; i<this.alto ; i++)
			{
				for(int j = MENOR_VALOR_MATRIZ ; j<this.ancho ; j++) {
					writer.print(this.imagen[i][j] + " ");
				}
					writer.println();
			}
			writer.close();
				
	    } catch (IOException e) {
	      System.out.println(MENSAJE_ERROR_ESCRITURA_ARCHIVO);
	      e.printStackTrace();
	    }	
	}
	
	//Esto solo era una prueba para ver si se guardarba bien la imagen 
	public void invertir() {
		for(int i = MENOR_VALOR_MATRIZ ; i<this.alto ; i++)
		{
			for(int j = MENOR_VALOR_MATRIZ ; j<this.ancho ; j++) {
				this.imagen[i][j] = this.maxValEscala - this.imagen[i][j];
			}
		}
	}
	
	//umbral = número de 0 a 1
	public void umbralizar(float umbral) {
		if(umbral > 1f || umbral < 0f)
			umbral = 0.5f;
		
		for(int i = MENOR_VALOR_MATRIZ ; i<this.alto ; i++)
		{
			for(int j = MENOR_VALOR_MATRIZ ; j<this.ancho ; j++) {
				this.imagen[i][j] = this.imagen[i][j] >= this.maxValEscala*umbral ?this.maxValEscala:MENOR_VALOR_ESCALA_GRISES;
			}
		}
	}

	public void dilatacion(int lv, int lh) {
		int[][] newMatrix = new int[this.alto][this.ancho];
		
		for(int i=0; i < this.alto; i++)
			for(int j=0; j<this.ancho; j++)
				newMatrix[i][j] = dilatar(this.imagen, i, j, lv, lh);
		
		this.imagen = newMatrix;
	}
	
	private static int dilatar(int[][]m, int pv, int ph, int lv, int lh) {
		int max = 0;
		int fi = (pv - lv/2 >= 0) ? pv - lv/2 : 0;
		int ci = (ph - lh/2 >= 0) ? ph - lh/2 : 0;
		int ff = (pv + lv/2 <= m.length-1) ? pv + lv/2 : m.length-1;
		int cf = (ph + lh/2 <= m[0].length-1) ? ph + lh/2 : m[0].length-1;
		
		for(int i=fi; i<=ff; i++)
			for(int j=ci; j<=cf; j++)
				max = max<m[i][j]?m[i][j]:max; 
		
		return max;
	}
	
	public void erosion(int lv, int lh) {
		int[][] newMatrix = new int[this.alto][this.ancho];
		
		for(int i=0; i < this.alto; i++)
			for(int j=0; j<this.ancho; j++)
				newMatrix[i][j] = erosionar(this.imagen, i, j, lv, lh);
		
		this.imagen = newMatrix;
	}
	
	private static int erosionar(int[][]m, int pv, int ph, int lv, int lh) {
		int min = Integer.MAX_VALUE;
		int fi = (pv - lv/2 >= 0) ? pv - lv/2 : 0;
		int ci = (ph - lh/2 >= 0) ? ph - lh/2 : 0;
		int ff = (pv + lv/2 <= m.length-1) ? pv + lv/2 : m.length-1;
		int cf = (ph + lh/2 <= m[0].length-1) ? ph + lh/2 : m[0].length-1;
		
		for(int i=fi; i<=ff; i++)
			for(int j=ci; j<=cf; j++)
				min = min>m[i][j]?m[i][j]:min; 
		
		return min;
	}
	
}
