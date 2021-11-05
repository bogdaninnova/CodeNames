package com.bope.dao.model;


import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "games")
public class GameMongo {

    @Id private ObjectId id;
    @Getter @Setter private String binaryGameString;
    @Getter @Setter private Long gameId;
    @Setter private Date date;

    public GameMongo(Long gameId, String binaryGameString) {
        setBinaryGameString(binaryGameString);
        setGameId(gameId);
        setDate(new Date());
    }
}
