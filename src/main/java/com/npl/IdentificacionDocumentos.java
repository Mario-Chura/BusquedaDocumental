package com.npl;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class IdentificacionDocumentos {

    public static void main(String[] args) {
        try {
            // Leer archivo PDF
            String pdfPath = "Proyecto.pdf"; // Cambia este nombre al archivo deseado
            String textoExtraido = leerArchivo(pdfPath);

            // Limpiar el texto extraído
            String textoLimpio = limpiarTexto(textoExtraido);

            // Guardar texto limpio en un archivo .txt
            String txtPath = generarRutaSalida(pdfPath);
            guardarTextoEnArchivo(textoLimpio, txtPath);

            System.out.println("El texto limpio se ha guardado en: " + txtPath);

            // Leer el archivo de texto limpio generado
            String textoDocumento = Files.readString(Paths.get(txtPath));

            // Identificar tipo de documento y número de coincidencias
            Map.Entry<String, Integer> resultado = IdentificadorTipoDocumento.identificarTipoDocumentoConCoincidencias(textoDocumento);
            String tipoDocumento = resultado.getKey();
            int coincidencias = resultado.getValue();

            // Procesar atributos específicos según el tipo de documento
            Map<String, List<String>> atributos = new HashMap<>();
            if ("Carta".equals(tipoDocumento)) {
                // Obtener las palabras clave para el tipo "Carta"
                List<String> palabrasClaveCargadas = new ArrayList<>(IdentificadorTipoDocumento.getPalabrasClavePorCategoria("Carta"));
                // Procesar con las palabras clave cargadas
                atributos = ProcesadorCarta.procesar(textoDocumento, palabrasClaveCargadas);
            } else if ("Artículo".equals(tipoDocumento)) {
                // Obtener las palabras clave para el tipo "Artículo"
                List<String> palabrasClaveCargadas = new ArrayList<>(IdentificadorTipoDocumento.getPalabrasClavePorCategoria("Artículo"));
                // Procesar con las palabras clave cargadas
                atributos = ProcesadorArticulo.procesar(textoDocumento, palabrasClaveCargadas);
            } else if ("Proyecto".equals(tipoDocumento)) {
                // Obtener las palabras clave para el tipo "Artículo"
                List<String> palabrasClaveCargadas = new ArrayList<>(IdentificadorTipoDocumento.getPalabrasClavePorCategoria("Proyecto"));
                // Procesar con las palabras clave cargadas
                atributos = ProcesadorProyecto.procesar(textoDocumento, palabrasClaveCargadas);
            }else {
                System.out.println("Procesador no disponible para el tipo de documento: " + tipoDocumento);
                return;
            }

            // Mostrar el tipo de documento y número de coincidencias
            System.out.println("Tipo de documento identificado: " + tipoDocumento);
            System.out.println("Número de coincidencias con palabras clave: " + coincidencias);

            // Mostrar los atributos extraídos
            System.out.println("\nAtributos extraídos:");
            atributos.forEach((clave, valor) -> System.out.println(clave + ": " + valor));

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Método para leer archivos (PDF o texto)
    public static String leerArchivo(String rutaArchivo) throws IOException {
        if (rutaArchivo.toLowerCase().endsWith(".pdf")) {
            // Leer archivo PDF desde el classpath
            try (InputStream is = IdentificacionDocumentos.class.getClassLoader().getResourceAsStream(rutaArchivo);
                 PDDocument documento = PDDocument.load(is)) {
                PDFTextStripper textStripper = new PDFTextStripper();
                return textStripper.getText(documento);
            } catch (NullPointerException e) {
                throw new FileNotFoundException("Archivo no encontrado en recursos: " + rutaArchivo);
            }
        } else {
            // Leer archivo de texto
            InputStream is = IdentificacionDocumentos.class.getClassLoader().getResourceAsStream(rutaArchivo);
            if (is != null) {
                return new String(is.readAllBytes(), "UTF-8");
            }

            Path path = Paths.get(rutaArchivo);
            if (Files.exists(path)) {
                return Files.readString(path, java.nio.charset.StandardCharsets.UTF_8);
            }

            throw new IllegalArgumentException("Archivo no encontrado: " + rutaArchivo);
        }
    }

    // Método para limpiar texto eliminando caracteres especiales, pero conservando saltos de línea
    public static String limpiarTexto(String texto) {
        return texto.replaceAll("[^\\p{L}\\p{N}\\p{P}\\p{Z}\\r\\n]", "") // Permitir saltos de línea (\r\n)
                    .replaceAll("(\\r?\\n)+", "\n") // Asegurar saltos de línea consistentes (\n)
                    .trim();
    }

    // Método para guardar texto limpio en un archivo .txt, respetando saltos de línea
    public static void guardarTextoEnArchivo(String texto, String outputPath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
            // Dividir el texto en líneas para asegurarse de que cada línea se escribe correctamente
            String[] lineas = texto.split("\n");
            for (String linea : lineas) {
                writer.write(linea);
                writer.newLine(); // Escribir un salto de línea después de cada línea
            }
        }
    }

    // Método para generar la ruta del archivo de salida basado en el archivo PDF
    public static String generarRutaSalida(String rutaArchivoPDF) {
        return rutaArchivoPDF.replaceAll("\\.pdf$", "_limpio.txt");
    }
}

