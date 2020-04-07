import handler.InputDataHandler;

public class OrderBookApp {
    public static void main(String[] args) {
        String filePath = "C:\\Users\\USER\\IdeaProjects\\basic-order-book\\src\\main\\resources\\input.txt";
        InputDataHandler inputDataHandler = new InputDataHandler();
        inputDataHandler.handleFile(filePath);

    }
}
