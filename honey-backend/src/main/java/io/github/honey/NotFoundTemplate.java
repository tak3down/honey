package io.github.honey;

import java.util.regex.Pattern;

final class NotFoundTemplate {

  private static final Pattern XSS_FILTER = Pattern.compile("[^A-Za-z0-9/._\\- ]");

  private NotFoundTemplate() {}

  static String createNotFoundPage(final String details) {
    return """
        <html lang="en">
          <head>
            <meta charset="UTF-8" />
            <title>404 Not Found</title>
            <style>
              body { height: calc(100vh - 170px); display: flex; justify-content: center; align-items: center; font-family: Arial; }
              .error-view { text-align: center; width: 100vh; height: 100px; }
              .spooky p { margin: 0; font-size: 1.2rem; font-weight: lighter; }
              a { color: rebeccapurple; text-decoration: none; }
            </style>
          </head>
          <body>
            <div class='error-view'>
              <h1 style="font-size: 1.5rem"><span style="color: gray;">404︱</span>Resource not found</h1>
              %s
              <p>Looking for something?</p>
              <div class="spooky"><p>{\\__/}</p><p>(●ᴗ●)</p><p>( >🥕</p></div>
            </div>
          </body>
        </html>
        """
        .formatted(
            details.isEmpty()
                ? ""
                : "<p><i>" + XSS_FILTER.matcher(details).replaceAll("") + "</i></p>");
  }
}
