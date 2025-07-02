# Flat Finance

A modern, reliable, and beautiful Android finance tracking app for college students who live together in a shared flat. The app helps split common expenses, track personal finances, set bill reminders, and generate monthly/yearly reports.

## Features

### 👥 User Management
- Sign up / Log in (via email, Google)
- Create or join a "Flat Group" using a flat code
- Each flat supports multiple users (start with 4, scalable to N)
- User profiles with name, email, and avatar

### 🏠 Flat Management Dashboard
- Overview of all common expenses: rent, electricity, Wi-Fi, etc.
- One-time setup for monthly rent, electricity cap, internet bill, etc.
- Add one-time deposit and auto-split

### 🔁 Expense Splitting System
- Add a common expense → auto split equally or custom ratio
- Support for various expense types (rent, electricity, Wi-Fi, groceries, maintenance)
- Auto-update balances among flatmates
- See who owes what, with breakdowns
- Real-time sync and change history

### 👤 Personal Finance Tracker
- Add personal expenses (food, travel, books, subscriptions)
- Categorize personal spending
- Set daily/monthly budgets
- Visual charts and insights

### 🧠 Smart Reminders
- Bill due reminders (rent, electricity, Wi-Fi)
- Recurring reminders via push notifications
- Friendly language ("Rent due in 2 days – don't ghost your landlord 😅")

### 📊 Analytics & Reports
- Monthly and Yearly Report with total spent (personal + shared)
- Category-wise breakdown
- Graphs and insights
- Downloadable as PDF or shareable snapshot
- Toggle: view "Group Expense", "My Expenses", or "Both"

### 🎨 UI/UX Design
- Animated onboarding flow (Lottie animations)
- Dashboard with animated graphs (bar/pie charts)
- Minimalist but colorful theme
- Dark mode support
- Smooth micro-interactions

## Architecture

The app follows the MVVM (Model-View-ViewModel) architecture pattern with Clean Architecture principles:

- **UI Layer**: Jetpack Compose for modern, declarative UI
- **Domain Layer**: Use cases and business logic
- **Data Layer**: Repositories, data sources, and models

## Tech Stack

- **UI**: Jetpack Compose, Material 3 Design
- **Architecture**: MVVM, Clean Architecture
- **Dependency Injection**: Hilt
- **Local Database**: Room
- **Remote Database**: Firebase Firestore
- **Authentication**: Firebase Auth
- **Analytics**: Firebase Analytics
- **Notifications**: Firebase Cloud Messaging
- **Charts**: MPAndroidChart, YCharts
- **Image Loading**: Coil
- **PDF Generation**: iTextG
- **Animations**: Lottie
- **Testing**: JUnit, Mockk, Compose UI Testing

## Getting Started

### Prerequisites
- Android Studio Hedgehog or newer
- JDK 17 or newer
- Android SDK 24+
- Firebase project with Authentication, Firestore, and Cloud Messaging enabled

### Setup
1. Clone the repository
2. Open the project in Android Studio
3. Add your `google-services.json` file to the app directory
4. Sync Gradle files
5. Run the app on an emulator or physical device

### Testing
1. Run unit tests: `./gradlew test`
2. Run instrumented tests: `./gradlew connectedAndroidTest`

### Building
1. Debug build: `./gradlew assembleDebug`
2. Release build: `./gradlew assembleRelease`

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- Material Design 3 for design inspiration
- Firebase for backend services
- Jetpack Compose for modern UI development
- iTextG for PDF generation
- MPAndroidChart and YCharts for data visualization
- Lottie for beautiful animations

## Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/flatfinance/app/
│   │   │   ├── data/                  # Data layer
│   │   │   │   ├── local/             # Room database and DAOs
│   │   │   │   ├── models/            # Data models
│   │   │   │   ├── remote/            # Firebase services
│   │   │   │   └── repositories/      # Repository implementations
│   │   │   ├── di/                    # Dependency injection modules
│   │   │   ├── services/              # Background services
│   │   │   ├── ui/                    # UI layer
│   │   │   │   ├── components/        # Reusable UI components
│   │   │   │   ├── navigation/        # Navigation components
│   │   │   │   ├── screens/           # App screens
│   │   │   │   └── theme/             # App theme
│   │   │   └── utils/                 # Utility classes
│   │   └── res/                       # Resources
│   ├── test/                          # Unit tests
│   └── androidTest/                   # Instrumented tests
└── build.gradle                       # App-level build file
```