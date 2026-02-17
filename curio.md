# Curio

A forms and survey tool for companies that use Slack.

## Overview

Curio lets companies create and distribute surveys via email, with automated Slack bot reminders to drive completion rates. Forms are sent to employees through company email, and a Slack bot follows up with reminders until the survey is completed.

## Core Features

- **Form Builder** - Create surveys with various question types (multiple choice, text, rating scales, etc.)
- **Email Distribution** - Send forms to employees via company email
- **Slack Bot Reminders** - Automatically remind users on Slack to complete pending surveys
- **Response Dashboard** - View and analyze survey results

## How It Works

1. Admin creates a form
2. Admin sends the form to a list of employee emails
3. Employees receive the form link via email
4. The Slack bot sends reminders to employees who haven't completed the form
5. Reminders stop once the employee submits their response

## Tech Stack

- **Language:** Java 21
- **Framework:** Spring Boot 3
- **API:** Spring Web (REST)
- **Database:** PostgreSQL + Spring Data JPA
- **DB Migrations:** Flyway
- **Email:** Spring Mail (SMTP)
- **Slack Bot:** slack-sdk for Java (official Slack SDK)
- **Scheduling:** Spring Scheduler (`@Scheduled`) for reminder jobs
- **Auth:** Spring Security
- **Frontend:** Thymeleaf (server-side rendered form pages)

### Spring Boot Dependencies

Scaffold via [start.spring.io](https://start.spring.io):

- Spring Web
- Spring Data JPA
- PostgreSQL Driver
- Spring Mail
- Spring Security
- Thymeleaf
- Flyway

The Slack SDK is added separately as a Maven/Gradle dependency.
