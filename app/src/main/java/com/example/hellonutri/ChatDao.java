package com.example.hellonutri;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface ChatDao {
    @Insert
    void insert(ChatMessage chatMessage);

    // Untuk memuat chat per sesi
    @Query("SELECT * FROM chat_history WHERE sessionId = :sId ORDER BY timestamp ASC")
    List<ChatMessage> getMessagesBySession(long sId);

    // Untuk daftar di halaman riwayat (Hanya baris pertama tiap sesi)
    @Query("SELECT * FROM chat_history GROUP BY sessionId ORDER BY timestamp DESC")
    List<ChatMessage> getUniqueSessions();

    @Query("DELETE FROM chat_history")
    void deleteAll();
}