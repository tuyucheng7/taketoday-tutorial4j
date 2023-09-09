package cn.tuyucheng.taketoday.mockito.callbacks;

public class ActionHandler {

   private Service service;

   public ActionHandler(Service service) {
      this.service = service;
   }

   public void doAction() {
      service.doAction("our-request", this::handleResponse);
   }

   private void handleResponse(Response response) {
      if (response.isValid()) {
         response.setData(new Data("Successful data response"));
      }
   }
}