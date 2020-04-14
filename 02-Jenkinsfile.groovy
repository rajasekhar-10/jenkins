node {

   stage('Preparation') { 
      // Get some code from a GitHub repository
      git 'https://github.com/jglick/simple-maven-project-with-tests.git'

   }
   stage('Build') {
      // Run the maven build
      sh 'mvn clean package'
   }
   stage('Results') {
      // Archive Artifacts
      junit '**/target/surefire-reports/TEST-*.xml'
      archiveArtifacts 'target/*.jar'
   }
}
