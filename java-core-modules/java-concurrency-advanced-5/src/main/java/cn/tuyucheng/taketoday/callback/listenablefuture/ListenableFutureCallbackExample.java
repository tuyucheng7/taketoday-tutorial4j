package cn.tuyucheng.taketoday.callback.listenablefuture;

import com.google.common.util.concurrent.*;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ListenableFutureCallbackExample {

   public static void main(String[] args) {
      ExecutorService executorService = Executors.newFixedThreadPool(1);
      ListeningExecutorService pool = MoreExecutors.listeningDecorator(executorService);
      ListenableFuture<String> listenableFuture = pool.submit(downloadFile());

      Futures.addCallback(listenableFuture, new FutureCallback<String>() {
         @Override
         public void onSuccess(String result) {
            // code to push fileName to DB
         }

         @Override
         public void onFailure(Throwable throwable) {
            // code to take appropriate action when there is an error
         }
      }, executorService);
   }

   private static Callable<String> downloadFile() {
      return () -> {
         // Mimicking the downloading of a file by adding a sleep call
         Thread.sleep(5000);
         return "pic.jpg";
      };
   }
}