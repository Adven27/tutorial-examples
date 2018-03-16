package javaeetutorial.batch.messagesender.beans;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Car {
    private final Long id;
    private final String brand;
    private final int year;
    private final String color;
    private final int price;
    private final boolean soldState;
}
