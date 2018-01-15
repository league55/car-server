package root.utils;

import com.google.common.collect.Lists;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class ScheduledOutputTask implements Runnable {
    private List<OutputDto> data = Lists.newArrayList();
    private BufferedWriter out;

    public ScheduledOutputTask(BufferedWriter out) {
        this.out = out;
    }

    @Override
    public void run() {
        data.forEach(outputDto -> {
            try {
                out.write("\n ");
                out.write(outputDto.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        try {
            out.write("\n ----------- ");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public List<OutputDto> getData() {
        return data;
    }

    public void setData(List<OutputDto> data) {
        this.data = data;
    }
}
