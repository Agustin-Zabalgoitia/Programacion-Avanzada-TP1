package main;

import imagen.Imagen;

public class Main {

	static final String NOMBRE_ARCHIVO_PGM = "duck.pgm";
	static final String RUTA_IMAGENES = "imagenes";
	static final float PORCENTAJE_UMBRALIZACION = 0.7f;
	static final int DILATACION_LV = 3;
	static final int DILATACION_LH = 3;
	static final int EROSION_LV = 3;
	static final int EROSION_LH = 3;
	
	public static void main(String[] args) {
		System.out.println("=== INICIO ===");
		
		Imagen umbralizada = new Imagen(RUTA_IMAGENES, NOMBRE_ARCHIVO_PGM);
		umbralizada.umbralizar(PORCENTAJE_UMBRALIZACION);
		umbralizada.guardar(NOMBRE_ARCHIVO_PGM + " UMBRALIZADA");
		
		Imagen dilatada = new Imagen(RUTA_IMAGENES, NOMBRE_ARCHIVO_PGM);
		dilatada.umbralizar(PORCENTAJE_UMBRALIZACION);
		dilatada.dilatacion(DILATACION_LV, DILATACION_LH);
		dilatada.guardar(NOMBRE_ARCHIVO_PGM + " DILATADA");
		
		Imagen erosionada = new Imagen(RUTA_IMAGENES, NOMBRE_ARCHIVO_PGM);
		erosionada.umbralizar(PORCENTAJE_UMBRALIZACION);
		erosionada.erosion(EROSION_LV, EROSION_LH);
		erosionada.guardar(NOMBRE_ARCHIVO_PGM + " EROSIONADA");
		
		System.out.println("===  FIN ===");
	}
}