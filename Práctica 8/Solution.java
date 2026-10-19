import java.io.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

/*
 =====================================================
 TIPOS PROPORCIONADOS POR HACKERRANK
 =====================================================
 */

enum TipoReembolso {
    ALIMENTOS,
    TRANSPORTE,
    HOSPEDAJE
}

enum ResultadoValidacion {
    APROBADO,
    SIN_COMPROBANTE,
    FUERA_DE_PERIODO,
    EXCEDE_LIMITE,
    REQUIERE_AUTORIZACION
}

/*
 =====================================================
 DTOs DE ENTRADA Y SALIDA
 =====================================================
 */

final class ReembolsoInputDTO {
    private final TipoReembolso tipo;
    private final String folio;
    private final String empleado;
    private final double monto;
    private final LocalDate fechaGasto;
    private final boolean tieneComprobante;
    private final boolean autorizadoGerente;
    private final LocalDate fechaEvaluacion;

    public ReembolsoInputDTO(
            TipoReembolso tipo,
            String folio,
            String empleado,
            double monto,
            LocalDate fechaGasto,
            boolean tieneComprobante,
            boolean autorizadoGerente,
            LocalDate fechaEvaluacion) {
        this.tipo = tipo;
        this.folio = folio;
        this.empleado = empleado;
        this.monto = monto;
        this.fechaGasto = fechaGasto;
        this.tieneComprobante = tieneComprobante;
        this.autorizadoGerente = autorizadoGerente;
        this.fechaEvaluacion = fechaEvaluacion;
    }

    public TipoReembolso getTipo() { return tipo; }
    public String getFolio() { return folio; }
    public String getEmpleado() { return empleado; }
    public double getMonto() { return monto; }
    public LocalDate getFechaGasto() { return fechaGasto; }
    public boolean tieneComprobante() { return tieneComprobante; }
    public boolean estaAutorizadoGerente() { return autorizadoGerente; }
    public LocalDate getFechaEvaluacion() { return fechaEvaluacion; }
}

final class ReembolsoOutputDTO {
    private final String folio;
    private final TipoReembolso tipo;
    private final ResultadoValidacion resultado;

    public ReembolsoOutputDTO(String folio, TipoReembolso tipo, ResultadoValidacion resultado) {
        this.folio = folio;
        this.tipo = tipo;
        this.resultado = resultado;
    }

    public String getFolio() { return folio; }
    public TipoReembolso getTipo() { return tipo; }
    public ResultadoValidacion getResultado() { return resultado; }
}

/*
 =====================================================
 1. JERARQUIA DE REEMBOLSOS (PRODUCTOS)
 =====================================================
 */

abstract class Reembolso {
    private final String folio;
    private final String empleado;
    private final double monto;
    private final LocalDate fechaGasto;
    private final boolean tieneComprobante;
    private final boolean autorizadoGerente;

    public Reembolso(String folio, String empleado, double monto) {
        this(folio, empleado, monto, LocalDate.now(), true, false);
    }

    public Reembolso(String folio, String empleado, double monto, LocalDate fechaGasto, boolean tieneComprobante, boolean autorizadoGerente) {
        this.folio = folio;
        this.empleado = empleado;
        this.monto = monto;
        this.fechaGasto = fechaGasto;
        this.tieneComprobante = tieneComprobante;
        this.autorizadoGerente = autorizadoGerente;
    }

    public abstract double getLimitePermitido();

    public String getFolio() { return folio; }
    public String getEmpleado() { return empleado; }
    public double getMonto() { return monto; }
    public LocalDate getFechaGasto() { return fechaGasto; }
    public boolean tieneComprobante() { return tieneComprobante; }
    public boolean estaAutorizadoGerente() { return autorizadoGerente; }
}

class ReembolsoAlimentos extends Reembolso {
    public ReembolsoAlimentos(String folio, String empleado, double monto, LocalDate fechaGasto, boolean tieneComprobante, boolean autorizadoGerente) {
        super(folio, empleado, monto, fechaGasto, tieneComprobante, autorizadoGerente);
    }

    public double getLimitePermitido() {
        return 1000.0;
    }
}

class ReembolsoTransporte extends Reembolso {
    public ReembolsoTransporte(String folio, String empleado, double monto, LocalDate fechaGasto, boolean tieneComprobante, boolean autorizadoGerente) {
        super(folio, empleado, monto, fechaGasto, tieneComprobante, autorizadoGerente);
    }

    public double getLimitePermitido() {
        return 1500.0;
    }
}

class ReembolsoHospedaje extends Reembolso {
    public ReembolsoHospedaje(String folio, String empleado, double monto, LocalDate fechaGasto, boolean tieneComprobante, boolean autorizadoGerente) {
        super(folio, empleado, monto, fechaGasto, tieneComprobante, autorizadoGerente);
    }

    public double getLimitePermitido() {
        return 4000.0;
    }
}

/*
 =====================================================
 3. FACTORY METHOD (CREATORS)
 =====================================================
 */

abstract class RegistroReembolso {
    public abstract Reembolso crearReembolso(ReembolsoInputDTO input);
}

class RegistroAlimentos extends RegistroReembolso {
    public Reembolso crearReembolso(ReembolsoInputDTO input) {
        return new ReembolsoAlimentos(
            input.getFolio(),
            input.getEmpleado(),
            input.getMonto(),
            input.getFechaGasto(),
            input.tieneComprobante(),
            input.estaAutorizadoGerente()
        );
    }
}

class RegistroTransporte extends RegistroReembolso {
    public Reembolso crearReembolso(ReembolsoInputDTO input) {
        return new ReembolsoTransporte(
            input.getFolio(),
            input.getEmpleado(),
            input.getMonto(),
            input.getFechaGasto(),
            input.tieneComprobante(),
            input.estaAutorizadoGerente()
        );
    }
}

class RegistroHospedaje extends RegistroReembolso {
    public Reembolso crearReembolso(ReembolsoInputDTO input) {
        return new ReembolsoHospedaje(
            input.getFolio(),
            input.getEmpleado(),
            input.getMonto(),
            input.getFechaGasto(),
            input.tieneComprobante(),
            input.estaAutorizadoGerente()
        );
    }
}

/*
 =====================================================
 4 y 5. CHAIN OF RESPONSIBILITY Y REGLAS
 =====================================================
 */

abstract class ValidadorReembolso {
    protected ValidadorReembolso siguiente;

    public ValidadorReembolso enlazarCon(ValidadorReembolso siguiente) {
        this.siguiente = siguiente;
        return siguiente;
    }

    public abstract ResultadoValidacion validar(Reembolso reembolso, LocalDate fechaEvaluacion);

    protected ResultadoValidacion validarSiguiente(Reembolso reembolso, LocalDate fechaEvaluacion) {
        if (siguiente == null) {
            return ResultadoValidacion.APROBADO;
        }
        return siguiente.validar(reembolso, fechaEvaluacion);
    }
}

class ValidadorComprobante extends ValidadorReembolso {
    public ResultadoValidacion validar(Reembolso reembolso, LocalDate fechaEvaluacion) {
        if (!reembolso.tieneComprobante()) {
            return ResultadoValidacion.SIN_COMPROBANTE;
        }
        return validarSiguiente(reembolso, fechaEvaluacion);
    }
}

class ValidadorPeriodo extends ValidadorReembolso {
    public ResultadoValidacion validar(Reembolso reembolso, LocalDate fechaEvaluacion) {
        long diasTranscurridos = ChronoUnit.DAYS.between(reembolso.getFechaGasto(), fechaEvaluacion);
        if (diasTranscurridos > 30) {
            return ResultadoValidacion.FUERA_DE_PERIODO;
        }
        return validarSiguiente(reembolso, fechaEvaluacion);
    }
}

class ValidadorLimite extends ValidadorReembolso {
    public ResultadoValidacion validar(Reembolso reembolso, LocalDate fechaEvaluacion) {
        if (reembolso.getMonto() > reembolso.getLimitePermitido()) {
            return ResultadoValidacion.EXCEDE_LIMITE;
        }
        return validarSiguiente(reembolso, fechaEvaluacion);
    }
}

class ValidadorGerencia extends ValidadorReembolso {
    public ResultadoValidacion validar(Reembolso reembolso, LocalDate fechaEvaluacion) {
        if (reembolso.getMonto() > 2000.0 && !reembolso.estaAutorizadoGerente()) {
            return ResultadoValidacion.REQUIERE_AUTORIZACION;
        }
        return validarSiguiente(reembolso, fechaEvaluacion);
    }
}

/*
 =====================================================
 6. CLASE RESULT
 =====================================================
 */

class Result {

    public static ReembolsoOutputDTO procesarReembolso(ReembolsoInputDTO input) {
        RegistroReembolso creador;
        switch (input.getTipo()) {
            case ALIMENTOS:
                creador = new RegistroAlimentos();
                break;
            case TRANSPORTE:
                creador = new RegistroTransporte();
                break;
            case HOSPEDAJE:
                creador = new RegistroHospedaje();
                break;
            default:
                throw new IllegalArgumentException("Tipo de reembolso no valido");
        }

        Reembolso reembolso = creador.crearReembolso(input);

        ValidadorReembolso vComprobante = new ValidadorComprobante();
        ValidadorReembolso vPeriodo = new ValidadorPeriodo();
        ValidadorReembolso vLimite = new ValidadorLimite();
        ValidadorReembolso vGerencia = new ValidadorGerencia();

        vComprobante.enlazarCon(vPeriodo)
                    .enlazarCon(vLimite)
                    .enlazarCon(vGerencia);

        ResultadoValidacion resultado = vComprobante.validar(reembolso, input.getFechaEvaluacion());

        return new ReembolsoOutputDTO(input.getFolio(), input.getTipo(), resultado);
    }
}

/*
 =====================================================
 CLASE PRINCIPAL SOLUTION
 =====================================================
 */

public class Solution {

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        String lineaFecha = br.readLine();
        LocalDate fechaEvaluacion = LocalDate.parse(lineaFecha.trim());

        String lineaSolicitud = br.readLine();
        String[] datos = lineaSolicitud.split("\\|");

        TipoReembolso tipo = TipoReembolso.valueOf(datos[0].trim());
        String folio = datos[1].trim();
        String empleado = datos[2].trim();
        double monto = Double.parseDouble(datos[3].trim());
        LocalDate fechaGasto = LocalDate.parse(datos[4].trim());
        boolean tieneComprobante = Boolean.parseBoolean(datos[5].trim());
        boolean autorizadoGerente = Boolean.parseBoolean(datos[6].trim());

        ReembolsoInputDTO input = new ReembolsoInputDTO(
                tipo,
                folio,
                empleado,
                monto,
                fechaGasto,
                tieneComprobante,
                autorizadoGerente,
                fechaEvaluacion
        );

        ReembolsoOutputDTO output = Result.procesarReembolso(input);

        System.out.println("REEMBOLSO " + output.getFolio());
        System.out.println("TIPO " + output.getTipo());
        System.out.println("RESULTADO " + output.getResultado());
    }
}
