package com.bope.db;


import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "games")
public class GameMongo {

    @Id private ObjectId id;
    @Getter @Setter private String binaryGameString;
    @Getter @Setter private Long gameId;

    public GameMongo(Long gameId, String binaryGameString) {
        setBinaryGameString(binaryGameString);
        setGameId(gameId);
    }
}
