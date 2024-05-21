# PRIV-OngakuBot
The only discord music bot you will ever need.

Does not require to vote to any bot website nor buy a license for "premium" features.

## Prerequisites
**REQUIRED**
- Java v.11 or higher (to see your version run `java -version`)

**OPTIONAL**
- mysql-server (to be used as an external database)


## How to generate JAR file
- Open the MAVEN terminal and execute the following command: `mvn clean compile assembly:single`
- The JAR file with dependencies just built is placed in the **target** directory


## How to use
- Download the latest release of OngakuBot from GitHub (link)
- Open the terminal
- Run the command `java -jar ongakubot.jar`
- Edit the `config.properties` file that has been created in the same folder of the jar file
- Re-run the command `java -jar ongakubot.jar`

> **Note:** By default, this bot uses [SQLite](https://www.sqlite.org/index.html) as database library. This library basically
> creates a database file where it saves the necessary data of this bot.
> 
> If you want, you can use an external database (even on a different machine) where the bot can save its data. Just edit
> the `DB_TYPE` key to `mysql` and edit the other values such as `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME` and `DB_PASSWORD`
> so that the bot can access the mysql server.


## Open source libraries
Uses the [lavalink-devs/lavaplayer](https://github.com/lavalink-devs/lavaplayer) and the [lavalink-devs/youtube-source](https://github.com/lavalink-devs/youtube-source#) libraries.

## Donate
[PayPal](https://paypal.me/LeoPantani)

*Thank you for your support!* 😁