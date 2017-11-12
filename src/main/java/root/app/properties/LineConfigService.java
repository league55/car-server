package root.app.properties;

import root.app.model.MarkersPair;

import java.util.List;

/**
 * Read / Write app.properties
 */
public interface LineConfigService {
    void save(MarkersPair pair);

    MarkersPair findOne(Long aLong);

    boolean exists(Long aLong);

    List<MarkersPair> findAll();

    long count();

    void delete(Long aLong);

    void delete(MarkersPair markersPair);

    void deleteAll();

    void updateDistance(Long id, Integer distance);
}
