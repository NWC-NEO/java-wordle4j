package ru.yandex.practicum;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/*
этот класс содержит в себе всю рутину по работе с файлами словарей и с кодировками
    ему нужны методы по загрузке списка слов из файла по имени файла
    на выходе должен быть класс WordleDictionary
 */
public class WordleDictionaryLoader {

    public WordleDictionary loadFromFile(String filename) throws GameException {
        List<String> words = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filename), StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String word = line.trim();
                if (!word.isEmpty()) {
                    words.add(word);
                }
            }

            if (words.isEmpty()) {
                throw new GameException("Словарь пуст");
            }

        } catch (IOException e) {
            if (Files.notExists(Paths.get(filename))) {
                throw new GameException("Файл словаря не найден: " + filename, e);
            } else {
                throw new GameException("Ошибка при чтении файла словаря: " + filename, e);
            }
        }

        return new WordleDictionary(words);
    }
}
