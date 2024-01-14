package cn.tuyucheng.taketoday.concurrent.futurevscompletablefuturevsrxjava;

public class ObjectHydrator {

   public TestObject hydrateTestObject(TestObject testObject) {
      testObject.setDataPointTwo(20);
      return testObject;
   }

}
