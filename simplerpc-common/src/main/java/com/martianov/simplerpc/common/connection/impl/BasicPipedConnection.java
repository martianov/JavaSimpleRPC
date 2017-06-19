package com.martianov.simplerpc.common.connection.impl;

import java.io.*;

/**
 * @author Andrey Martyanov <martianovas@gmail.com>
 */
public class BasicPipedConnection extends AbsctractBasicConnection {
    private InputStream inputStream;
    private OutputStream outputStream;
    private BasicPipedConnection otherSide;

    private BasicPipedConnection() {
    }

    public static BasicPipedConnection create() throws IOException {
        PipedInputStream inputStreamA = new PipedInputStream();
        PipedInputStream inputStreamB = new PipedInputStream();

        PipedOutputStream outputStreamA = new PipedOutputStream();
        PipedOutputStream outputStreamB = new PipedOutputStream();

        outputStreamA.connect(inputStreamB);
        outputStreamB.connect(inputStreamA);

        BasicPipedConnection connA = new BasicPipedConnection();
        connA.inputStream = inputStreamA;
        connA.outputStream = outputStreamA;

        BasicPipedConnection connB = new BasicPipedConnection();
        connB.inputStream = inputStreamB;
        connB.outputStream = outputStreamB;

        connA.otherSide = connB;
        connB.otherSide = connA;

        return connA;
    }

    @Override
    protected OutputStream outputStream() throws IOException {
        return outputStream;
    }

    @Override
    protected InputStream inputStream() throws IOException {
        return inputStream;
    }

    public BasicPipedConnection getOtherSide() {
        return otherSide;
    }
}
