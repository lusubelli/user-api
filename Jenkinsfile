
pipeline {
    agent any
    tools {
        maven 'maven'
        jdk 'jdk8'
    }
    stages  {
        stage ('Initialize') {
            steps {
                sh '''
                    echo "PATH = ${PATH}"
                    echo "M2_HOME = ${M2_HOME}"
                '''
            }
        }
        stage('Clone repository') {
            steps {
                checkout scm
            }
        }
        stage('Build jar') {
            steps {
                sh 'mvn clean install'
            }
        }
        stage('Build Docker Image') {
            steps {
                sh 'oc login -u developer -p developer --insecure-skip-tls-verify localhost:8443'
                sh 'docker login -u developer -p $(oc whoami -t) 172.30.1.1:5000'
                sh 'docker build -t 172.30.1.1:5000/accounting-dev/user-api:1.0.0 .'
                sh 'docker push 172.30.1.1:5000/accounting-dev/user-api:1.0.0'
                sh 'docker logout'
                sh 'oc logout'
            }
        }
    }
}
