import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PruebaEvidenciaVibe {
    public static void main(String[] args) {
        List<Publicable> publicaciones = new ArrayList<>();

        publicaciones.add(new PublicacionTexto(
                1L, "Ana", "Java", 100, 20, 10, "Aprendiendo polimorfismo"
        ));
        publicaciones.add(new PublicacionImagen(
                2L, "Luis", "Viaje", 100, 20, 10, "Foto del viaje", FormatoImagen.JPG
        ));
        publicaciones.add(new PublicacionVideo(
                3L, "Maria", "Tutorial", 50, 10, 5, 180, 5000
        ));
        publicaciones.add(new PublicacionTexto(
                4L, "Carlos", "Corto", 50, 10, 5, "Hola"
        ));

        Feed feed = new Feed();
        List<ResultadoPublicacion> resultados = feed.procesar(publicaciones);

        System.out.println("=== PUBLICACIONES PROCESADAS ===");
        for (ResultadoPublicacion r : resultados) {
            System.out.printf(Locale.US, "%d|%s|%.2f|%s%n",
                    r.getIdPublicacion(), r.getDescripcion(), r.getRelevancia(), r.getEstado());
        }

        System.out.println("\n=== RANKING ===");
        for (ResultadoPublicacion r : feed.generarRanking()) {
            System.out.printf(Locale.US, "%d|%.2f|%s%n",
                    r.getIdPublicacion(), r.getRelevancia(), r.getEstado());
        }

        System.out.println("\n=== VIRALES ===");
        for (ResultadoPublicacion r : feed.obtenerVirales()) {
            System.out.printf(Locale.US, "%d|%s|%.2f%n",
                    r.getIdPublicacion(), r.getDescripcion(), r.getRelevancia());
        }
    }
}