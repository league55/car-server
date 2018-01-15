package root.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OutputDto {

    private Integer wayNumber;
    private Integer carsAmount;
    private Long timeCounting;
    private Double timeAfterLastCall;
}
