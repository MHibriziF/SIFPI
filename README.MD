# SIFPI - Sistem Informasi Fasilitator Projek IPFO

## Running locally

Below are the steps to run this project locally

### Clone this repository

Clone this repository with `git clone {PROJECT_URL}`. You can view the project URL in Code dropdown at the top of this repository.

### Creating `.env` files

Make sure to create a .env file at the root of this project. Follow the example in `.env.example`, we have already setup the needed configurations there for development purposes. You can adjust it with your preferred environtment if needed.

### With Docker

- Make sure that Docker is installed and is currently running on your device
- Run `docker compose up -d` in your terminal.
- Project should be running and you can do a healtcheck at `http://localhost:8080/actuator/health`

### With Gradle

- Run `./gradlew bootRun` in your terminal.
- Project should be running and you can do a healtcheck at `http://localhost:8080/actuator/health`
