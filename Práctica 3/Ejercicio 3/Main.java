import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        CuentaBancaria cuenta1 = new CuentaBancaria(
                "001",
                "Melanie",
                5000.00
        );

        CuentaBancaria cuenta2 = new CuentaBancaria(
                "002",
                "Juan",
                3000.00
        );

        CuentaBancaria[] cuentas = {cuenta1, cuenta2};
        CuentaBancariaService servicio = new CuentaBancariaService(cuentas);
        int opcion;

        do {
            System.out.println("\n========== SISTEMA BANCARIO ==========");
            System.out.println("1. Transferir dinero");
            System.out.println("2. Depositar dinero");
            System.out.println("3. Retirar dinero");
            System.out.println("4. Salir");
            System.out.print("\nSeleccione una opción: ");
            opcion = scanner.nextInt();

            switch (opcion) {

                case 1:
                    System.out.print("Número de cuenta origen: ");
                    String origen = scanner.next();
                    System.out.print("Número de cuenta destino: ");
                    String destino = scanner.next();
                    System.out.print("Cantidad a transferir: ");
                    double cantidadTransferir = scanner.nextDouble();
                    if (servicio.transferir(origen, destino, cantidadTransferir)) {
                        System.out.println("Transferencia realizada correctamente.");
                    } else {
                        System.out.println("No se pudo realizar la transferencia.");
                    }
                    break;

                case 2:
                    System.out.print("Número de cuenta: ");
                    String cuentaDeposito = scanner.next();
                    System.out.print("Cantidad a depositar: ");
                    double cantidadDeposito = scanner.nextDouble();
                    if (servicio.depositar(cuentaDeposito, cantidadDeposito)) {
                        System.out.println("Depósito realizado correctamente.");
                    } else {
                        System.out.println("No se pudo realizar el depósito.");
                    }
                    break;

                case 3:
                    System.out.print("Número de cuenta: ");
                    String cuentaRetiro = scanner.next();
                    System.out.print("Cantidad a retirar: ");
                    double cantidadRetiro = scanner.nextDouble();
                    if (servicio.retirar(cuentaRetiro, cantidadRetiro)) {
                        System.out.println("Retiro realizado correctamente.");
                    } else {
                        System.out.println("No se pudo realizar el retiro.");
                    }
                    break;

                case 4:
                    System.out.println("Gracias por utilizar el sistema bancario.");
                    break;
                default:
                    System.out.println("Opción no válida.");
            }

        } while (opcion != 4);

        scanner.close();
    }
}