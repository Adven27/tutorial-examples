package javaeetutorial.batch.messagesender.beans;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import static java.lang.System.currentTimeMillis;

@ManagedBean(name = "carService")
@ApplicationScoped
public class CarService {

    private final static String[] colors;

    private final static String[] brands;

    static {
        colors = new String[10];
        colors[0] = "Black";
        colors[1] = "White";
        colors[2] = "Green";
        colors[3] = "Red";
        colors[4] = "Blue";
        colors[5] = "Orange";
        colors[6] = "Silver";
        colors[7] = "Yellow";
        colors[8] = "Brown";
        colors[9] = "Maroon";

        brands = new String[10];
        brands[0] = "BMW";
        brands[1] = "Mercedes";
        brands[2] = "Volvo";
        brands[3] = "Audi";
        brands[4] = "Renault";
        brands[5] = "Fiat";
        brands[6] = "Volkswagen";
        brands[7] = "Honda";
        brands[8] = "Jaguar";
        brands[9] = "Ford";
        createCars(50);
    }

    private static List<Car> cars = createCars(50);

    private static List<Car> createCars(int size) {
        List<Car> list = new ArrayList<>();
        for(int i = 0 ; i < size ; i++) {
            list.add(new Car(currentTimeMillis(), getRandomBrand(), getRandomYear(), getRandomColor(), getRandomPrice(), getRandomSoldState()));
            sleep();
        }
        return list;
    }

    private static void sleep() {
        try {
            Thread.sleep(100L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static int getRandomYear() {
        return (int) (Math.random() * 50 + 1960);
    }

    private static String getRandomColor() {
        return colors[(int) (Math.random() * 10)];
    }

    private static String getRandomBrand() {
        return brands[(int) (Math.random() * 10)];
    }

    public static int getRandomPrice() {
        return (int) (Math.random() * 100000);
    }

    public static boolean getRandomSoldState() {
        return (Math.random() > 0.5) ? true: false;
    }

    public List<String> getColors() {
        return Arrays.asList(colors);
    }

    public List<String> getBrands() {
        return Arrays.asList(brands);
    }

    public List<Car> getPage(int first, int pageSize) {
        return cars.subList(first, first+ pageSize);
    }
}