pipeline {
    agent {
        dockerContainer {
            image 'maven:3.9.16-eclipse-temurin-21-alpine'
        }
    }
    triggers {
        pollSCM('H/1 * * * *')
    }
    stages {
        stage('build') {
            steps {
                checkout scm
                sh 'mvn --version'
            }
        }
    }
}