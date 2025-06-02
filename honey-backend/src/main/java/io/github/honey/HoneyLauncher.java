package io.github.honey;

import org.slf4j.LoggerFactory;

public final class HoneyLauncher {

  private HoneyLauncher() {}

  public static void main(final String[] args) {
    final Honey honey = new Honey();
    new Thread((SafeRunnable) honey::start).start();
    Runtime.getRuntime().addShutdownHook(new Thread((SafeRunnable) honey::stop));
  }

  @FunctionalInterface
  interface SafeRunnable extends Runnable {

    @Override
    default void run() {
      try {
        runSafely();
      } catch (final Exception exception) {
        LoggerFactory.getLogger(HoneyLauncher.class).error("Failed to run honey", exception);
        System.exit(0);
      }
    }

    void runSafely() throws Exception;
  }
}
