# Simple Ballistic Calculator 🎯

A lightweight, Java-based desktop application that calculates and visualizes projectile trajectories. Built with Maven and featuring a clean Graphical User Interface (GUI), this tool allows users to input initial launch parameters and instantly view flight path.

## ✨ Features

* **Intuitive GUI:** Easy-to-use interface for entering variables such as initial velocity, launch angle,...
* **Instant Calculations:** Rapidly computes trajectory metrics using standard kinematic equations.
* **Modern Java:** Leverages features and performance improvements of Java 22.

## 🛠 Prerequisites

Before you begin, ensure you have the following installed on your system:

* **Java Development Kit (JDK) 22** or higher.
    * Verify your installation: `java --version`
* **Apache Maven** (Version 4.x.x or higher recommended).
    * Verify your installation: `mvn --version`

## 🚀 Launching the app

### Clone the Repository and compile it
```
git clone https://github.com/dbrada-dev/ballistic-calculator.git
cd ballistic-calculator
mvn compile exec:exec
```

### Download a compiled jar
Download the latest jar [here](https://github.com/dbrada-dev/ballistic-calculator.git)
or
```
wget https://github.com/dbrada-dev/ballistic-calculator/raw/refs/heads/main/ballistic-calculator.jar
```
and then
```
java -jar ballistic-calculator.jar
```
