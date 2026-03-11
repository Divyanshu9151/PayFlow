package com.payflow.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

  //If @Async has no executor name:
//•	If only one Executor bean exists, Spring uses it automatically
//	•	If multiple exist, Spring looks for taskExecutor
//	•	Otherwise it uses SimpleAsyncTaskExecutor
   @Async
    public void sendTransactionEmail(String email)
     {
         try{
             Thread.sleep(10000);
             System.out.println("Email sent to "+email +" by Thread " + Thread.currentThread().getName());
         }catch (InterruptedException ex)
         {
             Thread.currentThread().interrupt();
         }
     }
}
