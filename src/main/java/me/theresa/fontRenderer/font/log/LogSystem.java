package me.theresa.fontRenderer.font.log;

public interface LogSystem {

    void error(String message, Throwable e);

    void error(Throwable e);

    void error(String message);

    void warn(String message);

    void warn(String message, Throwable e);

    void info(String message);

    void debug(String message);
}
