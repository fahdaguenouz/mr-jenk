---

````markdown
## 🛠️ Jenkins CI/CD Setup & Configuration

This project uses Jenkins for automated CI/CD. Below are the steps to configure the Jenkins server, grant necessary Docker permissions, and securely set up pipeline notifications.

### 1. Starting and Enabling Jenkins

To start the Jenkins service and ensure it automatically runs on system boot:

```bash
sudo systemctl start jenkins
sudo systemctl enable jenkins
sudo systemctl status jenkins
```
````

### 2. Retrieving the Initial Admin Password

When accessing the Jenkins dashboard for the first time, unlock it by retrieving the initial admin password:

```bash
sudo cat /var/lib/jenkins/secrets/initialAdminPassword
```

### 3. Granting Jenkins Docker Permissions

By default, Jenkins does not have permission to execute Docker commands. To allow the pipeline to run `docker-compose up`, add the `jenkins` user to the `docker` group:

```bash
sudo usermod -aG docker jenkins
sudo systemctl restart jenkins
```

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

```

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
```
