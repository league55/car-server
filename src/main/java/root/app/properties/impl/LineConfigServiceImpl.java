package root.app.properties.impl;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import root.app.model.MarkersPair;
import root.app.properties.LineConfigService;
import root.app.properties.IOService;

import java.io.IOException;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;


@Slf4j
@Service
public class LineConfigServiceImpl implements LineConfigService {
    private static final String fileName = "../lineMarkersProps.yml";

    private final IOService<List<MarkersPair>> saver;

    @Autowired
    public LineConfigServiceImpl(IOService<List<MarkersPair>> saver) {
        this.saver = saver;
    }

    @Override
    public void save(MarkersPair pair) {
        try {
            List<MarkersPair> markersPairs = saver.readProperty(fileName);

            if (pair.getId() == null) {
                pair.setId(getNextId(markersPairs));
            }

            if (markersPairs.stream().anyMatch(p -> pair.getId().equals(p.getId()))) {
                markersPairs = markersPairs.stream().map(p -> p.getId().equals(pair.getId()) ? pair : p).collect(toList());
            } else {
                markersPairs.add(pair);
            }

           saver.writeProperty(fileName, markersPairs);
        } catch (IOException e) {
            log.error("Failed to save pair,", e);
        }
    }

    private Long getNextId(List<MarkersPair> markersPairs) {
        if (markersPairs.size() == 0) return 1L;
        return markersPairs.stream().mapToLong(MarkersPair::getId).max().getAsLong() + 1;
    }

    @Override
    public MarkersPair findOne(Long aLong) {
        try {
            return saver.readProperty(fileName).stream().filter(pair -> aLong.equals(pair.getId())).findFirst().get();
        } catch (IOException e) {
            log.error("Failed to save pair,", e);
        }
        return null;
    }

    @Override
    public boolean exists(Long aLong) {
        try {
            return saver.readProperty(fileName).stream().anyMatch(pair -> aLong.equals(pair.getId()));
        } catch (IOException e) {
            log.error("Failed to save pair,", e);
        }
        return false;
    }

    @Override
    public List<MarkersPair> findAll() {
        try {
            return saver.readProperty(fileName);
        } catch (IOException e) {
            log.error("Failed to save pair,", e);
        }
        return null;
    }

    @Override
    public long count() {
        return findAll().size();
    }

    @Override
    public void delete(Long aLong) {
        List<MarkersPair> all = findAll();
        OptionalInt index = IntStream.range(0, all.size())
                .filter(userInd-> all.get(userInd).getId().equals(aLong))
                .findFirst();

        if(index.isPresent()) {
            all.remove(index.getAsInt());
        }

        try {
            saver.writeProperty(fileName, all);
        } catch (IOException e) {
            log.error("cant delete, ", e);
        }
    }

    @Override
    public void delete(MarkersPair markersPair) {
        delete(markersPair.getId());
    }


    @Override
    public void deleteAll() {
        try {
            saver.writeProperty(fileName, Lists.newArrayList());
        } catch (IOException e) {
            log.error("cant delete all, ", e);
        }
    }

    @Override
    public void updateDistance(Long id, Integer distance) {
        MarkersPair markersPair = findOne(id);

        if(markersPair != null) {
            markersPair.setDistance(distance);
            save(markersPair);
        } else {
            log.error("Can't update distance for {}", id);
        }
    }

}

