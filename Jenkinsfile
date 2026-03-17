pipeline {
    agent any

    // This calls the tools you just named in the Jenkins Dashboard
    tools {
        jdk 'JDK-17' 
        nodejs 'NodeJS-18' 
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
                    // Your exact list of microservices
                    def services = ["discovery-server", "api-gateway", "user-service", "product-service", "media-service"]
                    
                    for (service in services) {
                        echo "⚙️ Processing backend/${service}..."
                        
                        // dir() tells Jenkins to 'cd' into this folder
                        dir("backend/${service}") {
                            // 1. Ensure the wrapper has execution permissions
                            sh 'chmod +x mvnw'
                            
                            // 2. clean package compiles the code AND runs the JUnit tests automatically. 
                            // If a test fails here, Jenkins will instantly halt the pipeline.
                            sh './mvnw clean package'
                        }
                    }
                }
            }
        }

        stage('Frontend: Build Angular') {
            steps {
                dir('frontend') {
                    echo '📦 Building Angular Frontend...'
                    sh 'npm install'
                    sh 'npm run build'
                    
                    // Note: You can add 'ng test --watch=false' here later for frontend testing
                }
            }
        }
    }

    post {
        success {
            echo '🎉 SUCCESS: All microservices and frontend built and tested perfectly!'
        }
        failure {
            echo '❌ FAILURE: A build or test failed. Check the Jenkins console output.'
        }
    }
}