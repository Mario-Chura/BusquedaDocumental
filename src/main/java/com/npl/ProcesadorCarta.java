package com.npl;

import edu.stanford.nlp.ling.CoreAnnotations.*;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProcesadorCarta {

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
        atributos.put("Asunto", new ArrayList<>());
        atributos.put("Fecha", new ArrayList<>());
        atributos.put("Remitente", new ArrayList<>());
        atributos.put("Destinatario", new ArrayList<>());
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
                    if (atributos.get("Remitente").isEmpty()) {
                        atributos.get("Remitente").add(palabra);
                    } else if (atributos.get("Destinatario").isEmpty()) {
                        atributos.get("Destinatario").add(palabra);
                    }
                } else if ("DATE".equals(nerEtiqueta)) {
                    atributos.get("Fecha").add(palabra);
                }

                // Verificar si la palabra coincide con las palabras clave cargadas
                if (palabrasClaveCargadas.contains(palabra.toLowerCase())) {
                    palabrasClaveUnicas.add(palabra);
                }
            }
        }

        // Convertir el conjunto de palabras clave únicas en una lista y asignarlo al mapa
        atributos.get("Palabras Clave Coincidentes").addAll(palabrasClaveUnicas);

        // Extraer fechas con expresión regular si NER no las detecta
        if (atributos.get("Fecha").isEmpty()) {
            List<String> fechasEncontradas = extraerFechas(texto);
            atributos.get("Fecha").addAll(fechasEncontradas);
        }

        // Extraer asunto del texto
        String asunto = detectarAsunto(texto);
        if (asunto != null) {
            atributos.get("Asunto").add(asunto);
        }

        return atributos;
    }

    private static List<String> extraerFechas(String texto) {
        List<String> fechas = new ArrayList<>();
        Pattern patronFecha = Pattern.compile("\\b\\d{1,2}/\\d{1,2}/\\d{4}\\b");
        Matcher matcher = patronFecha.matcher(texto);
        while (matcher.find()) {
            fechas.add(matcher.group());
        }
        return fechas;
    }

    private static String extraerPatron(String texto, String patron) {
        Pattern pattern = Pattern.compile(patron);
        Matcher matcher = pattern.matcher(texto);
        return matcher.find() ? matcher.group(1) : null;
    }

    private static String detectarAsunto(String texto) {
        // 1. Intentar extraer asunto explícito con patrón
        String asuntoExplicito = extraerPatron(texto, "Asunto:\\s*(.+)");
        if (asuntoExplicito != null) {
            return asuntoExplicito;
        }

        // 2. Inferir el asunto de las primeras líneas si no está explícito
        String[] lineas = texto.split("\\n");
        for (String linea : lineas) {
            linea = linea.trim();
            // Ignorar líneas vacías o saludos comunes
            if (!linea.isEmpty() && !linea.matches("^(Estimado|Querido|Atentamente|Saludos).*")) {
                return linea; // Retorna la primera línea significativa
            }
        }

        return null; // Si no se encuentra nada
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
