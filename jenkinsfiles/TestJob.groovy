pipeline {
    agent any

    environment {
        APP_NAME = 'g2048'
        MANIFEST_FILE = 'g2048/deployment.yml'
        GIT_BRANCH = 'master'
    }

    triggers {
        pollSCM('* * * * *')
    }

    stages {
        stage('Get commit hash') {
            steps {
                script {
                    env.COMMIT_HASH = sh(
                            script: 'git rev-parse --short HEAD',
                            returnStdout: true
                    ).trim()

                    env.IMAGE_TAG = "${env.APP_NAME}:${env.COMMIT_HASH}"
                }
            }
        }

        stage('Build image') {
            steps {
                sh 'docker build -f Dockerfile -t ${IMAGE_TAG} . '
            }
        }

        stage('Clone repo with manifests') {
            steps {
                sh 'git clone https://github.com/alexandr-cassa/ac-k8s.git'
            }
        }

        stage('Update Argo manifest') {
            steps {
                withCredentials([usernamePassword(
                        credentialsId: 'github-access-k8s',
                        usernameVariable: 'GIT_USERNAME',
                        passwordVariable: 'GIT_TOKEN'
                )]) {
                    sh '''
              cd ac-k8s

              sed -i '/image: g2048:/s|image: .*|image: '"$IMAGE_TAG"'|' "$MANIFEST_FILE"
              git config user.name "jenkins"
              git config user.email "jenkins@example.com"

              git add "$MANIFEST_FILE"
              git commit -m "Update ${APP_NAME} image to ${COMMIT_HASH}" || echo "No changes to commit"

              git push https://${GIT_USERNAME}:${GIT_TOKEN}@github.com/alexandr-cassa/ac-k8s.git "$GIT_BRANCH"
            '''
                }
            }
        }
    }

    post {
        always {
            sh 'rm -rf ac-k8s'
        }
    }
}