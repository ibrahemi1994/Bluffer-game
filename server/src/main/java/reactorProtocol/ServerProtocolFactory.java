package reactorProtocol;

public interface ServerProtocolFactory<T> {
   AsyncServerProtocol<T> create();
}
