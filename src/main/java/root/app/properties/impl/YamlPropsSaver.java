package root.app.properties.impl;

import com.esotericsoftware.yamlbeans.YamlReader;
import com.esotericsoftware.yamlbeans.YamlWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import root.app.properties.IOService;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

@Repository
@Slf4j
public class YamlPropsSaver<T> implements IOService<T> {
    @Override
    public T readProperty(String fileName) throws IOException {
        File file = new File(fileName);
        if (!file.exists()) {
            final FileWriter fileWriter = new FileWriter(fileName);
            fileWriter.write("");
            fileWriter.close();
        }

        YamlReader reader = new YamlReader(new FileReader(fileName));
        T read = null;

        try {
            read = (T) reader.read();

        } catch (YamlReader.YamlReaderException e) {
            log.error("Failed to read file", e);
        } finally {
            reader.close();
        }

        reader.close();
        return read;
    }

    @Override
    public void writeProperty(String fileName, T property) throws IOException {
        final FileWriter writer1 = new FileWriter(fileName);
        YamlWriter writer = new YamlWriter(writer1);
        writer.write(property);
        writer.close();
        writer1.close();
    }
}
