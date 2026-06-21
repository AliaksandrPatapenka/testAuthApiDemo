pipeline {
    agent any

    tools {
        maven 'maven3'
        jdk 'jdk21'
    }

    stages {
        stage('Run') {
            steps {
                script {
                    try {
                        // ---- ТВОЙ ОСНОВНОЙ КОД ----
                        def buildUrl = "http://localhost:8080/job/${JOB_NAME}/${BUILD_NUMBER}/"

                        // Уведомление о старте
                        withCredentials([string(credentialsId: 'telegram-token', variable: 'TOKEN')]) {
                            sh """
                                curl -s -X POST "https://api.telegram.org/bot${TOKEN}/sendMessage" \
                                -d "chat_id=-1004366972797" \
                                -d "text=🚀 Сборка #${BUILD_NUMBER} [${JOB_NAME}] запущена. Ссылка: <code>${buildUrl}</code>" \
                                -d "parse_mode=HTML"
                            """
                        }

                        // Checkout
                        checkout scm

                        // Test
                        try {
                            sh 'mvn clean test'
                        } catch (Exception e) {
                            echo "Тесты упали, но продолжаем..."
                            currentBuild.result = 'UNSTABLE'
                        }

                        // Allure report
                        sh 'mvn allure:report'

                        // ---- УСПЕШНО ----
                        withCredentials([string(credentialsId: 'telegram-token', variable: 'TOKEN')]) {
                            sh """
                                curl -s -X POST "https://api.telegram.org/bot${TOKEN}/sendMessage" \
                                -d "chat_id=-1004366972797" \
                                -d "text=✅ Сборка #${BUILD_NUMBER} [${JOB_NAME}] УСПЕШНА. Ссылка: <code>${buildUrl}</code>" \
                                -d "parse_mode=HTML"
                            """
                        }

                    } catch (Exception e) {
                        // ---- ПАДЕНИЕ (ЛЮБОЕ) ----
                        currentBuild.result = 'FAILURE'
                        echo "Сборка упала: ${e.message}"

                        withCredentials([string(credentialsId: 'telegram-token', variable: 'TOKEN')]) {
                            sh """
                                curl -s -X POST "https://api.telegram.org/bot${TOKEN}/sendMessage" \
                                -d "chat_id=-1004366972797" \
                                -d "text=❌ Сборка #${BUILD_NUMBER} [${JOB_NAME}] УПАЛА! Ошибка: ${e.message}. Ссылка: <code>${buildUrl}</code>" \
                                -d "parse_mode=HTML"
                            """
                        }

                        // Пробрасываем исключение дальше, чтобы Jenkins показал красный статус
                        throw e
                    }
                }
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
        }

        unstable {
            script {
                def buildUrl = "http://localhost:8080/job/${JOB_NAME}/${BUILD_NUMBER}/"
                withCredentials([string(credentialsId: 'telegram-token', variable: 'TOKEN')]) {
                    sh """
                        curl -s -X POST "https://api.telegram.org/bot${TOKEN}/sendMessage" \
                        -d "chat_id=-1004366972797" \
                        -d "text=⚠️ Сборка #${BUILD_NUMBER} [${JOB_NAME}] НЕСТАБИЛЬНА (тесты упали). Ссылка: <code>${buildUrl}</code>" \
                        -d "parse_mode=HTML"
                    """
                }
            }
        }
    }
}