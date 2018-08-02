#!groovy

// https://github.com/feedhenry/fh-pipeline-library
@Library('fh-pipeline-library') _

def prLabels = getPullRequestLabels {}

if ( prLabels.contains("test/integration") ) {
    def metricsApiHost
    def repositoryName = "android-sdk"
    def projectName = "test-${repositoryName}-${currentBuild.number}-${currentBuild.startTimeInMillis}"
    def linkToApiMetricsTemplate = "https://raw.githubusercontent.com/aerogear/aerogear-app-metrics/master/openshift-template.yml"
    def apiMetricsTemplateFilename = "api-metrics-template.yml"

    stage('Trust') {
      enforceTrustedApproval('aerogear')
    }

    openshift.withCluster() {
        // Node with privilege to create projects on Wendy
        node ('apb-test') {
            stage ('Deploy app metrics service') {

                sh "curl ${linkToApiMetricsTemplate} > ${apiMetricsTemplateFilename}"

                openshift.newProject(projectName)
                openshift.withProject(projectName) {
                    def routeSelector

                    openshift.newApp( "--file", "${apiMetricsTemplateFilename}" )
                    // Get URL of deployed metricsApi
                    routeSelector = openshift.selector("route", "aerogear-app-metrics")
                    metricsApiHost = "https://" + routeSelector.object().spec.host
                }
            }

            node ('osx') {
                stage('Cleanup osx workspace') {                    
                    // Clean workspace
                    deleteDir()
                }
    
                stage ('Checkout to osx slave') {
                    checkout scm
                }
    
                stage ('Prepare config file') {
                    def servicesConfigJsonPath = "./core/src/test/assets/mobile-services.json"
                    def servicesConfigJson = readJSON file: servicesConfigJsonPath

                    // Update URL for metrics in mobile-services.json file
                    servicesConfigJson.services.each {
                        if ( it.type == "metrics" ) {
                            it.url = metricsApiHost + "/metrics"
                        }
                    }
                    writeJSON(file: servicesConfigJsonPath, json: servicesConfigJson)
                }

                stage ('Run integration test') {
                    sh "./gradlew :core:testDebug -PintegrationTests=true"
                }
            }

            openshift.delete("project", projectName)
        }
    }
}
