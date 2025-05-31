package io.github.honey;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Honey {

  private Honey() {}

  public static void main(final String[] args) {
    final long now = System.currentTimeMillis();
    SpringApplication.run(Honey.class, args);
    System.out.println(
        "Done (" + (System.currentTimeMillis() - now) / 1000 + "s)! For help, type 'help' or '?'");
    System.out.println("Listening on port " + System.getenv("PORT"));

    // keep-alive
    while (true) {
      try {
        Thread.sleep(1000);
      } catch (final InterruptedException exception) {
        exception.printStackTrace();
      }
    }
  }
}
