package com.npl;

import edu.stanford.nlp.ling.CoreAnnotations.*;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProcesadorProyecto {

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
        atributos.put("Nombre del Proyecto", new ArrayList<>());
        atributos.put("Participantes", new ArrayList<>());
        atributos.put("Fechas", new ArrayList<>());
        atributos.put("Objetivo", new ArrayList<>());
        atributos.put("Alcance", new ArrayList<>());
        atributos.put("Presupuesto", new ArrayList<>());
        atributos.put("Tareas", new ArrayList<>());
        atributos.put("Recursos", new ArrayList<>());
        atributos.put("Hitos", new ArrayList<>());
        atributos.put("Riesgos", new ArrayList<>());
        atributos.put("Palabras Clave Coincidentes", new ArrayList<>());

        Annotation documento = new Annotation(texto);
        pipeline.annotate(documento);

        // Usar un conjunto para evitar duplicados en las palabras clave coincidentes
        Set<String> palabrasClaveUnicas = new HashSet<>();

        // Extraer entidades nombradas con NER
        for (CoreMap oracion : documento.get(SentencesAnnotation.class)) {
            for (CoreLabel token : oracion.get(TokensAnnotation.class)) {
                String palabra = token.originalText();
                String nerEtiqueta = token.get(NamedEntityTagAnnotation.class);

                if ("PERSON".equals(nerEtiqueta)) {
                    atributos.get("Participantes").add(palabra);
                } else if ("DATE".equals(nerEtiqueta)) {
                    atributos.get("Fechas").add(palabra);
                } else if ("MONEY".equals(nerEtiqueta)) {
                    atributos.get("Presupuesto").add(palabra);
                }

                // Verificar si la palabra coincide con las palabras clave cargadas
                if (palabrasClaveCargadas.contains(palabra.toLowerCase())) {
                    palabrasClaveUnicas.add(palabra);
                }
            }
        }

        // Convertir el conjunto de palabras clave únicas en una lista y asignarlo al mapa
        atributos.get("Palabras Clave Coincidentes").addAll(palabrasClaveUnicas);

        // Extraer campos específicos del texto con patrones
        String nombreProyecto = extraerPatron(texto, "(?i)proyecto:\\s*(.+)");
        if (nombreProyecto != null) {
            atributos.get("Nombre del Proyecto").add(nombreProyecto);
        }

        String objetivo = extraerPatron(texto, "(?i)objetivo:\\s*(.+)");
        if (objetivo != null) {
            atributos.get("Objetivo").add(objetivo);
        }

        String alcance = extraerPatron(texto, "(?i)alcance:\\s*(.+)");
        if (alcance != null) {
            atributos.get("Alcance").add(alcance);
        }

        String tareas = extraerPatron(texto, "(?i)tareas:\\s*(.+)");
        if (tareas != null) {
            atributos.get("Tareas").add(tareas);
        }

        String recursos = extraerPatron(texto, "(?i)recursos:\\s*(.+)");
        if (recursos != null) {
            atributos.get("Recursos").add(recursos);
        }

        String hitos = extraerPatron(texto, "(?i)hitos:\\s*(.+)");
        if (hitos != null) {
            atributos.get("Hitos").add(hitos);
        }

        String riesgos = extraerPatron(texto, "(?i)riesgos:\\s*(.+)");
        if (riesgos != null) {
            atributos.get("Riesgos").add(riesgos);
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
