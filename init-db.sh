#!/bin/bash

# Simple script to set up profile_db database and profile_dev_user
set -e

echo "Creating profile_db database and profile_dev_user..."
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
    -- Create database
    CREATE DATABASE profile_db;
    
    -- Create user with explicit password
    CREATE USER profile_dev_user WITH PASSWORD 'profile_dev_password';
    
    -- Grant privileges
    GRANT ALL PRIVILEGES ON DATABASE profile_db TO profile_dev_user;
    
    -- Connect to the database and set up schema permissions
    \c profile_db
    
    -- Grant schema privileges
    GRANT ALL PRIVILEGES ON SCHEMA public TO profile_dev_user;
EOSQL

echo "Database setup completed successfully." 