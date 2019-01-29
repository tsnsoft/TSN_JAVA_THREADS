package tsn.java.threads;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Date;
import java.util.Properties;

class Expr { // КЛАСС РЕШЕНИЯ ЗАДАНИЯ

    ReentrantLock lock = new ReentrantLock(); // Блокировщик потоков
    float[] exprArr; // Массив вычисленных значений заданий

    Expr(float[] exprArr) { // Инициализация решателя заданий
        this.exprArr = exprArr; // Получаем доступ к внешнему массиву вычисленных значений заданий
    }

    void doExpr(int numTask) { // Метод решения заданий
        lock.lock(); // Блокировка других потоков
        try {
            System.out.println("Поток " + numTask + " успешно захватил управление решателем заданий ...");
            // ------------------------------------------------------- АЛГОРИТМ РЕШЕНИЯ ВАРИАНТА ЗАДАНИЯ ---

            Scanner sc = new Scanner(System.in); // Подключение к консоли
            System.out.print("Введите A: "); // Вывод значения
            float a = sc.nextFloat(); // Ввод с консоли вещественного значения
            System.out.print("Введите B: "); // Вывод вопроса
            float b = sc.nextFloat();// Ввод с консоли вещественного значения
            float c = a / b; // Расчет значения по заданному алгоритму
            exprArr[numTask - 1] = c; // Сохранение решения в результирующем массиве

            // -------------------------------------------------------
            System.out.println("Решение задания " + numTask + " было успешно получено и передано в основной поток!");
        } catch (Exception e) {
            exprArr[numTask - 1] = 0; // Сохранение решения в результирующем массиве при ошибке расчета
        } finally {
            System.out.println("Поток " + numTask + " отключился от управления решателем заданий.");
            System.out.println();
            lock.unlock(); // Снятие блокировки на запись другими потоками
        }
    }
}

public class App1 { // РАБОТА С ПОТОКАМИ, ПОТОКОВЫМИ БАРЬЕРАМИ И БЛОКИРОВКАМИ

    public static void main(String args[]) throws InterruptedException {
        final int numTask = 3; // Количество потоков-задач
        float[] exprArr = new float[numTask];  // Массив вычисленных значений заданий
        Expr expr = new Expr(exprArr); // Объект-вычислитель значений
        FinalTask finalTask = new FinalTask(expr, exprArr); // Финальный поток для барьера
        CyclicBarrier CyclicBarrier = new CyclicBarrier(numTask, finalTask); // Потоковый барьер
        System.out.println("Laboratory work");
        System.out.println("Task: C = A / B");
        System.out.println("Активирован потоковый барьер для " + numTask + " потоков ...");
        for (int i = 1; i <= numTask; i++) { // Запускаем нужное количество потоков задач
            new MyTask(CyclicBarrier, expr, i); // Запуск потоков-задач
            System.out.println("Запущен поток " + i + " ...");
        }
        System.out.println("Основной поток программы переведен в сон ...");
        while (!finalTask.completion) { // Пока не завершен финальный поток - ожидание
            Thread.sleep(10); // Ожидание этого потока в 1 мс
        }
        System.out.println("Основной поток и программа успешно завершены!!!");
    }
}

class MyTask implements Runnable { // ПОТОК-ЗАДАЧА

    CyclicBarrier сyclicBarrier; // Потоковый барьер
    Expr expr; // Объект-вычисление
    int numTask; // Номер задания

    MyTask(CyclicBarrier cyclicBarrier, Expr expr, int numTask) {
        this.numTask = numTask; // Установка номера задания
        this.сyclicBarrier = cyclicBarrier; // Установка потока-барьера
        this.expr = expr; // Инициализация объекта-вычисления
        new Thread(this).start(); // Самозапуск потока
    }

    @Override
    public void run() { // Основной метод потока-задачи
        try {
            expr.doExpr(numTask); // Вычисляем значение
            сyclicBarrier.await(); // Сообщаем барьеру что поток закончил работу
        } catch (InterruptedException | BrokenBarrierException ex) {
        }
    }
}

class FinalTask implements Runnable { // ФИНАЛЬНЫЙ ПОТОК-ЗАДАЧА

    volatile boolean completion; // Флаг завершения данного потока
    Expr expr;  // Объект-вычисление
    float[] exprArr; // Массив вычисленных значений заданий

    FinalTask(Expr expr, float[] exprArr) {
        this.exprArr = exprArr;
        this.expr = expr; // Инициализация объекта-решателя
        this.completion = false; // Инициализация флага завершения данного потока
    }

    @Override
    public void run() {
        System.out.println("Активирован финальный поток потокового барьера ...");
        System.out.println("Полученные решения от потоков: ");
        // ------------------------------------------------------- АЛГОРИТМ РЕШЕНИЯ ВАРИАНТА ЗАДАНИЯ ---

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

        for (int i = 0; i < exprArr.length; i++) { // Цикл по результатам вычислений
            System.out.print(exprArr[i]); // Выводим на экран результат i-го вычисления
            System.out.println(" --> сохраняем этот результат в XML-файле ...");
            p.setProperty(String.valueOf(i), String.valueOf(exprArr[i])); // Сохраняем результат i-го вычисления
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
