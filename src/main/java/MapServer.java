import controller.RouteHandlerBuilder;

/**
 * The entry point for running the JavaSpark Web Server
 */
public class MapServer {
    public static void main(String[] args) {
        ServerInitializer.initializeServer(RouteHandlerBuilder.handlerMap);
    }
}
