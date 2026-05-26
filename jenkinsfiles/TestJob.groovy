pipeline {
    agent any

    triggers {
        pollSCM('H/1 * * * *')
    }
    stages {
        stage('Check versions') {
            steps {
                sh 'mvn --version'
                sh 'java --version'
            }
        }
        stage('Build docker image') {
            steps {
                sh 'docker build -f Dockerfile -t g2048:latest .'
            }
        }
    }
}