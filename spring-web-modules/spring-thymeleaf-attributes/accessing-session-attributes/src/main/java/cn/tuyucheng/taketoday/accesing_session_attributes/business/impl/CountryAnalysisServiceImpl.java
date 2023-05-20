package cn.tuyucheng.taketoday.accesing_session_attributes.business.impl;

import java.net.URI;
import java.text.MessageFormat;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import cn.tuyucheng.taketoday.accesing_session_attributes.business.CountryAnalysisService;
import cn.tuyucheng.taketoday.accesing_session_attributes.business.entities.NameCountriesEntity;

@Component
public class CountryAnalysisServiceImpl implements CountryAnalysisService {
   @Value("${name-analysis-controller.name-countries-api-url:https://api.nationalize.io/?name={0}}")
   private String nameCountriesApiUrl;

   @Override
   public ResponseEntity<NameCountriesEntity> getCountryAnalysisForName(String nameToAnalyze) {
      RestTemplate restTemplate = new RestTemplate();
      return restTemplate.getForEntity(URI.create(MessageFormat.format(nameCountriesApiUrl, nameToAnalyze)), NameCountriesEntity.class);
   }
}