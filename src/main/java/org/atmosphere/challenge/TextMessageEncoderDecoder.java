package org.atmosphere.challenge;

import java.io.IOException;

import org.atmosphere.config.managed.Decoder;
import org.atmosphere.config.managed.Encoder;
import org.codehaus.jackson.map.ObjectMapper;

public final class TextMessageEncoderDecoder implements Encoder<TextMessage, String>, Decoder<String, TextMessage>{
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public TextMessage decode(final String s){
        try{
            return mapper.readValue(s, TextMessage.class);
        }catch(IOException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public String encode(final TextMessage message){
        try{
            return mapper.writeValueAsString(message);
        }catch(IOException e){
            throw new RuntimeException(e);
        }
    }
}
