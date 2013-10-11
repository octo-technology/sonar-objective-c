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

## END OF PARAMETERS


## SCRIPT
echo 'Creating compiler commands and flags for use by OCLint...'
set -x #echo on
xctool -project $projectFile -scheme $scheme clean
xctool -project $projectFile -scheme $scheme -reporter json-compilation-database:compile_commands.json build
set +x #echo off

if [ ! -d "oclint" ]; then
  mkdir oclint
fi

# Run OCLint with the right set of compiler options
echo 'Running OCLint...'
set -x
oclint-json-compilation-database -- -report-type pmd -o oclint/oclint.xml
set +x

echo 'Running SonarQube using SonarQube Runner...'
set -x
sonar-runner
set +x