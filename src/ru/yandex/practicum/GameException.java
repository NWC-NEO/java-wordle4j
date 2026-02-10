package ru.yandex.practicum;

public class GameException extends RuntimeException {

    public GameException(String msg) {
        super(msg);
    }

    public GameException(String msg, Throwable t) {
        super(msg, t);
    }
}