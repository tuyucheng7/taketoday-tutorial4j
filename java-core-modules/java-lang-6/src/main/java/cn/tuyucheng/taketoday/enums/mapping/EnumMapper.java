package cn.tuyucheng.taketoday.enums.mapping;

import cn.tuyucheng.taketoday.enums.mapping.order.CmsOrderStatus;
import cn.tuyucheng.taketoday.enums.mapping.order.OrderStatus;
import cn.tuyucheng.taketoday.enums.mapping.user.ExternalUserStatus;
import cn.tuyucheng.taketoday.enums.mapping.user.UserStatus;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ValueMapping;

@Mapper
public interface EnumMapper {

   CmsOrderStatus map(OrderStatus orderStatus);

   @ValueMapping(source = "PENDING", target = "INACTIVE")
   @ValueMapping(source = "BLOCKED", target = "INACTIVE")
   @ValueMapping(source = "INACTIVATED_BY_SYSTEM", target = "INACTIVE")
   @ValueMapping(source = "DELETED", target = "INACTIVE")
   ExternalUserStatus map(UserStatus userStatus);

   @ValueMapping(source = MappingConstants.ANY_REMAINING, target = "INACTIVE")
   ExternalUserStatus mapDefault(UserStatus userStatus);

}
