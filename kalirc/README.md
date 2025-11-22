# Kalirc

An Android IRC client for IRCCloud.

## Plan

1.  **Set up the Android project.** Configure the `build.gradle.kts` and `settings.gradle.kts` files to support Android 36, Jetpack Compose, and other necessary dependencies. I will also create the basic directory structure for the app, including a single application module.
2.  **Create a README.md file.** Create a README.md file and add the plan and design to it.
3.  **Implement the login screen.** Create a login screen with fields for username and password, a "Remember me" checkbox, and a login button. I will use Android's `Keystore` system to securely store the user's credentials. I will also implement the logic to authenticate with the IRCCloud API.
4.  **Implement the channel list screen.** After a successful login, the app will display a list of the user's IRC channels. The list will be sorted by recent activity. I will use a Jetpack Compose `LazyColumn` to display the list of channels.
5.  **Implement the message view screen.** When a user selects a channel, the app will display the recent messages from that channel. The messages will be displayed in the format "username: message" with the timestamp on the right. Join, part, and quit messages will be displayed in italics. I will use a Jetpack Compose `LazyColumn` to display the messages.
6.  **Implement image display and caching.** I will use a library like Coil to display images in the message view. I will configure the library to cache images for 24 hours. If an image fails to load, I will display a placeholder image.
7.  **Implement text and data caching.** I will use a local database like Room to cache the channel list and messages. When the app is opened, it will first display the cached content and then fetch the latest data from the IRCCloud API in the background.
8.  **Implement offline support.** If the app is opened without an internet connection, it will display the cached content with a "disconnected" status indicator.
9.  **Complete pre commit steps.** I will complete pre commit steps to make sure proper testing, verifications, reviews and reflections are done.
10. **Submit the change.** Once all tests pass, I will submit the change with a descriptive commit message.

## Design

*   **Platform:** Android 36
*   **UI:** Jetpack Compose
*   **Styling:** Simple, minimal, dark theme
*   **Login:**
    *   Error message on login failure.
    *   "Remember me" checkbox.
*   **Channel List:**
    *   Sorted by recent activity.
*   **Message View:**
    *   Backlog: 50 messages or API default.
    *   Join/part/quit messages in italics.
*   **Images:**
    *   Cache for 24 hours.
    *   Display error on load failure.
*   **Caching:**
    *   Display from cache first, then update in the background.
*   **Offline:**
    *   Display cached content with a "disconnected" status.
