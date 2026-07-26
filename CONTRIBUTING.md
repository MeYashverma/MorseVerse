# Contributing to MorseVerse

Thank you for your interest in contributing to MorseVerse! This document provides guidelines and instructions for contributing.

## Code of Conduct

Please be respectful and inclusive in all interactions.

## How to Contribute

### Reporting Issues
- Use GitHub Issues
- Include steps to reproduce
- Include device/OS information
- Include screenshots if applicable

### Suggesting Features
- Open a GitHub Issue with the "enhancement" label
- Describe the feature and its use case
- Explain why it would be valuable

### Submitting Code

1. **Fork the repository**
2. **Create a feature branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```
3. **Make your changes**
4. **Follow the coding style**
   - Kotlin coding conventions
   - Material Design guidelines
   - Clean Architecture principles
5. **Write tests**
   - Unit tests for business logic
   - UI tests for Compose screens
6. **Commit your changes**
   ```bash
   git commit -m "feat: add your feature description"
   ```
7. **Push to your fork**
   ```bash
   git push origin feature/your-feature-name
   ```
8. **Open a Pull Request**

## Development Setup

1. Clone the repository
2. Open in Android Studio
3. Sync Gradle
4. Run on device or emulator

## Code Style

### Kotlin
- Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use meaningful variable/function names
- Add KDoc comments for public APIs
- Use extension functions where appropriate

### Compose
- Use Material 3 components
- Follow single responsibility principle
- Extract reusable composables
- Use remember for state management
- Use state hoisting for events

### Architecture
- Follow Clean Architecture principles
- Keep modules independent
- Use dependency injection
- Handle errors appropriately

## Testing

### Unit Tests
- Test use cases
- Test ViewModels
- Test repositories
- Test utilities

### UI Tests
- Test screen rendering
- Test user interactions
- Test navigation

## Commit Messages

Follow conventional commits:
- `feat:` - New feature
- `fix:` - Bug fix
- `docs:` - Documentation
- `style:` - Code style
- `refactor:` - Code refactoring
- `test:` - Tests
- `chore:` - Build/tooling

## Pull Request Process

1. Update documentation if needed
2. Add tests for new features
3. Ensure all tests pass
4. Request review from maintainers
5. Address feedback promptly

## Questions?

Open a GitHub Issue for any questions about contributing.

Thank you for contributing! 🎉
