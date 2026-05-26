pipeline {
    agent any

    stages {
        stage('Build image') {
            steps {
                script {
                    def commitHash = sh(
                            script: 'git rev-parse --short HEAD',
                            returnStdout: true
                    ).trim()

                    sh "docker build -t g2048:${commitHash} ."
                }
            }
        }
    }
}