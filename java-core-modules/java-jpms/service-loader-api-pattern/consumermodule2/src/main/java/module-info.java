import cn.tuyucheng.taketoday.servicemodule.TextService;

module cn.tuyucheng.taketoday.consumermodule {
   requires cn.tuyucheng.taketoday.servicemodule;
   uses TextService;
}
