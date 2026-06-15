pipeline {
    agent any

    tools {
        maven 'maven3'
        jdk 'jdk17'
        allure 'allure' // Имя Allure Commandline, которое вы задали в настройках.
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

    // Секция post выполнится в любом случае: и при успехе, и при падении тестов.
    post {
        always {
            // Эта команда берет сырые данные из target/allure-results и генерирует отчет.
            allure results: [[path: 'target/allure-results']]
        }
    }
}