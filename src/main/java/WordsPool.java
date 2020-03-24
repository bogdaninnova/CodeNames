import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class WordsPool {

    private List<String> wordList;
    private Random rand = new Random();

    public WordsPool() {
        setWordList(getData("src\\main\\resources\\wordSet.txt"));
    }

    private List<String> getData(String path) {
        String[] array = {""};
        try {
            String result = new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
            array = result.split(", ");
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<String> set = new ArrayList<>();
        Collections.addAll(set, array);

        return set;
    }

    public List<String> getWordList() {
        return wordList;
    }

    public void setWordList(List<String> wordList) {
        this.wordList = wordList;
    }

    public String getRandomWord() {
        String word = wordList.remove(rand.nextInt(wordList.size()));
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }
}
