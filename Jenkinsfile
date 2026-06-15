pipeline {
    agent any

    tools {
        maven 'maven3'
        jdk 'jdk17'
        allure 'allure'
        allure 'allure'
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
    post {
        always {
            allure results: [[path: 'target/allure-results']]
        }
post {
    always {
        allure([
            includeProperties: false,
            jdk: '',
            results: [[path: 'target/allure-results']]
        ])
    }
}
}