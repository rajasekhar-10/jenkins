pipeline {
   agent any
   
   environment {
     // Global Var, but can be overidden by Local var defined in Stage
     color = "Blank"  
   }
   
   stages {
      stage('Fetch Code') {
         steps {
            git branch: 'master', url: 'https://github.com/ravi2krishna/JavaCalculator-Jenkins.git'
            // environment variable used
            echo "The Color is ${color}"
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
        environment {
            // Local Var, scope is within Stage - Deploy 
            color = "Green"  
        }
        when {
            expression {
            currentBuild.currentResult == 'SUCCESS'
            }
        }
         steps {
            sleep 3
            echo "Application Deployed - Build ${env.BUILD_ID} is Stable"
            echo "Please go to ${BUILD_URL} and verify the build"
            echo "The Color is ${color}"
         }
      }
   }
}
