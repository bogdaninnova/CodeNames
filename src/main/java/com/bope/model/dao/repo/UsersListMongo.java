package com.bope.model.dao.repo;
import com.bope.model.dao.model.UserMongo;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UsersListMongo extends MongoRepository<UserMongo, String> {
    UserMongo findByUserName(String userName);
    void removeByUserName(String userName);
}
