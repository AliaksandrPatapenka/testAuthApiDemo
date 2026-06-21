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
                def rawUrl = "http://localhost:8080/job/${JOB_NAME}/${BUILD_NUMBER}/"
                def buildUrl = rawUrl.replace('#', '\\#')
                withCredentials([string(credentialsId: 'telegram-token', variable: 'TOKEN')]) {
                    echo "=== ОТПРАВКА В TELEGRAM ==="
                    echo "Текст: 🚀 Сборка #${BUILD_NUMBER} [${JOB_NAME}] запущена. [Ссылка на сборку](${buildUrl})"
                    echo "parse_mode: MarkdownV2"
                    def response = sh(script: """
                        curl -s -X POST "https://api.telegram.org/bot${TOKEN}/sendMessage" \
                        -d "chat_id=-1004366972797" \
                        -d "text=🚀 Сборка #${BUILD_NUMBER} [${JOB_NAME}] запущена. [Ссылка на сборку](${buildUrl})" \
                        -d "parse_mode=MarkdownV2"
                    """, returnStdout: true).trim()
                    echo "=== ОТВЕТ TELEGRAM API ==="
                    echo response
                }
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
        }

        success {
            script {
                withCredentials([string(credentialsId: 'telegram-token', variable: 'TOKEN')]) {
                    sh """
                        curl -s -X POST "https://api.telegram.org/bot${TOKEN}/sendMessage" \
                        -d "chat_id=-1004366972797" \
                        -d "text=✅ Сборка #${BUILD_NUMBER} [${JOB_NAME}] УСПЕШНА. Ссылка: <a href='${env.BUILD_URL}'>Ссылка на сборку</a>" \
                        -d "parse_mode=HTML"
                    """
                }
            }
        }

        failure {
            script {
                withCredentials([string(credentialsId: 'telegram-token', variable: 'TOKEN')]) {
                    sh """
                        curl -s -X POST "https://api.telegram.org/bot${TOKEN}/sendMessage" \
                        -d "chat_id=-1004366972797" \
                        -d "text=❌ Сборка #${BUILD_NUMBER} [${JOB_NAME}] УПАЛА! <a href='${env.BUILD_URL}'>Ссылка на сборку</a>" \
                        -d "parse_mode=HTML"
                    """
                }
            }
        }

        unstable {
            script {
                withCredentials([string(credentialsId: 'telegram-token', variable: 'TOKEN')]) {
                    sh """
                        curl -s -X POST "https://api.telegram.org/bot${TOKEN}/sendMessage" \
                        -d "chat_id=-1004366972797" \
                        -d "text=⚠️ Сборка #${BUILD_NUMBER} [${JOB_NAME}] НЕСТАБИЛЬНА (тесты упали). <a href='${env.BUILD_URL}'>Ссылка на сборку</a>" \
                        -d "parse_mode=HTML"
                    """
                }
            }
        }
    }
}