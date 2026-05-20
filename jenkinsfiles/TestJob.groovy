pipeline {
    agent any

    stages {
        stage('Docker Images') {
            steps {
                sh 'docker images'
            }
        }
    }
}