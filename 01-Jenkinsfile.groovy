node {

   stage('Preparation') { // for display purposes
      // Perform Some Action
      sh 'echo Hello!!!'
   }
   stage('Build') {
      // Simulating Build
            sh 'sleep 5'
   }
   stage('Deploy') {
       sh 'echo Bye!!!'
   }
}
