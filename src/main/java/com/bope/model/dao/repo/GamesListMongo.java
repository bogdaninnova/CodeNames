package com.bope.model.dao.repo;

import com.bope.model.dao.model.GameMongo;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GamesListMongo extends MongoRepository<GameMongo, String> {
    void removeByGameId(Long gameId);
    boolean existsByGameId(Long gameId);
    GameMongo findFirstByGameIdOrderByDateDesc(Long gameId);
}
