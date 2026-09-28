import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

class Result {
    
    enum TipoLicencia {
        BASICA, PROFESIONAL, EMPRESARIAL, TEMPORAL
    }

    enum EstadoLicencia {
        VIGENTE, PROXIMA_A_VENCER, VENCIDA, BLOQUEADA
    }

    public static String evaluarLicencia(String fechaActual, String fechaVencimiento, String tipoLicencia, int renovacionesPrevias) {
        
        Calendar calActual = convertirCalendar(fechaActual);
        Calendar calVencimiento = convertirCalendar(fechaVencimiento);

        long diferenciaMs = calVencimiento.getTimeInMillis() - calActual.getTimeInMillis();
        long diferenciaDias = Math.round((double) diferenciaMs / (1000 * 60 * 60 * 24));

        EstadoLicencia estado;
        if (diferenciaDias > 30) {
            estado = EstadoLicencia.VIGENTE;
        } else if (diferenciaDias >= 0) {
            estado = EstadoLicencia.PROXIMA_A_VENCER;
        } else if (diferenciaDias >= -90) {
            estado = EstadoLicencia.VENCIDA;
        } else {
            estado = EstadoLicencia.BLOQUEADA;
        }

        if (estado == EstadoLicencia.BLOQUEADA) {
            return String.format(Locale.US, "%s %d 0.00 NO_DISPONIBLE", estado.name(), diferenciaDias);
        }

        TipoLicencia tipo = TipoLicencia.valueOf(tipoLicencia.trim().toUpperCase());

        double costoBase = 0;

        switch (tipo) {
            case BASICA:
                costoBase = 1000.00;
                break;
            case PROFESIONAL:
                costoBase = 1500.00;
                break;
            case EMPRESARIAL:
                costoBase = 2500.00;
                break;
            case TEMPORAL:
                costoBase = 600.00;
                break;
        }

        double costoFinal = costoBase;

        if (estado == EstadoLicencia.VIGENTE) {
            costoFinal *= 0.90; // -10%
        } else if (estado == EstadoLicencia.VENCIDA) {
            costoFinal *= 1.20; // +20%
        }

        if (renovacionesPrevias > 3) {
            costoFinal *= 0.95; // -5% adicional
        }

        Calendar calNueva = Calendar.getInstance();
        if (estado == EstadoLicencia.VENCIDA) {
            calNueva.setTime(calActual.getTime());
        } else {
            calNueva.setTime(calVencimiento.getTime());
        }

        switch (tipo) {
            case BASICA:
                calNueva.add(Calendar.YEAR, 1);
                break;
            case PROFESIONAL:
                calNueva.add(Calendar.YEAR, 2);
                break;
            case EMPRESARIAL:
                calNueva.add(Calendar.YEAR, 3);
                break;
            case TEMPORAL:
                calNueva.add(Calendar.MONTH, 6);
                break;
        }

        String nuevaFechaStr = String.format("%02d/%02d/%04d",
                calNueva.get(Calendar.DAY_OF_MONTH),
                calNueva.get(Calendar.MONTH) + 1,
                calNueva.get(Calendar.YEAR));

        return String.format(Locale.US, "%s %d %.2f %s", estado.name(), diferenciaDias, costoFinal, nuevaFechaStr);
    }

    private static Calendar convertirCalendar(String fecha) {
        String[] partes = fecha.split("/");
        int dia = Integer.parseInt(partes[0]);
        int mes = Integer.parseInt(partes[1]) - 1; 
        int anio = Integer.parseInt(partes[2]);

        Calendar calendar = Calendar.getInstance();
        calendar.set(anio, mes, dia, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar;
    }
}

public class RenovacionLicencias {

    public static void main(String[] args) throws IOException {

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

        System.out.print("Fecha Actual (DD/MM/YYYY): ");
        String fechaActual = bufferedReader.readLine().trim();

        System.out.print("Fecha Vencimiento (DD/MM/YYYY): ");
        String fechaVencimiento = bufferedReader.readLine().trim();

        System.out.print("Tipo de Licencia (BASICA, PROFESIONAL, EMPRESARIAL, TEMPORAL): ");
        String tipoLicencia = bufferedReader.readLine().trim();

        System.out.print("Renovaciones Previas: ");
        int renovacionesPrevias = Integer.parseInt(bufferedReader.readLine().trim());

        String result = Result.evaluarLicencia(fechaActual, fechaVencimiento, tipoLicencia, renovacionesPrevias);

        System.out.println("\nResultado:");
        System.out.println(result);

        bufferedReader.close();
    }
}