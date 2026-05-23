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
        stage('Run mvn clean install') {
            steps {
                sh 'mvn clean install -DskipTests'
            }
        }
    }
}