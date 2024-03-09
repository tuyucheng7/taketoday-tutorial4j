package cn.tuyucheng.taketoday.enums.mapping;

import cn.tuyucheng.taketoday.enums.mapping.order.CmsOrderStatus;
import cn.tuyucheng.taketoday.enums.mapping.order.OrderStatus;
import cn.tuyucheng.taketoday.enums.mapping.user.ExternalUserStatus;
import cn.tuyucheng.taketoday.enums.mapping.user.UserStatus;
import cn.tuyucheng.taketoday.enums.mapping.user.UserStatusMapper;
import cn.tuyucheng.taketoday.enums.mapping.user.UserStatusWithFieldVariable;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EnumConversionUnitTest {

   @Test
   void whenUsingSwitchStatement_thenEnumConverted() {
      UserStatus userStatusDeleted = UserStatus.DELETED;
      UserStatus userStatusPending = UserStatus.PENDING;
      UserStatus userStatusActive = UserStatus.ACTIVE;

      assertEquals(ExternalUserStatus.INACTIVE, userStatusDeleted.toExternalUserStatusViaSwitchStatement());
      assertEquals(ExternalUserStatus.INACTIVE, userStatusPending.toExternalUserStatusViaSwitchStatement());
      assertEquals(ExternalUserStatus.ACTIVE, userStatusActive.toExternalUserStatusViaSwitchStatement());
   }

   @Test
   void whenUsingSwitch_thenEnumConverted() {
      UserStatus userStatusDeleted = UserStatus.DELETED;
      UserStatus userStatusPending = UserStatus.PENDING;
      UserStatus userStatusActive = UserStatus.ACTIVE;

      assertEquals(ExternalUserStatus.INACTIVE, userStatusDeleted.toExternalUserStatusViaRegularSwitch());
      assertEquals(ExternalUserStatus.INACTIVE, userStatusPending.toExternalUserStatusViaRegularSwitch());
      assertEquals(ExternalUserStatus.ACTIVE, userStatusActive.toExternalUserStatusViaRegularSwitch());
   }

   @Test
   void whenUsingFieldVariable_thenEnumConverted() {
      UserStatusWithFieldVariable userStatusDeleted = UserStatusWithFieldVariable.DELETED;
      UserStatusWithFieldVariable userStatusPending = UserStatusWithFieldVariable.PENDING;
      UserStatusWithFieldVariable userStatusActive = UserStatusWithFieldVariable.ACTIVE;

      assertEquals(ExternalUserStatus.INACTIVE, userStatusDeleted.toExternalUserStatus());
      assertEquals(ExternalUserStatus.INACTIVE, userStatusPending.toExternalUserStatus());
      assertEquals(ExternalUserStatus.ACTIVE, userStatusActive.toExternalUserStatus());
   }

   @Test
   void whenUsingEnumMap_thenEnumConverted() {
      UserStatus userStatusDeleted = UserStatus.DELETED;
      UserStatus userStatusPending = UserStatus.PENDING;
      UserStatus userStatusActive = UserStatus.ACTIVE;

      assertEquals(ExternalUserStatus.INACTIVE, UserStatusMapper.toExternalUserStatus(userStatusDeleted));
      assertEquals(ExternalUserStatus.INACTIVE, UserStatusMapper.toExternalUserStatus(userStatusPending));
      assertEquals(ExternalUserStatus.ACTIVE, UserStatusMapper.toExternalUserStatus(userStatusActive));
   }

   @Test
   void whenUsingOrdinalApproach_thenEnumConverted() {
      OrderStatus orderStatusApproved = OrderStatus.APPROVED;
      OrderStatus orderStatusDelivered = OrderStatus.DELIVERED;
      OrderStatus orderStatusPending = OrderStatus.PENDING;

      assertEquals(CmsOrderStatus.APPROVED, orderStatusApproved.toCmsOrderStatusOrdinal());
      assertEquals(CmsOrderStatus.DELIVERED, orderStatusDelivered.toCmsOrderStatusOrdinal());
      assertEquals(CmsOrderStatus.PENDING, orderStatusPending.toCmsOrderStatusOrdinal());
   }

   @Test
   void whenUsingEnumName_thenEnumConverted() {
      OrderStatus orderStatusApproved = OrderStatus.APPROVED;
      OrderStatus orderStatusDelivered = OrderStatus.DELIVERED;
      OrderStatus orderStatusPending = OrderStatus.PENDING;

      assertEquals(CmsOrderStatus.APPROVED, orderStatusApproved.toCmsOrderStatus());
      assertEquals(CmsOrderStatus.DELIVERED, orderStatusDelivered.toCmsOrderStatus());
      assertEquals(CmsOrderStatus.PENDING, orderStatusPending.toCmsOrderStatus());
   }

   @Test
   void whenUsingDefaultMapstruct_thenEnumConverted() {
      UserStatus userStatusDeleted = UserStatus.DELETED;
      UserStatus userStatusPending = UserStatus.PENDING;
      UserStatus userStatusActive = UserStatus.ACTIVE;

      EnumMapper enumMapper = new EnumMapperImpl();

      assertEquals(ExternalUserStatus.INACTIVE, enumMapper.map(userStatusDeleted));
      assertEquals(ExternalUserStatus.INACTIVE, enumMapper.map(userStatusPending));
      assertEquals(ExternalUserStatus.ACTIVE, enumMapper.map(userStatusActive));
   }

   @Test
   void whenUsingConfiguredMapstruct_thenEnumConverted() {
      OrderStatus orderStatusApproved = OrderStatus.APPROVED;
      OrderStatus orderStatusDelivered = OrderStatus.DELIVERED;
      OrderStatus orderStatusPending = OrderStatus.PENDING;

      EnumMapper enumMapper = new EnumMapperImpl();

      assertEquals(CmsOrderStatus.APPROVED, enumMapper.map(orderStatusApproved));
      assertEquals(CmsOrderStatus.DELIVERED, enumMapper.map(orderStatusDelivered));
      assertEquals(CmsOrderStatus.PENDING, enumMapper.map(orderStatusPending));
   }

   @Test
   void whenUsingConfiguredWithRemainingMapstruct_thenEnumConverted() {
      UserStatus userStatusDeleted = UserStatus.DELETED;
      UserStatus userStatusPending = UserStatus.PENDING;
      UserStatus userStatusActive = UserStatus.ACTIVE;

      EnumMapper enumMapper = new EnumMapperImpl();

      assertEquals(ExternalUserStatus.INACTIVE, enumMapper.mapDefault(userStatusDeleted));
      assertEquals(ExternalUserStatus.INACTIVE, enumMapper.mapDefault(userStatusPending));
      assertEquals(ExternalUserStatus.ACTIVE, enumMapper.mapDefault(userStatusActive));
   }
}
