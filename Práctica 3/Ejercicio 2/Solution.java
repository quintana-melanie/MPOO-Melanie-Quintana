package Práctica 3.Ejercicio 2;

import java.io.*;
import java.math.*;
import java.security.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;

class Result {

    /*
     * Complete the 'comprimir' function below.
     *
     * The function is expected to return a STRING.
     * The function accepts STRING datos as parameter.
     */

    public static String comprimir(String datos) {
      String resultado = "";
       int contador = 1;

      for (int i = 1; i <= datos.length(); i++) {
       if (i < datos.length() && datos.charAt(i) == datos.charAt(i - 1)) {
        contador++;
    } else {
        while (contador > 9) {
            resultado += "9" + datos.charAt(i - 1);
            contador -= 9;
        }

        resultado += contador + "" + datos.charAt(i - 1);
        contador = 1;
    }
}

return resultado;
    }

}

public class Solution {
    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(System.getenv("OUTPUT_PATH")));

        String datos = bufferedReader.readLine();

        String result = Result.comprimir(datos);

        bufferedWriter.write(result);
        bufferedWriter.newLine();

        bufferedReader.close();
        bufferedWriter.close();
    }
}

