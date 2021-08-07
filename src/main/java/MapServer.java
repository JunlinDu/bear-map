import controller.RouteHandlerFactory;

/**
 * The entry point for running the JavaSpark Web Server
 */
public class MapServer {
    public static void main(String[] args) {
        ServerInitializer.initializeServer(RouteHandlerFactory.handlerMap);
    }
}
