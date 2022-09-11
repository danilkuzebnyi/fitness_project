package org.danylo.service;

import org.danylo.model.Country;
import org.danylo.repository.CountryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpSession;
import java.util.List;

@Service
public class CountryService {
    CountryRepository countryRepository;

    @Autowired
    public CountryService(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    public List<Country> getAll() {
        return countryRepository.getAll();
    }

    public Country getById(int id) {
        return countryRepository.getById(id);
    }

    public void setCountryFieldsToHttpSession(Country selectedCountry, HttpSession httpSession) {
        if (httpSession.getAttribute("countries") == null) {
            List<Country> countries = getAll();
            httpSession.setAttribute("countries", countries);
        }
        httpSession.setAttribute("selectedCountry", selectedCountry);
        String code = selectedCountry == null ? "" : selectedCountry.getCode();
        httpSession.setAttribute("code", code);
    }
}
