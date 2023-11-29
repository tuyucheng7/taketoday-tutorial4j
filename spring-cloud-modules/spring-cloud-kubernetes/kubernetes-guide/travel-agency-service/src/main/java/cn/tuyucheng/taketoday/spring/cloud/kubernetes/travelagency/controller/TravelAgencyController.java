package cn.tuyucheng.taketoday.spring.cloud.kubernetes.travelagency.controller;

import cn.tuyucheng.taketoday.spring.cloud.kubernetes.travelagency.model.TravelDeal;
import cn.tuyucheng.taketoday.spring.cloud.kubernetes.travelagency.repository.TravelDealRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Random;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class TravelAgencyController {

   @Autowired
   private TravelDealRepository travelDealRepository;

   private static final Log log = LogFactory.getLog(TravelAgencyController.class);

   @RequestMapping(method = GET, path = "/deals")
   public String deals() {
      log.info("Client is requesting new deals!");

      List<TravelDeal> travelDealList = travelDealRepository.findAll();
      if (!travelDealList.isEmpty()) {
         int randomDeal = new Random().nextInt(travelDealList.size());
         return travelDealList.get(randomDeal)
               .toString();
      } else {
         return "NO DEALS";
      }
   }

   @RequestMapping(method = GET, path = "/")
   @ResponseBody
   public String get() throws UnknownHostException {
       String stringBuilder = "Host: " +
               InetAddress.getLocalHost()
                       .getHostName() +
               "<br/>" +
               "IP: " +
               InetAddress.getLocalHost()
                       .getHostAddress() +
               "<br/>" +
               "Type: " +
               "Travel Agency" +
               "<br/>";
      return stringBuilder;
   }
}