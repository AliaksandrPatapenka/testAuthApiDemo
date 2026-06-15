pipeline {
    agent any #запускать на любом доступном агенте (в вашем случае — на самом Jenkins)

    tools {
        maven 'maven3'
        jdk 'jdk17'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Test') {
            steps {
                sh 'mvn clean test'
            }
        }
    }
}