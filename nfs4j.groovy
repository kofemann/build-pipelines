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
        recordIssues tools: [java(), javaDoc()], aggregatingResults: 'true', id: 'java', name: 'Java'
        recordIssues tools: [checkStyle(), spotBugs(pattern: 'target/spotbugsXml.xml'), cpd(pattern: 'target/cpd.xml')]
    }
  }
}
