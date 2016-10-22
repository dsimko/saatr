#!/bin/bash

# https://wiki.jenkins-ci.org/display/JENKINS/Building+a+software+project#Buildingasoftwareproject-JenkinsSetEnvironmentVariables
function addAllVariables(){
    declare -a arr=("NODE_NAME" "NODE_LABELS" "BUILD_URL" "TARGET" "DB" "JDK" "jdk" "EAP_VERSION" "LABEL" "OBJECT_STORE_TYPE", "AMQ_DISTRO", "IOTYPE")
    for i in "${arr[@]}"
    do
        var=$i
        echo "-F \"$i=${!var}\""
    done
}

function addReportsIfExists(){
    if [ -f testsuite.zip ]; then
      echo "-F \"testsuite=@testsuite.zip\"" 
    fi
}

echo "--------------UPLOAD-TESTS-RESULTS-TO-SAATR---------------"
if [ -f testsuite.zip ] ; then
    rm testsuite.zip
fi
find . -name \TEST-*.xml -exec zip testsuite.zip {} \;
command="curl \
  -u saatr:S44TR! \
  -F \"jobName=${JOB_NAME-testing_job}\" \
  -F \"buildNumber=${BUILD_NUMBER-0}\" \
  $(addAllVariables)
  $(addReportsIfExists)
  http://46.183.65.66:14414/UploadServlet"
eval ${command}
