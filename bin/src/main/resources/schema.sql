-- Create Database (if needed)
CREATE DATABASE IF NOT EXISTS task_management_db;
USE task_management_db;

-- Clear existing tables for a clean start
DROP TABLE IF EXISTS tasks;
DROP TABLE IF EXISTS users;

-- Table for Users (Authentication)
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_by VARCHAR(255),
    created_on DATETIME DEFAULT CURRENT_TIMESTAMP,
    modified_by VARCHAR(255),
    modified_on DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    role VARCHAR(50) DEFAULT 'USER'
);

-- Table for Tasks
CREATE TABLE tasks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'Open',
    priority VARCHAR(50) DEFAULT 'MEDIUM',
    due_date DATETIME,
    created_by VARCHAR(255),
    created_on DATETIME DEFAULT CURRENT_TIMESTAMP,
    modified_by VARCHAR(255),
    modified_on DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
