package com.example.demo;

import com.example.demo.beans.Currency;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class CurrencyServiceImpl {

    @Value("classpath:data/fx_rates.json")
    Resource ratesFile;

    ObjectMapper mapper = new ObjectMapper();

    TypeReference<HashMap<String, Currency>> typeRef
            = new TypeReference<HashMap<String, Currency>>() {};

    public Map<String, Currency> loadCurrencyRatesDict() throws IOException {
        return mapper.readValue(ratesFile.getInputStream(), typeRef);
    }

    public Currency loadCurrencyByKey(String key) throws IOException {
        Map<String, Currency> currencyMap =  mapper.readValue(ratesFile.getInputStream(), typeRef);
        return currencyMap.get(key);
    }
}