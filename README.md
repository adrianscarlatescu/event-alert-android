## Getting Started
Event Alert - Android is the client application which uses the endpoints provided by [event-alert-backend](https://github.com/adrianscarlatescu/event-alert-backend).
The technology stack consits of:
* [Java](https://www.oracle.com/java/) - The programming language used to develop the application.
* [Android SDK](https://developer.android.com/about) - The kit that provides all the features for Android development.  
* [Google Maps API](https://developers.google.com/android/reference/com/google/android/gms/maps/package-summary) - The API that provides all the required features for map interaction.
* [Location API](https://developers.google.com/android/reference/com/google/android/gms/location/package-summary) - The API that provides all the required features for location usage.
* [Firebase Cloud Messaging](https://firebase.google.com/docs/cloud-messaging/android/client) - The cross-platform messaging solution to handle notifications.
* [Retrofit](https://square.github.io/retrofit/) - The API used to handle HTTP requests.
* [Room](https://developer.android.com/training/data-storage/room) - The persistence library.
* [JWTDecode](https://github.com/auth0/JWTDecode.Android) - The library that decodes the JWTs.
* [Picasso](https://square.github.io/picasso/) - The library used for accessing server images.
* [Butter Knife](https://jakewharton.github.io/butterknife/) - The library used for binding the views.
* [EventBus](https://github.com/greenrobot/EventBus) - The publish/subscribe library for reactive components.

## Project scope
The purpose of this project is to demonstrate the usage of the endpoints provided by the [server application](https://github.com/adrianscarlatescu/event-alert-backend) and also to provide a unique UI and UX.
By using this application, a user can update his profile, report incidents in his area, check his incidents, search for incidents reported by others and much more.

## Run prerequisites
In order to run the application locally, the following steps must be set:
* A Google Maps API key must be generated and put in [AndroidManifest.xml](https://github.com/adrianscarlatescu/event-alert-android/blob/master/app/src/main/AndroidManifest.xml#L25).
* The server IP must be set in [network_security_config.xml](https://github.com/adrianscarlatescu/event-alert-android/blob/master/app/src/main/res/xml/network_security_config.xml#L4) and [Constants.java](https://github.com/adrianscarlatescu/event-alert-android/blob/master/app/src/main/java/com/as/eventalertandroid/defaults/Constants.java#L5).
* Push notifications feature:
    * To skip this feature, nothing has to be done.
    * In order to receive push notifications, the application must be bound to the Firebase project. 
    `google-services.json` must be downloaded from Firebase and put in the application's root directory (`/app`).

## Video demonstration
The YouTube link is available [here](https://youtu.be/wVzswI4R9k8).

## Screenshots
<div align="center">
<img src="https://github.com/adrianscarlatescu/event-alert-android/blob/master/app/src/main/res/drawable/screenshot_01.png" width="280">
<img src="https://github.com/adrianscarlatescu/event-alert-android/blob/master/app/src/main/res/drawable/screenshot_02.png" width="280">
<img src="https://github.com/adrianscarlatescu/event-alert-android/blob/master/app/src/main/res/drawable/screenshot_03.png" width="280">
<img src="https://github.com/adrianscarlatescu/event-alert-android/blob/master/app/src/main/res/drawable/screenshot_04.png" width="280">
<img src="https://github.com/adrianscarlatescu/event-alert-android/blob/master/app/src/main/res/drawable/screenshot_05.png" width="280">
<img src="https://github.com/adrianscarlatescu/event-alert-android/blob/master/app/src/main/res/drawable/screenshot_06.png" width="280">
<img src="https://github.com/adrianscarlatescu/event-alert-android/blob/master/app/src/main/res/drawable/screenshot_07.png" width="280">
<img src="https://github.com/adrianscarlatescu/event-alert-android/blob/master/app/src/main/res/drawable/screenshot_08.png" width="280">
<img src="https://github.com/adrianscarlatescu/event-alert-android/blob/master/app/src/main/res/drawable/screenshot_09.png" width="280">
<img src="https://github.com/adrianscarlatescu/event-alert-android/blob/master/app/src/main/res/drawable/screenshot_10.png" width="280">
</div>