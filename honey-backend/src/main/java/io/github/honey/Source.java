package io.github.honey;

import java.io.InputStream;

@FunctionalInterface
interface Source {

  InputStream get();
}
