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
        char[] answerChars = answer.toCharArray();
        boolean[] used = new boolean[5];

        // +
        for (int i = 0; i < 5; i++) {
            if (guess.charAt(i) == answerChars[i]) {
                result[i] = '+';
                used[i] = true;
            }
        }

        // ^ и -
        for (int i = 0; i < 5; i++) {

            if (result[i] == '+') continue;

            char ch = guess.charAt(i);
            boolean found = false;

            for (int j = 0; j < 5; j++) {
                if (!used[j] && answerChars[j] == ch) {
                    used[j] = true;
                    found = true;
                    break;
                }
            }

            result[i] = found ? '^' : '-';
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

            for (char c : absent)
                if (word.indexOf(c) >= 0)
                    continue outer;

            for (var e : correct.entrySet())
                if (word.charAt(e.getKey()) != e.getValue())
                    continue outer;

            for (var e : wrong.entrySet()) {
                if (word.indexOf(e.getKey()) == -1)
                    continue outer;

                for (int pos : e.getValue())
                    if (word.charAt(pos) == e.getKey())
                        continue outer;
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