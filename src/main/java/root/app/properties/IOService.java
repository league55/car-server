package root.app.properties;

import java.io.IOException;

public interface IOService<T> {

    T readProperty(String fileName) throws IOException;

    void writeProperty(String fileName, T propValue) throws IOException;
}
