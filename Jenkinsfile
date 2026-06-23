pipeline {
    agent any

    // ====================================================
    // 1. ПАРАМЕТРЫ СБОРКИ
    // ====================================================
    parameters {
        string(name: 'BRANCH_NAME', defaultValue: 'master', description: 'Название ветки. По умолчанию "master"')
        string(name: 'REPO_URL', defaultValue: 'https://github.com/AliaksandrPatapenka/testAuthApiDemo', description: 'URL репозитория с кодом')
        choice(name: 'TEST_SUITE', choices: ['all', 'auth', 'users'], description: 'Пакет тестов. По умолчанию "all"')
        string(name: 'BASE_URL', defaultValue: 'https://api.escuelajs.co', description: 'Базовый URL API')
        string(name: 'BASE_PATHS', defaultValue: '/api/v1', description: 'Базовый путь API')
        string(name: 'USER_EMAIL', defaultValue: '', description: 'Email пользователя для авторизации. По умолчанию подставится Email дефолтного пользователя ')
        password(name: 'USER_PASSWORD', defaultValue: '', description: 'Пароль пользователя для авторизации. По умолчанию подставится пароль дефолтного пользователя ')
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
                        withCredentials([
                            string(credentialsId: 'telegram.token', variable: 'TOKEN'),
                            usernamePassword(credentialsId: 'user-credentials',
                                             usernameVariable: 'USERNAME',
                                             passwordVariable: 'PASSWORD')
                        ]) {
                            // --------------------------------------------
                            // 3.1. Уведомление о СТАРТЕ сборки
                            // --------------------------------------------
                            sh """
                                curl -s -X POST "https://api.telegram.org/bot${TOKEN}/sendMessage" \
                                -d "chat_id=-1004366972797" \
                                -d "text=🚀 <b>Тесты ЗАПУЩЕНЫ!</b>\n\n      Тесты: <code>[${JOB_NAME}]</code>\n      Номер запуска: <code>${BUILD_NUMBER}</code>\n      Проект: <code>${REPO_URL}</code>\n      Ветка: <code>${env.BRANCH_NAME}</code>\n      Запустил: <code>${env.BUILD_USER}</code>\n\n<code>${buildUrl}</code>" \
                                -d "parse_mode=HTML"
                            """

                            // --------------------------------------------
                            // 3.2. Клонирование ВЫБРАННОЙ ВЕТКИ
                            // --------------------------------------------
                            git branch: "${params.BRANCH_NAME}",
                                url: "${params.REPO_URL}"

                            // --------------------------------------------
                            // 3.3. ЗАПУСК ТЕСТОВ с параметрами
                            // --------------------------------------------
                            try {
                                def testPattern = params.TEST_SUITE == 'all' ? '' : params.TEST_SUITE + '/*'

                                // Используем одинарные кавычки, чтобы Groovy не интерполировал переменные.
                                // Параметры BASE_URL и BASE_PATHS подставляем через конкатенацию,
                                // так как они не являются секретами.
                                sh '''
                                    mvn clean test -e \
                                    -Dbase.uri=''' + params.BASE_URL + ''' \
                                    -Dbase.path=''' + params.BASE_PATHS + ''' \
                                    -Duser.email=$USERNAME \
                                    -Duser.password=$PASSWORD \
                                    -Dtest=''' + testPattern
                            } catch (Exception e) {
                                testsFailed = true
                                currentBuild.result = 'UNSTABLE'
                                echo "Error in test execution: ${e.message}"
                            }

                            // --------------------------------------------
                            // 3.4. Генерация ALLURE-ОТЧЁТА
                            // --------------------------------------------
                            sh 'mvn allure:report'
                        }

                    } catch (Exception e) {
                        // --------------------------------------------
                        // 3.5. Если сборка УПАЛА (ошибка в пайплайне)
                        // --------------------------------------------
                        currentBuild.result = 'FAILURE'
                        withCredentials([string(credentialsId: 'telegram.token', variable: 'TOKEN')]) {
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
                    withCredentials([string(credentialsId: 'telegram.token', variable: 'TOKEN')]) {
                        def statusIcon = testsFailed ? "⚠️" : "✅"
                        def statusText = testsFailed ? "НЕСТАБИЛЬНА (тесты упали)" : "УСПЕШНА"
                        sh """
                            curl -s -X POST "https://api.telegram.org/bot${TOKEN}/sendMessage" \
                            -d "chat_id=-1004366972797" \
                            -d "text=${statusIcon} Сборка #${BUILD_NUMBER} [${JOB_NAME}] ${statusText}. Ссылка: <code>${buildUrl}</code>" \
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