package cn.tuyucheng.taketoday.soap.ws.client.generated;

import jakarta.xml.ws.Service;
import jakarta.xml.ws.WebEndpoint;
import jakarta.xml.ws.WebServiceClient;
import jakarta.xml.ws.WebServiceException;
import jakarta.xml.ws.WebServiceFeature;

import javax.xml.namespace.QName;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 3.0.2
 * Generated source version: 3.0
 */
@WebServiceClient(name = "CountryServiceImplService", targetNamespace = "http://server.ws.soap.tuyucheng.com/", wsdlLocation = "http://localhost:8888/ws/country?wsdl")
public class CountryServiceImplService
      extends Service {

   private final static URL COUNTRYSERVICEIMPLSERVICE_WSDL_LOCATION;
   private final static WebServiceException COUNTRYSERVICEIMPLSERVICE_EXCEPTION;
   private final static QName COUNTRYSERVICEIMPLSERVICE_QNAME = new QName("http://server.ws.soap.tuyucheng.com/", "CountryServiceImplService");

   static {
      URL url = null;
      WebServiceException e = null;
      try {
         url = new URL("http://localhost:8888/ws/country?wsdl");
      } catch (MalformedURLException ex) {
         e = new WebServiceException(ex);
      }
      COUNTRYSERVICEIMPLSERVICE_WSDL_LOCATION = url;
      COUNTRYSERVICEIMPLSERVICE_EXCEPTION = e;
   }

   public CountryServiceImplService() {
      super(__getWsdlLocation(), COUNTRYSERVICEIMPLSERVICE_QNAME);
   }

   public CountryServiceImplService(WebServiceFeature... features) {
      super(__getWsdlLocation(), COUNTRYSERVICEIMPLSERVICE_QNAME, features);
   }

   public CountryServiceImplService(URL wsdlLocation) {
      super(wsdlLocation, COUNTRYSERVICEIMPLSERVICE_QNAME);
   }

   public CountryServiceImplService(URL wsdlLocation, WebServiceFeature... features) {
      super(wsdlLocation, COUNTRYSERVICEIMPLSERVICE_QNAME, features);
   }

   public CountryServiceImplService(URL wsdlLocation, QName serviceName) {
      super(wsdlLocation, serviceName);
   }

   public CountryServiceImplService(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
      super(wsdlLocation, serviceName, features);
   }

   /**
    * @return returns CountryService
    */
   @WebEndpoint(name = "CountryServiceImplPort")
   public CountryService getCountryServiceImplPort() {
      return super.getPort(new QName("http://server.ws.soap.tuyucheng.com/", "CountryServiceImplPort"), CountryService.class);
   }

   /**
    * @param features A list of {@link WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
    * @return returns CountryService
    */
   @WebEndpoint(name = "CountryServiceImplPort")
   public CountryService getCountryServiceImplPort(WebServiceFeature... features) {
      return super.getPort(new QName("http://server.ws.soap.tuyucheng.com/", "CountryServiceImplPort"), CountryService.class, features);
   }

   private static URL __getWsdlLocation() {
      if (COUNTRYSERVICEIMPLSERVICE_EXCEPTION != null) {
         throw COUNTRYSERVICEIMPLSERVICE_EXCEPTION;
      }
      return COUNTRYSERVICEIMPLSERVICE_WSDL_LOCATION;
   }

}
