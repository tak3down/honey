package io.github.honey;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Honey {

  private Honey() {}

  public static void main(final String[] args) {
    SpringApplication.run(Honey.class, args);
  }
}
