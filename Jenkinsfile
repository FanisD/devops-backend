pipeline {
    agent any
    environment {
        DOCKER_IMAGE = "crowdfunding-backend:${env.BUILD_NUMBER}"
    }
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Test') {
            steps {
                sh './mvnw test'
            }
        }
        stage('Build') {
            steps {
                sh './mvnw package -DskipTests'
            }
        }
        stage('Build Docker Image') {
            steps {
                script {
                    sh 'docker build -f nonroot.Dockerfile -t $DOCKER_IMAGE .'
                }
            }
        }
        stage('Publish Docker Image') {
            when {
                expression { env.DOCKER_REGISTRY_CREDENTIALS != null }
            }
            steps {
                withCredentials([usernamePassword(credentialsId: env.DOCKER_REGISTRY_CREDENTIALS, usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh 'echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin'
                    sh 'docker push $DOCKER_IMAGE'
                }
            }
        }
    }
} 