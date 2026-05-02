# FallControl

FallControl is an Android app for real-time fall detection using accelerometer and microphone data. It monitors possible fall events in the background, stores local fall history, and sends alert notifications when suspicious movement or sound is detected.

This is an older personal Android project that I revisited and refined for portfolio presentation. As part of the update, I moved fall monitoring into a foreground service, added background fall alert notifications, improved state restoration, and tuned the detection flow for a clearer demo experience.

## Demo

<img src="assets/demo/demo.gif" width="300" alt="FallControl demo">

> Demo note: the current configuration is tuned for presentation, so an alert can be triggered by a sharp phone movement or a loud sound instead of requiring a real fall.

## Screenshots

<p>
  <img src="assets/screenshots/home.jpg" width="220" alt="Home screen">
  <img src="assets/screenshots/home_active.jpg" width="220" alt="Active monitoring screen">
  <img src="assets/screenshots/history.jpg" width="220" alt="Fall history screen">
  <img src="assets/screenshots/notifications.jpg" width="220" alt="Fall notification screen">
</p>

## Features

- Real-time fall detection using accelerometer data
- Sound level monitoring through microphone input
- Foreground service for background monitoring
- Alert notifications for possible fall events
- Local fall history stored with Room
- Timer-based monitoring mode
- Monitoring restoration after device reboot
- Google Play Billing integration for unlocking the full mode

## Tech Stack

- Kotlin
- Android SDK
- Coroutines and Flow
- Room
- Koin
- ViewBinding
- Navigation Component
- Foreground Service
- Google Play Billing

## Implementation Highlights

- The accelerometer detector calculates movement intensity from the combined X/Y/Z vector and compares it against a fall threshold.
- The microphone detector reads audio amplitude through `MediaRecorder`, converts it into a normalized sound level, and can trigger a fall event on loud impact-like sounds.
- The app uses Room as a source of truth for monitoring state, allowing the UI and background service to stay synchronized.
- Fall events are debounced to avoid duplicate alerts when multiple sensors detect the same incident.
- A boot receiver restores active monitoring after device restart if control mode was enabled.

## Project Status

This project was originally built as a personal Android experiment and later improved for portfolio use. The main focus is demonstrating Android sensor handling, background services, local persistence, notifications, and coroutine-based event synchronization.
