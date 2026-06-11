package com.wolflang.database;

import java.util.Map;
import java.util.UUID;

public interface DatabaseProvider {
    void connect();
    void disconnect();
    void createTable();
    void saveLanguage(UUID playerId, String language);
    Map<UUID, String> loadAllLanguages();
}
