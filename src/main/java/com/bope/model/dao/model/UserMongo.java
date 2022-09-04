package com.bope.model.dao.model;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Document(collection = "users")
@Getter
@Setter
public class UserMongo implements Serializable {

    @Id private ObjectId id;
    private String userName;
    private String userId;

    public UserMongo(String userName, String userId) {
        setUserName(userName);
        setUserId(String.valueOf(userId));
    }

    public long getLongId() {return Long.parseLong(userId);}
}
