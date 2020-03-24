import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class CodeNamesBot extends TelegramLongPollingBot {

    Schema schema;

    @Override
    public void onUpdateReceived(Update update) {

        String text = update.getMessage().getText();

        System.out.println(text);
        System.out.println(text.substring(1));

        if (text.equals("/start")) {
            schema = new Schema();
            sendPicture(update);
            return;
        }

        if (schema.checkWord(text.substring(1))) {
            new Drawer(schema, "field", false);
            sendPicture(update);
            return;
        }


    }


    private void sendPicture(Update update) {
        new Drawer(schema, "field", false);
        try {
            SendPhoto photo = new SendPhoto().setPhoto("field", new FileInputStream(new File("src\\main\\images\\field.jpg")));
            photo.setChatId(update.getMessage().getChatId());
            this.execute(photo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return "CodeNamesBot";
    }

    @Override
    public String getBotToken() {
        return "";
    }
}