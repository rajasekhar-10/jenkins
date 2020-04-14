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
            // `mvn test` returns non-zero on test failures
            // using `true` to allow the Pipeline to continue nonetheless
            sh 'mvn test || true'
            junit 'target/surefire-reports/*.xml'
         }
      }
      stage('Deploy') {
         steps {
            sleep 3
            echo "Application Deployed"
         }
      }
   }
}
