pipeline {
    agent any

    stages {
        stage('Perform Maven Clean') {
            steps {
                sh 'mvn clean'
            }
        }
    }
}