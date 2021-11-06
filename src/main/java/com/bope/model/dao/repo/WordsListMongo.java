package com.bope.model.dao.repo;
import com.bope.model.dao.model.WordMongo;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface WordsListMongo extends MongoRepository<WordMongo, String> {
    List<WordMongo> findByLang(String lang);
}
