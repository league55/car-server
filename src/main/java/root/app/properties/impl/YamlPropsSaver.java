package root.app.properties.impl;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.esotericsoftware.yamlbeans.YamlWriter;
import org.springframework.stereotype.Repository;
import root.app.properties.IOService;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

@Repository
public class YamlPropsSaver<T> implements IOService<T> {
    @Override
    public T readProperty(String fileName) throws IOException {
        YamlReader reader = new YamlReader(new FileReader(fileName));
        T read = (T) reader.read();
        reader.close();
        return read;
        }

    @Override
    public void writeProperty(String fileName, T property) throws IOException {
        YamlWriter writer = new YamlWriter(new FileWriter(fileName));
        writer.write(property);
        writer.close();
    }
}
