import cn.tuyucheng.taketoday.providermodule.LowercaseTextService;

module cn.tuyucheng.taketoday.providermodule {
   requires cn.tuyucheng.taketoday.servicemodule;
   provides cn.tuyucheng.taketoday.servicemodule.TextService with LowercaseTextService;
}