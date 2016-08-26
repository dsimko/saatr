#!/bin/bash
set -x

# https://wiki.jenkins-ci.org/display/JENKINS/Building+a+software+project#Buildingasoftwareproject-JenkinsSetEnvironmentVariables
function allVariables(){
    declare -a arr=("NODE_NAME" "NODE_LABELS" "BUILD_URL" "TARGET" "DB" "JDK" "jdk" "EAP_VERSION" "LABEL")
    for i in "${arr[@]}"
    do
    	var=$i
        echo "-F $i=${!var}" 
    done
}

echo "--------------UPLOAD-TESTS-RESULTS-TO-SAATR---------------"
find . -path \*target/surefire-reports/TEST-*.xml | zip testsuite -@
command="curl \
  -u saatr:S44TR! \
  -F \"jobName=${JOB_NAME-testing_job}\" \
  -F \"buildNumber=${BUILD_NUMBER-0}\" \
  -F \"timestamp=$(date +%s)\" \
  -F \"testsuite=@testsuite.zip\" \
  $(allVariables)
  http://46.183.65.66:14414/UploadServlet"
eval ${command}