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

                        try {
                            sh 'mvn clean test'
                        } catch (Exception e) {
                            testsFailed = true
                            currentBuild.result = 'UNSTABLE'
                        }

                        sh 'mvn allure:report'

                    } catch (Exception e) {
                        currentBuild.result = 'FAILURE'
                        withCredentials([string(credentialsId: 'telegram-token', variable: 'TOKEN')]) {
                            sh """
                                curl -s -X POST "https://api.telegram.org/bot${TOKEN}/sendMessage" \
                                -d "chat_id=-1004366972797" \
                                -d "text=❌ Сборка #${BUILD_NUMBER} [${JOB_NAME}] УПАЛА! Ссылка: <code>${buildUrl}</code>" \
                                -d "parse_mode=HTML"
                            """
                        }
                        throw e
                    }

                    // Отправка итогового сообщения
                    withCredentials([string(credentialsId: 'telegram-token', variable: 'TOKEN')]) {
                        def statusText = testsFailed ? "⚠️ НЕСТАБИЛЬНА (тесты упали)" : "✅ УСПЕШНА"
                        sh """
                            curl -s -X POST "https://api.telegram.org/bot${TOKEN}/sendMessage" \
                            -d "chat_id=-1004366972797" \
                            -d "text=${statusText}. Сборка #${BUILD_NUMBER} [${JOB_NAME}]. Ссылка: <code>${buildUrl}</code>" \
                            -d "parse_mode=HTML"
                        """
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