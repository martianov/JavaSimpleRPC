package com.martianov.simplerpc.server.test;

import com.martianov.simplerpc.common.connection.ConnectionException;
import com.martianov.simplerpc.common.message.IError;
import com.martianov.simplerpc.common.message.IMessage;
import com.martianov.simplerpc.common.message.IResult;
import com.martianov.simplerpc.common.message.IVoidResult;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Andrey Martyanov <martianovas@gmail.com>
 */
public class  BasicServerTest extends AbstractServerTest {
    public static class DelayService {
        public Long delay(Long delay) {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                //skip.
            }
            return new Long(delay);
        }
    }

    public static class StringService {
        public void stringArg(String arg) {}
    }

    public static class FailService {
        public void fail() throws Exception {
            throw new Exception("Always fails");
        }
    }

    AtomicLong atomicLongService =  new AtomicLong(0);

    @Override
    protected void registerServices() {
        addService("atomicLong", atomicLongService);
        addService("delayService", new DelayService());
        addService("service", new Object());
        addService("stringService", new StringService());
        addService("failService", new FailService());
    }

    @Test
    public void testReturnValue() throws IOException, ConnectionException {
        IMessage res = sendSync(messageFactory().createRequest(0, "atomicLong", "incrementAndGet", new Object[0]));

        Assert.assertTrue(res instanceof IResult);
        Assert.assertEquals(atomicLongService.get(), ((IResult)res).getResult());
    }

    @Test
    public void testConcurrentServices() throws ExecutionException, InterruptedException {
        CompletableFuture<IMessage> futA = sendAsync(messageFactory().createRequest(0, "delayService", "delay",
                new Object[] {new Long(2000)}));
        CompletableFuture<IMessage> futB = sendAsync(messageFactory().createRequest(0, "delayService", "delay",
                new Object[] {new Long(1000)}));

        IResult resA = (IResult) futA.get();
        IResult resB = (IResult) futB.get();

        Assert.assertEquals(new Long(2000), resA.getResult());
        Assert.assertEquals(new Long(1000), resB.getResult());
    }

    @Test
    public void testServiceNotExist() throws IOException, ConnectionException {
        IMessage res = sendSync(messageFactory().createRequest(0, "serviceNotExists", "any", new Object[] {}));

        Assert.assertTrue(res instanceof IError);
        Assert.assertTrue("Unexpected error message", ((IError) res).getMessage().startsWith("Service not found"));
    }

    @Test
    public void testMethodNotExist() throws IOException, ConnectionException {
        IMessage res = sendSync(messageFactory().createRequest(0, "service", "methodNotExist", new Object[] {}));

        Assert.assertTrue(res instanceof IError);
        Assert.assertTrue("Unexpected error message", ((IError) res).getMessage().startsWith("Method not found"));
    }

    @Test
    public void testWrongArgumentType() throws IOException, ConnectionException {
        IMessage res = sendSync(messageFactory().createRequest(0, "stringService", "stringArg", new Object[] { 100 }));

        Assert.assertTrue(res instanceof IError);
        Assert.assertTrue("Unexpected error message", ((IError) res).getMessage().startsWith("argument type mismatch"));
    }

    @Test
    public void testWrongArgumentsNumber() throws IOException, ConnectionException {
        IMessage res = sendSync(messageFactory().createRequest(0, "stringService", "stringArg", new Object[] { "a", "b" }));

        Assert.assertTrue(res instanceof IError);
        Assert.assertTrue("Unexpected error message", ((IError) res).getMessage().startsWith("wrong number of arguments"));
    }

    @Test
    public void testVoidFunction() throws IOException, ConnectionException {
        IMessage res = sendSync(messageFactory().createRequest(0, "stringService", "stringArg", new Object[] { "a" }));
        Assert.assertTrue(res instanceof IVoidResult);
    }

    @Test
    public void testMethodFails() throws IOException, ConnectionException {
        IMessage res = sendSync(messageFactory().createRequest(0, "failService", "fail", new Object[]{}));

        Assert.assertTrue(res instanceof IError);
        Assert.assertTrue("Unexpected error message", ((IError) res).getMessage().startsWith("Always fails"));
    }
}
