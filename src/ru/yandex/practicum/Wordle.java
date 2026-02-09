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

        System.out.println("Добро пожаловать в Wordle!");

        while (!game.isGameOver()) {

            int attemptNumber = 6 - game.getStepsLeft() + 1;
            System.out.println("Попытка " + attemptNumber + "/6: ");
            System.out.println("Введите слово ...");
            System.out.println("Что бы получить подсказку ничего не вводите и нажимайте Enter");

            String input = scanner.nextLine().trim();

            try {
                if (input.isEmpty()) {
                    String hint = game.getHint();
                    System.out.println("Подсказка: " + (hint == null ? "нет" : hint));
                    continue;
                }

                String result = game.makeGuess(input);

                StringBuilder sb = new StringBuilder();
                sb.append(input).append("\n").append(result);
                System.out.println(sb);

            } catch (WordleException e) {
                System.out.println(e.getMessage());
            }
        }

        System.out.println("Ответ: " + game.getAnswer());
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
}