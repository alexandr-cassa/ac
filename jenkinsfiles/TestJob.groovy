pipeline {
    agent any

    environment {
        APP_NAME = 'g2048'
        MANIFEST_FILE = 'g2048/deployment.yaml'
        GIT_BRANCH = 'master'
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
                sh '''
                  cd ac-k8s
                  yq -i '(.spec.template.spec.containers[] | select(.image | test("^g2048:")) | .image) = strenv(IMAGE_TAG)' "$MANIFEST_FILE"

                  git config user.name "jenkins"
                  git config user.email "jenkins@example.com"

                  git add ${MANIFEST_FILE}
                  git commit -m "Update ${APP_NAME} image to ${COMMIT_HASH}" || echo "No changes to commit"
                  git push origin ${GIT_BRANCH}
                '''
            }
        }
    }

    post {
        always {
            sh 'rm -rf ac-k8s'
        }
    }
}