pipeline {
    agent any

    tools {
        maven 'maven3'
        jdk 'jdk21'
    }

    stages {
        stage('Start') {
            steps {
                script {
                    notifyEvents message: "🚀 Сборка #${env.BUILD_NUMBER} запущена", token: 'h9r3wyymftw__3gqa-qoqfpvjbevl_cw'
                }
            }
        }

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Test') {
            steps {
                script {
                    try {
                        sh 'mvn clean test'
                    } catch (Exception e) {
                        echo "Тесты упали, но продолжаем..."
                        currentBuild.result = 'UNSTABLE'
                    }
                }
            }
        }

        stage('Generate Report') {
            steps {
                sh 'mvn allure:report'
            }
        }
    }

    post {
        always {
            publishHTML([
                allowMissing: true,
                alwaysLinkToLastBuild: true,
                keepAll: true,
                reportDir: 'target/site/allure-maven-plugin',
                reportFiles: 'index.html',
                reportName: 'Allure Report'
            ])

            script {
                def status = currentBuild.currentResult
                notifyEvents message: "🏁 Сборка #${env.BUILD_NUMBER} завершена со статусом: ${status}", token: 'h9r3wyymftw__3gqa-qoqfpvjbevl_cw'
            }
        }
    }
}