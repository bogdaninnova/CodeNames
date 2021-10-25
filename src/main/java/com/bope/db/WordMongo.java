package com.bope.db;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "vocabulary")
@RequiredArgsConstructor
public class WordMongo {
    @Id private ObjectId id;
    @Getter @Setter private String lang;
    @Getter @Setter private String word;
}
