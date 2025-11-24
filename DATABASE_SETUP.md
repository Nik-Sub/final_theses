# Database Development Setup

This project uses a containerized MySQL database for local development while running the server application locally.

## Quick Start

### 1. Setup local.properties
```bash
cp local.properties.example local.properties
```

### 2. Start the database
```bash
./gradlew startDatabase
```

### 3. Wait for database to be ready (check status)
```bash
./gradlew databaseStatus
```

### 4. Run the server
```bash
./gradlew :server:run
```

## Available Database Commands

- `./gradlew startDatabase` - Start MySQL container
- `./gradlew stopDatabase` - Stop MySQL container
- `./gradlew resetDatabase` - Stop and remove MySQL container and data
- `./gradlew databaseStatus` - Check if database is running
- `./gradlew databaseLogs` - View database logs

## Database Configuration

- **Host**: localhost:3306
- **Database**: iwbi_db
- **User**: iwbi_user
- **Password**: iwbi_password
- **Root Password**: rootpassword

## Troubleshooting

### Database won't start
```bash
# Check if port 3306 is already in use
lsof -i :3306

# Check Docker logs
./gradlew databaseLogs
```

### Connection refused
- Make sure the database is running: `./gradlew databaseStatus`
- Wait for health check to pass (check logs: `./gradlew databaseLogs`)
- Verify local.properties has correct connection details

### Reset everything
```bash
./gradlew resetDatabase
./gradlew startDatabase
```
