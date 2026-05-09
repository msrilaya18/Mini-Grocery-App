# Mini Grocery Delivery App (Blinkit Style)

## Features
- **Login / OTP:** Smooth mobile verification flow with safely restricted simulated OTP entry.
- **Home Screen:** Dynamic product search across dedicated internal categories (Vegetables, Dairy, Snacks). High-resolution image caching.
- **Cart Screen:** Features local persistence of the entire shopping cart using an offline SQLite approach. Users can dynamically add, decrease quantity or remove cart items.
- **Checkout Flow:** Data capture for delivery addresses and interactive payment options (Online / Cash on Delivery).
- **Success Screen:** Displays auto-generated order tracking ID with intelligent Back-Stack clearing capability to navigate safely back home without retaining history.

## Tech Stack & Bonus Architecture
- **Language:** Kotlin (100% Kotlin as requested)
- **Architecture:** Professional MVVM (Model-View-ViewModel) paired with Clean Architecture principles.
- **State Management:** Reactive Kotlin Coroutines `StateFlow` (instead of LiveData) to prevent sluggishness on the UI thread.
- **Local Database:** Room Database (SQLite abstraction) built exclusively to preserve the internal user cart.
- **UI System:** XML Layouts constructed using Material Components & AndroidX Navigation Components (`NavHostFragment`) for swift fragment transitions.
- **Image Loading:** Glide Image library

## How to run the project
1. Close your currently running emulator or processes.
2. Open this downloaded root folder using **Android Studio** (Koala / Latest version recommended).
3. Ensure you have an active network connection for Gradle dependencies and Glide to fetch product images.
4. Wait untill the bottom status bar reads "Sync successful" or manually select "File -> Sync Project with Gradle Files".
5. Run the configuration (`app`) via an Emulator or Physical testing device.
6. **Important Test Note:** In the login screen, enter **any** valid 10-digit number. When prompted for the OTP validation, securely type the simulated code: `1234`.
