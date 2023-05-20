package cn.tuyucheng.taketoday.accesing_session_attributes.business;

import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;

import cn.tuyucheng.taketoday.accesing_session_attributes.business.beans.NameRequest;
import cn.tuyucheng.taketoday.accesing_session_attributes.business.entities.NameAnalysisEntity;

@Service
public interface NameAnalysisService {
   public NameRequest getLastNameRequest();

   public CompletableFuture<NameAnalysisEntity> searchForName(NameRequest nameRequest);
}