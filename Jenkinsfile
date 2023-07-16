pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                // This step checks out your code from GitHub
                git 'https://github.com/kaweesha/Amused-Group-Assessment'
            }
        }
        stage('Build') {
            steps {
                // Replace the following command with the appropriate build command for your project
                sh 'mvn clean package'
            }
        }
        stage('Test') {
            steps {
                // Replace the following command with the appropriate test command for your project
                sh 'mvn test'
            }
        }
        // Add more stages as needed (e.g., deployment, publishing, etc.)
    }
}
