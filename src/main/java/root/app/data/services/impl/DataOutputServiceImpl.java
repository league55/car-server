package root.app.data.services.impl;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import root.app.data.services.DataOutputService;
import root.app.model.Car;
import root.app.properties.AppConfigService;
import root.app.properties.ConfigAttribute;
import root.utils.OutputDto;
import root.utils.ScheduledOutputTask;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DataOutputServiceImpl implements DataOutputService {
    private final static String FILE_NAME = "config/log.txt";
    private ScheduledOutputTask task;
    private FileWriter fw;
    private BufferedWriter bw;
    private PrintWriter out;
    private Map<Integer, OutputDto> outputDtoMap = new HashMap<>();
    private ScheduledExecutorService executor;

    private final AppConfigService appConfigService;
    private Integer period;
    private long lastCall = System.currentTimeMillis();

    @Autowired
    public DataOutputServiceImpl(AppConfigService appConfigService) {
        this.appConfigService = appConfigService;
    }

    @Override
    public void writeOnFixedRate(Integer millis) {
        this.period = millis;
        try {
            File file = new File(FILE_NAME);

            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }
            fw = new FileWriter(file.getAbsoluteFile(), true);
            bw = new BufferedWriter(fw);


        } catch (IOException e) {
            log.error("IOException occur on starting output writers: ", e);
        }
        this.task = new ScheduledOutputTask(this.bw);

        this.executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(task, millis, millis, TimeUnit.MILLISECONDS);
    }

    @Override
    public void updateCarData(List<Car> carsList) {
        if(period == null) return;

        final List<Car> carList = carsList.stream().filter(car -> (car.getCrossedPairs().size() > 0) && car.isStillTracked).collect(Collectors.toList());
        final String value = appConfigService.findOne(ConfigAttribute.RoadWaysAmount).getValue();
        final int roadWayNumberAmount = Integer.parseInt(value);
        final long timeCounting = System.currentTimeMillis() - this.lastCall;

        for (Integer i = 1; i <= roadWayNumberAmount; i++) {
            final int roadWayNumber = i;
            final OutputDto outputDto = outputDtoMap.getOrDefault(roadWayNumber, new OutputDto(roadWayNumber, 0, timeCounting, period.doubleValue()));
            final List<Car> thisWayCars = carList.stream().filter(car -> car.getCrossedPairs().get(car.getCrossedPairs().size() - 1).getWayNumber() == roadWayNumber).collect(Collectors.toList());

            outputDto.setCarsAmount(outputDto.getCarsAmount() + thisWayCars.size());
            outputDto.setTimeAfterLastCall(period.doubleValue());
            outputDto.setTimeCounting(timeCounting);
            this.lastCall = System.currentTimeMillis();

            outputDtoMap.put(roadWayNumber, outputDto);
        }

        this.task.setData(Lists.newArrayList(outputDtoMap.values()));
    }

    @Override
    public void stopWriting() {
        executor.shutdown();
        try {
            bw.close();
            fw.close();
        } catch (IOException e) {
            log.error("IOException occur on closing output writers: ", e);
        }
    }
}
