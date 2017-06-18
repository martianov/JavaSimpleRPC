package com.martianov.simplerpc.server;

import com.martianov.simplerpc.server.services.IServiceProvider;
import com.martianov.simplerpc.server.services.ServiceProviderException;
import com.martianov.simplerpc.server.services.impl.ReflectionProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Properties;

/**
 * Starts SimpleRpc server.
 *
 * @author Andrey Martyanov <martianovas@gmail.com>
 */
public class Main {
    private static final Logger LOG = LogManager.getLogger(Main.class.getName());
    private static final String SERVER_PROPS_FILENAME = "server.properties";

    private static Properties loadServerProperties() throws ServerException {
        InputStream propInputStream;

        File propertiesFile = new File(SERVER_PROPS_FILENAME);
        //looking for server properties file into current directory
        if (propertiesFile.exists()) {
            try {
                propInputStream = new FileInputStream(propertiesFile);
            } catch (FileNotFoundException e) {
                throw new ServerException("Server properties file not found", e);
            }
        } else {
            //else look into resources
            propInputStream = Main.class.getClassLoader().getResourceAsStream(SERVER_PROPS_FILENAME);
        }

        if (null == propInputStream) {
            throw new ServerException("Server properties file not found");
        }

        Properties props = new Properties();
        try {
            props.load(propInputStream);
        } catch (IOException e) {
            throw new ServerException("Failed to read server properties file", e);
        }
        return props;
    }

    private static IServiceProvider createServiceProvider(Properties props) throws ServiceProviderException {
        ReflectionProvider provider = new ReflectionProvider();

        for (String prop : props.stringPropertyNames()) {
            provider.register(prop, props.getProperty(prop));
        }

        return provider;
    }


    public static void main (String[] args) {
        int port = Integer.parseInt(args[0]);

        try {
            Properties props = loadServerProperties();
            IServiceProvider provider = createServiceProvider(props);

            final Server server = new Server(port, provider);

            Runtime.getRuntime().addShutdownHook(new Thread(Utils.genThreadName("ShutdownHook")) {
                @Override
                public void run() {
                    LOG.info("Ctrl+C: stopping server.. ");
                    server.stop();
                }
            });

            server.serve();
        } catch (ServerException e) {
            LOG.error("Failed to start server", e);
        }
    }
}
