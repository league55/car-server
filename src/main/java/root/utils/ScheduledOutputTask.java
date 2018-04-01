package root.utils;

import lombok.extern.slf4j.Slf4j;
import root.app.model.Car;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class ScheduledOutputTask implements Runnable {
    private Map<Integer, OutputDto> data = new HashMap<>();

    private final static String FILE_NAME = "../log.txt";
    private FileWriter fw;
    private BufferedWriter bw;
    private Integer period;
    private long lastCall = System.currentTimeMillis();



    @Override
    public void run() {
        final long timeCounting = System.currentTimeMillis() - this.lastCall;
        before();

        data.values().forEach(outputDto -> {
            outputDto.setTimeCounting(timeCounting);
            try {
                bw.write("\n ");
                bw.write(outputDto.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        try {
            bw.write("\n ----------- ");
        } catch (IOException e) {
            e.printStackTrace();
        }

        data.clear();
        this.lastCall = System.currentTimeMillis();

        after();
    }

    public void updateCarData(List<Car> carsList, int roadWayNumberAmount) {
        if (period == null) return;


        for (Integer i = 1; i <= roadWayNumberAmount; i++) {
            final int roadWayNumber = i;
            final OutputDto outputDto = data.getOrDefault(roadWayNumber, new OutputDto(roadWayNumber, 0, 0L, period.doubleValue()));

            final List<Car> carList = carsList.stream().filter(car -> (car.getCrossedPairs().size() > 0) && car.isStillTracked && !car.isWasCounted()).collect(Collectors.toList());
            final List<Car> thisWayCars = carList.stream().filter(car -> car.getCrossedPairs().get(car.getCrossedPairs().size() - 1).getWayNumber() == roadWayNumber).collect(Collectors.toList());
            thisWayCars.forEach(car -> car.setWasCounted(true));
            outputDto.setCarsAmount(outputDto.getCarsAmount() + thisWayCars.size());
            outputDto.setTimeAfterLastCall(period.doubleValue());

            data.put(roadWayNumber, outputDto);
        }

    }

    private void before() {
        try {
            File file = new File(FILE_NAME);

            // if file doesn't exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }
            fw = new FileWriter(file.getAbsoluteFile(), true);
            bw = new BufferedWriter(fw);


        } catch (IOException e) {
            log.error("IOException occur on starting output writers: ", e);
        }
    }

    private void after() {
        try {
            bw.close();
            fw.close();
        } catch (IOException e) {
            log.error("IOException occur on closing output writers: ", e);
        }
    }

    public void setPeriod(Integer period) {
        this.period = period;
    }
}
