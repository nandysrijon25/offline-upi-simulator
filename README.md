# Offline UPI Simulator

**Live Demo:** https://offline-upi-simulator.onrender.com  
**GitHub:** https://github.com/nandysrijon25/offline-upi-simulator

A Java Spring Boot project that simulates the basic workflow of an offline payment system.

The idea behind this project is to demonstrate how a payment could be created while a device is offline, kept in a pending state, propagated through a simple simulated mesh network, and synchronized later when connectivity becomes available.

I built this project as a learning project to understand backend development with Spring Boot, REST APIs, transaction states, synchronization, idempotency, and basic distributed-system concepts.

> **Important:** This is an educational simulator. It is not connected to the actual UPI network, banks, NPCI infrastructure, or real financial accounts. No real money is transferred.

---

## Live Demo

The current version is deployed on Render:

**https://offline-upi-simulator.onrender.com**

The live application provides a simple web interface through which the complete simulated payment flow can be tested.

Because the application currently stores its data in memory, the demo data may reset whenever the application restarts or is redeployed.

---

## What This Project Demonstrates

The project focuses on a simplified offline-payment workflow:

```text
Create Payment
      ↓
QUEUED_OFFLINE
      ↓
Mesh Packet Created
      ↓
Gossip / Packet Propagation
      ↓
Synchronization
      ↓
SETTLED