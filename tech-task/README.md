# Fast and Reckless Bank - Technical Task

A banking application with a Spring Boot backend and React frontend.

## Prerequisites

- **Java 21** or higher
- **Node.js** (v16 or higher) and **npm**
- **Gradle** (included via Gradle wrapper)

## Project Structure

```
tech-task/
├── src/                    # Spring Boot backend source
│   ├── main/
│   └── test/
├── frontend/              # React frontend application
├── build.gradle          # Gradle build configuration
└── README.md            # This file
```

## Backend Setup (Spring Boot)

The backend is a Spring Boot application that provides REST APIs for banking operations.

### Running the Backend

From the `tech-task` directory:

```bash
# Using Gradle wrapper (recommended)
./gradlew bootRun

# Or on Windows
gradlew.bat bootRun
```

The backend will start on **http://localhost:8080**

### Running Backend Tests

```bash
./gradlew test
```

### Building the Backend

```bash
./gradlew build
```

This creates a JAR file in `build/libs/` that you can run with:
```bash
java -jar build/libs/demo-0.0.1-SNAPSHOT.jar
```

## Frontend Setup (React)

The frontend is a React application that connects to the backend API.

### Installing Frontend Dependencies

First, navigate to the frontend directory and install dependencies:

```bash
cd frontend
npm install
```

### Running the Frontend

After installing dependencies:

```bash
npm start
```

The frontend will start on **http://localhost:3000** and will automatically proxy API requests to the backend on port 8080.

### Building the Frontend for Production

```bash
npm run build
```

## Running the Full Application

### Option 1: Using Convenience Scripts (Recommended)

1. **Start the backend** (in one terminal):
   ```bash
   cd tech-task
   ./start-backend.sh
   ```

2. **Start the frontend** (in another terminal):
   ```bash
   cd tech-task
   ./start-frontend.sh
   ```

3. Open your browser to **http://localhost:3000**

### Option 2: Manual Commands

1. **Start the backend** (in one terminal):
   ```bash
   cd tech-task
   ./gradlew bootRun
   ```

2. **Start the frontend** (in another terminal):
   ```bash
   cd tech-task/frontend
   npm install  # Only needed first time
   npm start
   ```

3. Open your browser to **http://localhost:3000**

## API Endpoints

The backend provides the following REST endpoints:

- `POST /api/accounts` - Create a new account
- `GET /api/accounts/{id}/balance` - Get account balance
- `POST /api/accounts/{id}/deposit` - Deposit money
- `POST /api/accounts/{id}/withdraw` - Withdraw money
- `POST /api/accounts/transfer` - Transfer between accounts
- `GET /api/accounts/{id}/outgoing-transfers` - Get transfer history

## Technology Stack

### Backend
- Spring Boot 3.5.7
- Java 21
- MapStruct (for object mapping)
- Lombok (to reduce boilerplate)
- In-memory data storage

### Frontend
- React 19.2.0
- React Scripts 5.0.1
- Testing Library

## Troubleshooting

### Port Already in Use
If port 8080 or 3000 is already in use, you can change:
- **Backend port**: Edit `src/main/resources/application.properties` and add `server.port=8081`
- **Frontend port**: Set environment variable `PORT=3001` before running `npm start`

### Frontend Can't Connect to Backend
Make sure the backend is running on port 8080 before starting the frontend.

