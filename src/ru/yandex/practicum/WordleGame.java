package ru.yandex.practicum;

import java.io.PrintWriter;
import java.util.*;

/*
в этом классе хранится словарь и состояние игры
    текущий шаг
    всё что пользователь вводил
    правильный ответ

в этом классе нужны методы, которые
    проанализируют совпадение слова с ответом
    предложат слово-подсказку с учётом всего, что вводил пользователь ранее

не забудьте про специальные типы исключений для игровых и неигровых ошибок
 */
public class WordleGame {

    private static final Random RANDOM = new Random();

    private final WordleDictionary dictionary;
    private final String answer;
    private final PrintWriter log; // Поле для логирования
    private final List<String> attempts = new ArrayList<>();
    private final List<String> hints = new ArrayList<>();
    private final Set<Character> absent = new HashSet<>();
    private final Map<Integer, Character> correct = new HashMap<>();
    private final Map<Character, Set<Integer>> wrong = new HashMap<>();
    private int steps = 6;
    private boolean gameOver;
    private boolean gameWon;

    public WordleGame(WordleDictionary dictionary, PrintWriter log) {
        this.dictionary = dictionary;
        this.answer = dictionary.getRandomWord();
        this.log = log;

        log.println("НОВАЯ ИГРА НАЧАТА");
        log.println("Загаданное слово: " + answer);
    }

    public String makeGuess(String word) throws WordleException {

        if (gameOver)
            throw new InvalidWordException("Игра окончена");

        String normalized = WordleDictionary.normalizeWord(word);

        if (normalized.length() != 5)
            throw new InvalidWordException("Слово должно состоять из 5 букв");

        if (!dictionary.contains(normalized)) {
            log.println("Валидация: Слово '" + normalized + "' отсутствует в словаре.");
            throw new WordNotFoundInDictionaryException(normalized);
        }

        steps--;
        attempts.add(normalized);

        String result = dictionary.analyzeWord(normalized, answer);
        hints.add(result);

        log.println("Ход " + (attempts.size()) + ": '" + normalized + "' -> [" + result + "]");

        updateFilters(normalized, result);

        if (normalized.equals(answer)) {
            gameWon = true;
            gameOver = true;
            log.println("Результат: ПОБЕДА на ходу " + attempts.size());
        } else if (steps == 0) {
            gameOver = true;
            log.println("Результат: ПРОИГРЫШ. Попытки исчерпаны.");
        }

        return result;
    }

    private void updateFilters(String guess, String hint) {

        for (int i = 0; i < 5; i++) {

            char g = guess.charAt(i);
            char h = hint.charAt(i);

            switch (h) {

                case '+':
                    correct.put(i, g);
                    break;

                case '^':
                    Set<Integer> positions = wrong.get(g);
                    if (positions == null) {
                        positions = new HashSet<>();
                        wrong.put(g, positions);
                    }
                    positions.add(i);
                    break;

                case '-':
                    // НЕ добавляем в absent, если буква уже подтверждена
                    if (!correct.containsValue(g) && !wrong.containsKey(g)) {
                        absent.add(g);
                    }
                    break;
            }
        }

        log.println("Обновление состояния фильтров:");
        log.println("  - Отсутствуют: " + absent);
        log.println("  - Точные позиции: " + correct);
        log.println("  - Неправильные позиции: " + wrong);
    }

    public String getHint() {

        if (gameOver) return null;

        List<String> candidates = dictionary.filter(absent, correct, wrong);
        log.println("  - Найдено подходящих слов в словаре: " + candidates.size());
        candidates.removeAll(attempts);

        if (candidates.isEmpty()) {
            log.println("  - Подходящих слов (кроме уже введённых) не найдено.");
            return null;
        }

        return candidates.get(RANDOM.nextInt(candidates.size()));
    }

    public List<String> getAttempts() {
        return new ArrayList<>(attempts);
    }

    public List<String> getHints() {
        return new ArrayList<>(hints);
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isGameWon() {
        return gameWon;
    }

    public int getStepsLeft() {
        return steps;
    }

    public String getAnswer() {
        return answer;
    }
}
