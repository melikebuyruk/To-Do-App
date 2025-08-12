// src/main/java/com/example/demo/TaskAppApplication.java
package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.Locale;

@SpringBootApplication
public class TaskAppApplication {

  public static void main(String[] args) {
    Locale.setDefault(Locale.US);   // 💡 Türkçe 'i' -> 'İ' problemine kesin çözüm
    SpringApplication.run(TaskAppApplication.class, args);
  }
}
