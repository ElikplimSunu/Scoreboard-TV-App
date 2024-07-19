# Sports Live Scores TV App

Welcome to the Sports Live Scores TV App, an advanced Android TV application crafted using Kotlin and Jetpack Compose. This app delivers real-time scores, historical match data, and upcoming match schedules, along with match headlines tailored to your preferred sports and leagues.

## Features

- **Live Scores:** Stay updated with real-time scores of ongoing matches.
- **Past Matches:** Review detailed information of previously played matches.
- **Upcoming Matches:** Explore schedules of future matches.
- **Match Headlines:** Get the latest news and headlines for your chosen sports and leagues.
- **Customization:** Select and personalize your preferred sports and leagues.

## Screenshots

![Live Scores](path/to/live_scores_screenshot.png)
![Match Details](path/to/match_details_screenshot.png)
![Upcoming Matches](path/to/upcoming_matches_screenshot.png)

## Installation

To set up the project locally, follow these steps:

1. **Clone the repository:**
    ```sh
    git clone https://github.com/ElikplimSunu/Scoreboard-TV-App.git
    ```
2. **Open the project:** Launch Android Studio and open the cloned project.
3. **Build and run:** Deploy the app on an Android TV device or emulator.

## Usage

1. **Customize Preferences:**
   - Open the project files and navigate to com/sunueric/espnscoreboardapp/data/model/CallDetails.kt.
   - In the list of CallDetails objects add your preferred sport and league slug. eg. `sport = "soccer"`, `leagueSlug = "uefa.europa_qual"`
   - Launch build and run the app.
3. **Navigate Through the App:** View live scores, past match details, and upcoming schedules (these are autoscrolling).
4. **Stay Informed:** Read the latest headlines and news for your selected sports.

## Some Sports with Their Various League Slugs

1. **Soccer**
   - **UEFA Competitions**
     - `uefa.champions` - UEFA Champions League
     - `uefa.champions_qual` - UEFA Champions League Qualifying
     - `uefa.europa` - UEFA Europa League
     - `uefa.europa_qual` - UEFA Europa League Qualifying
     - `uefa.europa.conf` - UEFA Conference League
   - **Domestic Leagues**
     - `esp.1` - Spanish La Liga
     - `eu.1` - English Premier League
     - `ger.1` - German Bundesliga
     - `fra.1` - French Ligue 1
     - `eng.2` - English League Championship
     - `por.1` - Portuguese Primeira Liga
     - `ned.1` - Dutch Eredivisie
   - **Other Competitions**
     - `club.friendly` - Club Friendly
     - ...

## Technologies Used

- **Kotlin:** The primary programming language for Android development.
- **Jetpack Compose:** Modern UI toolkit for building native Android interfaces.
- **Retrofit:** Facilitates network requests and data retrieval.

## Contribution
Contributions are welcome! Please follow these steps:

Fork the repository.
Create a new branch (`git checkout -b feature-branch`).
Make your changes and commit them (`git commit -m 'Add new feature'`).
Push to the branch (`git push origin feature-branch`).
Create a pull request.

## License
This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

## Contact
For any inquiries or feedback, please reach out at sunuerico@gmail.com.
