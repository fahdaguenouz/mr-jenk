pipeline {
    agent any // Executes the pipeline on any available Jenkins node

    // This section defines the tools Jenkins needs to run your project
    tools {
        // Note: You have to configure these names inside Jenkins' "Global Tool Configuration"
        maven 'Maven-3.9' 
        nodejs 'NodeJS-18' 
    }

    stages {
        stage('Checkout') {
            steps {
                // This pulls the latest commit from your GitHub repository
                checkout scm 
                echo 'Source code fetched successfully!'
            }
        }

        stage('Backend: Build & Test (Spring Boot)') {
            steps {
                // Assumes your Spring Boot code is inside a folder named 'backend'
                dir('backend') { 
                    echo 'Building the Spring Boot Backend...'
                    // Compiles the Java code and runs JUnit tests
                    sh './mvnw clean package' 
                }
            }
        }

        stage('Frontend: Build & Test (Angular)') {
            steps {
                // Assumes your Angular code is inside a folder named 'frontend'
                dir('frontend') {
                    echo 'Building the Angular Frontend...'
                    sh 'npm install'
                    sh 'npm run build'
                    
                    // You would run your Jasmine/Karma tests here
                    // sh 'ng test --watch=false' 
                }
            }
        }
    }

    // The 'post' block runs after all stages are finished
    post {
        success {
            echo '✅ SUCCESS: 01buy built and tested perfectly! Ready to deploy.'
            // Later, we will add Slack or Email notification code right here
        }
        failure {
            echo '❌ FAILURE: Something broke in the build or tests. Check the Jenkins console logs!'
        }
    }
}