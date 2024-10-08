# Android Showcase App

Concise and comprehensive project that highlights key aspects of Android development for showcasing my skills.

## The Project Overview

<!-- TODO insert screenshots and video here --->

This project includes two screens that cover the following key functionalities:

* Fetching and Displaying Data
  * Utilize the CoinCap REST API to fetch a list of crypto coins.
  * Display a list of the top 100 best or worst coins on the first screen of the app.
* Navigation and Detailed Information
  * Enable users to navigate to a second screen by clicking on a coin list item.
  * On the second screen, display more detailed information about the selected coin.

## This project shows

* MVI Architecture, Clean Architecture
* a well-organized folders structure with **data, di, ui** on the top level
* asynchronous communication with **e.g., Kotlin Coroutines, Flows, Retrofit**
* unit tests to validate the functionality of critical components like **ViewModel, UseCase, Repository**
* dependency injection for managing component dependencies with **Hilt**
* a full ui in *Jetpack compose*
* how to store fetched data in a database with **Room**

## The implemented features are

* Displaying a list of crypto coins
* Fetching data from the internet
* Store fetched data in a database for offline app use
* Show skeleton loader when data is loaded
* Pull to refresh the list data with compose animation
* Switch coins in list based on their performance
* Implementing navigation between screens
* Using and understanding the MVI architecture
* Organizing files in a clear and efficient manner
* Employing the right libraries for asynchronous communication
* Writing unit tests for robust code
* Implementing Dependency Injection for better code maintainability