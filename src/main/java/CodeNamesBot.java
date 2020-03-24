import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

public class CodeNamesBot extends TelegramLongPollingBot {

    Schema schema;
    UsersList usersList = new UsersList();
    Set<String> caps = new HashSet<>();
    private String token = "";


    public CodeNamesBot() {
        try {
            FileInputStream fileInput = new FileInputStream(new File("src\\main\\resources\\token.properties"));
            Properties properties = new Properties();
            properties.load(fileInput);
            fileInput.close();
            token = properties.getProperty("token");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onUpdateReceived(Update update) {

        String text = update.getMessage().getText();
        User user = update.getMessage().getFrom();
        long chatId = update.getMessage().getChatId();

        System.out.println(text);
        System.out.println(user);

        if (text.toLowerCase().equals("/new") || text.equals("/new@CheCodeNamesBot")) {
            if (caps.size() == 0) {
                sendSimpleMessage("Пожалуйста выберите капитанов", chatId);
                return;
            }
            schema = new Schema();
            sendPicture(chatId, "field");
            usersList.print();
            for (String cap : caps) {
                if (usersList.allUsers.containsKey(cap)) {
                    new Drawer(schema, "fieldAdmin", true);
                    sendPicture(usersList.allUsers.get(cap), "fieldAdmin");
                } else {
                    sendSimpleMessage("User @" + cap + " is not registered. Please send me /start in private message", chatId);
                }
            }
            return;
        }

        if (text.equals("/start")) {
            usersList.addUser(user.getUserName(), user.getId());
        }

        if (text.equals("/привет")) {
            sendSimpleMessage("Ну привет, " + user.getFirstName(), chatId);
        }

        if (text.length() > 7)
            if (text.toLowerCase().substring(0, 7).equals("/caps @")) {
                Set<String> set = new HashSet<>(Arrays.asList(text.toLowerCase().replace(" ", "").substring(text.indexOf("@")).split("@")));
                if (set.size() == 2) {
                    caps.clear();
                    caps.addAll(set);
                    sendCaptains(chatId);
                } else {
                    sendSimpleMessage("Капитана должно быть два!", chatId);
                }
            }

        if (text.equals("/caps")) {
            sendCaptains(chatId);
        }

        if (schema.checkWord(text.substring(1))) {
            new Drawer(schema, "field", false);
            sendPicture(chatId, "field");
        }


    }


    private void sendSimpleMessage(String text, long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    private void sendCaptains(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        StringBuilder sb = new StringBuilder("Captains:");
        for (String cap : caps) {
            sb.append(" @");
            sb.append(cap);
        }

        message.setText(sb.toString());
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }



    private void sendPicture(long chatId, String name) {
        new Drawer(schema, "field", false);
        try {
            SendPhoto photo = new SendPhoto().setPhoto("field", new FileInputStream(new File("src\\main\\images\\" + name + ".jpg")));
            photo.setChatId(chatId);
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
        return token;
    }
}
