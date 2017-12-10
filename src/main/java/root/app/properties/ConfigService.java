package root.app.properties;

import java.util.List;

/**
 * Generic Read / Write app.properties
 */
public interface ConfigService<T> {
    Long save(T t);

    T findOne(Long aLong);

    boolean exists(Long aLong);

    List<T> findAll();

    long count();

    void delete(Long aLong);

    void delete(T markersPair);

    void deleteAll();
}
