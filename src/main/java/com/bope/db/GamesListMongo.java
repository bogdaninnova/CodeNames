package com.bope.db;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface GamesListMongo extends MongoRepository<GameMongo, String> {
    GameMongo findByGameId(Long gameId);
    void removeByGameId(Long gameId);
    boolean existsByGameId(Long gameId);
}
