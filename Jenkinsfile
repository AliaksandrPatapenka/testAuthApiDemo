pipeline {
    agent any

    tools {
        maven 'maven3'
        jdk 'jdk17'
        allure 'allure'
    }

    stages {
        stage('Test') {
            steps {
                checkout scm
                sh 'mvn clean test'
            }
        }
    }

    post {
        always {
            allure results: [[path: 'target/allure-results']]
        }
    }
}