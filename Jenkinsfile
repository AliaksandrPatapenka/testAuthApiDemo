pipeline {
    agent any

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

        stage('Test & Report') {
            steps {
                sh 'mvn clean test allure:report'
            }
        }
    }

    post {
        always {
            publishHTML([
                allowMissing: true,
                reportDir: 'target/site/allure-maven-plugin',
                reportFiles: 'index.html',
                reportName: 'Allure Report'
            ])
        }
    }
}