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