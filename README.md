# Online Marketplace Backend API

A comprehensive RESTful API for an online marketplace that allows users to buy and sell products, manage their inventory, and process orders.

## Features

- User authentication and authorization (JWT)
- Role-based access control (Admin, Seller, Shopper)
- Email verification and notifications
- Store management
- Product management with categories
- Order processing with async queue
- Product reviews and ratings
- Swagger API documentation

## Tech Stack

- Java 17
- Spring Boot 3.2.3
- PostgreSQL
- RabbitMQ
- JWT Authentication
- MapStruct
- Lombok
- Swagger/OpenAPI 3

## Prerequisites

- Java 17 or higher
- Maven
- Docker and Docker Compose
- PostgreSQL
- RabbitMQ

## Setup Instructions

1. Clone the repository:
```bash
git clone <repository-url>
cd online-marketplace-backend
```

2. Set up environment variables:
Create a `.env` file in the root directory with the following variables:
```
EMAIL_USERNAME=your-email@gmail.com
EMAIL_PASSWORD=your-app-password
JWT_SECRET=your-256-bit-secret
```

3. Start the required services using Docker Compose:
```bash
docker-compose up -d
```

4. Build and run the application:
```bash
mvn clean install
mvn spring-boot:run
```

The application will be available at `http://localhost:8080`

## API Documentation

Once the application is running, you can access the Swagger UI at:
```
http://localhost:8080/swagger-ui.html
```

## Testing

Run the tests using:
```bash
mvn test
```

## Project Structure

```
src/main/java/com/marketplace/
├── config/           # Configuration classes
├── controller/       # REST controllers
├── dto/             # Data Transfer Objects
├── entity/          # JPA entities
├── exception/       # Custom exceptions
├── repository/      # JPA repositories
├── security/        # Security configuration
├── service/         # Business logic
└── util/            # Utility classes
```

## Entity Relationships

- User (1) -> (1) Store
- Store (1) -> (N) Product
- Product (N) -> (1) Category
- User (1) -> (N) Order
- Order (1) -> (N) OrderItem
- Product (1) -> (N) Review
- User (1) -> (N) Review

## Roles and Permissions

1. Admin
   - Manage all users
   - Manage all stores
   - Manage all products
   - Manage all orders
   - Manage categories
   - Mark products as featured

2. Seller
   - Create and manage their store
   - Manage products in their store
   - View and manage orders in their store

3. Shopper
   - Browse products
   - Place orders
   - View order history
   - Review purchased products

## Contributing

1. Fork the repository
2. Create your feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

