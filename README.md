# Offline UPI Simulator

A simple Java Spring Boot project that simulates how an offline payment could be queued and later synchronized when connectivity becomes available.

I built this project to understand the basic ideas behind offline payment systems, transaction handling, duplicate transaction protection, and message propagation between devices.

> **Note:** This is only an educational simulation. It is not connected to the real UPI network and should not be used for actual payments.

## Features

* Create offline payment requests
* Queue payments when the system is offline
* Simulate payment synchronization
* Simulate a simple mesh/gossip network
* Track mesh packet hops and TTL
* Prevent the same transaction from being settled twice
* View accounts, payments and mesh packets through a simple web interface
* REST APIs for interacting with the backend
* Basic automated tests
* Docker support

## Tech Stack

* Java 21
* Spring Boot 3
* Maven
* REST API
* HTML, CSS and JavaScript
* JUnit
* Docker

## How the project works

The project uses a few demo accounts stored in memory.

For example:

```text
srijon@upi  -> ₹5000
rahul@upi   -> ₹1500
demo@upi    -> ₹3000
```

When an offline payment is created, the payment is not immediately transferred.

It is first stored with the status:

```text
QUEUED_OFFLINE
```

A mesh packet is also created for the transaction.

Running a gossip round simulates the packet being passed through the network. Each round increases the hop count and decreases the TTL.

When the payment is synchronized, the amount is transferred from the sender to the receiver and the transaction becomes:

```text
SETTLED
```

The application also checks whether a transaction has already been processed. This prevents the same payment from being transferred twice.

## Web Interface

The project includes a simple browser-based interface.

After starting the application, open:

```text
http://localhost:8080
```

From the interface you can:

1. Select a sender and receiver
2. Enter a payment amount
3. Queue an offline payment
4. Run a mesh gossip round
5. Synchronize the payment
6. Check account balances
7. View payment and mesh packet details

## Running the Project Locally

### Requirements

Make sure the following are installed:

* Java 21
* Maven 3.9+
* Git

### Clone the repository

```bash
git clone https://github.com/nandysrijon25/offline-upi-simulator.git
cd offline-upi-simulator
```

### Start the application

Using Maven:

```bash
mvn spring-boot:run
```

On Windows, if Maven is not added to PATH, you can also run Maven using its full path.

The application starts on:

```text
http://localhost:8080
```

## API Endpoints

### Get accounts

```http
GET /api/accounts
```

### Get payments

```http
GET /api/payments
```

### Create an offline payment

```http
POST /api/payments/offline
```

Example request:

```json
{
  "senderUpiId": "srijon@upi",
  "receiverUpiId": "rahul@upi",
  "amount": 500,
  "note": "Test payment"
}
```

### Synchronize a payment

```http
POST /api/payments/{transactionId}/sync
```

### Get mesh packets

```http
GET /api/mesh/packets
```

### Run a gossip round

```http
POST /api/mesh/gossip
```

### Health check

```http
GET /health
```

## Example Flow

Suppose Srijon wants to send ₹500 to Rahul.

Initially:

```text
Srijon = ₹5000
Rahul  = ₹1500
```

After creating the offline payment:

```text
Status = QUEUED_OFFLINE
```

The balances remain unchanged because the payment
