package tsn.java.threads;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class SolvingThread implements Runnable { // ПОТОК-ЗАДАЧА
    CyclicBarrier cyclicBarrier; // Потоковый барьер
    SolvingProblem solvingProblem; // Объект-вычисление
    int numSolution; // Номер задания

    SolvingThread(CyclicBarrier cyclicBarrier, SolvingProblem solvingProblem, int numSolution) {
        this.cyclicBarrier = cyclicBarrier; // Установка потока-барьера
        this.solvingProblem = solvingProblem; // Инициализация объекта-вычисления
        this.numSolution = numSolution; // Установка номера задания
        new Thread(this).start(); // Самозапуск этого потока
    }

    @Override
    public void run() { // Основной метод потока-задачи
        try {
            solvingProblem.doExpr(numSolution); // Вычисляем значение
            // Сообщаем cyclicBarrier-барьеру чтобы контролировал окончание этого потока
            cyclicBarrier.await(); 
        } catch (InterruptedException | BrokenBarrierException ex) {
        }
    }
}
