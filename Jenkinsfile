pipeline {
   agent any
   
   options {
       buildDiscarder(logRotator(numToKeepStr: '3'))
       disableConcurrentBuilds()
   }
   
   stages{
       stage('Checkout') {
           steps{ 
               echo "------------>Checkout<------------"
               checkout([$class: 'GitSCM',
                        branches: [[name: '*/master']],
                        doGenerateSubmoduleConfigurations: false,
                        extensions: [],
                        submoduleCfg: [],
                        url:'https://github.com/andresorozco427/example.git'])
           }  
       }
       stage('Unit Tests') {
           steps{
               echo "------------>Unit Tests<------------"
               bat 'gradle --b ./infraestructura/build.gradle test'
               bat 'gradle --b ./aplicacion/build.gradle test'
               bat 'gradle --b ./dominio/build.gradle test'
           }
       }
       stage('Integration Tests') {
           steps {
               echo "------------>Integration Tests<------------"
           }
       }
       
      stage('Analisis de codigo statico'){
				environment {
				    scannerHome = tool 'Scanner 4.2.0'
				}
			   steps{
	               echo '------------>Analisis de codigo estatico<------------'
	               withSonarQubeEnv('SonarQube') {
	                   bat "\"${scannerHome}/bin/sonar-scanner.bat\""
	               }	
	           }			
		}
   
       stage('Build') {
           steps {
               echo "------------>Build<------------"
               bat 'gradle --b ./infraestructura/build.gradle build -x test'
               bat 'gradle --b ./aplicacion/build.gradle build -x test'
               bat 'gradle --b ./dominio/build.gradle build -x test'
           } 
       }
   }
 
	   post {
	         always {
	             echo 'This will always run'
	         }
	         success {
	             echo 'This will run only if successful'
	             junit '**/build/test-results/test/*.xml'
	         }
		         failure {
	 		echo 'This will run only if failed' 
	 		mail (to: 'andres.orozco@ceiba.com.co',subject: "Failed Pipeline:${currentBuild.fullDisplayName}",
	 		body: "Something is wrongwith ${env.BUILD_URL}")
	 		}
		 }
	       
  }