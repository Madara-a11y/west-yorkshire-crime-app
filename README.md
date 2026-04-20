# West Yorkshire Crime App (Android)

An Android application built in Java that visualises crime data across West Yorkshire, with real-time data handling, role-based access control, and Google Maps integration.

## Features
- User authentication with Firebase
- Role-based access (admin / standard user)
- Real-time crime data storage and retrieval
- Interactive map visualisation using Google Maps
- Search and filtering functionality
- Responsive mobile UI

## Project Overview
This project was developed as part of a university coursework to demonstrate full-stack mobile development using modern tools and services.

The application integrates Firebase for authentication and database management, and Google Maps for visualising over 1,600+ crime records, improving accessibility and user interaction with location-based data.

## Technologies Used
- Java
- Android Studio
- Firebase Authentication
- Firebase Realtime Database
- Google Maps API
- Gradle

## Repository Structure
- `app/` – main Android application code
- `gradle/` – build configuration
- `build.gradle.kts` – project build file
- `settings.gradle.kts` – project configuration

## Setup Instructions

To run this project locally:

1. Clone the repository
2. Open in Android Studio
3. Create a file named `local.properties` in the root directory
4. Add your Google Maps API key:

MAPS_API_KEY=your_api_key_here

5. Sync the project and run the application

## Notes
Sensitive files such as API keys and local configurations are excluded from this repository for security reasons.
