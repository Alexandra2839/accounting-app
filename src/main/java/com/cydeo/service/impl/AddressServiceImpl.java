package com.cydeo.service.impl;

import com.cydeo.client.CountryClient;
import com.cydeo.dto.TokenDto;
import com.cydeo.service.AddressService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddressServiceImpl implements AddressService {

    private final CountryClient countryClient;

    private String apiToken = "4xGQzd5eB_Wa1wUWg4nsiz7I4LBJWtuvdK40lBj0l4agHRXbkYHoDIUQW7hPLP_OPFQ";
    private String email = "anitataksa3440@gmail.com";

    public AddressServiceImpl(CountryClient countryClient) {
        this.countryClient = countryClient;
    }

    @Override
    public List<String> getCountryList() {

        TokenDto token = countryClient.auth(this.email, this.apiToken);
        String bearerToken = "Bearer " + token.getAuthToken();
        List<String> countryNameList = countryClient.getCountryList(bearerToken).stream()
                .map(countryDto -> countryDto.getCountryName())
                .sorted()
                .collect(Collectors.toList());

        countryNameList.add(0, "United States");

        return countryNameList;

    }
}
