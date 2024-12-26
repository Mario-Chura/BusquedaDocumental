package com.npl;

import edu.stanford.nlp.ling.CoreAnnotations.*;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProcesadorArticulo {

    private static StanfordCoreNLP pipeline;

    static {
        inicializar();
    }

    public static void inicializar() {
        if (pipeline == null) {
            Properties props = new Properties();
            props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner");
            props.setProperty("tokenize.language", "es");
            pipeline = new StanfordCoreNLP(props);
        }
    }

    public static Map<String, List<String>> procesar(String texto, List<String> palabrasClaveCargadas) {
        Map<String, List<String>> atributos = new LinkedHashMap<>();
        atributos.put("Título", new ArrayList<>());
        atributos.put("Autor", new ArrayList<>());
        atributos.put("Fecha de publicación", new ArrayList<>());
        atributos.put("DOI", new ArrayList<>());
        atributos.put("Palabras clave", new ArrayList<>());
        atributos.put("Resumen", new ArrayList<>());
        atributos.put("Palabras Clave Coincidentes", new ArrayList<>());

        Annotation documento = new Annotation(texto);
        pipeline.annotate(documento);

        // Usar un conjunto para evitar duplicados en las palabras clave coincidentes
        Set<String> palabrasClaveUnicas = new HashSet<>();

        // Bandera para identificar si hemos pasado "Referencias" o "Bibliografía"
        boolean despuesDeReferencias = false;

        // Extraer entidades nombradas con NER
        for (CoreMap oracion : documento.get(SentencesAnnotation.class)) {
            String oracionTexto = oracion.get(TextAnnotation.class).toLowerCase();

            // Verificar si hemos llegado a "Referencias" o "Bibliografía"
            if (oracionTexto.contains("referencias") || oracionTexto.contains("bibliografía")) {
                despuesDeReferencias = true;
            }

            if (!despuesDeReferencias) {
                for (CoreLabel token : oracion.get(TokensAnnotation.class)) {
                    String palabra = token.originalText();
                    String nerEtiqueta = token.get(NamedEntityTagAnnotation.class);

                    if ("PERSON".equals(nerEtiqueta)) {
                        atributos.get("Autor").add(palabra);
                    } else if ("DATE".equals(nerEtiqueta)) {
                        atributos.get("Fecha de publicación").add(palabra);
                    } else if ("ORGANIZATION".equals(nerEtiqueta)) {
                        // Organizaciones pueden ser relevantes para la revista o institución
                        atributos.computeIfAbsent("Organización", k -> new ArrayList<>()).add(palabra);
                    }

                    // Verificar si la palabra coincide con las palabras clave cargadas
                    if (palabrasClaveCargadas.contains(palabra.toLowerCase())) {
                        palabrasClaveUnicas.add(palabra);
                    }
                }
            }
        }

        // Convertir el conjunto de palabras clave únicas en una lista y asignarlo al mapa
        atributos.get("Palabras Clave Coincidentes").addAll(palabrasClaveUnicas);

        // Extraer título, resumen, DOI y palabras clave del texto
        String titulo = extraerPatron(texto, "Título:\\s*(.+)");
        if (titulo != null) {
            atributos.get("Título").add(titulo);
        }

        String resumen = extraerPatron(texto, "Resumen:\\s*(.+)");
        if (resumen != null) {
            atributos.get("Resumen").add(resumen);
        }

        String doi = extraerPatron(texto, "DOI:\\s*([0-9.]+/[a-z0-9.]+)");
        if (doi != null) {
            atributos.get("DOI").add(doi);
        }

        String palabrasClave = extraerPatron(texto, "Palabras clave:\\s*(.+)");
        if (palabrasClave != null) {
            atributos.get("Palabras clave").addAll(Arrays.asList(palabrasClave.split(",\\s*")));
        }

        return atributos;
    }

    private static String extraerPatron(String texto, String patron) {
        Pattern pattern = Pattern.compile(patron);
        Matcher matcher = pattern.matcher(texto);
        return matcher.find() ? matcher.group(1) : null;
    }

    public static void imprimirAtributos(Map<String, List<String>> atributos) {
        // Crear un mapa para separar "Palabras Clave Coincidentes"
        List<String> palabrasClaveCoincidentes = atributos.remove("Palabras Clave Coincidentes");

        // Imprimir atributos que no son "Palabras Clave Coincidentes"
        atributos.forEach((clave, valor) -> System.out.println(clave + ": " + valor));

        // Imprimir "Palabras Clave Coincidentes" al final
        System.out.println("Palabras Clave Coincidentes: " + palabrasClaveCoincidentes);
    }
}

