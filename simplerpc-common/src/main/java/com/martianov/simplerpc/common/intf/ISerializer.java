package com.martianov.simplerpc.common.intf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Serialize/deserialize message.
 *
 * @author Andrey Martyanov <martianovas@gmail.com>
 */
public interface ISerializer {
//    /**
//     * Serialize message.
//     *
//     * @param msg message.
//     * @returns binary representation of message.
//     * */
//    byte[] toBytes(IMessage msg) throws SerializerException;
//
//    /**
//     * Deserialize message.
//     *
//     * @param bytes serialized message.
//     * @returns deserialized message.
//     * */
//    IMessage fromBytes(byte[] bytes) throws SerializerException;
    /**
     *
     * */
    IMessage read(InputStream is) throws IOException, SerializerException;

    void write(OutputStream os, IMessage msg) throws IOException, SerializerException;
}
