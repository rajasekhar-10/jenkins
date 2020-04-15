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
            // Using Environmnet variable syntax - ${env.VARIABLE}
            echo "Application NOT Deployed - Build ${env.BUILD_ID} is Unstable"
            echo "Please go to ${BUILD_URL} and verify the build"
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
            echo "Application Deployed - Build ${env.BUILD_ID} is Stable"
            echo "Please go to ${BUILD_URL} and verify the build"
         }
      }
   }
}
