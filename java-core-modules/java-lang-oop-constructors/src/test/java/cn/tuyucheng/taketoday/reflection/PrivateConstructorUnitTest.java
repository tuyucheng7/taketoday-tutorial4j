package cn.tuyucheng.taketoday.reflection;

import cn.tuyucheng.taketoday.privateconstructors.PrivateConstructorClass;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PrivateConstructorUnitTest {

    @Test
    void whenConstructorIsPrivate_thenInstanceSuccess() throws Exception {
        Constructor<PrivateConstructorClass> pcc = PrivateConstructorClass.class.getDeclaredConstructor();
        pcc.setAccessible(true);
        PrivateConstructorClass privateConstructorInstance = pcc.newInstance();
        assertTrue(privateConstructorInstance instanceof PrivateConstructorClass);
    }
}