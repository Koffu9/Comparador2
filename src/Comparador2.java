import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Comparador2 {

    static class Record {
        String originalLine;  // la línea completa (por si quieres imprimirla tal cual)
        String idLeft;        // la parte izquierda antes del '='

        public Record(String originalLine, String idLeft) {
            this.originalLine = originalLine;
            this.idLeft = idLeft;
        }
    }

    public static void main(String[] args) {
        // Rutas de tus ficheros
        String filePath1 = "D:\\SteamLibrary\\steamapps\\common\\ProjectZomboid\\media\\lua\\shared\\Translate\\EN\\Farming_EN.txt";
        String filePath2 = "D:\\SteamLibrary\\steamapps\\common\\ProjectZomboid\\media\\lua\\shared\\Translate\\ES\\Farming_ES.txt";
        String outputFile = "C:\\Users\\Raúl\\Desktop\\aa\\Farming_ES1.txt";

        // 1) Leer ambos ficheros en listas que conserven el orden
        List<Record> recordsFileEn = readFilePreservingOrder(filePath1);
        List<Record> recordsFileEs = readFilePreservingOrder(filePath2);

        // 2) Meter IDs del segundo (ES) en un Set (para saber si un ID existe o no rápidamente)
        Set<String> idsFileEs = new HashSet<>();
        for (Record rec : recordsFileEs) {
            idsFileEs.add(rec.idLeft);
        }

        // 3) Del primer fichero, recolectamos las líneas que NO estén en el segundo
        //    (IDs únicos de EN, en orden EN)
        List<String> linesUniqueFromEn = new ArrayList<>();
        // 3.1) También guardo un set de IDs EN, para luego saber si se repite en ES
        Set<String> idsFileEn = new HashSet<>();
        for (Record rec : recordsFileEn) {
            idsFileEn.add(rec.idLeft);
        }

        for (Record recordEn : recordsFileEn) {
            if (!idsFileEs.contains(recordEn.idLeft)) {
                // ID no existe en ES => línea única
                linesUniqueFromEn.add(recordEn.originalLine);
            }
        }

        // 4) Ahora, del segundo fichero, queremos las líneas que SÍ están en EN (IDs repetidos).
        //    Y queremos mantener el orden del ES
        //    (Ojo: si prefieres mantener orden EN, tendrías que iterar en base a la lista EN).
        List<String> linesRepeatedUsingEs = new ArrayList<>();
        for (Record recordEs : recordsFileEs) {
            // Si el ID del recordEs existe en EN, significa que se repite en ambos
            if (idsFileEn.contains(recordEs.idLeft)) {
                linesRepeatedUsingEs.add(recordEs.originalLine);
            }
        }

        // 5) Escribir el resultado en Farming_ES1.txt
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))) {
            // Si quieres, puedes añadir una cabecera
            bw.write("Farming_ES = {\r\n");

            // 5.1) Primero las líneas únicas del EN
            for (String line : linesUniqueFromEn) {
                // Ajusta la indentación si quieres
                bw.write("    " + line + "\r\n");
            }

            // 5.2) Luego, al final, las líneas repetidas, pero en versión ES
            for (String line : linesRepeatedUsingEs) {
                bw.write("    " + line + "\r\n");
            }

            // Cerrar con llave
            bw.write("}\r\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Proceso completado. El fichero se ha guardado en: " + outputFile);
    }

    /**
     * Lee un fichero, conserva el orden y devuelve cada línea en forma
     * de Record (línea completa + ID extraído).
     */
    private static List<Record> readFilePreservingOrder(String filePath) {
        List<Record> result = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String trimmed = line.trim();
                // Ignoramos líneas vacías, comentarios o que no contengan '='
                if (trimmed.isEmpty()
                    || trimmed.startsWith("//")
                    || trimmed.startsWith("--")
                    || !line.contains("=")) {
                    continue;
                }
                // Partir en 2 por '='
                String[] parts = line.split("=", 2);
                if (parts.length >= 1) {
                    String leftSide = parts[0].trim(); // ID
                    if (!leftSide.isEmpty()) {
                        // Guardar la línea completa y su ID
                        Record record = new Record(line, leftSide);
                        result.add(record);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
