# Honey (*miodek*)

> Modern flag quiz application built with enterprise-grade architecture and full-stack development practices.

## Overview

A production-ready flag quiz game demonstrating modern full-stack development practices, featuring secure user
authentication, real-time leaderboards, and responsive design patterns.

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
├── honey-frontend/     # Client application
└── honey-backend/      # API server and business logic
```

## Key Features

- **User Authentication System** - Secure login/registration workflow
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

- **Self-contained Architecture**: No external dependencies like Nginx required
- **Modern Tech Stack**: Leverages current industry standards and best practices
- **Scalable Architecture**: Modular design supporting future feature expansion
- **Clean Codebase**: Well-structured code following industry best practices

---