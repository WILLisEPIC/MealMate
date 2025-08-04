# 🍽️ MealMate Android Application (Version 2.0)

This is the new version (v2.0) which has many updates.

--The followings updates were made

## 1. Firebase Authentication

The original version relied on SQLite for local user authentication. In v2.0, I have 
integrated Firebase Authentication for more secure and cloud-based user login and registration.
This enhances data reliability, reduces risk of data loss, and improves scalability.

## 2. Performance Optimization

Data inserting and fetching were performed using proper methods to boost the system performace 
and redundant or outdated code were replaced with more suitable installations to provide
faster system.

## 3. UI/UX enhancement

To provide a better user experience, the interfaces have been redesigned with cleaner layouts, 
vibrant visuals, and more intuitive components.


- **Java** – Core development language
- **Android Studio** – Integrated development environment (IDE)
- **XML** – Used for designing the user interface
- **SQLite** – Local database for storing meals, recipes, and some user data
- **Firebase** - Implemented for user management and authentication
---

# User Manual

To try the MealMate app, first create a Firebase project and register an Android app using my package name 
com.example.waiyan_mealmate. During registration, add the SHA-1 key and SHA-256 key of my app (you can 
get it by running ./gradlew signingReport in the terminal) to the Firebase project. After registration, 
download the google-services.json file in the Firebase project and place it in the app/ directory of my project.
In strings.xml, replace the web_client_id with the OAuth 2.0 Web client ID found in your Firebase project under
Authentication > Sign-in method > Google. Enable Email/Password and Google sign-in methods in Firebase Authentication. 
Finally, connect a device or emulator and run the app.

Feel free to clone the repository, explore the code, and contribute to the project!
