pipeline {
    agent any

    parameters {
        choice(name: 'TEST_SUITE', choices: ['all', 'auth.*', 'users.*'], description: 'Пакет тестов. По умолчанию "all"')
        string(name: 'base.uri', defaultValue: 'https://api.escuelajs.co', description: 'Базовый URL API. По умолчанию "https://api.escuelajs.co"')
        string(name: 'base.path', defaultValue: '/api/v1', description: 'Базовый путь API. По умолчанию "/api/v1"')
        string(name: 'user.email', defaultValue: 'john@mail.com', description: 'Email для авторизации. По умолчанию "user.email"')
        password(name: 'user.password', defaultValue: 'changeme', description: 'Пароль для авторизации')
        gitParameter(
                name: 'BRANCH_NAME',
                type: 'PT_BRANCH',
                branchFilter: 'origin/(.*)',
                defaultValue: 'master',
                description: 'Выберите ветку для сборки',
                selectedValue: 'DEFAULT',
                sortMode: 'DESCENDING_SMART'
            )
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

                        script {
                                    git branch: "${params.BRANCH_NAME}",
                                        url: 'https://github.com/AliaksandrPatapenka/testAuthApiDemo'
                                }

                        // Тесты с параметрами из Jenkins
                        try {
                            def testPattern = params.TEST_SUITE == 'all' ? '' : params.TEST_SUITE + '.*'
                            sh """
                                mvn clean test \
                                -Dbase.uri=${params.'base.uri'} \
                                -Dbase.path=${params.'base.path'} \
                                -Duser.email=${params.'user.email'} \
                                -Duser.password=${params.'user.password'} \
                                -Dtest=${testPattern}
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