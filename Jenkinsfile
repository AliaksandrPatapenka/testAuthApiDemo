pipeline {
    agent any

    // ====================================================
    // 1. ПАРАМЕТРЫ СБОРКИ
    // ====================================================
    parameters {
        string(name: 'BRANCH_NAME', defaultValue: 'master', description: 'Название ветки. По умолчанию "master"')
        choice(name: 'TEST_SUITE', choices: ['all', 'auth', 'users'], description: 'Пакет тестов. По умолчанию "all"')
        string(name: 'BASE_URL', defaultValue: 'https://api.escuelajs.co', description: 'Базовый URL API')
        string(name: 'BASE_PATHS', defaultValue: '/api/v1', description: 'Базовый путь API')
        string(name: 'USER_EMAIL', defaultValue: '', description: 'Email пользователя для авторизации')
        password(name: 'USER_PASSWORD', defaultValue: '', description: 'Пароль пользователя для авторизации')
    }


    // ====================================================
    // 2. ИНСТРУМЕНТЫ (Maven и Java)
    // ====================================================
    tools {
        maven 'maven3'
        jdk 'jdk21'
    }


    // ====================================================
    // 3. ОСНОВНАЯ ЛОГИКА СБОРКИ
    // ====================================================
    stages {
        stage('Run') {
            steps {
                script {
                    def buildUrl = "http://localhost:8080/job/${JOB_NAME}/${BUILD_NUMBER}/"
                    boolean testsFailed = false


                    try {
                        // --------------------------------------------
                        // 3.1. Уведомление о СТАРТЕ сборки
                        // --------------------------------------------
                        withCredentials([
                                 string(credentialsId: 'telegram-token', variable: 'TOKEN'),
                                 string(credentialsId: 'user.email', variable: 'EMAIL'),
                                 string(credentialsId: 'user.password', variable: 'PASSWORD')
                                ]) {
                            sh """
                                curl -s -X POST "https://api.telegram.org/bot${TOKEN}/sendMessage" \
                                -d "chat_id=-1004366972797" \
                                -d "text=🚀 Сборка #${BUILD_NUMBER} [${JOB_NAME}] запущена. Ссылка: <code>${buildUrl}</code>" \
                                -d "parse_mode=HTML"
                            """
                        }


                        // --------------------------------------------
                        // 3.2. Клонирование ВЫБРАННОЙ ВЕТКИ
                        // --------------------------------------------
                        git branch: "${params.BRANCH_NAME}",
                            url: 'https://github.com/AliaksandrPatapenka/testAuthApiDemo'


                        // --------------------------------------------
                        // 3.3. ЗАПУСК ТЕСТОВ с параметрами
                        // --------------------------------------------
                        try {
                            def testPattern = params.TEST_SUITE == 'all' ? '' : params.TEST_SUITE + '/*'
                            sh """
                                mvn clean test \
                                -Dbase.uri=${params.'base.uri'} \
                                -Dbase.path=${params.'base.path'} \
                                -Duser.email=${email} \
                                -Duser.password=${password} \
                                -Dtest=${testPattern}
                            """
                        } catch (Exception e) {
                            testsFailed = true
                            currentBuild.result = 'UNSTABLE'
                        }


                        // --------------------------------------------
                        // 3.4. Генерация ALLURE-ОТЧЁТА
                        // --------------------------------------------
                        sh 'mvn allure:report'

                    } catch (Exception e) {
                        // --------------------------------------------
                        // 3.5. Если сборка УПАЛА (ошибка в пайплайне)
                        // --------------------------------------------
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


                    // --------------------------------------------
                    // 3.6. ИТОГОВОЕ сообщение (УСПЕШНА / НЕСТАБИЛЬНА)
                    // --------------------------------------------
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


    // ====================================================
    // 4. ДЕЙСТВИЯ ПОСЛЕ СБОРКИ (всегда)
    // ====================================================
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