import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

class Result {

    enum TipoVehiculo {
        MOTOCICLETA, AUTOMOVIL, CAMIONETA, ELECTRICO
    }

    enum TipoEstancia {
        NORMAL, NOCTURNA, FIN_SEMANA, MIXTA
    }

    public static String calcularEstancia(String tipoVehiculo, String fechaEntrada, String horaEntrada, String fechaSalida, String horaSalida) {
    
        Calendar entrada = convertirCalendar(fechaEntrada, horaEntrada);
        Calendar salida = convertirCalendar(fechaSalida, horaSalida);

        if (!salida.after(entrada)) {
            return "INVALID";
        }

        long diferenciaMillis = salida.getTimeInMillis() - entrada.getTimeInMillis();
        double horas = diferenciaMillis / (1000.0 * 60 * 60);

        int horasCobradas = (int) Math.ceil(horas);

        TipoVehiculo tipo = TipoVehiculo.valueOf(tipoVehiculo.trim().toUpperCase());

        double tarifaHora = 0;
        double maximo24 = 0;

        switch (tipo) {
            case MOTOCICLETA:
                tarifaHora = 15.00;
                maximo24 = 100.00;
                break;

            case AUTOMOVIL:
                tarifaHora = 25.00;
                maximo24 = 180.00;
                break;

            case CAMIONETA:
                tarifaHora = 35.00;
                maximo24 = 250.00;
                break;

            case ELECTRICO:
                tarifaHora = 20.00;
                maximo24 = 150.00;
                break;
        }

        double costoBase = 0;
        int horasRestantes = horasCobradas;

        while (horasRestantes > 0) {
            int horasBloque = Math.min(horasRestantes, 24);
            double costoBloque = horasBloque * tarifaHora;
            costoBloque = Math.min(costoBloque, maximo24);

            costoBase += costoBloque;
            horasRestantes -= horasBloque;
        }

        boolean finSemana =
                entrada.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||
                entrada.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY ||
                salida.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||
                salida.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;

        boolean diferenteDia = 
                entrada.get(Calendar.YEAR) != salida.get(Calendar.YEAR) ||
                entrada.get(Calendar.MONTH) != salida.get(Calendar.MONTH) ||
                entrada.get(Calendar.DAY_OF_MONTH) != salida.get(Calendar.DAY_OF_MONTH);

        boolean nocturna =
                entrada.get(Calendar.HOUR_OF_DAY) >= 20 ||
                salida.get(Calendar.HOUR_OF_DAY) < 6 ||
                diferenteDia;

        double costoFinal = costoBase;

        if (finSemana) {
            costoFinal *= 1.20;
        }

        if (nocturna) {
            costoFinal *= 1.15;
        }

        if (tipo == TipoVehiculo.ELECTRICO) {
            costoFinal *= 0.90;
        }
        
        TipoEstancia tipoEstancia;

        if (finSemana && nocturna) {
            tipoEstancia = TipoEstancia.MIXTA;
        } else if (finSemana) {
            tipoEstancia = TipoEstancia.FIN_SEMANA;
        } else if (nocturna) {
            tipoEstancia = TipoEstancia.NOCTURNA;
        } else {
            tipoEstancia = TipoEstancia.NORMAL;
        }

        return String.format(Locale.US, "%d %.2f %s", horasCobradas, costoFinal, tipoEstancia);
    }

    private static Calendar convertirCalendar(String fecha, String hora) {

        String[] partesFecha = fecha.split("/");
        String[] partesHora = hora.split(":");

        int dia = Integer.parseInt(partesFecha[0]);
        int mes = Integer.parseInt(partesFecha[1]) - 1; 
        int anio = Integer.parseInt(partesFecha[2]);

        int horas = Integer.parseInt(partesHora[0]);
        int minutos = Integer.parseInt(partesHora[1]);

        Calendar calendar = Calendar.getInstance();
        calendar.set(anio, mes, dia, horas, minutos, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar;
    }
}

public class TarifaEstacionamiento {
    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

        System.out.print("Tipo de vehículo: ");
        String tipoVehiculo = bufferedReader.readLine().trim();

        System.out.print("Fecha entrada (DD/MM/YYYY): ");
        String fechaEntrada = bufferedReader.readLine().trim();

        System.out.print("Hora entrada (HH:MM): ");
        String horaEntrada = bufferedReader.readLine().trim();

        System.out.print("Fecha salida (DD/MM/YYYY): ");
        String fechaSalida = bufferedReader.readLine().trim();

        System.out.print("Hora salida (HH:MM): ");
        String horaSalida = bufferedReader.readLine().trim();

        String result = Result.calcularEstancia(tipoVehiculo, fechaEntrada, horaEntrada, fechaSalida, horaSalida);

        System.out.println("\nResultado:");
        System.out.println(result);

        bufferedReader.close();
    }
}