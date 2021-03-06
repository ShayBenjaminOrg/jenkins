pipeline {
    agent { node { label 'linux' } }
    environment {
        DOCKERHUB_CREDENTIALS=credentials('DockerhubCreds')
    }
    
    
    stages {
        stage("create dockerfile") {
            steps {
                sh """
                tee Dockerfile <<-'EOF'
                FROM ubuntu:latest
                RUN touch file-01.txt
EOF
                """
            }
        }
        
        stage("build docker") {
            steps {
                sh "docker build -t shayben/shay-test:latest ."
//                docker.build("shayben/shay-test:latest")
            }
        }
        stage("verify dockers") {
            steps {
                sh "docker images"
            }
        }
        stage("login") {
            steps {
                sh 'echo $DOCKERHUB_CREDENTIALS_PSW | docker login -u $DOCKERHUB_CREDENTIALS_USR --password-stdin'
            }
        }
        stage("push") {
            steps {
                sh 'docker push shayben/shay-test:latest'
            }
        }
    }
    post {
        always {
            sh 'docker logout'
        }
    }
}