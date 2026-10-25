import java.time.LocalDate;

public class PruebaEvidencia {

    public static void main(String[] args) {
        MaquinaDespachadora maquina = Result.crearMaquina(500.0);

        RegistroProductoDTO p1 = new RegistroProductoDTO(
                "P001", "Agua Ciel 600ml", LocalDate.now().plusDays(30), CategoriaProducto.BEBIDA, 15.0
        );
        RegistroProductoDTO p2 = new RegistroProductoDTO(
                "P002", "Papas Sabritas 45g", LocalDate.now().plusDays(60), CategoriaProducto.SNACK, 22.0
        );

        maquina.registrarProducto(p1);
        maquina.registrarProducto(p2);

        maquina.reabastecer(new ReabastecimientoDTO("P001", 3));
        maquina.reabastecer(new ReabastecimientoDTO("P002", 1));

        System.out.println("==================================================");
        System.out.println("       EVIDENCIA DE COMPRA CORRECTA (EXITO)       ");
        System.out.println("==================================================");

        ResultadoCompra resCorrecta = maquina.comprar(new CompraDTO("P001", 50.0));
        System.out.println("Estado de la compra : " + resCorrecta.getEstado());
        System.out.println("Producto entregado  : " + resCorrecta.getProducto().getNombre());
        System.out.println("Cambio devuelto     : $" + resCorrecta.getCambio());
        System.out.println("Fondos acumulados   : $" + maquina.consultarFondos());
        System.out.println();

        System.out.println("==================================================");
        System.out.println("      EVIDENCIA DE COMPRA RECHAZADA (ERROR)       ");
        System.out.println("==================================================");

        ResultadoCompra resRechazada = maquina.comprar(new CompraDTO("P002", 10.0));
        System.out.println("Estado de la compra : " + resRechazada.getEstado());
        System.out.println("Cambio devuelto     : $" + resRechazada.getCambio());
        System.out.println("==================================================");
    }
}