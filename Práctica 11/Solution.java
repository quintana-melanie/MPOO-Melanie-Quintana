import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

interface Publicable {
    ResultadoPublicacion publicar();
    Long getId();
    String getAutor();
}

abstract class PublicacionDTO {
    private final Long id;
    private final String autor;
    private final String titulo;
    private final int meGusta;
    private final int comentarios;
    private final int compartidos;

    protected PublicacionDTO(
            Long id,
            String autor,
            String titulo,
            int meGusta,
            int comentarios,
            int compartidos
    ) {
        this.id = id;
        this.autor = autor;
        this.titulo = titulo;
        this.meGusta = meGusta;
        this.comentarios = comentarios;
        this.compartidos = compartidos;
    }

    public Long getId() { return id; }
    public String getAutor() { return autor; }
    public String getTitulo() { return titulo; }
    public int getMeGusta() { return meGusta; }
    public int getComentarios() { return comentarios; }
    public int getCompartidos() { return compartidos; }
}

final class PublicacionTextoDTO extends PublicacionDTO {
    private final String texto;

    public PublicacionTextoDTO(
            Long id,
            String autor,
            String titulo,
            int meGusta,
            int comentarios,
            int compartidos,
            String texto
    ) {
        super(id, autor, titulo, meGusta, comentarios, compartidos);
        this.texto = texto;
    }

    public String getTexto() { return texto; }
}

final class PublicacionImagenDTO extends PublicacionDTO {
    private final String descripcionImagen;
    private final String formato;

    public PublicacionImagenDTO(
            Long id,
            String autor,
            String titulo,
            int meGusta,
            int comentarios,
            int compartidos,
            String descripcionImagen,
            String formato
    ) {
        super(id, autor, titulo, meGusta, comentarios, compartidos);
        this.descripcionImagen = descripcionImagen;
        this.formato = formato;
    }

    public String getDescripcionImagen() { return descripcionImagen; }
    public String getFormato() { return formato; }
}

final class PublicacionVideoDTO extends PublicacionDTO {
    private final int duracionSegundos;
    private final int visualizaciones;

    public PublicacionVideoDTO(
            Long id,
            String autor,
            String titulo,
            int meGusta,
            int comentarios,
            int compartidos,
            int duracionSegundos,
            int visualizaciones
    ) {
        super(id, autor, titulo, meGusta, comentarios, compartidos);
        this.duracionSegundos = duracionSegundos;
        this.visualizaciones = visualizaciones;
    }

    public int getDuracionSegundos() { return duracionSegundos; }
    public int getVisualizaciones() { return visualizaciones; }
}

/* =========================================================
   SOLUCION DE DOMINIO - VIBE
   ========================================================= */

enum FormatoImagen {
    JPG,
    PNG,
    WEBP
}

enum EstadoPublicacion {
    RECHAZADA,
    PUBLICADA,
    DESTACADA,
    VIRAL
}

class ResultadoPublicacion {
    private final Long idPublicacion;
    private final String autor;
    private final String descripcion;
    private final double relevancia;
    private final EstadoPublicacion estado;
    private final LocalDateTime fechaProcesamiento;

    public ResultadoPublicacion(
            Long idPublicacion,
            String autor,
            String descripcion,
            double relevancia,
            EstadoPublicacion estado,
            LocalDateTime fechaProcesamiento
    ) {
        this.idPublicacion = idPublicacion;
        this.autor = autor;
        this.descripcion = descripcion;
        this.relevancia = relevancia;
        this.estado = estado;
        this.fechaProcesamiento = fechaProcesamiento;
    }

    public Long getIdPublicacion() { return idPublicacion; }
    public String getAutor() { return autor; }
    public String getDescripcion() { return descripcion; }
    public double getRelevancia() { return relevancia; }
    public EstadoPublicacion getEstado() { return estado; }
    public LocalDateTime getFechaProcesamiento() { return fechaProcesamiento; }
}

abstract class Publicacion implements Publicable {
    protected final Long id;
    protected final String autor;
    protected final String titulo;
    protected final LocalDateTime fechaCreacion;
    protected final int meGusta;
    protected final int comentarios;
    protected final int compartidos;

    protected Publicacion(
            Long id,
            String autor,
            String titulo,
            int meGusta,
            int comentarios,
            int compartidos
    ) {
        this.id = id;
        this.autor = autor;
        this.titulo = titulo;
        this.fechaCreacion = LocalDateTime.now();
        this.meGusta = meGusta;
        this.comentarios = comentarios;
        this.compartidos = compartidos;
    }

    public Long getId() { return id; } 

    public String getAutor() { return autor; }

    public String getTitulo() { return titulo; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public int getMeGusta() { return meGusta; }
    public int getComentarios() { return comentarios; }
    public int getCompartidos() { return compartidos; }

    private boolean validarDatosGenerales() {
        return id != null && id > 0
                && autor != null && !autor.isBlank()
                && titulo != null && !titulo.isBlank()
                && meGusta >= 0
                && comentarios >= 0
                && compartidos >= 0;
    }

    protected abstract boolean validarContenido();
    protected abstract double calcularRelevancia();
    protected abstract String getDescripcion();

    public final ResultadoPublicacion publicar() {
        String descripcion = getDescripcion();

        if (!validarDatosGenerales() || !validarContenido()) {
            return new ResultadoPublicacion(
                    id,
                    autor,
                    descripcion,
                    0.0,
                    EstadoPublicacion.RECHAZADA,
                    LocalDateTime.now()
            );
        }

        double relevancia = calcularRelevancia();
        EstadoPublicacion estado;

        if (relevancia >= 300.0) {
            estado = EstadoPublicacion.VIRAL;
        } else if (relevancia >= 100.0) {
            estado = EstadoPublicacion.DESTACADA;
        } else {
            estado = EstadoPublicacion.PUBLICADA;
        }

        return new ResultadoPublicacion(
                id,
                autor,
                descripcion,
                relevancia,
                estado,
                LocalDateTime.now()
        );
    }
}

class PublicacionTexto extends Publicacion {
    private final String texto;

    public PublicacionTexto(
            Long id,
            String autor,
            String titulo,
            int meGusta,
            int comentarios,
            int compartidos,
            String texto
    ) {
        super(id, autor, titulo, meGusta, comentarios, compartidos);
        this.texto = texto;
    }

    public String getTexto() { return texto; }

    protected boolean validarContenido() {
        if (texto == null) {
            return false;
        }
        int longitud = texto.length();
        return longitud >= 10 && longitud <= 280;
    }

    protected double calcularRelevancia() {
        return meGusta + (comentarios * 3.0) + (compartidos * 5.0);
    }

    protected String getDescripcion() {
        return String.format("Texto | %s | %s", titulo, autor);
    }
}

class PublicacionImagen extends Publicacion {
    private final String descripcionImagen;
    private final FormatoImagen formato;

    public PublicacionImagen(
            Long id,
            String autor,
            String titulo,
            int meGusta,
            int comentarios,
            int compartidos,
            String descripcionImagen,
            FormatoImagen formato
    ) {
        super(id, autor, titulo, meGusta, comentarios, compartidos);
        this.descripcionImagen = descripcionImagen;
        this.formato = formato;
    }

    public String getDescripcionImagen() { return descripcionImagen; }
    public FormatoImagen getFormato() { return formato; }

    protected boolean validarContenido() {
        return descripcionImagen != null && !descripcionImagen.isBlank() && formato != null;
    }

    protected double calcularRelevancia() {
        return (meGusta * 2.0) + (comentarios * 3.0) + (compartidos * 5.0);
    }

    protected String getDescripcion() {
        String fmt = (formato != null) ? formato.name() : "SIN_FORMATO";
        return String.format("Imagen | %s | %s | %s", titulo, fmt, autor);
    }
}

class PublicacionVideo extends Publicacion {
    private final int duracionSegundos;
    private int visualizaciones;

    public PublicacionVideo(
            Long id,
            String autor,
            String titulo,
            int meGusta,
            int comentarios,
            int compartidos,
            int duracionSegundos,
            int visualizaciones
    ) {
        super(id, autor, titulo, meGusta, comentarios, compartidos);
        this.duracionSegundos = duracionSegundos;
        this.visualizaciones = visualizaciones;
    }

    public int getDuracionSegundos() { return duracionSegundos; }
    public int getVisualizaciones() { return visualizaciones; }

    public void registrarVisualizacion() {
        this.visualizaciones++;
    }

    protected boolean validarContenido() {
        return duracionSegundos > 0 && duracionSegundos <= 600 && visualizaciones >= 0;
    }

    protected double calcularRelevancia() {
        return (meGusta * 2.0) + (comentarios * 4.0) + (compartidos * 6.0) + (visualizaciones / 100.0);
    }

    @Override
    protected String getDescripcion() {
        return String.format("Video | %s | %d s | %s", titulo, duracionSegundos, autor);
    }
}

class Feed {
    private final List<ResultadoPublicacion> publicaciones = new ArrayList<>();

    public ResultadoPublicacion procesar(Publicable publicacion) {
        if (publicacion == null) {
            return null;
        }
        ResultadoPublicacion resultado = publicacion.publicar();
        publicaciones.add(resultado);
        return resultado;
    }

    public List<ResultadoPublicacion> procesar(List<Publicable> listaPublicaciones) {
        List<ResultadoPublicacion> resultados = new ArrayList<>();
        if (listaPublicaciones == null) {
            return resultados;
        }
        for (Publicable pub : listaPublicaciones) {
            resultados.add(procesar(pub));
        }
        return resultados;
    }

    public ResultadoPublicacion agregar(Publicable publicacion) {
        return procesar(publicacion);
    }

    public List<ResultadoPublicacion> agregar(List<Publicable> listaPublicaciones) {
        return procesar(listaPublicaciones);
    }

    public List<ResultadoPublicacion> obtenerVirales() {
        List<ResultadoPublicacion> virales = new ArrayList<>();
        for (ResultadoPublicacion res : publicaciones) {
            if (res.getEstado() == EstadoPublicacion.VIRAL) {
                virales.add(res);
            }
        }
        return virales;
    }

    public List<ResultadoPublicacion> generarRanking() {
        List<ResultadoPublicacion> ranking = new ArrayList<>(publicaciones);
        ranking.sort(Comparator.comparingDouble(ResultadoPublicacion::getRelevancia).reversed());
        return ranking;
    }
}

/* =========================================================
   ADAPTADOR ENTRE HACKERRANK Y TU DOMINIO
   ========================================================= */

class Result {

    public static Publicable convertir(PublicacionTextoDTO entrada) {
        return new PublicacionTexto(
                entrada.getId(),
                entrada.getAutor(),
                entrada.getTitulo(),
                entrada.getMeGusta(),
                entrada.getComentarios(),
                entrada.getCompartidos(),
                entrada.getTexto()
        );
    }

    public static Publicable convertir(PublicacionImagenDTO entrada) {
        FormatoImagen formato = null;
        if (entrada.getFormato() != null && !entrada.getFormato().isBlank()) {
            try {
                formato = FormatoImagen.valueOf(entrada.getFormato().trim().toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException e) {
                formato = null;
            }
        }
        return new PublicacionImagen(
                entrada.getId(),
                entrada.getAutor(),
                entrada.getTitulo(),
                entrada.getMeGusta(),
                entrada.getComentarios(),
                entrada.getCompartidos(),
                entrada.getDescripcionImagen(),
                formato
        );
    }

    public static Publicable convertir(PublicacionVideoDTO entrada) {
        return new PublicacionVideo(
                entrada.getId(),
                entrada.getAutor(),
                entrada.getTitulo(),
                entrada.getMeGusta(),
                entrada.getComentarios(),
                entrada.getCompartidos(),
                entrada.getDuracionSegundos(),
                entrada.getVisualizaciones()
        );
    }
}

/* =========================================================
   INFRAESTRUCTURA HACKERRANK
   ========================================================= */

final class HackerRankVibe {

    private HackerRankVibe() {}

    public static void ejecutar() throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String primeraLinea = reader.readLine();

        if (primeraLinea == null || primeraLinea.isBlank()) {
            return;
        }

        int numeroPublicaciones = Integer.parseInt(primeraLinea.trim());
        List<Publicable> publicaciones = new ArrayList<>();

        for (int i = 0; i < numeroPublicaciones; i++) {
            String linea = reader.readLine();
            if (linea == null) {
                throw new IllegalArgumentException("Faltan publicaciones");
            }
            Publicable publicacion = convertirEntrada(linea);
            if (publicacion == null) {
                throw new IllegalStateException("La conversion regreso null");
            }
            publicaciones.add(publicacion);
        }

        Feed feedIndividual = new Feed();
        for (Publicable publicacion : publicaciones) {
            ResultadoPublicacion resultado = feedIndividual.procesar(publicacion);
            imprimirResultado(resultado);
        }

        Feed feed = new Feed();
        feed.procesar(publicaciones);

        System.out.println("RANKING");
        List<ResultadoPublicacion> ranking = feed.generarRanking();
        for (ResultadoPublicacion resultado : ranking) {
            imprimirRanking(resultado);
        }

        System.out.println("VIRALES");
        List<ResultadoPublicacion> virales = feed.obtenerVirales();
        for (ResultadoPublicacion resultado : virales) {
            imprimirViral(resultado);
        }
    }

    private static Publicable convertirEntrada(String linea) {
        if (linea == null || linea.isBlank()) {
            throw new IllegalArgumentException("La linea no puede estar vacia");
        }
        String[] partes = linea.split("\\|", -1);
        String tipo = partes[0].trim().toUpperCase();

        switch (tipo) {
            case "TEXTO": return convertirTexto(partes);
            case "IMAGEN": return convertirImagen(partes);
            case "VIDEO": return convertirVideo(partes);
            default: throw new IllegalArgumentException("Tipo de publicacion invalido");
        }
    }

    private static Publicable convertirTexto(String[] partes) {
        if (partes.length != 8) {
            throw new IllegalArgumentException("Formato TEXTO invalido");
        }
        PublicacionTextoDTO entrada = new PublicacionTextoDTO(
                Long.parseLong(partes[1].trim()),
                partes[2].trim(),
                partes[3].trim(),
                Integer.parseInt(partes[4].trim()),
                Integer.parseInt(partes[5].trim()),
                Integer.parseInt(partes[6].trim()),
                partes[7].trim()
        );
        return Result.convertir(entrada);
    }

    private static Publicable convertirImagen(String[] partes) {
        if (partes.length != 9) {
            throw new IllegalArgumentException("Formato IMAGEN invalido");
        }
        PublicacionImagenDTO entrada = new PublicacionImagenDTO(
                Long.parseLong(partes[1].trim()),
                partes[2].trim(),
                partes[3].trim(),
                Integer.parseInt(partes[4].trim()),
                Integer.parseInt(partes[5].trim()),
                Integer.parseInt(partes[6].trim()),
                partes[7].trim(),
                partes[8].trim().toUpperCase()
        );
        return Result.convertir(entrada);
    }

    private static Publicable convertirVideo(String[] partes) {
        if (partes.length != 9) {
            throw new IllegalArgumentException("Formato VIDEO invalido");
        }
        PublicacionVideoDTO entrada = new PublicacionVideoDTO(
                Long.parseLong(partes[1].trim()),
                partes[2].trim(),
                partes[3].trim(),
                Integer.parseInt(partes[4].trim()),
                Integer.parseInt(partes[5].trim()),
                Integer.parseInt(partes[6].trim()),
                Integer.parseInt(partes[7].trim()),
                Integer.parseInt(partes[8].trim())
        );
        return Result.convertir(entrada);
    }

    private static void imprimirResultado(ResultadoPublicacion resultado) {
        System.out.printf(
                Locale.US,
                "%d|%s|%.2f|%s%n",
                resultado.getIdPublicacion(),
                resultado.getDescripcion(),
                resultado.getRelevancia(),
                resultado.getEstado()
        );
    }

    private static void imprimirRanking(ResultadoPublicacion resultado) {
        System.out.printf(
                Locale.US,
                "%d|%.2f|%s%n",
                resultado.getIdPublicacion(),
                resultado.getRelevancia(),
                resultado.getEstado()
        );
    }

    private static void imprimirViral(ResultadoPublicacion resultado) {
        System.out.printf(
                Locale.US,
                "%d|%s|%.2f%n",
                resultado.getIdPublicacion(),
                resultado.getDescripcion(),
                resultado.getRelevancia()
        );
    }
}

public class Solution {
    public static void main(String[] args) throws Exception {
        HackerRankVibe.ejecutar();
    }
}