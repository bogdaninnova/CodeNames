package com.bope.model.dao.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Document(collection = "vocabulary")
@RequiredArgsConstructor
@Setter
@Getter
public class WordMongo implements Serializable {
    @Id private ObjectId id;
    private String lang;
    private String word;
}
