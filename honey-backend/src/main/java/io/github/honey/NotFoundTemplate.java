package io.github.honey;

import static java.util.regex.Pattern.compile;

import java.util.regex.Pattern;
import org.intellij.lang.annotations.Language;

final class NotFoundTemplate {

  private static final Pattern XSS_FILTER = compile("[^A-Za-z0-9/._\\- ]");

  private NotFoundTemplate() {}

  static @Language("html") String createNotFoundPage(final String details) {
    return """
        <html lang="pl">
          <head>
            <meta charset="UTF-8" />
            <title>404 — Nie znaleziono</title>
            <style>
              body {
                margin: 0;
                padding: 0;
                height: 100vh;
                background: radial-gradient(circle at center, #0a0a0a 0%, #000 100%);
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
              <h1 class="glitch" data-text="404 NIE ZNALEZIONO">404 NIE ZNALEZIONO</h1>
              <p>Wpadłeś w cyfrową otchłań.</p>
              <div class="not-found-info">
                {{DETAILS}}
              </div>
              <div class="ascii">
                {\\\\__/}<br>
                (⚆_⚆)<br>
                ( >💾 )<br>
              </div>
              <a href="/">Powrót do bezpieczeństwa</a>
            </div>
          </body>
          <footer style="
            position: absolute;
            bottom: 10px;
            width: 100%;
            text-align: center;
            font-size: 0.85rem;
            font-family: 'Courier New', monospace;
            color: #ff55aa;
            opacity: 0.8;
            letter-spacing: 0.05em;
          ">
            <div style="
              border-top: 1px solid rgba(255, 85, 170, 0.1);
              margin: 0 auto;
              width: 60%;
              padding-top: 0.5rem;
              animation: glowFooter 2s ease-in-out infinite alternate;
            ">
              &copy; <span class="footer-glitch" data-text="2025 Sergiusz">2025 Sergiusz — Coded in shadows</span>
            </div>

            <style>
              .footer-glitch {
                position: relative;
                display: inline-block;
                color: #ff55aa;
              }

              .footer-glitch::before,
              .footer-glitch::after {
                content: attr(data-text);
                position: absolute;
                left: 0;
                width: 100%;
                overflow: hidden;
              }

              .footer-glitch::before {
                color: #00ffe7;
                clip: rect(0, 0, 0, 0);
                animation: glitchFooterTop 2.5s infinite linear alternate-reverse;
              }

              .footer-glitch::after {
                color: #ff00c8;
                clip: rect(0, 0, 0, 0);
                animation: glitchFooterBottom 2.5s infinite linear alternate-reverse;
              }

              @keyframes glitchFooterTop {
                0% {
                  clip: rect(0, 900px, 0, 0);
                }
                100% {
                  clip: rect(0, 900px, 10px, 0);
                  transform: translate(-1px, -1px);
                }
              }

              @keyframes glitchFooterBottom {
                0% {
                  clip: rect(0, 900px, 0, 0);
                }
                100% {
                  clip: rect(12px, 900px, 22px, 0);
                  transform: translate(1px, 1px);
                }
              }

              @keyframes glowFooter {
                from {
                  text-shadow: 0 0 2px #ff55aa, 0 0 5px #ff55aa44, 0 0 10px #ff55aa22;
                }
                to {
                  text-shadow: 0 0 4px #ff55aa, 0 0 8px #ff55aa88, 0 0 16px #ff55aa33;
                }
              }
            </style>
          </footer>
        </html>
        """
        .replace(
            "{{DETAILS}}",
            details.isEmpty()
                ? "<p><i>Nie podano żadnych szczegółów.</i></p>"
                : "<p><i>Ścieżka: " + XSS_FILTER.matcher(details).replaceAll("") + "</i></p>");
  }
}
