package org.atmosphere.challenge

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.atmosphere.config.managed.Decoder
import org.atmosphere.config.managed.Encoder
import java.util.*

/**
 * Created on 10/23/17.
 * @author Hugo Novajarque
 */
data class TextMessageKt(
        val pushMessage: String? = null,
        val senderId: UUID? = null
)

class TextMessageKtEncoderDecoder : Encoder<TextMessageKt, String>, Decoder<String, TextMessageKt> {

    //    val mapper = ObjectMapper()
    val mapper = jacksonObjectMapper()

    override fun encode(message: TextMessageKt?): String {
        return mapper.writeValueAsString(message)
    }

    override fun decode(message: String?): TextMessageKt {
        return mapper.readValue<TextMessageKt>(message!!)
    }
}

