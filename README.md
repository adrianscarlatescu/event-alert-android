## Getting Started
Event Alert - Android is the client application which uses the endpoints provided by [event-alert-backend](https://github.com/adrianscarlatescu/event-alert-backend).<br/>
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
* Minimum software versions:
  * JDK 11
* The phone on which the app is deployed must have minimum Android 13 ([API level 33](https://developer.android.com/about/versions/13))
* A Google Maps API key must be generated and put in [AndroidManifest.xml](https://github.com/adrianscarlatescu/event-alert-android/blob/master/app/src/main/AndroidManifest.xml#L29).
* The server IP must be set in [network_security_config.xml](https://github.com/adrianscarlatescu/event-alert-android/blob/master/app/src/main/res/xml/network_security_config.xml#L4) and [Constants.java](https://github.com/adrianscarlatescu/event-alert-android/blob/master/app/src/main/java/com/as/eventalertandroid/defaults/Constants.java#L5).
* Push notifications feature (to skip this feature, nothing has to be done):
    * In order to receive push notifications, the application must be bound to the Firebase project.<br/> 
    `google-services.json` must be downloaded from Firebase and put in the application's root directory (`/app`).
    * Enable `google-services` dependency in [build.gradle](https://github.com/adrianscarlatescu/event-alert-android/blob/master/build.gradle#L10).
    * Enable `google-services` plugin in [app/build.gradle](https://github.com/adrianscarlatescu/event-alert-android/blob/master/app/build.gradle#L2).

## Main features
Register, login, view/update profile
<table>
 <tr>
  <td><img src="app/readme/capture_auth_login.jpg" width="250"></td>
  <td><img src="app/readme/capture_auth_register.jpg" width="250"></td>
  <td><img src="app/readme/capture_profile.jpg" width="250"></td>
 </tr>
</table>

Filter and sort events by certain criteria
<table>
 <tr>
  <td><img src="app/readme/capture_filter.jpg" width="250"></td>
  <td><img src="app/readme/capture_order.jpg" width="250"></td>
 </tr>
</table>

Select types, severities, statuses
<table>
 <tr>
  <td><img src="app/readme/capture_select_types.jpg" width="250"></td>
  <td><img src="app/readme/capture_select_severities.jpg" width="250"></td>
  <td><img src="app/readme/capture_select_statuses.jpg" width="250"></td>
 </tr>
</table>

Filter result (map and list mode)
<table>
 <tr>
  <td><img src="app/readme/capture_map.jpg" width="250"></td>
  <td><img src="app/readme/capture_list.jpg" width="250"></td>
 </tr>
</table>

View event marker info and check event details
<table>
 <tr>
  <td><img src="app/readme/capture_map_marker.jpg" width="250"></td>
  <td><img src="app/readme/capture_event_details.jpg" width="250"></td>
 </tr>
</table>

View reported events and report new event
<table>
 <tr>
  <td><img src="app/readme/capture_reporter.jpg" width="250"></td>
  <td><img src="app/readme/capture_report_new_event.jpg" width="250"></td>
 </tr>
</table>

View received notifications and view/update notifications settings
<table>
 <tr>
  <td><img src="app/readme/capture_notifications.jpg" width="250"></td>
  <td><img src="app/readme/capture_notifications_settings.jpg" width="250"></td>
 </tr>
</table>

Form validation across all pages
<table>
 <tr>
  <td><img src="app/readme/capture_validation_filter.jpg" width="250"></td>
  <td><img src="app/readme/capture_validation_report_new_event.jpg" width="250"></td>
  <td><img src="app/readme/capture_validation_profile.jpg" width="250"></td>
 </tr>
</table>
