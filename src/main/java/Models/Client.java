package Models;

public class Client {

    private String brand;
    private String name;

    public Client(String brand, String name) {
        this.brand = brand;
        this.name = name;
    }

    public String getName() {return name;}

    public String getPrice() {
        return brand;
    }

    @Override
    public String toString() {
        return brand + " - " + name;
    }
}
