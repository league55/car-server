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
    private ScheduledOutputTask task;
    private ScheduledExecutorService executor;

    private final AppConfigService appConfigService;
    private Integer period;

    @Autowired
    public DataOutputServiceImpl(AppConfigService appConfigService) {
        this.appConfigService = appConfigService;
    }

    @Override
    public void writeOnFixedRate(Integer millis) {
        this.period = millis;

        this.task = new ScheduledOutputTask();
        this.task.setPeriod(millis);
        this.executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(task, millis, millis, TimeUnit.MILLISECONDS);
    }

    @Override
    public void updateCarData(List<Car> carsList) {
        if(period == null) return;

        final String value = appConfigService.findOne(ConfigAttribute.RoadWaysAmount).getValue();
        final int roadWayNumberAmount = Integer.parseInt(value);

        this.task.updateCarData(carsList, roadWayNumberAmount);
    }

    @Override
    public void stopWriting() {
        try {
            executor.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("InterruptedException on task stop ", e);
        }
        executor.shutdown();
    }
}
