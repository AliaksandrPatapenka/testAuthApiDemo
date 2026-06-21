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
                    def buildUrl = "http://localhost:8080/job/${JOB_NAME}/${BUILD_NUMBER}/"
                    boolean testsFailed = false

                    try {
                        // Уведомление о старте
                        withCredentials([string(credentialsId: 'telegram-token', variable: 'TOKEN')]) {
                            sh """
                                curl -s -X POST "https://api.telegram.org/bot${TOKEN}/sendMessage" \
                                -d "chat_id=-1004366972797" \
                                -d "text=🚀 Сборка #${BUILD_NUMBER} [${JOB_NAME}] запущена. Ссылка: <code>${buildUrl}</code>" \
                                -d "parse_mode=HTML"
                            """
                        }

                        checkout scm

                        // Тесты с перехватом ошибок
                        try {
                            sh 'mvn clean test'
                        } catch (Exception e) {
                            testsFailed = true
                            echo "Тесты упали, но продолжаем..."
                            currentBuild.result = 'UNSTABLE'
                        }

                        // Allure report (даже если тесты упали)
                        sh 'mvn allure:report'

                        // Если тесты упали — отправляем одно сообщение о нестабильности
                        if (testsFailed) {
                            withCredentials([string(credentialsId: 'telegram-token', variable: 'TOKEN')]) {
                                sh """
                                    curl -s -X POST "https://api.telegram.org/bot${TOKEN}/sendMessage" \
                                    -d "chat_id=-1004366972797" \
                                    -d "text=⚠️ Сборка #${BUILD_NUMBER} [${JOB_NAME}] НЕСТАБИЛЬНА (тесты упали). Ссылка: <code>${buildUrl}</code>" \
                                    -d "parse_mode=HTML"
                                """
                            }
                        } else {
                            // Всё успешно
                            withCredentials([string(credentialsId: 'telegram-token', variable: 'TOKEN')]) {
                                sh """
                                    curl -s -X POST "https://api.telegram.org/bot${TOKEN}/sendMessage" \
                                    -d "chat_id=-1004366972797" \
                                    -d "text=✅ Сборка #${BUILD_NUMBER} [${JOB_NAME}] УСПЕШНА. Ссылка: <code>${buildUrl}</code>" \
                                    -d "parse_mode=HTML"
                                """
                            }
                        }

                    } catch (Exception e) {
                        // Любая другая ошибка (падение сборки)
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
    }
}