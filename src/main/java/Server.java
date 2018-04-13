import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerRequest;

public class Server {
    public static void main(String [] args) {
        Vertx vertx = Vertx.vertx();

        int port = 9999;
        HttpServerOptions options = new HttpServerOptions()
                .setPort(port)
                .setIdleTimeout(200) //make sure it's the client that expires a connection
                .setMaxHeaderSize(32768);


        HttpServer server = vertx.createHttpServer(options);

        server.requestHandler(new Handler<HttpServerRequest>() {

            private int requestNumber = 0;
            @Override
            public void handle(HttpServerRequest httpServerRequest) {
                if(requestNumber == 0) {
                    //First response is delayed to force creation of new connection on the client
                    vertx.setTimer(1000, aLong ->  {
                        httpServerRequest.response().end("Response to request " + requestNumber);
                        System.out.println("Responding to request " + requestNumber);
                    });
                }
                else {
                    System.out.println("Responding to request " + requestNumber);
                    httpServerRequest.response().end("Response to request " + requestNumber);
                }

                requestNumber++;
            }
        });

        server.listen();
    }
}
