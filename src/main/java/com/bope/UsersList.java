package com.bope;

import com.mongodb.*;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Component
public class UsersList {

    public long getUserId(String userName) {
        long id = 0;
        MongoClient mongo = new MongoClient( "localhost" , 27017 );
        DB db = mongo.getDB("test");
        DBCollection table = db.getCollection("users");

        BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.put("userName", userName);

        DBCursor cursor = table.find(searchQuery);



        while (cursor.hasNext()) {
            String ids = String.valueOf(cursor.next().get("userId"));
            System.out.println(ids);
            id = Long.parseLong(ids);
            System.out.println("-------------------------------------------------");
        }
        return id;
    }

    public void addUser(String userName, int id) {
        MongoClient mongo = new MongoClient( "localhost" , 27017 );
        DB db = mongo.getDB("test");
        DBCollection table = db.getCollection("users");
        BasicDBObject document = new BasicDBObject();
        document.put("userName", userName);
        document.put("userId", String.valueOf(id));
        table.insert(document);
    }

}
