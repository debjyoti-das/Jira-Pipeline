def CONTAINER_NAME="app-pipeline-332488"
def CONTAINER_TAG="latest"
def DOCKER_HUB_USER="debjyotidas"
def HTTP_PORT="8090"


node {

        def app_name = 'app-pipeline-332488'
        def app_dockerfile_name = 'Dockerfile'
        def app_container_name = 'app-pipeline-332488'
        def app_tag="latest"


    stage('Initialize'){
        def dockerHome = tool 'myDocker'
        def mavenHome  = tool 'myMaven'
        env.PATH = "${dockerHome}/bin:${mavenHome}/bin:${env.PATH}"
    }

    stage('Checkout') {
        checkout scm
    }

    stage('Build'){
	try {
		sh "mvn clean install"

		def searchResults = jiraJqlSearch jql: "project = DEBJ AND resolution = Unresolved", site: 'JIRA'
		def issues = searchResults.data.issues

		if ( issues.size() > 0) {
			for (i = 0; i <issues.size(); i++) {
				def transitionInput = [ transition: [ id: 31] ]
				response = jiraTransitionIssue idOrKey: issues[i].key, input: transitionInput, site: 'JIRA'

				echo response.successful.toString()
				echo response.data.toString()
			}
		}
	}
	catch (exc) {
		echo exc.getMessage()
        	def searchResults = jiraJqlSearch jql: "project = DEBJ", site: 'JIRA'
		def issues = searchResults.data.issues

		if ( issues.size() > 0) {
			for (i = 0; i <issues.size(); i++) {
				def comment = [ body: 'Build is still failing' ]
				response = jiraAddComment idOrKey: issues[i].key, input: comment, site: 'JIRA'

				echo response.successful.toString()
				echo response.data.toString()
			}
		}
		else {

			echo 'Build failed, Creating issue in JIRA!'

			def testIssue = [fields: [ project: [key: 'DEBJ'],
                                 	summary: 'Build failed in Jenkins.',
                                 	description: 'Build failed in Jenkins.',
                                 	issuetype: [name : 'Task']]]

			response = jiraNewIssue issue: testIssue, site: 'JIRA'

			echo response.successful.toString()
			echo response.data.toString()
		}
	}
    }

    stage('Sonar'){
    	try {
        	sh "mvn sonar:sonar -Dsonar.host.url=http://51.137.24.134/debjyoti/sonarqube -Dsonar.login=c8554f519b55dd83d85968887427498f3d30daca"
		
		def searchResults = jiraJqlSearch jql: "project = SONA AND resolution = Unresolved", site: 'JIRA'
		def issues = searchResults.data.issues

		if ( issues.size() > 0) {
			for (i = 0; i <issues.size(); i++) {
				def transitionInput = [ transition: [ id: 31] ]
				response = jiraTransitionIssue idOrKey: issues[i].key, input: transitionInput, site: 'JIRA'

				echo response.successful.toString()
				echo response.data.toString()
			}
		}
	} 
	catch(error){
        	echo "The sonar server could not be reached ${error}"
        	def searchResults = jiraJqlSearch jql: "project = SONA", site: 'JIRA'
		def issues = searchResults.data.issues

		if ( issues.size() > 0) {
			for (i = 0; i <issues.size(); i++) {
				def comment = [ body: 'Static code analysis is still failing' ]
				response = jiraAddComment idOrKey: issues[i].key, input: comment, site: 'JIRA'

				echo response.successful.toString()
				echo response.data.toString()
			}
		}
		else {

			echo 'Static code analysis failed, Creating issue in JIRA!'

			def testIssue = [fields: [ project: [key: 'SONA'],
                                 	summary: 'Static code analysis failed in Sonar.',
                                 	description: 'Static code analysis failed in Sonar.',
                                 	issuetype: [name : 'Task']]]

			response = jiraNewIssue issue: testIssue, site: 'JIRA'

			echo response.successful.toString()
			echo response.data.toString()
		}
        }
    }


    stage('Build and Push to Docker Registry'){
                withCredentials([usernamePassword(credentialsId: 'dockerHubAccount', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
                        sh ("docker login -u ${USERNAME} -p ${PASSWORD}")
                        sh ("docker build -t ${USERNAME}/${app_name}:${BUILD_NUMBER} --pull --no-cache .")
                        sh ("docker push ${USERNAME}/${app_name}:${BUILD_NUMBER}")
                }
                echo "Image push complete"
    }

    stage('Deploy Application on K8s') {
    	try {
                sh("curl -LO https://storage.googleapis.com/kubernetes-release/release/\$(curl -s https://storage.googleapis.com/kubernetes-release/release/stable.txt)/bin/linux/amd64/kubectl")
                sh("chmod +x ./kubectl")
                sh("cat ./${app_name}.yaml | sed s/1.0.0/${BUILD_NUMBER}/g | ./kubectl apply -f -")
                echo "Application started on port: HTTP_PORT (http)"

                def searchResults = jiraJqlSearch jql: "project = DEPL AND resolution = Unresolved", site: 'JIRA'
                def issues = searchResults.data.issues

                if ( issues.size() > 0) {
                        for (i = 0; i <issues.size(); i++) {
                                def transitionInput = [ transition: [ id: 31] ]
                                response = jiraTransitionIssue idOrKey: issues[i].key, input: transitionInput, site: 'JIRA'

                                echo response.successful.toString()
                                echo response.data.toString()
                        }
                }
        }
        catch(exc){
		echo exc.getMessage()
                def searchResults = jiraJqlSearch jql: "project = DEPL", site: 'JIRA'
                def issues = searchResults.data.issues

                if ( issues.size() > 0) {
                        for (i = 0; i <issues.size(); i++) {
                                def comment = [ body: 'Deploy to k8s is still failing' ]
                                response = jiraAddComment idOrKey: issues[i].key, input: comment, site: 'JIRA'

                                echo response.successful.toString()
                                echo response.data.toString()
                        }
                }
                else {

                        echo 'Deploy to k8s failed, Creating issue in JIRA!'

                        def testIssue = [fields: [ project: [key: 'DEPL'],
                                        summary: 'Deploy to k8s failed.',
                                        description: 'Deploy to k8s failed.',
                                        issuetype: [name : 'Task']]]

                        response = jiraNewIssue issue: testIssue, site: 'JIRA'

                        echo response.successful.toString()
                        echo response.data.toString()
                }
        }
    }

}

