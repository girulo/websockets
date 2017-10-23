package org.atmosphere.challenge;

import java.util.UUID;

public final class TextMessage {
    private String pushMessage;
    private UUID senderId;

    public TextMessage(){
        this("", UUID.randomUUID());
    }

    public TextMessage(String pushMessage, UUID senderId) {
        this.pushMessage = pushMessage;
        this.senderId = senderId;
    }


    public String getPushMessage() {
        return pushMessage;
    }

    public void setPushMessage(String pushMessage) {
        this.pushMessage = pushMessage;
    }

    public UUID getSenderId() {
        return senderId;
    }

    public void setSenderId(UUID senderId) {
        this.senderId = senderId;
    }
}
