package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.Locale;

@SpringBootApplication
public class TaskAppApplication {

  public static void main(String[] args) {
    Locale.setDefault(Locale.US);
    SpringApplication.run(TaskAppApplication.class, args);
  }

}
