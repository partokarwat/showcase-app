# Android Showcase App

Concise and comprehensive project that highlights key aspects of Android development for showcasing my skills.

## The Project Overview

| List Screen |  Detail Screen |
| --- |  --- | 
| <img src="https://github.com/user-attachments/assets/875bb5f8-3851-45ec-9464-131dc01d2d1d" width="200"> |  <img src="https://github.com/user-attachments/assets/7103f2e0-225a-4677-9568-979bad86cfda" width="200"> | 

| Skeleton Loader | Switch Coins | Pull to refresh | Offline mode | Orientation change |
| --- |  --- |  --- | --- | --- | 
| <img src="https://github.com/user-attachments/assets/56961511-e576-45cc-9248-48c4be162612" width="200"> |  <img src="https://github.com/user-attachments/assets/ceef7a58-6b93-45e8-9a18-1f6e78516b0c" width="200"> |  <img src="https://github.com/user-attachments/assets/8f678d94-8cf6-49f9-8f99-61e4092a08b7" width="200"> | <img src="https://github.com/user-attachments/assets/9a2fcc1f-8ee1-44c8-80de-af9f58528585" width="200">  | --- | 

This project includes two screens that cover the following key functionalities:

* Fetching and Displaying Data
  * Utilize the CoinCap REST API to fetch a list of crypto coins.
  * Display a list of the top 100 best or worst coins on the first screen of the app.
* Navigation and Detailed Information
  * Enable users to navigate to a second screen by clicking on a coin list item.
  * On the second screen, display more detailed information about the selected coin.

## This project shows

* MVVM and Clean Code architecture with presentation layer of **Compose UI** and **ViewModel**, domain layer of **UseCases** and data layer of **Repository** and **Datasource**
* a well-organized folders structure with **data, di, ui, usecases** on the top level
* asynchronous communication with **e.g., Kotlin Coroutines, Flows, Retrofit**
* **unit tests** to validate the functionality of critical components like ViewModel, UseCase, Repository
* dependency injection for managing component dependencies with **Hilt**
* a full UI in **Jetpack compose**
* the storage of fetched data in a database with **Room** and in the **DataStore**

## The implemented features are

* Displaying a list of crypto coins
* Fetching data from the internet
* Store fetched data in a database for offline app use
* Show skeleton loader when data is loaded
* Pull to refresh the list data with compose animation
* Switch coins in list based on their performance
* Implementing navigation between screens
* Using and understanding the MVI and Clean Code architecture
* Organizing files in a clear and efficient manner
* Employing the right libraries for asynchronous communication
* Writing unit tests for robust code
* Implementing Dependency Injection for better code maintainability
