package cn.tuyucheng.taketoday.accesing_session_attributes.business.entities.factories;

import org.springframework.stereotype.Service;

import cn.tuyucheng.taketoday.accesing_session_attributes.business.entities.NameAgeEntity;
import cn.tuyucheng.taketoday.accesing_session_attributes.business.entities.NameAnalysisEntity;
import cn.tuyucheng.taketoday.accesing_session_attributes.business.entities.NameCountriesEntity;
import cn.tuyucheng.taketoday.accesing_session_attributes.business.entities.NameGenderEntity;

@Service
public class NameAnalysisEntityFactory {

   public NameAnalysisEntity getInstance(String nameRequest, NameGenderEntity gender, NameAgeEntity age, NameCountriesEntity countries) {
      NameAnalysisEntity nameAnalysis = new NameAnalysisEntity();
      nameAnalysis.setName(nameRequest);
      nameAnalysis.setGender(gender);
      nameAnalysis.setAge(age);
      nameAnalysis.setCountries(countries);
      return nameAnalysis;
   }
}