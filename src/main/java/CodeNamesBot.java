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

    UsersList usersList = new UsersList();
    private String token = "";

    private Map<Long, Game> games = new HashMap<>();

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

        if (text.equals("/start")) {
            usersList.addUser(user.getUserName(), user.getId());
            return;
        }

        if (text.equals("/caps")) {
            if (games.containsKey(chatId))
                sendCaptains(games.get(chatId));
            else
                sendSimpleMessage("The game has not started", chatId);
            return;
        }

        if (text.length() > 10)
            if (text.toLowerCase().substring(0, 10).equals("/newgame @")) {
                Set<String> set = new HashSet<>(Arrays.asList(text.toLowerCase().replace(" ", "").substring(text.indexOf("@")).split("@")));
                if (set.size() != 2) {
                    sendSimpleMessage("You need to choose two captains!", chatId);
                    return;
                }
                for (String cap : set) {
                    if (!usersList.allUsers.containsKey(cap)) {
                        sendSimpleMessage("User @" + cap + " is not registered. Please send me /start in private message", chatId);
                        return;
                    }
                }

                Game game = new Game(chatId);
                game.setCaps(set);
                game.createSchema();
                games.put(chatId, game);

                sendPicture(games.get(chatId), chatId, false);
                for (String cap : set)
                    sendPicture(games.get(chatId), usersList.allUsers.get(cap), true);
                return;
            }

        if (games.get(chatId).getSchema().checkWord(text.substring(1))) {
            sendPicture(games.get(chatId), chatId, false);
            for (String cap : games.get(chatId).getCaps())
                sendPicture(games.get(chatId), usersList.allUsers.get(cap), true);
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


    private void sendCaptains(Game game) {
        SendMessage message = new SendMessage();
        message.setChatId(game.getChatId());
        StringBuilder sb = new StringBuilder("Captains:");
        for (String cap : game.getCaps()) {
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

    private void sendPicture(Game game, long chatId, boolean isAdmin) {
        String filepath = getFilePath(game.getChatId(), isAdmin);
        new Drawer(game.getSchema(), filepath, isAdmin);
        try {
            SendPhoto photo = new SendPhoto().setPhoto("board", new FileInputStream(new File(filepath)));
            photo.setChatId(chatId);
            this.execute(photo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getFilePath(long chatId, boolean isAdmin) {
        String name = chatId + "_admin" + isAdmin + ".jpg";
        return "src\\main\\images\\" + name;
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
