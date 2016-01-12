package helpers

/**
 * Created by Nikita on 05.01.2016.
 */
class Server {
    String username
    String password
    String hostname
    int port = 9990

    boolean domain

    List<String> domainServerGroups = []

    boolean isLocal() { return hostname == null }

    String deployFile
}
