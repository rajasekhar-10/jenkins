pipeline {
   agent any
   
   environment {
     // Global Var, but can be overidden by Local var defined in Stage
     color = "Blank"
     // two secret text credentials are assigned to separate environment variables to AWS
     // These credentials would have been configured in Jenkins with their respective credential IDs AWS-ACK & AWS-SCK
     AWS_ACCESS_KEY_ID = credentials('AWS-ACK')
     AWS_SECRET_ACCESS_KEY = credentials('AWS-SCK')
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
        stage('AWS Fetch') {
         steps {
            // AWS IAM Credentials
                echo "Listing contents of an S3 bucket."
                    sh "AWS_ACCESS_KEY_ID=${AWS_ACCESS_KEY_ID} \
                        AWS_SECRET_ACCESS_KEY=${AWS_SECRET_ACCESS_KEY} \
                        AWS_REGION=us-east-1 \
                        aws s3 ls"

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
