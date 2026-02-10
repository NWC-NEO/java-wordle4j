package ru.yandex.practicum;

import java.io.PrintWriter;
import java.util.*;

/*
этот класс содержит в себе список слов List<String>
    его методы похожи на методы списка, но учитывают особенности игры
    также этот класс может содержать рутинные функции по сравнению слов, букв и т.д.
 */
public class WordleDictionary {

    private static final Random RANDOM = new Random();

    private final List<String> wordsList;
    private final Set<String> wordsSet;

    public WordleDictionary(List<String> rawWords) {

        wordsList = new ArrayList<>();
        wordsSet = new HashSet<>();

        for (String w : rawWords) {
            String normalized = normalizeWord(w);

            if (normalized.length() == 5) {
                wordsList.add(normalized);
                wordsSet.add(normalized);
            }
        }

        if (wordsList.isEmpty()) {
            throw new IllegalStateException("Словарь пуст");
        }
    }

    public static String normalizeWord(String word) {
        return word == null ? "" :
                word.toLowerCase().replace('ё', 'е').trim();
    }

    public boolean contains(String word) {
        return wordsSet.contains(normalizeWord(word));
    }

    public String getRandomWord() {
        return wordsList.get(RANDOM.nextInt(wordsList.size()));
    }

    public String analyzeWord(String guess, String answer) {
        guess = normalizeWord(guess);
        answer = normalizeWord(answer);

        char[] result = new char[5];
        Map<Character, Integer> unusedCount = new HashMap<>();

        // Ищем только точные совпадения
        for (int i = 0; i < 5; i++) {
            char g = guess.charAt(i);
            char a = answer.charAt(i);

            if (g == a) {
                result[i] = '+';
            } else {
                unusedCount.put(a, unusedCount.getOrDefault(a, 0) + 1);
            }
        }

        // Ищем буквы не на своих местах
        for (int i = 0; i < 5; i++) {
            if (result[i] == '+') {
                continue;
            }

            char g = guess.charAt(i);
            int count = unusedCount.getOrDefault(g, 0);

            if (count > 0) {
                result[i] = '^';
                unusedCount.put(g, count - 1);
            } else {
                result[i] = '-';
            }
        }

        return new String(result);
    }

    public List<String> filter(
            Set<Character> absent,
            Map<Integer, Character> correct,
            Map<Character, Set<Integer>> wrong) {

        List<String> result = new ArrayList<>();

        outer:
        for (String word : wordsList) {

            for (char c : absent) {
                if (word.indexOf(c) >= 0) {
                    continue outer;
                }
            }

            for (var e : correct.entrySet()) {
                if (word.charAt(e.getKey()) != e.getValue()) {
                    continue outer;
                }
            }

            for (var e : wrong.entrySet()) {
                if (word.indexOf(e.getKey()) == -1) {
                    continue outer;
                }

                for (int pos : e.getValue()) {
                    if (word.charAt(pos) == e.getKey()) {
                        continue outer;
                    }
                }
            }

            result.add(word);
        }

        return result;
    }

    public void logState(PrintWriter log) {
        StringBuilder sb = new StringBuilder();
        sb.append("Словарь: ").append(wordsList.size()).append(" слов");
        log.println(sb);
    }
}