pipeline {
    agent any

    parameters {
        choice(name: 'TEST_SUITE', choices: ['all', 'users', 'auth'], description: 'Пакет тестов. По умолчанию "all"')
        string(name: 'BASE_URI', defaultValue: 'https://api.escuelajs.co', description: 'Базовый URL API. По дефолту "https://api.escuelajs.co"')
        string(name: 'BASE_PATH', defaultValue: '/api/v1', description: 'Базовый путь API. По дефолту /api/v1')
        string(name: 'USER_EMAIL', defaultValue: 'john@mail.com', description: 'Email для авторизации. По дефолту "john@mail.com"')
        password(name: 'USER_PASSWORD', defaultValue: 'changeme', description: 'Пароль для авторизации')
    }

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

                        // Тесты с параметрами из Jenkins
                        try {
                            sh """
                                mvn clean test \
                                -Dbase.uri=${params.BASE_URI} \
                                -Dbase.path=${params.BASE_PATH} \
                                -Duser.email=${params.USER_EMAIL} \
                                -Duser.password=${params.USER_PASSWORD}
                                -Dtest=${params.TEST_SUITE == 'all' ? '' : params.TEST_SUITE + '.*'}

                            """
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