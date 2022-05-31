import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.time.format.DateTimeFormatter;

import static java.lang.Integer.parseInt;

//https://polygon.io/

public class StockMarket {

    private ArrayList<String> stocks;
    private Stock stockInfo;
    private String apiKey;
    private String stockName;

    public StockMarket(){
        stocks = new ArrayList<String>();
        stocks.add("AMZN");
        stocks.add("AAPL");
        stocks.add("TSLA");
        stocks.add("NVDA");
        stocks.add("AMC");
        apiKey = "Dgd8uy3CevzAlTWZJ79VyKfDnq7Q6J_2";
    }

    public void stockOptions(){
        System.out.print("Some Stock Options: ");
        for(int i = 0; i < stocks.size(); i++) {
            if(i == stocks.size() - 1) {
                System.out.print(stocks.get(i) + "\n");
            }
            else{
                System.out.print(stocks.get(i) + ", ");
            }
        }
        System.out.println("If you have other stocks that you want to check, you could also input those.");
    }

    public void findStock() {
        Scanner s = new Scanner(System.in);
        System.out.print("Please choose a stock: ");
        this.stockName = s.nextLine();
        stockName = stockName.toUpperCase();
        System.out.print("How long do you want to run the simulation (ie. day, month, year): ");
        String timeSpan = s.nextLine();
        while(!timeSpan.equals("day") && !timeSpan.equals("month") && !timeSpan.equals("year")) {
            System.out.print("Time span inputted incorrectly. Please input a valid run time (ie. day, month, year): ");
            timeSpan = s.nextLine();
        }
        System.out.print("When do you want to start the simulation (ie. 2021-07-22): ");
        String start = s.nextLine();
        while(!dateChecker(start)){
            System.out.print("Date inputted incorrectly. Please input a valid start date (ie. 2021-07-22): ");
            start = s.nextLine();
        }
        System.out.print("When do you want to end the simulation (ie. 2021-07-22): ");
        String end = s.nextLine();
        while(!dateChecker(end)){
            System.out.print("Date inputted incorrectly. Please input a valid end date (ie. 2021-07-22): ");
            end = s.nextLine();
        }

        String urlStock = "https://api.polygon.io/v2/aggs/ticker/" + stockName + "/range/1/"+ timeSpan + "/" + start + "/" + end + "?apiKey=" + apiKey;
        makeAPICall(urlStock);
    }

    public boolean dateChecker(String date){
        if(date.indexOf("-") == 4 && date.indexOf("-", 5) == 7){
            if(date.length() == 10){
                String year = date.substring(0, 4);
                if(parseInt(year) < 2022){
                    String month = date.substring(5, 7);
                    if(parseInt(month) < 12){
                        String day = date.substring(8);
                        if(parseInt(day) < 31){
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public void adviceStock(){
        Scanner s = new Scanner(System.in);
        System.out.print("Please choose a stock: ");
        this.stockName = s.nextLine();
        stockName = stockName.toUpperCase();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        System.out.println("Yesterday's date: " + dtf.format(yesterday));
        String urlStock = "https://api.polygon.io/v2/aggs/ticker/" + stockName + "/range/1/day/" + yesterday + "/" + yesterday + "?apiKey=" + apiKey;
        makeAPICall(urlStock);

        LocalDate year = today.minusDays(1).minusYears(1);
        System.out.println("Last year's date: " + dtf.format(year));

        urlStock = "https://api.polygon.io/v2/aggs/ticker/" + stockName + "/range/1/year/" + year + "/" + yesterday + "?apiKey=" + apiKey;
        makeAPICall(urlStock);

        decider();
    }

    public void decider(){
        if(stockInfo.getOpening() > stockInfo.getClosing() && stockInfo.getHighest() > stockInfo.getOpening()){
            System.out.println("If you bought " + stockName + " within a year, it is recommended that you hold " + stockInfo.getName() + " since it is likely to rise again.");
        }
        if(stockInfo.getOpening() < stockInfo.getClosing() && stockInfo.getHighest() > stockInfo.getOpening()){
            System.out.println("If you bought " + stockName + " within a year, it is recommended that you sell " + stockInfo.getName() + " since it is likely to drop soon or it is going to continue dropping.");
        }
    }

    public void makeAPICall(String url)
    {
        try {
            URI myUrl = URI.create(url);
            HttpRequest.Builder builder = HttpRequest.newBuilder();
            builder.uri(myUrl);
            HttpRequest request = builder.build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            parseJSON(response.body());

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void parseJSON(String json) {
        JSONObject jsonObj = new JSONObject(json);
        JSONArray stockList = jsonObj.getJSONArray("results");
        int resultCount = jsonObj.getInt("resultsCount");

        double opening = 0;
        double closing = 0;
        double lowest = 0;
        double highest = 0;
        double highestOne = 0;
        double highestTwo = 0;
        double lowestOne = 0;
        double lowestTwo = 0;

        if (resultCount != 1) {
            JSONObject listOne = stockList.getJSONObject(0);
            JSONObject listTwo = stockList.getJSONObject(1);
            opening = listOne.getDouble("o");
            highestOne = listOne.getDouble("h");
            lowestOne = listOne.getDouble("l");
            closing = listTwo.getDouble("c");
            highestTwo = listTwo.getDouble("h");
            lowestTwo = listTwo.getDouble("l");
            if (highestOne > highestTwo) {
                highest = highestOne;
            } else {
                highest = highestTwo;
            }
            if (lowestOne > lowestTwo) {
                lowest = lowestTwo;
            } else {
                lowest = lowestOne;
            }
            stockInfo = new Stock(stockName, opening, closing, highest, lowest);
            System.out.println(stockInfo.toString());
        }
        else {
            for (int i = 0; i < stockList.length(); i++) {
                JSONObject stockObj = stockList.getJSONObject(i);
                opening = stockObj.getDouble("o");
                closing = stockObj.getDouble("c");
                highest = stockObj.getDouble("h");
                lowest = stockObj.getDouble("l");
                stockInfo = new Stock(stockName, opening, closing, highest, lowest);
                System.out.println(stockInfo.toString());
            }
        }
    }
}   