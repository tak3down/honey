package io.github.honey;

import java.io.IOException;
import java.io.InputStream;

@FunctionalInterface
interface ResourceSupplier {

  Either<IOException, InputStream> supply();
}
