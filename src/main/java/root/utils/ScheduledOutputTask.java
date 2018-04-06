package root.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;
import root.app.model.Car;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ScheduledOutputTask implements Runnable {
    private Map<Integer, OutputDto> data = new HashMap<>();

    private final DateTimeFormatter FORMATTER = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);
    private final static String FILE_NAME = "../config/log.txt";
    private FileWriter fw;
    private BufferedWriter bw;
    private Integer period;
    private long lastCall = System.currentTimeMillis();


    private final SimpMessageSendingOperations messagingTemplate;


    public ScheduledOutputTask(SimpMessageSendingOperations messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void run() {
        final long timeCounting = System.currentTimeMillis() - this.lastCall;
        StringBuilder[] res = new StringBuilder[data.values().size()];

        for (int i = 0; i < data.values().size(); i++) {
            OutputDto outputDto = data.get(i + 1);
            res[i] = new StringBuilder();
            outputDto.setTimeCounting(timeCounting);
            res[i].append("Полоса: ").append(outputDto.getWayNumber()).append(" | ");
            res[i].append("Кол-во авто: ").append(outputDto.getCarsAmount()).append(" ");
            res[i].append("ΔT: ").append(outputDto.getTimeAfterLastCall()).append(" ");
            res[i].append("T: ").append(LocalDateTime.now().format(FORMATTER)).append("\n");
        }

        sentToListeners(res);
        writeLogFile(Arrays.toString(res));

        data.clear();
    }

    private void sentToListeners(StringBuilder[] res) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            messagingTemplate.convertAndSend("/log", mapper.writeValueAsString(res));
        } catch (JsonProcessingException e) {
            log.error("Failed to map", e);
        }
    }

    private void writeLogFile(String output) {
        before();

        try {
            bw.write(output);
        } catch (IOException e) {
            e.printStackTrace();
        }

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
