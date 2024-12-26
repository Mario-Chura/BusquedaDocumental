package com.npl;

import java.util.*;

public class IdentificadorTipoDocumento {

    // Palabras clave por categoría precargadas
    private static final Map<String, Set<String>> PALABRAS_CLAVE_POR_CATEGORIA = new HashMap<>();

    static {
        PALABRAS_CLAVE_POR_CATEGORIA.put("Carta", Set.of("de", "para", "asunto", "fecha", "estimado", "querido", "apreciado", "sr.", "sra.", "dr.", "lic.", "por la presente", "me dirijo a usted", "en relación a", "le escribo para", "con el propósito de", "adjunto", "solicitud de", "confirmación de", "recomendación", "invitación", "agradecimiento", "disculpa", "cordialmente", "atentamente", "sinceramente", "con respeto", "agradeciendo su atención", "en espera de su respuesta", "quedo a su disposición", "firma", "dirección", "teléfono de contacto", "correo electrónico", "nombre completo", "puesto o cargo", "empresa", "organización"));
        PALABRAS_CLAVE_POR_CATEGORIA.put("Artículo", Set.of("título", "autor", "resumen", "palabras clave", "publicado en", "fecha de publicación", "introducción", "referencias", "bibliografía", "citas", "conclusiones", "abstract", "discusión", "metodología", "resultados", "doi", "issn", "palabras destacadas", "subtítulos", "investigación", "análisis", "revista", "periódico"));
        PALABRAS_CLAVE_POR_CATEGORIA.put("Proyecto", Set.of("proyecto", "participantes", "fecha de inicio", "fecha de finalización", "objetivo", "alcance", "presupuesto", "tareas", "cronograma", "hitos", "resultados esperados", "recursos", "fases", "evaluación", "plan de acción", "descripción del proyecto", "propuesta", "actividades", "responsable", "equipo", "riesgos", "beneficiarios", "justificación"));
        PALABRAS_CLAVE_POR_CATEGORIA.put("Informe", Set.of("informe", "autor", "resumen ejecutivo", "resultados", "conclusiones", "fecha", "análisis", "recomendaciones", "metodología", "introducción", "gráficos", "estadísticas", "datos", "evaluación", "interpretación", "alcances", "limitaciones", "apéndices", "tablas", "referencias"));
        PALABRAS_CLAVE_POR_CATEGORIA.put("Contrato", Set.of("contrato", "partes involucradas", "fecha de firma", "cláusulas", "duración", "objeto del contrato", "firmas", "obligaciones", "condiciones", "penalidades", "pago", "resolución", "garantía", "plazo", "renuncia", "confidencialidad", "indemnización", "jurisdicción", "contratante", "contratista", "derecho aplicable", "vigencia"));
        PALABRAS_CLAVE_POR_CATEGORIA.put("Tesis o Trabajo de Investigación", Set.of("tesis", "autor", "tutor", "institución", "fecha de defensa", "palabras clave", "resumen", "introducción", "marco teórico", "metodología", "resultados", "conclusiones", "bibliografía", "referencias", "anexos", "objetivo", "hipótesis", "tema", "área de investigación", "discusión", "revisión literaria", "propuesta", "agradecimientos"));
        PALABRAS_CLAVE_POR_CATEGORIA.put("Memorando", Set.of("memorando", "de", "para", "asunto", "fecha", "cuerpo del mensaje", "instrucciones", "notificación", "recordatorio", "comunicación interna", "firma", "aprobación", "encabezado", "tema", "anexo", "reunión", "circular", "tareas asignadas", "responsabilidades"));
        PALABRAS_CLAVE_POR_CATEGORIA.put("Factura", Set.of("factura", "número de factura", "cliente", "proveedor", "fecha de emisión", "fecha de vencimiento", "total", "concepto", "cantidad", "precio unitario", "iva", "descuento", "monto a pagar", "detalles del producto", "forma de pago", "condiciones de pago", "firma", "código de barras", "recibo"));
        PALABRAS_CLAVE_POR_CATEGORIA.put("Minuta de Reunión", Set.of("minuta de reunión", "fecha", "lugar", "participantes", "agenda", "temas tratados", "decisiones", "acuerdos", "tareas asignadas", "próxima reunión", "objetivo", "moderador", "secretario", "hora de inicio", "hora de cierre", "responsables", "resumen"));
        PALABRAS_CLAVE_POR_CATEGORIA.put("Certificado", Set.of("certificado", "se otorga a", "por", "fecha de emisión", "reconocimiento", "participación", "aprobación", "capacitación", "distinción", "entidad emisora", "nombre del receptor", "firma", "sello", "evento", "curso", "examen", "competencia", "título"));
        PALABRAS_CLAVE_POR_CATEGORIA.put("Presentación", Set.of("presentación", "título", "autor", "tema", "fecha", "objetivos", "resumen", "diapositiva", "puntos clave", "introducción", "conclusión", "público objetivo", "gráficos", "imágenes", "ejemplos", "citas", "agenda"));
        PALABRAS_CLAVE_POR_CATEGORIA.put("Noticia", Set.of("noticia", "título", "subtítulo", "fecha", "autor", "fuente", "cuerpo", "declaraciones", "hechos", "opinión", "reportaje", "entrevista", "testimonio", "evento", "lugar", "hora", "imagen"));
        PALABRAS_CLAVE_POR_CATEGORIA.put("Ensayo", Set.of("ensayo", "título", "autor", "introducción", "argumentos", "opinión", "conclusión", "referencias", "bibliografía", "análisis", "reflexión", "tema", "subtítulos", "crítica", "hipótesis", "desarrollo", "citas", "propuesta"));
        PALABRAS_CLAVE_POR_CATEGORIA.put("Solicitud", Set.of("solicitud", "de", "para", "asunto", "fecha", "firma", "aprobación", "requisitos", "documento adjunto", "solicito", "autorización", "justificación", "motivo", "beneficio", "procedimiento", "entrega"));
        PALABRAS_CLAVE_POR_CATEGORIA.put("Resumen Ejecutivo", Set.of("resumen ejecutivo", "proyecto", "análisis", "resultados", "conclusiones", "introducción", "objetivo", "recomendaciones", "estadísticas", "datos clave", "impacto", "beneficio", "alcance", "descripción", "tareas"));
        PALABRAS_CLAVE_POR_CATEGORIA.put("Reporte Técnico", Set.of("reporte técnico", "autor", "fecha", "introducción", "metodología", "resultados", "conclusiones", "recomendaciones", "datos", "tablas", "gráficos", "estadísticas", "evaluación", "problemas", "soluciones", "resumen", "costo", "tiempo", "alcance"));
        PALABRAS_CLAVE_POR_CATEGORIA.put("Declaración Jurada", Set.of("declaración jurada", "declaro", "nombre completo", "documento de identidad", "fecha", "firma", "bajo juramento", "afirmo", "hechos", "veracidad", "testimonio", "autoridad", "confidencialidad", "testigos"));
        PALABRAS_CLAVE_POR_CATEGORIA.put("Currículum Vitae (CV)", Set.of("currículum", "nombre", "dirección", "teléfono", "correo electrónico", "educación", "experiencia laboral", "habilidades", "idiomas", "certificaciones", "referencias", "objetivo profesional", "proyectos", "logros"));
        PALABRAS_CLAVE_POR_CATEGORIA.put("Oficio", Set.of("oficio", "de", "para", "asunto", "fecha", "firma", "documento", "remitente", "destinatario", "aprobación", "sello", "notificación", "circular", "instrucciones", "número de oficio"));
        PALABRAS_CLAVE_POR_CATEGORIA.put("Manual", Set.of("manual", "título", "autor", "fecha", "instrucciones", "procedimientos", "uso", "objetivo", "normas", "instalación", "configuración", "guía", "ejemplo", "resolución de problemas"));
        PALABRAS_CLAVE_POR_CATEGORIA.put("Recibo", Set.of("recibo", "fecha", "número", "monto", "concepto", "cliente", "proveedor", "pago", "firma", "documento", "sello", "método de pago", "referencia", "total"));
        PALABRAS_CLAVE_POR_CATEGORIA.put("Publicidad", Set.of("publicidad", "producto", "servicio", "oferta", "descuento", "promoción", "cliente", "slogan", "marca", "fecha", "imágenes", "anuncio", "contacto", "sitio web", "redes sociales"));
    }

    // Método para obtener las palabras clave de una categoría específica
    public static Set<String> getPalabrasClavePorCategoria(String categoria) {
        return PALABRAS_CLAVE_POR_CATEGORIA.getOrDefault(categoria, Set.of());
    }

    // Método que devuelve solo el tipo de documento
    public static String identificarTipoDocumento(String texto) {
        return identificarTipoDocumentoConCoincidencias(texto).getKey();
    }

    // Método que devuelve el tipo de documento y las coincidencias
    public static Map.Entry<String, Integer> identificarTipoDocumentoConCoincidencias(String texto) {
        Map<String, Integer> puntuaciones = new HashMap<>();

        // Preprocesar el texto (normalizar)
        String textoNormalizado = normalizarTexto(texto);

        // Contar coincidencias únicas por categoría
        for (Map.Entry<String, Set<String>> categoria : PALABRAS_CLAVE_POR_CATEGORIA.entrySet()) {
            String nombreCategoria = categoria.getKey();
            Set<String> palabrasClave = categoria.getValue();

            // Contar coincidencias
            int coincidencias = 0;
            for (String palabraClave : palabrasClave) {
                if (textoNormalizado.contains(palabraClave.toLowerCase())) {
                    coincidencias++;
                }
            }
            puntuaciones.put(nombreCategoria, coincidencias);
        }

        // Determinar la categoría con mayor puntuación
        return puntuaciones.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElse(Map.entry("Documento Desconocido", 0));
    }

    // Método para normalizar el texto
    private static String normalizarTexto(String texto) {
        // Convertir a minúsculas y eliminar caracteres especiales excepto espacios
        return texto.toLowerCase().replaceAll("[^a-záéíóúñ\\s]", "").trim();
    }
}

