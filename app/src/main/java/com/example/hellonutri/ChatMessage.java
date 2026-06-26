package com.example.hellonutri;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "chat_history")
public class ChatMessage {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public long sessionId;
    public String message;
    public boolean isUser;
    public long timestamp;

    public ChatMessage(long sessionId, String message, boolean isUser, long timestamp) {
        this.sessionId = sessionId;
        this.message = message;
        this.isUser = isUser;
        this.timestamp = timestamp;
    }
}