
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.NoSuchElementException;
import java.util.Scanner;


public class Main {

    public static void main(String[] args) {

        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(new CodeNamesBot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }







    public static void runCodeNames() {
        Schema schema = new Schema();
        new Drawer(schema, "field", false);
        new Drawer(schema, "fieldAdmin", true);
        Scanner scanner = new Scanner(System.in);
        try {
            while (true) {
                String line = scanner.nextLine();
                if (schema.checkWord(line))
                    new Drawer(schema, "field", false);
            }
        } catch(IllegalStateException | NoSuchElementException e) {
            e.printStackTrace();
        }
    }





}
