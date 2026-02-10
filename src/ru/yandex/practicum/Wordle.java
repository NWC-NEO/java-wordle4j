package ru.yandex.practicum;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/*
в главном классе нам нужно:
    создать лог-файл (он должен передаваться во все классы)
    создать загрузчик словарей WordleDictionaryLoader
    загрузить словарь WordleDictionary с помощью класса WordleDictionaryLoader
    затем создать игру WordleGame и передать ей словарь
    вызвать игровой метод в котором в цикле опрашивать пользователя и передавать информацию в игру
    вывести состояние игры и конечный результат
 */
public class Wordle {

    private static PrintWriter log;

    public static void main(String[] args) {
        try {
            log = createLogFile();

            WordleDictionaryLoader loader = new WordleDictionaryLoader();
            WordleDictionary dictionary = loader.loadFromFile("words_ru.txt");

            dictionary.logState(log);

            WordleGame game = new WordleGame(dictionary, log);

            runGameLoop(game);

        } catch (Exception e) {
            logError(e);
        } finally {
            if (log != null) {
                log.println("Система: Завершение работы программы.");
                log.close();
            }
        }
    }

    private static void runGameLoop(WordleGame game) {
        Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
        int attemptsUsed = 0;
        final int MAX_ATTEMPTS = 6;

        printAndLog("Добро пожаловать в Wordle!");

        while (attemptsUsed < MAX_ATTEMPTS) {

            printAndLog("Попытка " + (attemptsUsed + 1) + "/6: ");
            printAndLog("Введите слово ...");
            printAndLog("Что бы получить подсказку ничего не вводите и нажимайте Enter");

            String input = scanner.nextLine().trim();

            try {
                if (input.isEmpty()) {
                    String hint = game.getHint();
                    printAndLog("Подсказка: " + (hint == null ? "нет" : hint));
                    continue;
                }

                String result = game.makeGuess(input);

                StringBuilder sb = new StringBuilder();
                sb.append(input).append("\n").append(result);
                System.out.println(sb);

                attemptsUsed++;

                if (result.equals("+++++")) {
                    printAndLog("Поздравляем! Вы угадали слово!");
                    return;
                }

            } catch (WordleException e) {
                System.out.println(e.getMessage());
            }
        }

        printAndLog("Ответ: " + game.getAnswer());
    }

    private static PrintWriter createLogFile() throws IOException {
        return new PrintWriter(
                new OutputStreamWriter(
                        new FileOutputStream("wordle.log"),
                        StandardCharsets.UTF_8),
                true
        );
    }

    private static void logError(Throwable e) {
        if (log != null) {
            log.println("Фатальная ошибка");
            e.printStackTrace(log);
        }
    }

    private static void printAndLog(String message) {
        System.out.println(message);
        if (log != null) {
            log.println("UI: " + message);
        }
    }
}