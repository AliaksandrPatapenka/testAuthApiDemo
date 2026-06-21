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
                def buildUrl = currentBuild.rawBuild.getUrl()
                withCredentials([string(credentialsId: 'telegram-token', variable: 'TOKEN')]) {
                        sh """
                            curl -s -X POST "https://api.telegram.org/bot${TOKEN}/sendMessage" \
                            -d "chat_id=-1004366972797" \
                            -d "text=🚀 Сборка #${BUILD_NUMBER} [${JOB_NAME}] запущена. Ссылка: ${buildUrl}" \
                            -d "parse_mode=HTML"
                        """
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
            def buildUrl = currentBuild.rawBuild.getUrl()
                withCredentials([string(credentialsId: 'telegram-token', variable: 'TOKEN')]) {
                    sh """
                        curl -s -X POST "https://api.telegram.org/bot${TOKEN}/sendMessage" \
                        -d "chat_id=-1004366972797" \
                        -d "text=✅ Сборка #${BUILD_NUMBER} [${JOB_NAME}] УСПЕШНА. Ссылка: ${buildUrl}" \
                        -d "parse_mode=HTML"
                    """
                }
            }
        }

        failure {
            script {
            def buildUrl = currentBuild.rawBuild.getUrl()
                withCredentials([string(credentialsId: 'telegram-token', variable: 'TOKEN')]) {
                    sh """
                        curl -s -X POST "https://api.telegram.org/bot${TOKEN}/sendMessage" \
                        -d "chat_id=-1004366972797" \
                        -d "text=❌ Сборка #${BUILD_NUMBER} [${JOB_NAME}] ПРОВАЛЕНА! Ссылка: ${buildUrl}"  \
                        -d "parse_mode=HTML"
                    """
                }
            }
        }

        unstable {
            script {
            def buildUrl = currentBuild.rawBuild.getUrl()
                withCredentials([string(credentialsId: 'telegram-token', variable: 'TOKEN')]) {
                    sh """
                        curl -s -X POST "https://api.telegram.org/bot${TOKEN}/sendMessage" \
                        -d "chat_id=-1004366972797" \
                        -d "text=⚠️ Сборка #${BUILD_NUMBER} [${JOB_NAME}] НЕСТАБИЛЬНА (тесты упали). Ссылка: ${buildUrl}" \
                        -d "parse_mode=HTML"
                    """
                }
            }
        }
    }
}