package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WordleTest {

    private WordleDictionary dictionary;
    private PrintWriter log;

    @BeforeEach
    void setUp() {
        List<String> words = Arrays.asList(
                "тесто", "место", "текст", "сетко",
                "герой", "гонец", "столб", "столы"
        );

        dictionary = new WordleDictionary(words);
        log = new PrintWriter(new StringWriter());
    }

    @Test
    void testCorrectGuess() throws Exception {

        WordleGame game = new WordleGame(
                new WordleDictionary(Arrays.asList("слово")) {
                    @Override
                    public String getRandomWord() {
                        return "слово";
                    }
                },
                log
        );

        String result = game.makeGuess("слово");

        assertEquals("+++++", result);
    }

    @Test
    void testAttemptsStored() throws Exception {

        WordleGame game = new WordleGame(dictionary, log);
        game.makeGuess("герой");

        assertEquals(1, game.getAttempts().size());
        assertEquals(1, game.getHints().size());
    }

    @Test
    void testRepeatedLettersHandledCorrectly() throws Exception {

        WordleDictionary dict = new WordleDictionary(
                Arrays.asList(
                        "тесто",
                        "ттттт",
                        "место",
                        "текст"
                )
        ) {
            @Override
            public String getRandomWord() {
                return "тесто";
            }
        };

        WordleGame game = new WordleGame(dict, log);

        String result = game.makeGuess("ттттт");

        assertEquals("+--+-", result);
        assertNotNull(game.getHint());
    }

    @Test
    void testGameOverAfterSixAttempts() throws Exception {

        WordleGame game = new WordleGame(dictionary, log);

        for (int i = 0; i < 6; i++) {
            game.makeGuess("герой");
        }

        assertEquals(6, game.getAttempts().size());
        assertDoesNotThrow(() -> game.makeGuess("столы"));
    }
}
