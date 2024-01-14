import cn.tuyucheng.taketoday.providermodule.LowercaseTextService;
import cn.tuyucheng.taketoday.servicemodule.TextService;

module cn.tuyucheng.taketoday.providermodule {
   requires cn.tuyucheng.taketoday.servicemodule;
   provides TextService with LowercaseTextService;
}