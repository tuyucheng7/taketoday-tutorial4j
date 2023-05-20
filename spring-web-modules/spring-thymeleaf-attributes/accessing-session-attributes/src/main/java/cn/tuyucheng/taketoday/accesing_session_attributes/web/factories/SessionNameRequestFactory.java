package cn.tuyucheng.taketoday.accesing_session_attributes.web.factories;

import java.util.Date;

import org.springframework.stereotype.Service;

import cn.tuyucheng.taketoday.accesing_session_attributes.business.beans.NameRequest;
import cn.tuyucheng.taketoday.accesing_session_attributes.web.beans.SessionNameRequest;

@Service
public class SessionNameRequestFactory {

   public SessionNameRequest getInstance(NameRequest nameRequest) {
      NameRequest cloned = new NameRequest();
      cloned.setName(nameRequest.getName());

      SessionNameRequest sessionNameRequest = new SessionNameRequest();
      sessionNameRequest.setRequestDate(new Date());
      sessionNameRequest.setNameRequest(cloned);

      return sessionNameRequest;
   }
}