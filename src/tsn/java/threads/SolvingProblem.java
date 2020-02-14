package tsn.java.threads;

import java.util.Scanner;
import java.util.concurrent.locks.ReentrantLock;

public class SolvingProblem { // КЛАСС РЕШЕНИЯ ЗАДАНИЯ
    ReentrantLock reentrantLock = new ReentrantLock(); // "Входящий замок" потоков
    float[] arraySolutions; // Массив вычисленных значений заданий

    SolvingProblem(float[] arraySolutions) { // Инициализация решателя заданий
        this.arraySolutions = arraySolutions; // Получаем доступ к внешнему массиву вычисленных значений заданий
    }

    void doExpr(int numSolution) { // Метод решения заданий
        reentrantLock.lock(); // Блокировка других потоков

        try {
            System.out.println("Поток " + numSolution + " успешно захватил управление решателем заданий ...");

            // ------------------------------------------------------- АЛГОРИТМ РЕШЕНИЯ ВАРИАНТА ЗАДАНИЯ ---
            Scanner sc = new Scanner(System.in); // Подключение к консоли
            System.out.print("Введите A: "); // Вывод значения
            float a = sc.nextFloat(); // Ввод с консоли вещественного значения
            System.out.print("Введите B: "); // Вывод вопроса
            float b = sc.nextFloat();// Ввод с консоли вещественного значения
            float c = a / b; // Расчет значения по заданному алгоритму
            arraySolutions[numSolution - 1] = c; // Сохранение решения в результирующем массиве
            // -------------------------------------------------------

            System.out.println("Решение задания " + numSolution + " было успешно получено и передано в основной поток!");
        } catch (Exception e) {
            arraySolutions[numSolution - 1] = 0; // Сохранение решения в результирующем массиве при ошибке расчета
        } finally {
            System.out.println("Поток " + numSolution + " отключился от управления решателем заданий.");
            System.out.println();
    
            reentrantLock.unlock(); // Снятие блокировки "замка" на разрешения работы других потоков
        }

    }
}
