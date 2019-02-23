package de.htw.ai.loz.gpan.app;

import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;
import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import de.htw.ai.loz.gpan.lpan.web.LpanBinder;
import de.htw.ai.loz.gpan.mac.web.MacBinder;
import de.htw.ai.loz.gpan.socket.SocketService;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

public class App {

    private final static int restPort = 8887;
    private final static int socketPort = 8889;
    private final static String host = "localhost";

    public static void main(String[] args) throws Exception{
        URI httpUri = UriBuilder.fromUri("http://" +host + "/").port(restPort).build();
        InetSocketAddress wsAddress = new InetSocketAddress(host, socketPort);
        ResourceConfig config = new ResourceConfig();
        config.register(new MacBinder());
        config.register(new LpanBinder());
        config.register(new CorsFilter());
        config.packages("de.htw.ai.loz");
        HttpServer httpServer = JdkHttpServerFactory.createHttpServer(httpUri, config);
        System.out.println("HTTP-SERVER STARTED!");
        if (!SocketService.startPublisher(wsAddress)) {
            System.out.println("WS-SERVER FAILED");
            throw new Exception();
        }
        System.out.println("WS-SERVER STARTED!");
        System.in.read();
        System.out.println("SERVER STOPPED");
        httpServer.stop(0);
        SocketService.stopService();
    }
}
