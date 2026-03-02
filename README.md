```markdown
# Identity Reconciliation Backend

A Spring Boot backend service that consolidates customer identities across multiple purchases by linking contacts based on shared email or phone number.

This project implements the Bitespeed Backend Task – Identity Reconciliation.

---

## 🚀 Live API

Base URL:

```

[https://identity-reconciliation-backend-project.onrender.com](https://identity-reconciliation-backend-project.onrender.com)

```

Endpoint:

```

POST /identify

```

---

## 📌 Problem Statement

Customers may place multiple orders using different email addresses or phone numbers.  
If two contacts share either an email or phone number, they must be linked under a single primary identity.

The oldest contact is treated as **primary**, and subsequent linked contacts become **secondary**.

---

## 🛠 Tech Stack

- Java 21
- Spring Boot 3
- Spring Data JPA
- H2 / PostgreSQL (depending on environment)
- Docker
- Render (deployment)

---

## 📂 API Usage

### Request

```

POST /identify
Content-Type: application/json

````

```json
{
  "email": "test@example.com",
  "phoneNumber": "123456"
}
````

---

### Response

```json
{
  "contact": {
    "primaryContactId": 1,
    "emails": ["test@example.com"],
    "phoneNumbers": ["123456"],
    "secondaryContactIds": []
  }
}
```

---

## 🔄 Identity Logic

* If no matching contact exists → create primary
* If match exists → link as secondary
* If two primaries conflict → merge under oldest
* All linked contacts return consolidated response

---

## 🐳 Deployment

This application is containerized using Docker and deployed on Render.

---

## 📦 Running Locally

Clone the repository:

```
git clone <repo-url>
cd identity-reconciliation-backend-project
```

Build:

```
./mvnw clean install
```

Run:

```
java -jar target/*.jar
```

---

## 👤 Author

Gnaneshwar R L
gnaneshwarnani8605@gmail.com
+91 8608698986

```

