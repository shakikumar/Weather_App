# Implementation Plan - Task 06: Process the JSON Response

Implement the logic to extract and process weather data from the OpenWeatherMap JSON response, ensuring all required fields are correctly mapped and handled safely.

## User Review Required

> [!NOTE]
> The existing model classes already correctly map the requested fields. This task will focus on the extraction logic within `HomeFragment` and updating the UI with real data instead of mock values.

## Proposed Changes

### Logic Implementation

#### [MODIFY] [HomeFragment.kt](file:///Users/shagiththikananthakumar/Documents/GitHub/WEATHERAPP/app/src/main/java/com/example/weatherapp/HomeFragment.kt)
- Update `onResponse` in `fetchWeatherData`:
    - Safely extract `name`, `main.temp`, `weather[0].description`, `main.humidity`, and `wind.speed` from the `WeatherResponse` object.
    - Implement null/missing value checks (e.g., using `?.` and `?:`).
    - Format extracted values for display:
        - Temperature: Append "°" (e.g., "25°").
        - Humidity: Append "%" (e.g., "60%").
        - Wind Speed: Append " km/h" (e.g., "15 km/h").
        - Condition: Capitalize the first letter of the description.
    - Update the corresponding TextViews in the UI:
        - `tvCityResult`
        - `tvTemp`
        - `tvCondition`
        - `tvHumidity`
        - `tvWind`
    - Log the extracted data for debugging (excluding the API key).

## Verification Plan

### Automated Tests
- Run `./gradlew assembleDebug` to ensure no compilation errors.

### Manual Verification
1. **Valid City Search**:
    - Search for a known city (e.g., "London").
    - Verify that all TextViews in the weather card update with real data from the API.
    - Confirm the values match the expected format (e.g., "22°", "Rain", "70%", "10 km/h").
2. **Data Integrity**:
    - Verify that the app handles missing fields in the JSON response without crashing (though unlikely for these core fields in OpenWeatherMap).
3. **UI Consistency**:
    - Ensure the layout remains exactly as designed while displaying real data.
