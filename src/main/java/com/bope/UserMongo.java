package com.bope;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
public class UserMongo {

    @Id
    private ObjectId id;
    private String userName;
    private String userId;

    public UserMongo(String userName, String userId) {
        setUserName(userName);
        setUserId(String.valueOf(userId));
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
