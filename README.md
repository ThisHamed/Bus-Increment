# 🚍 Bus-Increment / شارژینو

> A specialized Android app for managing bus fares using **MiFare cards**, featuring real-time **SQL Server synchronization**, **payment processing**, and **receipt printing**, all built on a modern **MVVM + UDF** architecture with **Jetpack Compose**.

---

## 📱 Overview

**Bus-Increment (شارژینو)** is a professional Android application tailored for public transportation systems.  
It provides a complete solution for managing bus fare transactions by directly interacting with **MiFare cards**.

The app supports:
- Reading and writing card data  
- Real-time data synchronization with SQL Server  
- Payment processing  
- Receipt printing  

---

## 🧩 Key Features

### 💳 Card Management
- Read and display **current card balance**
- Write new fare values after a **payment or top-up ("increment")**

### 💰 Payment & Receipts
- Integrated **payment workflows**
- Generate and **print receipts** for transactions

### 🌐 Server Connectivity
- Direct connection to **Microsoft SQL Server** for:
  - Real-time data sync
  - Transaction validation
  - Card information verification
- Local persistence via **Room** (for configurations and caching)

---

## ⚙️ Technical Stack

| Component | Technology |
|------------|-------------|
| **Architecture** | MVVM + UDF |
| **Dependency Injection** | Hilt |
| **Async Operations** | Kotlin Coroutines & Flow |
| **UI Toolkit** | Jetpack Compose |
| **Database (Local)** | Room |
| **Database (Remote)** | JDBC (jTDS) with SQL Server |
| **Hardware Interaction** | MiFare Reader/Writer, Payment Terminal, Receipt Printer |

---

## 🧠 Core Functionalities

| Function | Description |
|-----------|-------------|
| **Card Management** | Read and update MiFare bus card balances |
| **Fare Handling** | Write new values after payment or increment |
| **Payment Processing** | Integrated, secure, and responsive payment flow |
| **Receipt Printing** | Generate and print digital receipts |
| **Server Configuration** | Configure and test SQL Server connection with real-time feedback |

---

## 🧱 Architecture Highlights

```plaintext
┌────────────────────────────┐
│        Jetpack Compose     │  ← UI Layer (Reactive)
└────────────┬───────────────┘
             │
     [ Unidirectional Data Flow ]
             │
┌────────────▼───────────────┐
│         ViewModel          │  ← State Management (MVVM + UDF)
└────────────┬───────────────┘
             │
┌────────────▼───────────────┐
│        Repository           │  ← Business Logic / Data Source
└────────────┬───────────────┘
             │
┌────────────▼───────────────┐
│   SQL Server / Room DB     │  ← Data Layer
└────────────────────────────┘
