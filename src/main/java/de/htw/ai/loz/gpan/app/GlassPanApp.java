/*
Copyright 2019 Miles Lorenz

        Licensed under the Apache License, Version 2.0 (the "License");
        you may not use this file except in compliance with the License.
        You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.
*/
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

/**
 * Main class, that launches a http-server and a socket-server.
 * <p>
 * Exposes the endpoints for the two components MacAbstraction and PacketAdaptation.
 * </p>
 *
 * @author Miles Lorenz
 * @version 1.0
 */
public class GlassPanApp {

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
