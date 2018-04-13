import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpVersion;
import io.vertx.core.http.RecyclePolicy;

public class Client{
    public static void main(String [] args) {
        Vertx vertx = Vertx.vertx();
        int poolSize = 2;
        HttpClientOptions httpClientOptions = new HttpClientOptions();
        httpClientOptions.setKeepAlive(true)
                         .setUsePooledBuffers(true)
                         .setConnectTimeout(10000)
                         .setTryUseCompression(true)
                         .setIdleTimeout(60)
                         .setReuseAddress(false)
                         .setMaxRedirects(0)
                         .setMaxPoolSize(poolSize)
                         .setTrustAll(true)
                         .setVerifyHost(false)
                         .setPoolRecyclePolicy(RecyclePolicy.FIFO)
                         .setProtocolVersion(HttpVersion.HTTP_1_1);


        int targetPort = 9999;
        String targetHost = "127.0.0.1";
        HttpClient httpClient = vertx.createHttpClient(httpClientOptions);



        //Send two consecutive requests to create 2 connections, the server will delay the response to make sure 2 connections are created.

        System.out.println(System.currentTimeMillis() + " - Sending Request 0");

        httpClient.post(targetPort, targetHost, "/" , response -> System.out.println(System.currentTimeMillis() + " - Response to request 1 arrived ")).end("First Request");

        System.out.println(System.currentTimeMillis() + " - Sending Request 1");

        httpClient.post(targetPort, targetHost, "/" , response -> System.out.println(System.currentTimeMillis() + " - Response to request 2 arrived ")).end("Second Request");


        Handler<Long> requestSender = new Handler<Long>() {
            private int requestCounter = 2;
            @Override
            public void handle(Long aLong) {
                httpClient.post(targetPort, targetHost, "/" , response -> System.out.println(System.currentTimeMillis() + " - Response to request "+ requestCounter +"  arrived ")).end("Sending Request "+ requestCounter);
                requestCounter++;
                vertx.setTimer(1000, this::handle);
                //sends requests forever, using tcp-dump we can see that after 60 seconds the second connection should be closed

                if(requestCounter >120) {
                    System.out.println("we are done");
                    System.exit(0);
                }
            }
        };

        requestSender.handle(null);
    }
}
