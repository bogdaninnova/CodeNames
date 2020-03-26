import java.util.HashSet;
import java.util.Set;

public class Game {

    private long chatId;
    private Set<String> caps = new HashSet<>();
    private Schema schema;

    public Game(long chatId) {
        setChatId(chatId);
    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public Set<String> getCaps() {
        return caps;
    }

    public void setCaps(Set<String> set) {
        caps = new HashSet<>();
        caps.addAll(set);
    }

    public Schema getSchema() {
        return schema;
    }

    public void createSchema() {
        this.schema = new Schema();
    }
}
