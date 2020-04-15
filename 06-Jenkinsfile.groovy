pipeline {
   agent any

   stages {
      stage('Fetch Code') {
         steps {
            git branch: 'master', url: 'https://github.com/ravi2krishna/JavaCalculator-Jenkins.git'
         }
      }
      stage('Unit Testing') {
         steps {
            // using `true` to allow the Pipeline to continue nonetheless
            sh 'mvn test || true'
            junit '**/target/surefire-reports/*.xml'
         }
      }
      stage('QA') {
        when {
            expression {
            currentBuild.currentResult == 'UNSTABLE'
            }
        }
         steps {
            sleep 3
            echo "Application NOT Deployed - Build is Unstable"
         }
      }
      stage('Deploy') {
        when {
            expression {
            currentBuild.currentResult == 'SUCCESS'
            }
        }
         steps {
            sleep 3
            echo "Application Deployed - Build is Stable"
         }
      }
   }
}
