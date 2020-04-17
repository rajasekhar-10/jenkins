pipeline {
   agent any
   
   environment {
     // Global Variables
     color = "Blank"
     AWS_ACCESS_KEY_ID = credentials('AWS-ACK')
     AWS_SECRET_ACCESS_KEY = credentials('AWS-SCK')
   }
   
   parameters {
     // Passing build parameters 
     string defaultValue: '20', description: '', name: 'NUM1', trim: false
     string defaultValue: '10', description: '', name: 'NUM2', trim: false
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
      stage('Build Package') {
         steps {
            // Skipping tests as tests executed earlier
            sh 'mvn -Dmaven.test.skip=true package'
         }
      }
      stage('Run App') {
         steps {
            // Run the jar file with parameters
            sh "java -cp target/RaviCalculator-0.1.2.jar com.ravi.cal.RaviCalculator.Calculator ${params.NUM1} ${params.NUM2}"
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
      stage('Fail') {
         options {
            // On failure, retry this stage the specified number of times. 
             retry(3) 
         } 
         steps {
            // Skipping tests as tests executed earlier
            sh 'cat /opt/ravi.txt'
            //sh 'cat /etc/hosts'
         }
      }
   }
    post {
        // This will always run
        always {
            echo "Performing Cleanup"
            sh 'mvn clean'
        }
        // This will run when failed
        failure {
            echo "Build Failed Mail Sent"
        }
        // This will run when succeeded
        success {
            echo "Build Passed Mail Sent"
        }
    }
}
