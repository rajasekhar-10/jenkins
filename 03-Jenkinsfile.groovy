pipeline {
    agent any 
    stages {
        stage('Build') { 
            steps {
                echo "Build Success"
            }
        }
        stage('Test') { 
            steps {
                echo "Test Success"
            }
        }
        stage('Deploy') { 
            steps {
                echo "Deploy Success"
            }
        }
    }
}
