package F9;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

public class CarSorter {

    public static class Car implements Comparable<Car> {

        String brand;
        int year;
        int miles;

        public Car(String brand, int year, int miles) {
            this.brand = brand;
            this.year = year;
            this.miles = miles;
        }

        @Override
        public int compareTo(Car otherCar) {
            return brand.compareTo(otherCar.brand);
        }
    }

    public static class CompareCar implements Comparator<Car> {
        @Override
        public int compare(Car left, Car right) {
            return left.year - right.year;
        }
    }

    public static void main(String[] args) {
        var carSorter = new CarSorter("cars.txt");

        // Sort
        var apiSorted = carSorter.getSortedCopyByJavaAPI();
        var comparatorSorted =  carSorter.getSortedCopyByComparator(new CompareCar());

        // Write result to files
        writeCarsToFile("carsJavaAPI.txt", apiSorted);
        writeCarsToFile("carsComparator.txt", comparatorSorted);

    }

    private final ArrayList<Car> cars = new ArrayList<>();

    public CarSorter(String file) {
        try {
            File myObj = new File("src/main/java/F9/" + file);
            Scanner reader = new Scanner(myObj);

            while (reader.hasNextLine()) {
                String data = reader.nextLine();
                String[] separated = data.split("\\W+");
                cars.add(new Car(separated[0], Integer.parseInt(separated[1]), Integer.parseInt(separated[2])));
            }
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }


    public ArrayList<Car> getSortedCopyByJavaAPI() {
        var copy = new ArrayList<>(cars);
        Collections.sort(copy);
        return copy;
    }

    public ArrayList<Car> getSortedCopyByComparator(CompareCar compareCar) {
        var copy = new ArrayList<>(cars);
        copy.sort(compareCar);
        return copy;
    }


    public static void writeCarsToFile(String file, ArrayList<Car> cars) {
        try {
            FileWriter writer = new FileWriter("src/main/java/F9/" + file);
            for (var car : cars) {
                writer.write(car.brand + " " + car.year + " " + car.miles + "\n");
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }


}
