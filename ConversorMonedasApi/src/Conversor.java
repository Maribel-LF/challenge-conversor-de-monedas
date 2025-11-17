import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Scanner;

public class Conversor {

    private static final String API_KEY = "0899d353831994506baea012";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int opcion;

        do {
            System.out.println("""
                    \n********************************************************
                    ¡Bienvenido/a al Conversor de Moneda!
                    1 - Dólar =>> Peso Mexicano
                    2 - Peso mexicáno =>> Dólar
                    3 - Dolar =>> Euro
                    4 - Euro =>> Dólar
                    5 - Peso mexicano =>> Euro
                    6 - Euro =>> Peso mexicano
                    7 - salir 
                        Elija una opción válida:
                    *********************************************************** """);

            opcion = sc.nextInt(); //permite ingresar datos

            switch (opcion) {
                case 1 -> convertir("USD", "MXN");
                case 2 -> convertir("MXN", "USD");
                case 3 -> convertir("USD", "EUR");
                case 4 -> convertir("EUR", "USD");
                case 5 -> convertir("MXN", "EUR");
                case 6 -> convertir("EUR", "MXN");
                case 7 -> System.out.println("Saliendo del programa...");
                default -> System.out.println("Opción inválida, intenta otra vez.");
            }
        }while (opcion != 7) ;
    }

    private static void convertir(String desde, String hacia) {

        Scanner sc = new Scanner(System.in);
        System.out.print("Ingresa cantidad a convertir (" + desde + "/" + hacia + "): ");
        double cantidad = sc.nextDouble();

        try{
            String direccion = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/"  + desde;

            HttpClient client = HttpClient.newHttpClient();  //nosotros cliente que pedira datos al servidos
            HttpRequest request = HttpRequest.newBuilder()  // que vamos a pedir del servidor patron BUILDER (EJEMPLO CONSTRUIR ALGO QUE PUEDE TENER MUCHAS FORMAS)
                    .uri(URI.create(direccion))
                    .GET()
                    .build();

            HttpResponse<String> response = client
                    .send(request, HttpResponse.BodyHandlers.ofString());

            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .create();

            Map json = gson.fromJson((response.body()), Map.class);
            Map tasas = (Map) json.get("conversion_rates");

            double tasa = (double) tasas.get(hacia);

            double resultado = cantidad * tasa;



            FileWriter historial = new FileWriter("busquedas.json", true);
            historial.write(gson.toJson(resultado));
            historial.close();

            System.out.println("\n==============================");
            System.out.println(" Moneda origen: " + desde);
            System.out.println(" Moneda destino: " + hacia);
            System.out.println(" Tasa actual: " + tasa);
            System.out.println(" Resultado: " + resultado + " " + hacia);
            System.out.println("==============================\n");


        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }




    }
}
