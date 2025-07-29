# Honey (*miodek*)

## Overview

Flag quiz game, featuring user authentication, real-time leaderboards.

## Architecture

**Frontend**

- [TypeScript](https://www.typescriptlang.org/) - Type-safe development
- [Next.js](https://nextjs.org/) - React framework with SSR capabilities
- [React](https://react.dev/) - Component-based UI architecture
- [Tailwind CSS](https://tailwindcss.com/) - Utility-first styling framework

**Backend**

- [Java](https://www.java.com/) - Enterprise-grade runtime environment
- [Jackson](https://github.com/FasterXML/jackson-databind) - JSON processing library
- [Javalin](https://javalin.io/) - Lightweight web framework (why would sb choose to use Spring?)

## Project Structure

```
├── honey-frontend/
└── honey-backend/
```

## Key Features

- **Authentication System** - Login/registration workflow
- **Real-time Leaderboards** - Live ranking updates and competition tracking
- **Responsive Design** - Cross-device compatibility with Tailwind CSS
- **Single JAR Deployment** - Self-contained application requiring no external web server

## Deployment

Build production-ready JAR:

```bash
./gradlew shadowJar
```

Development server:

```bash
# Run HoneyLauncher class from IDE
# Server available at localhost:80
```

## Technical Highlights

- No external dependencies like Nginx required
- Well-structured code :)

---
