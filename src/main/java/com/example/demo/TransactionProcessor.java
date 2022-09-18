package com.example.demo;

import com.example.demo.beans.Currency;
import com.example.demo.beans.User;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author April Chou
 */
@Component
public class TransactionProcessor implements CommandLineRunner {

    @Autowired
    private CurrencyServiceImpl currencyService;

    @Autowired
    private UserServiceImpl userService;

    @Override
    public void run(String... args) throws Exception {
        readFile();
    }

    public void readFile(){
        try {

            ArrayList<User> users = userService.loadUsers();
            System.out.println("Users detail before converting:");
            System.out.println(users);

            Map<String, User> userMap = new HashMap<>();
            for (User user : users) {
                userMap.put(user.getName(), user);
            }

            // read all lines
            List<String> lines = Files.readAllLines(Paths.get("src/main/resources/data/transactions.txt"));
            for(int i = 0; i < lines.size(); i++){
                System.out.println("========>");
                System.out.println(lines.get(i));
                String[] s = lines.get(i).split(" ");
                // give the value of the every field in each line.
                // s[0] name;
                // s[1], fromCurrency
                // s[2], toCurrency
                // Double.parseDouble(s[3]), exchangeAmount
                convert(userMap, s[0], s[1], s[2], Double.parseDouble(s[3]));
                System.out.println("========>");
            }
            // print all lines
            System.out.println("Users detail after converting:");
            System.out.println(userMap.values());

            ObjectMapper objectMapper = new ObjectMapper();
            ObjectWriter writer = objectMapper.writer(new DefaultPrettyPrinter());
            writer.writeValue(Paths.get("src/main/resources/data/users1.json").toFile(), userMap.values());


        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public String convert(Map<String, User> userMap, String name, String fromCurrency, String toCurrency, double exchangeAmount) throws IOException {
        if (!userMap.containsKey(name)) {
            System.out.println("No such name in the users.json!");
            return "No such name in the users.json!";
        }

        Map<String, Currency> currencyDict = currencyService.loadCurrencyRatesDict();
        User user = userMap.get(name);
        if (user.getWallet().containsKey(fromCurrency)){
            // check if the user`s amount is enough for the exchange
            if(user.getWallet().get(fromCurrency) >= exchangeAmount){
                // check the fromCurrency and to Currency is usd or not
                if (fromCurrency.equals("usd")){
                    if (toCurrency.equals("usd")) {
                        // usd - usd -usd
                        user.getWallet().put(fromCurrency, user.getWallet().get(fromCurrency));
                    } else {
                        // usd - usd - to
                        user.getWallet().put(fromCurrency, (user.getWallet().get(fromCurrency) - exchangeAmount) );
                        double toCurrencyInit = user.getWallet().get(toCurrency) == null ? 0.0 : user.getWallet().get(toCurrency);
                        user.getWallet().put(toCurrency, toCurrencyInit + exchangeAmount * 1 * currencyDict.get(toCurrency).getRate());
                    }
                } else {
                    if (toCurrency.equals("usd")) {
                        // from - usd - usd
                        user.getWallet().put(fromCurrency, (user.getWallet().get(fromCurrency) - exchangeAmount) );
                        double toCurrencyInit = user.getWallet().get(toCurrency) == null ? 0.0 : user.getWallet().get(toCurrency);
                        user.getWallet().put(toCurrency, toCurrencyInit + exchangeAmount * currencyDict.get(fromCurrency).getInverseRate() * 1);
                    } else {
                        // from - usd - to
                        user.getWallet().put(fromCurrency, (user.getWallet().get(fromCurrency) - exchangeAmount) );
                        double toCurrencyInit = user.getWallet().get(toCurrency) == null ? 0.0 : user.getWallet().get(toCurrency);
                        user.getWallet().put(toCurrency,  toCurrencyInit + exchangeAmount * currencyDict.get(fromCurrency).getInverseRate() * currencyDict.get(toCurrency).getRate() );
                    }
                }

//                // update the map===> fromCurrency : fromCurrency - exchangeAmount
//                user.getWallet().put(fromCurrency, (user.getWallet().get(fromCurrency) - exchangeAmount) );
//                // update the map===> toCurrency : fromCurrency * fromCurrencyInverseRate * toCurrencyRate
//                // for instance 100 JPY change to chinese yuan ===>  100 * 0.00702 * 6.9
//                user.getWallet().put(toCurrency, (user.getWallet().get(fromCurrency) * currencyService.loadCurrencyByKey(fromCurrency).getInverseRate() * currencyService.loadCurrencyByKey(toCurrency).getRate()));
                System.out.println("Exchange Successfully!");
                return "Exchange Successfully!";
            } else {
                System.out.println("Not enough currency in the users.json!");
                return "Not enough currency in the users.json!";
            }
        }

        return "completed";
    }
}
