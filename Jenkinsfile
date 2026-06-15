pipeline {
    agent any

    tools {
        maven 'maven3'
        jdk 'jdk17'
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

        stage('Allure Report') {
            steps {
                withAllureEnvironment {
                    sh 'allure generate target/allure-results --clean -o target/allure-report'
                }
            }
        }
    }

    post {
        always {
            publishHTML([
                allowMissing: true,
                reportDir: 'target/allure-report',
                reportFiles: 'index.html',
                reportName: 'Allure Report'
            ])
        }
    }
}