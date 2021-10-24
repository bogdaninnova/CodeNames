package com.bope.db;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
public class UserMongo {

    @Id private ObjectId id;
    @Getter @Setter private String userName;
    @Getter @Setter private String userId;

    public UserMongo(String userName, String userId) {
        setUserName(userName);
        setUserId(String.valueOf(userId));
    }

    public long getLongId() {return Long.parseLong(userId);}
}
