

# 01buy - E-Commerce Microservices Platform & CI/CD Pipeline

This repository contains the source code for the 01buy E-commerce platform, built using a Spring Boot microservices architecture and an Angular frontend. It is fully containerized and features a professional-grade, automated CI/CD pipeline managed by Jenkins.

---

## 🚀 CI/CD Pipeline Overview

The `Jenkinsfile` in this repository defines a fully automated Declarative Pipeline. Upon a Git push, the pipeline automatically triggers and executes the following stages:

1. **Checkout Code:** Pulls the latest source code from GitHub.
2. **Backend: Build Microservices:** Compiles all 5 Spring Boot microservices (`discovery-server`, `api-gateway`, `user-service`, `product-service`, `media-service`) using Maven.
3. **Frontend: Build Angular:** Installs npm dependencies and builds the production-ready Angular application.
4. **Deploy to Production:** Uses Docker Compose to provision the infrastructure (MongoDB, Kafka, Zookeeper) and spin up the microservices.
5. **Health Check & Rollback:** Verifies that critical containers are running. If the deployment fails, the pipeline automatically executes a rollback (`docker-compose down`) to prevent a broken state.
6. **Notifications (Post-Build):** Sends automated pipeline status alerts via Discord Webhook and Gmail SMTP.

---

## 🛠️ Jenkins Setup & Configuration Guide

### 1. Starting and Enabling Jenkins

To start the Jenkins service and ensure it automatically runs on system boot:

```bash
sudo systemctl start jenkins
sudo systemctl enable jenkins
sudo systemctl status jenkins
```


### 2. Retrieving the Initial Admin Password

To unlock the Jenkins dashboard for the first time:

```bash
sudo cat /var/lib/jenkins/secrets/initialAdminPassword
```

### 3. Granting Jenkins Docker Permissions

By default, Jenkins does not have permission to execute Docker commands. To allow the pipeline to run deployments, add the `jenkins` user to the `docker` group:

```bash
sudo usermod -aG docker jenkins
sudo systemctl restart jenkins
```

---

## 🔐 Security & Credentials Management

To comply with security best practices, **no sensitive data (passwords, API keys, Webhook URLs) is hardcoded into the codebase.**

### 4. Setting up Discord Pipeline Notifications

To keep the team informed of build successes and failures, we use Discord Webhooks.

**Step A: Generate the Discord Webhook URL**

1. Open Discord and go to any server where you have Admin rights.
2. Click the gear icon next to a text channel (**Edit Channel**).
3. Go to **Integrations** -> **Webhooks** -> **New Webhook**.
4. Name it "Jenkins Bot", optionally assign an avatar, and click **Copy Webhook URL**.

**Step B: Store the URL Securely in Jenkins (Security Best Practice)**
_To pass security audits, we never hardcode sensitive URLs in the `Jenkinsfile`. We manage them via Jenkins Secrets._

1. Go to your Jenkins Dashboard.
2. Navigate to **Manage Jenkins** -> **Credentials**.
3. Click on the **(global)** domain under the "Stores scoped to Jenkins" table.
4. Click **+ Add Credentials** in the top right.
5. Set the **Kind** to **Secret text**.
6. Paste your Discord Webhook URL into the **Secret** box.
7. In the **ID** box, type exactly: `discord-webhook-url`
8. Click **Create**.



### 5. Setting up Email Pipeline Notifications
To satisfy enterprise alerting requirements, Jenkins is configured to send email reports upon build completion.

**Step A: Generate an App Password (Gmail)**
1. Navigate to your Google Account **Security** settings.
2. Under 2-Step Verification, generate a new **App Password** named `Jenkins CI`.
3. Save the 16-character password.

**Step B: Configure Jenkins SMTP**
1. Navigate to **Manage Jenkins** -> **System** -> **E-mail Notification**.
2. Configure the following settings:
   * **SMTP server:** `smtp.gmail.com`
   * **Use SMTP Authentication:** `Checked`
   * **User Name:** Your email address
   * **Password:** The generated App Password
   * **Use SSL:** `Checked`
   * **SMTP Port:** `465`
3. Click **Save**. The `Jenkinsfile` is now authorized to use the `mail` step in the `post` block.


### 6. Dashboard Authorization

The Jenkins dashboard is secured. Anonymous read access is disabled. Only logged-in, authorized users can view the pipeline, trigger builds, or access the credential manager.

---

## 🎛️ Parameterized Builds (Bonus Feature)

The pipeline supports **Parameterized Builds**, allowing developers to customize the deployment without changing the code. When clicking "Build with Parameters" in Jenkins, users can select:

- **`DEPLOY_ENV`:** A dropdown menu to select the target environment (`production`, `staging`, `development`).
- **`CLEAR_CACHE`:** A boolean checkbox. If checked, the pipeline runs `docker system prune -f` to clear old Docker data before building, optimizing disk space.

---

## ✅ Rubric Compliance Checklist

This project was built to satisfy strict DevOps auditing standards:

- [x] **Automated Trigger:** Pipeline uses SCM Polling / Webhooks to automatically trigger on push.
- [x] **End-to-End Execution:** Pipeline successfully builds and deploys both Frontend and Backend from scratch.
- [x] **Rollback Strategy:** The Deployment stage includes a `try-catch` block. If health checks fail, `docker-compose down` removes the corrupted deployment.
- [x] **Test Reporting:** Pipeline includes a `junit` post-step to archive `surefire-reports` for future reference.
- [x] **Dashboard Security:** Jenkins permissions are restricted to authorized users.
- [x] **Secrets Management:** Jenkins Credentials Manager securely handles the Discord Webhook.
- [x] **Notifications:** Dual-channel alerting (Email & Discord) configured for both Success and Failure states.
- [x] **Parameterized Builds:** Implemented for Environment Selection and Cache Management.

