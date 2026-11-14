import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PruebaEvidencia {
    public static void main(String[] args) {
        List<EntradaEnvioDTO> entradas = new ArrayList<>();

        entradas.add(new EntradaEnvioDTO(
                TipoEnvio.TERRESTRE, "Ana", 10.0, 100.0, Collections.emptyList()
        ));

        entradas.add(new EntradaEnvioDTO(
                TipoEnvio.AEREO, "Pedro", 5.0, 200.0,
                Arrays.asList(ServicioAdicional.SEGURO, ServicioAdicional.EXPRESS)
        ));

        entradas.add(new EntradaEnvioDTO(
                TipoEnvio.TERRESTRE, "Maria", 8.0, 50.0,
                Collections.singletonList(ServicioAdicional.SEGURO)
        ));

        List<String> resultados = Result.procesar(entradas);

        System.out.println("=========================================");
        System.out.println("   EVIDENCIA PRACTICA 10 - LOGIPACK      ");
        System.out.println("=========================================");
        for (String res : resultados) {
            System.out.println(res);
        }
        System.out.println("=========================================");
    }
}
