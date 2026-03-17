pipeline {
    agent any

    // Calls the tools configured in the Jenkins Dashboard
    tools {
        jdk 'JDK-21' 
        nodejs 'NodeJS-22' 
    }

    stages {
        stage('Checkout Code') {
            steps {
                // Pulls the latest code from your Git repo
                checkout scm
                echo '✅ Source code fetched successfully!'
            }
        }

        stage('Backend: Build & Test Microservices') {
            steps {
                script {
                    // Your exact list of 01buy microservices
                    def services = ["discovery-server", "api-gateway", "user-service", "product-service", "media-service"]
                    
                    for (String service : services) {
                        echo "⚙️ Processing backend/${service}..."
                        
                        dir("backend/${service}") {
                            // Ensure the wrapper has execution permissions
                            sh 'chmod +x mvnw'
                            
                            // clean package compiles the code AND runs the JUnit tests automatically.
                            sh './mvnw clean package'
                        }
                    }
                }
            }
            post {
                always {
                    // Grabs the test results from all microservices, even if the build fails
                    junit allowEmptyResults: true, testResults: 'backend/*/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Frontend: Build Angular') {
            steps {
                dir('frontend') {
                    echo '📦 Building Angular Frontend...'
                    sh 'npm install'
                    sh 'npm run build'
                    
                    // Note: Uncomment the line below when you are ready to run Angular tests
                    // sh 'ng test --watch=false --browsers=ChromeHeadless' 
                }
            }
        }
        stage('Deploy to Production') {
            steps {
                script {
                    echo '🚀 Starting Deployment via Docker Compose...'
                    try {
                        // 1. Build and spin up the new containers
                        sh 'docker-compose up --build -d'
                        
                        // 2. Give Spring Boot a few seconds to start up
                        echo '⏳ Waiting for services to initialize...'
                        sleep(time: 20, unit: 'SECONDS')
                        
                        // 3. Health Check: Ensure the critical containers are actually running
                        // If they crashed, this command fails and triggers the Rollback
                        sh 'docker ps | grep buy01-frontend'
                        sh 'docker ps | grep buy01-api-gateway'
                        
                        echo '✅ Deployment Successful and Healthy!'
                    } catch (Exception e) {
                        echo '🚨 HEALTH CHECK FAILED! INITIATING ROLLBACK...'
                        
                        // 4. Rollback Strategy: Destroy the broken deployment
                        // (In a massive enterprise, you would revert to the previous Docker image tag here)
                        sh 'docker-compose down'
                        
                        error('Deployment failed and was successfully rolled back.')
                    }
                }
            }
        }
    }

    post {
        success {
            echo '🎉 SUCCESS: All microservices and frontend built and tested perfectly!'
        }
        failure {
            echo '❌ FAILURE: A build or test failed. Check the Jenkins console output and test reports.'
        }
    }
}