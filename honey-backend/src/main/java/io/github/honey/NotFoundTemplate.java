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
            <title>404 — Not Found</title>
            <style>
              body {
                margin: 0;
                padding: 0;
                height: 100vh;
                background: radial-gradient(circle at center, #0f0f0f 0%, #000000 100%);
                display: flex;
                justify-content: center;
                align-items: center;
                font-family: 'Courier New', monospace;
                color: #ccc;
                overflow: hidden;
              }

              .container {
                text-align: center;
                animation: flicker 3s infinite alternate;
                padding: 0 2rem;
              }

              h1 {
                font-size: 3rem;
                color: #ff0055;
                text-shadow: 0 0 5px #ff0055, 0 0 10px #ff0055, 0 0 20px #ff0055;
                margin: 0;
              }

              .glitch {
                color: #00ffe7;
                position: relative;
              }

              .glitch::before,
              .glitch::after {
                content: attr(data-text);
                position: absolute;
                left: 0;
                width: 100%;
                overflow: hidden;
                color: #ff00c8;
                clip: rect(0, 0, 0, 0);
              }

              .glitch::before {
                animation: glitchTop 1.5s infinite linear alternate-reverse;
              }

              .glitch::after {
                animation: glitchBottom 1.5s infinite linear alternate-reverse;
              }

              p {
                margin-top: 1rem;
                font-size: 1.1rem;
                color: #aaa;
              }

              .ascii {
                margin-top: 2rem;
                font-size: 1.5rem;
                color: #00ffcc;
                line-height: 1.2;
                white-space: pre;
              }

              .not-found-info {
                margin-top: 1.5rem;
                font-size: 1rem;
                color: #ff55aa;
                background: rgba(255, 255, 255, 0.05);
                padding: 0.5rem 1rem;
                border-radius: 8px;
                border: 1px solid #ff55aa22;
                display: inline-block;
              }

              a {
                display: inline-block;
                margin-top: 2rem;
                text-decoration: none;
                color: #ff55aa;
                border: 1px solid #ff55aa;
                padding: 0.5rem 1rem;
                border-radius: 8px;
                transition: background 0.3s ease, color 0.3s ease;
              }

              a:hover {
                background: #ff55aa;
                color: black;
              }

              @keyframes flicker {
                0%, 19%, 21%, 23%, 25%, 54%, 56%, 100% {
                  opacity: 1;
                }
                20%, 24%, 55% {
                  opacity: 0.4;
                }
              }

              @keyframes glitchTop {
                0% {
                  clip: rect(0, 900px, 0, 0);
                }
                100% {
                  clip: rect(0, 900px, 60px, 0);
                  transform: translate(-2px, -2px);
                }
              }

              @keyframes glitchBottom {
                0% {
                  clip: rect(0, 900px, 0, 0);
                }
                100% {
                  clip: rect(85px, 900px, 140px, 0);
                  transform: translate(2px, 2px);
                }
              }
            </style>
          </head>
          <body>
            <div class="container">
              <h1 class="glitch" data-text="404 NOT FOUND">404 NOT FOUND</h1>
              <p>You’ve stumbled into the void.</p>
              <div class="not-found-info">
                {{DETAILS}}
              </div>
              <div class="ascii">
                {\\\\__/}<br>
                (⚆_⚆)<br>
                ( >🔧 )<br>
              </div>
              <a href="/">Return to safety</a>
            </div>
          </body>
        </html>
        """
        .replace(
            "{{DETAILS}}",
            details.isEmpty()
                ? ""
                : "<p><i>" + XSS_FILTER.matcher(details).replaceAll("") + "</i></p>");
  }
}
