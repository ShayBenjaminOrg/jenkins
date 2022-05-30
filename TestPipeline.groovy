pipeline {
    agent { node { label 'linux' } }
    environment {
        DOCKERHUB_CREDENTIALS=credentials('DockerhubCreds')
    }
    
    def customImage = ""
    stages {
        stage("create dockerfile") {
            sh """
            tee Dockerfile <<-'EOF'
            FROM ubuntu:latest
            RUN touch file-01.txt
EOF
            """
        }
        
        stage("build docker") {
            customImage = docker.build("shayben/shay-test:latest")
        }
        stage("verify dockers") {
            sh "docker images"
        }
        stage("login") {
            sh 'echo $DOCKERHUB_CREDENTIALS | docker login -u $DOCKERHUB_CREDENTIALS_USR --password-stdin'
        }
        stage("push") {
            sh 'docker push shayben/shay-test:latest'
        }
    }
    post {
        always {
            sh 'docker logout'
        }
    }
}
