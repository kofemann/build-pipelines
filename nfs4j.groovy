pipeline {
  agent {
    node {
      label 'openstack && java && small'
    }
  }

   stages {
      stage('Checkout') {
         steps {
            // Get some code from a GitHub repository
            git 'https://github.com/dCache/nfs4j.git'

          }
      }
      
      stage('Build') {
          
        steps {
            // Run Maven on a Unix agent.
            sh "mvn clean package pmd:cpd spotbugs:spotbugs -Dmaven.multiModuleProjectDirectory='${WORKSPACE}'"
         }
      }
   }
   
       post {
        always {
            junit '**/target/surefire-reports/TEST-*.xml'
            archiveArtifacts '**/target/*.jar'
            jacoco ()
            recordIssues enabledForFailure: true, 
                tools: [[tool: [$class: 'MavenConsole']], 
                        [tool: [$class: 'Java']], 
                        [tool: [$class: 'JavaDoc']]]
            recordIssues enabledForFailure: true, tools: [[tool: [$class: 'CheckStyle']]]
            recordIssues enabledForFailure: true, tools: [[tool: [$class: 'SpotBugs']]]
            recordIssues enabledForFailure: true, tools: [[pattern: '**/target/cpd.xml', tool: [$class: 'Cpd']]]
            recordIssues enabledForFailure: true, tools: [[pattern: '**/target/pmd.xml', tool: [$class: 'Pmd']]]
        }
    }
   
}
