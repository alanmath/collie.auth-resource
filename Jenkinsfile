pipeline {
    agent any
    stages {
        stage ('Jenkins Auth') {
            steps {
                echo 'Auth resource'
            }
        }
        stage ('Build interface auth'){
            steps {
                build job: 'auth', wait: true
            }
        }
        stage ('Build interface account'){
            steps {
                build job: 'account', wait: true
            }
        }
        stage ('Build') {
            steps {
                sh 'mvn clean package'
            }
        }
        stage('Build Image') {
            steps {
                script {
                    account = docker.build("alanmath/auth:${env.BUILD_ID}", "-f Dockerfile .")
                }
            }
        }
        stage('Push Image'){
            steps{
                script {
                    docker.withRegistry('https://registry.hub.docker.com', '594871ef-08ca-47da-8365-6dd98ca976e6') {
                        account.push("${env.BUILD_ID}")
                        account.push("latest")
                    }
                }
            }
        }
    }
}