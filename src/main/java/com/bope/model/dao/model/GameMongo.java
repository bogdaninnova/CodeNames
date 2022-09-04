package com.bope.model.dao.model;


import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "games")
@Getter
@Setter
public class GameMongo {

    @Id private ObjectId id;
    private String binaryGameString;
    private Long gameId;
    private Date date;

    public GameMongo(Long gameId, String binaryGameString) {
        setBinaryGameString(binaryGameString);
        setGameId(gameId);
        setDate(new Date());
    }
}
