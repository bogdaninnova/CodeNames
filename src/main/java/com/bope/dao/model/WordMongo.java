package com.bope.dao.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Document(collection = "vocabulary")
@RequiredArgsConstructor
public class WordMongo implements Serializable {
    @Id private ObjectId id;
    @Getter @Setter private String lang;
    @Getter @Setter private String word;
}
