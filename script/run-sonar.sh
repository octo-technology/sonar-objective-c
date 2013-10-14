#!/bin/sh
## INSTALLATION: script to copy in your Xcode project in the same directory as the .xcodeproj file
## WARNING: edit the parameters section first
## USAGE: ./run-sonar.sh
#

## PARAMETERS

# Your .xcodeproj filename
projectFile='myApplication.xcodeproj'

# The name of your application scheme in Xcode
scheme='myApplication'

# The name of your test scheme in Xcode
testScheme='myApplicationTests'

## END OF PARAMETERS

## SCRIPT

if [ ! -d "sonar-reports" ]; then
  mkdir sonar-reports
fi

# Unit tests 
echo 'Running tests using xctool...'
set -x #echo on
xctool -project $projectFile -scheme $testScheme -sdk iphonesimulator -reporter junit test > sonar-reports/TEST-report.xml
set +x #echo off

# OCLint
echo 'Running OCLint...'
set -x #echo on
# Creating compiler commands and flags for use by OCLint
xctool -project $projectFile -scheme $scheme clean
xctool -project $projectFile -scheme $scheme -reporter json-compilation-database:compile_commands.json build

# Run OCLint with the right set of compiler options
oclint-json-compilation-database -- -report-type pmd -o sonar-reports/oclint.xml
set +x

# SonarQube
echo 'Running SonarQube using SonarQube Runner...'
set -x
sonar-runner
set +x