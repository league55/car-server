package root.app.properties.impl;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import root.app.model.BasicModel;
import root.app.properties.ConfigService;
import root.app.properties.IOService;

import java.io.IOException;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

@Slf4j
public class ConfigServiceImpl<T extends BasicModel> implements ConfigService<T> {

    private final String fileName;
    private final IOService<List<T>> saver;

    public ConfigServiceImpl(IOService<List<T>> saver, String fileName) {
        this.saver = saver;
        this.fileName = fileName;
    }

    @Override
    public void save(T pair) {
        try {
            List<T> t = findAll();

            if (pair.getId() == null) {
                pair.setId(getNextId(t));
            }

            if (t.stream().anyMatch(p -> pair.getId().equals(p.getId()))) {
                t = t.stream().map(p -> p.getId().equals(pair.getId()) ? pair : p).collect(toList());
            } else {
                t.add(pair);
            }

            saver.writeProperty(fileName, t);
        } catch (IOException e) {
            log.error("Failed to save ,", e);
        }
    }

    private Long getNextId(List<T> t) {
        if (t.size() == 0) return 1L;
        return t.stream().mapToLong(T::getId).max().getAsLong() + 1;
    }

    @Override
    public T findOne(Long aLong) {
        try {
            return saver.readProperty(fileName).stream().filter(pair -> aLong.equals(pair.getId())).findFirst().get();
        } catch (IOException e) {
            log.error("Failed to find ,", e);
        }
        return null;
    }

    @Override
    public boolean exists(Long aLong) {
        try {
            return saver.readProperty(fileName).stream().anyMatch(pair -> aLong.equals(pair.getId()));
        } catch (IOException e) {
            log.error("Failed to check existing,", e);
        }
        return false;
    }

    @Override
    public List<T> findAll() {
        try {
            final List<T> t = saver.readProperty(fileName);
            return t == null ? Lists.newArrayList() : t;
        } catch (IOException e) {
            log.error("Failed to find all objects of type,", e);
        }
        return null;
    }

    @Override
    public long count() {
        return findAll().size();
    }

    @Override
    public void delete(Long aLong) {
        List<T> all = findAll();
        OptionalInt index = IntStream.range(0, all.size())
                .filter(userInd -> all.get(userInd).getId().equals(aLong))
                .findFirst();

        if (index.isPresent()) {
            all.remove(index.getAsInt());
        }

        try {
            saver.writeProperty(fileName, all);
        } catch (IOException e) {
            log.error("can't delete, ", e);
        }
    }

    @Override
    public void delete(T markersPair) {
        delete(markersPair.getId());
    }


    @Override
    public void deleteAll() {
        try {
            saver.writeProperty(fileName, Lists.newArrayList());
        } catch (IOException e) {
            log.error("can't delete all, ", e);
        }
    }
}
