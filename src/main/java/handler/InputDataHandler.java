package handler;

import model.PriceSize;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InputDataHandler {

    private int numberToRemoveAsks = 0;
    private int numberToRemoveBids = 0;

    private List<PriceSize> priceAndSizeList = new ArrayList<>();

    public void handleFile (String filePath) {
        File file = new File(filePath);
        try{
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            String[] fields; // String array of line divided by coma
            while ((line = br.readLine()) != null) { // read from file

                fields = line.split(",");
                // getting the operation to execute
                char operation = line.charAt(0);

                switch (operation){
                    case 'u': // update
                        update(line);
                        break;
                    case 'q': // query
                        if (fields.length == 3){ // size of specified price
                            System.out.println(
                                    getElementAtSpecifiedPrice(
                                            Integer.parseInt(line.split(",")[2]),
                                            priceAndSizeList).getSize()
                                    );

                        } else { // best price
                            PriceSize priceSizeObject;
                            if (fields[1].equals("best_bid"))
                                priceSizeObject = getElementWithMaxPrice(priceAndSizeList,'b');
                            else // best ask
                                priceSizeObject = getElementWithMaxPrice(priceAndSizeList,'a');

                            System.out.println(priceSizeObject.getPrice() + "," + priceSizeObject.getSize());
                        }
                        break;
                    case 'o': // buy-sell operation
                        if (fields[1].equals("sell")){ // sell
                            int numToSell = Integer.parseInt(fields[2]);
                            sellShares(numToSell);
                        }

                        if (fields[1].equals("buy")){ // buy
                            int numToBuy = Integer.parseInt(fields[2]);
                            buyShares(numToBuy);
                        }
                        break;
                }
            }
        } catch (IOException fileNotFoundException){
            System.out.println("fileNotFoundException");
        }
    }


    private void sellShares(int number){
        // sort list
        numberToRemoveBids =+ number; // sum up to whole number of shares to be removed
        priceAndSizeList = sortPriceSizeList(priceAndSizeList);
        for (int i = priceAndSizeList.size()-1; i >= 0; i--) {
            // check that element is a bid
            if (priceAndSizeList.get(i).getType() == 'b')
                deleteOnLayer(i, 'b');
        }
    }

    private void buyShares(int number){
        // sort list
        priceAndSizeList = sortPriceSizeList(priceAndSizeList);
        numberToRemoveAsks =+ number; // sum up to whole number of shares to be removed
        for (int i = 0; i < priceAndSizeList.size()-1; i++) {
            // check that element is an ask
            if (priceAndSizeList.get(i).getType() == 'a') {
                deleteOnLayer(i, 'a');
            }
        }
    }

    private void deleteOnLayer (int i, char type) {
        int number; // shares to be deleted
        if (type == 'a') number = numberToRemoveAsks;
        else number = numberToRemoveBids;
        // if number we want to buy is greater than
        // number of shells on the layer
        if (priceAndSizeList.get(i).getSize() <= number) {
            // reduce number to be removed on size of layer; remove element
            if (type == 'a') numberToRemoveAsks =- priceAndSizeList.get(i).getSize();
            if (type == 'b') numberToRemoveBids =- priceAndSizeList.get(i).getSize();
            priceAndSizeList.remove(i);
        } else {
            // set new size value
            PriceSize ps =
                    new PriceSize(
                            priceAndSizeList.get(i).getPrice(),
                            priceAndSizeList.get(i).getSize() - number,
                            priceAndSizeList.get(i).getType()
                    );
            if (type == 'a') numberToRemoveAsks = 0;
            if (type == 'b') numberToRemoveBids = 0;
            priceAndSizeList.set(i, ps);
        }
    }

    private void update(@NotNull String line){
        String [] fields = line.split(","); // getting fields
        int price = Integer.parseInt(fields[1]);
        int size = Integer.parseInt(fields[2]);
        char type;

        //priceAndSize.set(price, size); // updating price-size list
        if (fields[3].equals("ask")) type = 'a';
        else type = 'b';
        PriceSize priceSize = new PriceSize(price, size, type);
        // add to price-size list
        addToPriceSizeList(priceSize);
    }

    @Nullable
    private PriceSize getElementAtSpecifiedPrice(int searchPrice, @NotNull List<PriceSize> list) {
        for (int i = 0; i < list.size(); i++){
            if (searchPrice == list.get(i).getPrice()){
                //System.out.println("the size of " + searchPrice + " price is: " + list.get(i).getSize());
                return list.get(i);
            }
        }
        return null;
    }

    private void addToPriceSizeList(PriceSize priceSize){
        boolean containPrice = false;
        //check if the price is exists. if true update element in the list
        //the type a or b
        for (int i = 0; i < priceAndSizeList.size(); i++){
            if (priceSize.getPrice() == priceAndSizeList.get(i).getPrice()
                && priceSize.getType() == priceAndSizeList.get(i).getType()){

                PriceSize newPriceSizeElement =
                        new PriceSize( priceSize.getPrice()
                                ,priceSize.getSize() + priceAndSizeList.get(i).getSize()
                                , priceSize.getType());
                // set i element in the list with newPriceSizeElement
                priceAndSizeList.set(i, newPriceSizeElement);
                containPrice = true;
                break; // return;
            }
        }
        if (!containPrice) priceAndSizeList.add(priceSize);
    }

    private PriceSize getElementWithMaxPrice(@NotNull List<PriceSize> list, char type){
        PriceSize priceSize = null;
        int value = 0;

        for (PriceSize ps: list) {
            if (ps.getType() == type){
                priceSize = ps; // first element with set type
                value = priceSize.getPrice();
                break;
            }
        }

        for (PriceSize ps: list) {
            if (ps.getType() == type && ps.getPrice() > value){
                    value = ps.getPrice();
                    priceSize = ps;
            }
        }
        return priceSize;
    }

    @NotNull
    private List<PriceSize> sortPriceSizeList(@NotNull List<PriceSize> list){
        List<Integer> prices = new ArrayList<>();
        List<PriceSize> newList = new ArrayList<>();
        // list of all prices
        for (PriceSize element : list) prices.add(element.getPrice());
        // sort prices by value
        prices = prices.stream().sorted().collect(Collectors.toList());
        // for each price get Prise-Size object
        for (Integer price : prices) {
            PriceSize priceSize = getElementAtSpecifiedPrice(price, list);
            newList.add(priceSize);
        }
        return newList;
    }

}
