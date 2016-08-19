#!/bin/bash
 
# https://wiki.jenkins-ci.org/display/JENKINS/Building+a+software+project#Buildingasoftwareproject-JenkinsSetEnvironmentVariables
function getJobName(){
	if [[ -v JOB_NAME ]]
	then
	    echo "$JOB_NAME"
	else
	    echo "testing_job"
	fi
}

function getBuildNumber(){
	if [[ -v BUILD_NUMBER ]]
	then
	    echo "$BUILD_NUMBER"
	else
	    # echo "$(( ( RANDOM % 10 )  + 1 ))"
	    echo 0
	fi
}

echo "--------------UPLOAD-TESTS-RESULTS-TO-SAATR---------------"
find . -path \*target/surefire-reports/TEST-*.xml | zip testsuite -@
curl \
  -F "jobName=$(getJobName)" \
  -F "buildNumber=$(getBuildNumber)" \
  -F "testsuite=@testsuite.zip" \
  localhost:8080/UploadServlet

