public class CuentaBancariaService {

    private CuentaBancaria[] cuentas;
    public CuentaBancariaService(CuentaBancaria[] cuentas) {
        this.cuentas = cuentas;
    }

    private CuentaBancaria buscarCuenta(String numeroCuenta) {
        for (CuentaBancaria cuenta : cuentas) {
            if (cuenta != null && cuenta.getNumeroCuenta().equals(numeroCuenta)) {
                return cuenta;
            }
        }
        return null;
    }

    public boolean depositar(String numeroCuenta, double cantidad) {
        if (cantidad <= 0) {
            return false;
        }

        CuentaBancaria cuenta = buscarCuenta(numeroCuenta);
        if (cuenta == null || !cuenta.isActiva()) {
            return false;
        }
        cuenta.depositar(cantidad);
        return true;
    }

    public boolean retirar(String numeroCuenta, double cantidad) {
        if (cantidad <= 0) {
            return false;
        }

        CuentaBancaria cuenta = buscarCuenta(numeroCuenta);
        if (cuenta == null || !cuenta.isActiva()) {
            return false;
        }
        return cuenta.retirar(cantidad);
    }

    public boolean transferir(String numeroOrigen, String numeroDestino, double cantidad) {
        if (cantidad <= 0) {
            return false;
        }
        if (numeroOrigen.equals(numeroDestino)) {
            return false;
        }

        CuentaBancaria origen = buscarCuenta(numeroOrigen);
        CuentaBancaria destino = buscarCuenta(numeroDestino);
        if (origen == null || destino == null) {
            return false;
        }
        if (!origen.isActiva() || !destino.isActiva()) {
            return false;
        }
        if (origen.getSaldo() < cantidad) {
            return false;
        }

        origen.retirar(cantidad);
        destino.depositar(cantidad);
        return true;
    }
}