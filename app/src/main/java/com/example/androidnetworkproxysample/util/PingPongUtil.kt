package com.example.androidnetworkproxysample.util

import android.util.Log
import okhttp3.internal.ws.WebSocketProtocol
import okio.Buffer
import okio.ByteString
import java.io.IOException
import java.util.Random

object PingPongUtil {

    /** Magic value which must be appended to the key in a response header. */
    internal const val ACCEPT_MAGIC = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11"

    /*
    Each frame starts with two bytes of data.

     0 1 2 3 4 5 6 7    0 1 2 3 4 5 6 7
    +-+-+-+-+-------+  +-+-------------+
    |F|R|R|R| OP    |  |M| LENGTH      |
    |I|S|S|S| CODE  |  |A|             |
    |N|V|V|V|       |  |S|             |
    | |1|2|3|       |  |K|             |
    +-+-+-+-+-------+  +-+-------------+
    */

    /** Byte 0 flag for whether this is the final fragment in a message. */
    internal const val B0_FLAG_FIN = 128
    /** Byte 0 reserved flag 1. Must be 0 unless negotiated otherwise. */
    internal const val B0_FLAG_RSV1 = 64
    /** Byte 0 reserved flag 2. Must be 0 unless negotiated otherwise. */
    internal const val B0_FLAG_RSV2 = 32
    /** Byte 0 reserved flag 3. Must be 0 unless negotiated otherwise. */
    internal const val B0_FLAG_RSV3 = 16
    /** Byte 0 mask for the frame opcode. */
    internal const val B0_MASK_OPCODE = 15
    /** Flag in the opcode which indicates a control frame. */
    internal const val OPCODE_FLAG_CONTROL = 8

    /**
     * Byte 1 flag for whether the payload data is masked.
     *
     * If this flag is set, the next four
     * bytes represent the mask key. These bytes appear after any additional bytes specified by [B1_MASK_LENGTH].
     */
    internal const val B1_FLAG_MASK = 128
    /**
     * Byte 1 mask for the payload length.
     *
     * If this value is [PAYLOAD_SHORT], the next two
     * bytes represent the length. If this value is [PAYLOAD_LONG], the next eight bytes
     * represent the length.
     */
    internal const val B1_MASK_LENGTH = 127

    internal const val OPCODE_CONTINUATION = 0x0
    internal const val OPCODE_TEXT = 0x1
    internal const val OPCODE_BINARY = 0x2

    internal const val OPCODE_CONTROL_CLOSE = 0x8
    internal const val OPCODE_CONTROL_PING = 0x9
    internal const val OPCODE_CONTROL_PONG = 0xa

    private val isClient = true

    private val maskKey: ByteArray? = if (isClient) ByteArray(4) else null
    private val maskCursor: Buffer.UnsafeCursor? = if (isClient) Buffer.UnsafeCursor() else null
    fun ping(): ByteArray {
        return writePing(ByteString.EMPTY)
    }

    fun pong(): ByteArray {
        return writePong(ByteString.EMPTY)
    }

    @Throws(IOException::class)
    fun writePing(payload: ByteString): ByteArray {
        return writeControlFrame(OPCODE_CONTROL_PING, payload)
    }

    /** Send a pong with the supplied [payload]. */
    @Throws(IOException::class)
    fun writePong(payload: ByteString): ByteArray {
       return writeControlFrame(OPCODE_CONTROL_PONG, payload)
    }
    private var random = Random()
    private fun writeControlFrame(opcode: Int, payload: ByteString): ByteArray {
        val sinkBuffer = Buffer()
        val length = payload.size

        val b0 = B0_FLAG_FIN or opcode
        sinkBuffer.writeByte(b0)
        var b1 = length

        if (isClient) {
            b1 = b1 or B1_FLAG_MASK
            sinkBuffer.writeByte(b1)

            random.nextBytes(maskKey!!)
            sinkBuffer.write(maskKey)


            if (length > 0) {
                val payloadStart = sinkBuffer.size
                sinkBuffer.write(payload)

                sinkBuffer.readAndWriteUnsafe(maskCursor!!)
                maskCursor.seek(payloadStart)
                WebSocketProtocol.toggleMask(maskCursor, maskKey)
                maskCursor.close()
            }
        } else {
            sinkBuffer.writeByte(b1)
            sinkBuffer.write(payload)
        }
        val byteArray = ByteArray(sinkBuffer.size.toInt())
        Log.i("ZYP", "byteArray size: " + sinkBuffer.size)
        sinkBuffer.read(byteArray)
        sinkBuffer.readByteArray()
        return byteArray
    }
}