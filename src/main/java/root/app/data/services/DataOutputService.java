package root.app.data.services;


import root.app.model.Car;

import java.util.List;

public interface DataOutputService {

    void writeOnFixedRate(Integer millis);
    void updateCarData(List<Car> carsList);
    void stopWriting();
}
