package tsn.java.threads;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

// Финальный поток решения всех задач
// Вызывается автоматически CyclicBarrier, когда выполнятся все задачи
public class FinalThread implements Runnable { // ФИНАЛЬНЫЙ ПОТОК-ЗАДАЧА
    float[][] arraySolutions; // Массив вычисленных значений заданий
    volatile boolean completion; // Флаг завершения данного потока

    FinalThread(float[][] arraySolutions) {
        this.arraySolutions = arraySolutions;
        this.completion = false; // Инициализация флага завершения данного потока
    }

    @Override
    public void run() {
        System.out.println("Активирован финальный поток потокового барьера ...");
        System.out.println("Полученные решения от потоков: ");

        // -------------------------------------------------------
        String dir = new File(".").getAbsoluteFile().getParentFile().getAbsolutePath()
                + System.getProperty("file.separator");
        String FileName = dir + "expr_data.xml"; // Имя файла с настройками 
        File f = new File(FileName); // Создаем объект доступа к файлу параметров
        // Объект для хранения параметров со значениями (имена параметров чувствительны к регистру)
        Properties p = new Properties();

        try {
            if (f.exists() == false) { // Если нет файла, то создать его
                f.createNewFile();  // Создать файл
            } else {
                p.loadFromXML(new FileInputStream(FileName)); // Если файл есть, то загрузить его с диска
            }
        } catch (IOException ex) {
            System.err.println("Ошибка доступа к XML-файлу!"); // Вывести сообщения об ошибке
        }

        for (int i = 0; i < arraySolutions.length; i++) { // Цикл по результатам вычислений
            System.out.printf("Решение потока %d: %f для %f / %f", i+1, 
                    arraySolutions[i][2], arraySolutions[i][0], arraySolutions[i][1]); // Выводим на экран результат i-го вычисления
            System.out.println(" --> сохраняем этот результат в XML-файле ...");
            p.setProperty(String.valueOf(i)+"_a", String.valueOf(arraySolutions[i][0])); // Сохраняем a результата i-го вычисления
            p.setProperty(String.valueOf(i)+"_b", String.valueOf(arraySolutions[i][1])); // Сохраняем b результата i-го вычисления
            p.setProperty(String.valueOf(i)+"_c", String.valueOf(arraySolutions[i][2])); // Сохраняем c результата i-го вычисления
        }
        try {
            p.storeToXML(new FileOutputStream(FileName), new Date().toString()); // Сохраняем XML-файл на диск
        } catch (FileNotFoundException ex) {
            System.err.println("Ошибка записи в XML-файл"); // Вывести сообщения об ошибке
        } catch (IOException ex) {
            System.err.println("Ошибка записи в XML-файл"); // Вывести сообщения об ошибке
        }

        // -------------------------------------------------------
        completion = true; // Инициализация флага завершения данного потока
        System.out.println("Финальный поток закончил свою работу ...");
        System.out.println("Пробуждение основного потока программы ...");
    }
}
