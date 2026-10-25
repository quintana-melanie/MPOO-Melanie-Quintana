import java.io.*;
import java.time.LocalDate;
import java.util.*;

/* =========================================================
   1. DOMINIO DE PRODUCTOS (ENCAPSULAMIENTO)
   ========================================================= */

class Producto {
    private final String codigo;
    private final String nombre;
    private final LocalDate fechaCaducidad;
    private final CategoriaProducto categoria;

    protected Producto(String codigo, String nombre, LocalDate fechaCaducidad, CategoriaProducto categoria) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.fechaCaducidad = fechaCaducidad;
        this.categoria = categoria;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public LocalDate getFechaCaducidad() {
        return fechaCaducidad;
    }

    public CategoriaProducto getCategoria() {
        return categoria;
    }
}

class Bebida extends Producto {
    public Bebida(String codigo, String nombre, LocalDate fecha, CategoriaProducto categoria) {
        super(codigo, nombre, fecha, categoria);
    }
}

class Snack extends Producto {
    public Snack(String codigo, String nombre, LocalDate fecha, CategoriaProducto categoria) {
        super(codigo, nombre, fecha, categoria);
    }
}

/* =========================================================
   2. IMPLEMENTACION DE INVENTARIO
   ========================================================= */

class InventarioMemoria implements Inventario {
    private static class Entrada {
        private final Producto producto;
        private final double precio;
        private int existencias;

        Entrada(Producto producto, double precio) {
            this.producto = producto;
            this.precio = precio;
            this.existencias = 0;
        }

        ProductoInventario consultar() {
            return new ProductoInventario(producto, precio, existencias);
        }
    }

    private final java.util.Map<String, Entrada> productos = new java.util.LinkedHashMap<>();

    private boolean productoValido(Producto p) {
        return p != null
                && p.getCodigo() != null && !p.getCodigo().trim().isEmpty()
                && p.getNombre() != null && !p.getNombre().trim().isEmpty()
                && p.getFechaCaducidad() != null
                && p.getCategoria() != null;
    }
  
    public ResultadoRegistro registrarProducto(Producto producto, double precio) {
        if (!productoValido(producto)) {
            return ResultadoRegistro.PRODUCTO_INVALIDO;
        }
        if (!Double.isFinite(precio) || precio <= 0) {
            return ResultadoRegistro.PRECIO_INVALIDO;
        }
        String codigo = producto.getCodigo();
        if (productos.containsKey(codigo)) {
            return ResultadoRegistro.CODIGO_DUPLICADO;
        }
        productos.put(codigo, new Entrada(producto, precio));
        return ResultadoRegistro.PRODUCTO_REGISTRADO;
    }

    public Optional<ProductoInventario> buscarProducto(String codigo) {
        if (codigo == null) {
            return Optional.empty();
        }
        Entrada entrada = productos.get(codigo);
        if (entrada == null) {
            return Optional.empty();
        }
        return Optional.of(entrada.consultar());
    }

    public ResultadoReabastecimiento reabastecer(String codigo, int cantidad) {
        if (codigo == null || !productos.containsKey(codigo)) {
            return ResultadoReabastecimiento.PRODUCTO_NO_ENCONTRADO;
        }
        if (cantidad <= 0) {
            return ResultadoReabastecimiento.CANTIDAD_INVALIDA;
        }
        Entrada entrada = productos.get(codigo);
        if (entrada.existencias > Integer.MAX_VALUE - cantidad) {
            return ResultadoReabastecimiento.CANTIDAD_INVALIDA;
        }
        entrada.existencias += cantidad;
        return ResultadoReabastecimiento.REABASTECIMIENTO_REALIZADO;
    }

    public ResultadoEntrega despachar(String codigo) {
        if (codigo == null || !productos.containsKey(codigo)) {
            return ResultadoEntrega.PRODUCTO_NO_ENCONTRADO;
        }
        Entrada entrada = productos.get(codigo);
        if (entrada.existencias <= 0) {
            return ResultadoEntrega.SIN_EXISTENCIAS;
        }
        entrada.existencias--;
        return ResultadoEntrega.PRODUCTO_PREPARADO;
    }
}

/* =========================================================
   3. IMPLEMENTACION DE MONEDERO
   ========================================================= */

class MonederoEfectivo implements Monedero {
    private double fondos;

    public MonederoEfectivo(double fondosIniciales) {
        if (!Double.isFinite(fondosIniciales) || fondosIniciales < 0) {
            throw new IllegalArgumentException("Los fondos iniciales no son validos");
        }
        this.fondos = fondosIniciales;
    }

    public ResultadoCobro cobrar(double precio, double cantidadRecibida) {
        if (!Double.isFinite(precio) || !Double.isFinite(cantidadRecibida)
                || precio <= 0 || cantidadRecibida < precio || cantidadRecibida < 0) {
            return new ResultadoCobro(EstadoCobro.MONTO_INVALIDO, 0.0);
        }
        double cambio = cantidadRecibida - precio;
        if (fondos < cambio) {
            return new ResultadoCobro(EstadoCobro.SIN_FONDOS_SUFICIENTES, 0.0);
        }
        double nuevosFondos = fondos + precio;
        if (!Double.isFinite(nuevosFondos)) {
            return new ResultadoCobro(EstadoCobro.MONTO_INVALIDO, 0.0);
        }
        fondos = nuevosFondos;
        return new ResultadoCobro(EstadoCobro.COBRO_REALIZADO, cambio);
    }

    public double consultarFondos() {
        return fondos;
    }
}

/* =========================================================
   4. MAQUINA EXPENDEDORA
   ========================================================= */

class MaquinaExpendedora implements MaquinaDespachadora {
    private final Inventario inventario;
    private final Monedero monedero;

    private MaquinaExpendedora(Inventario inventario, Monedero monedero) {
        this.inventario = inventario;
        this.monedero = monedero;
    }

    public static Builder builder() {
        return new Builder();
    }

    static class Builder {
        private Inventario inventario;
        private Monedero monedero;

        public Builder inventario(Inventario inventario) {
            this.inventario = inventario;
            return this;
        }

        public Builder monedero(Monedero monedero) {
            this.monedero = monedero;
            return this;
        }

        public MaquinaExpendedora build() {
            if (inventario == null || monedero == null) {
                throw new IllegalStateException("Se requiere inventario y monedero");
            }
            return new MaquinaExpendedora(inventario, monedero);
        }
    }

    public ResultadoRegistro registrarProducto(RegistroProductoDTO solicitud) {
        if (solicitud == null) {
            return ResultadoRegistro.PRODUCTO_INVALIDO;
        }
        String codigo = solicitud.getCodigo();
        String nombre = solicitud.getNombre();
        LocalDate fecha = solicitud.getFechaCaducidad();
        CategoriaProducto categoria = solicitud.getCategoria();
        Producto producto;
        if (categoria == CategoriaProducto.BEBIDA) {
            producto = new Bebida(codigo, nombre, fecha, categoria);
        } else if (categoria == CategoriaProducto.SNACK) {
            producto = new Snack(codigo, nombre, fecha, categoria);
        } else {
            producto = null;
        }
        return inventario.registrarProducto(producto, solicitud.getPrecio());
    }

    public Optional<ProductoInventario> buscarProducto(String codigo) {
        return inventario.buscarProducto(codigo);
    }

    public ResultadoReabastecimiento reabastecer(ReabastecimientoDTO solicitud) {
        if (solicitud == null) {
            return ResultadoReabastecimiento.PRODUCTO_NO_ENCONTRADO;
        }
        return inventario.reabastecer(solicitud.getCodigo(), solicitud.getCantidad());
    }

    public ResultadoCompra comprar(CompraDTO solicitud) {
        if (solicitud == null) {
            return new ResultadoCompra(EstadoCompra.PRODUCTO_NO_ENCONTRADO, null, 0.0);
        }
        String codigo = solicitud.getCodigoProducto();
        double recibido = solicitud.getCantidadRecibida();
        Optional<ProductoInventario> encontrado = inventario.buscarProducto(codigo);
        if (encontrado.isEmpty()) {
            return new ResultadoCompra(EstadoCompra.PRODUCTO_NO_ENCONTRADO, null, 0.0);
        }
        ProductoInventario info = encontrado.get();
        Producto producto = info.getProducto();
        double precio = info.getPrecio();
        if (info.getExistencias() <= 0) {
            return new ResultadoCompra(EstadoCompra.SIN_EXISTENCIAS, producto, 0.0);
        }
        if (!Double.isFinite(recibido) || recibido < precio || recibido < 0) {
            return new ResultadoCompra(EstadoCompra.PAGO_INSUFICIENTE, producto, 0.0);
        }
        double cambio = recibido - precio;
        if (monedero.consultarFondos() < cambio) {
            return new ResultadoCompra(EstadoCompra.SIN_FONDOS_SUFICIENTES, producto, 0.0);
        }
        ResultadoEntrega entrega = inventario.despachar(codigo);
        if (entrega != ResultadoEntrega.PRODUCTO_PREPARADO) {
            EstadoCompra estado = (entrega == ResultadoEntrega.SIN_EXISTENCIAS)
                    ? EstadoCompra.SIN_EXISTENCIAS
                    : EstadoCompra.PRODUCTO_NO_ENCONTRADO;
            return new ResultadoCompra(estado, producto, 0.0);
        }
        ResultadoCobro cobro = monedero.cobrar(precio, recibido);
        if (cobro.getEstado() != EstadoCobro.COBRO_REALIZADO) {
            inventario.reabastecer(codigo, 1);
            EstadoCompra estado = (cobro.getEstado() == EstadoCobro.SIN_FONDOS_SUFICIENTES)
                    ? EstadoCompra.SIN_FONDOS_SUFICIENTES
                    : EstadoCompra.PAGO_INSUFICIENTE;
            return new ResultadoCompra(estado, producto, 0.0);
        }
        return new ResultadoCompra(EstadoCompra.VENTA_REALIZADA, producto, cobro.getCambio());
    }

    public double consultarFondos() {
        return monedero.consultarFondos();
    }
}

/* =========================================================
   5. CLASE RESULT
   ========================================================= */

class Result {
    public static MaquinaDespachadora crearMaquina(double fondosIniciales) {
        Inventario inventario = new InventarioMemoria();
        Monedero monedero = new MonederoEfectivo(fondosIniciales);
        return MaquinaExpendedora.builder()
                .inventario(inventario)
                .monedero(monedero)
                .build();
    }
}
