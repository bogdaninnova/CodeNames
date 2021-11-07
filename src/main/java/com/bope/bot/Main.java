package com.bope.bot;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
public class Main {

    public static AnnotationConfigApplicationContext ctx;

    public static void main(String[] args) {
        try {
            new TelegramBotsApi(DefaultBotSession.class);
            ctx = new AnnotationConfigApplicationContext(SpringConfig.class);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
