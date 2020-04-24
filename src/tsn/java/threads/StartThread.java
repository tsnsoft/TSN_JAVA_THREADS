package tsn.java.threads;

import java.util.concurrent.CyclicBarrier;

public class StartThread { // РАБОТА С ПОТОКАМИ, ПОТОКОВЫМИ БАРЬЕРАМИ И БЛОКИРОВКАМИ

    public static void main(String args[]) throws InterruptedException {
        final int numSolutions = 10; // Количество потоков-задач
        final int numSolutionsData = 3; // Количество данных в задаче

        // Массив вычисленных значений заданий
        // [][0] - a, [][1] - b, [][2] - c
        float[][] arraySolutions = new float[numSolutions][numSolutionsData]; 
        
        // Объект-вычислитель значений
        SolvingProblem solvingProblem = new SolvingProblem(arraySolutions); 
        
        // Финальный поток для подитоживания всех результатов вычислений
        FinalThread finalThread = new FinalThread(arraySolutions); 
        
         // Потоковый барьер, который вызовет finalThread, когда выполнятся все задачи (их количесвто numSolutions)
        CyclicBarrier cyclicBarrier = new CyclicBarrier(numSolutions, finalThread);

        System.out.println("Laboratory work");
        System.out.println("Task: C = A / B");
        System.out.println("Активирован потоковый барьер для " + numSolutions + " потоков ...");

        // Запускаем нужное количество потоков-решиней задачи
        for (int numSolution = 1; numSolution <= numSolutions; numSolution++) { 
            new SolvingThread(cyclicBarrier, solvingProblem, numSolution); // Запуск потоков-задач
            System.out.println("Запущен поток " + numSolution + " ...");
        }
        
        System.out.println("Основной поток программы переводится в сон с ожиданием выполнения всех задач...");
        
        while (!finalThread.completion) { // Пока не завершен финальный поток - ожидание
            Thread.sleep(10); // Ожидание этого потока в 10 мс
        }
        
        System.out.println("Основной поток и программа успешно завершены!!!");
    }
}
