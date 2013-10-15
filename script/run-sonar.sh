#!/bin/sh
## INSTALLATION: script to copy in your Xcode project in the same directory as the .xcodeproj file
## USAGE: ./run-sonar.sh
## DEBUG: ./run-sonar.sh -v
## WARNING: edit your project parameters in sonar-project.properties rather than modifying this script
#

trap "echo 'Script interrupted by Ctrl+C'; exit -1" SIGHUP SIGINT SIGTERM

## COMMAND LINE OPTIONS
vflag=""
oclint="on"
while [ $# -gt 0 ]
do
    case "$1" in
    -v)	vflag=on;;
	-nooclint) oclint="";;	    
	--)	shift; break;;
	-*)
                echo >&2 \
		"usage: $0 [-v]"
		exit 1;;
	*)	break;;		# terminate while loop
    esac
    shift
done

## READ PARAMETERS from sonar-project.properties

# Your .xcodeproj filename
projectFile=`sed '/^\#/d' sonar-project.properties | grep 'sonar.objectivec.projectFile' | tail -n 1 | cut -d "=" -f2- | sed 's/^[[:space:]]*//;s/[[:space:]]*$//'`
returnValue=$?

# The name of your application scheme in Xcode
appScheme=`sed '/^\#/d' sonar-project.properties | grep 'sonar.objectivec.appScheme' | tail -n 1 | cut -d "=" -f2- | sed 's/^[[:space:]]*//;s/[[:space:]]*$//'`
returnValue=$?

# The name of your test scheme in Xcode
testScheme=`sed '/^\#/d' sonar-project.properties | grep 'sonar.objectivec.testScheme' | tail -n 1 | cut -d "=" -f2- | sed 's/^[[:space:]]*//;s/[[:space:]]*$//'`
returnValue=$?

# The file patterns to exclude from coverage report
excludedPathsFromCoverage=`sed '/^\#/d' sonar-project.properties | grep 'sonar.objectivec.excludedPathsFromCoverage' | tail -n 1 | cut -d "=" -f2- | sed 's/^[[:space:]]*//;s/[[:space:]]*$//'`
returnValue=$?

if [ "$vflag" = "on" ]; then
 	echo "Xcode project file is: $projectFile"
 	echo "Xcode application scheme is: $appScheme"
 	echo "Xcode test scheme is: $testScheme"
 	echo "Excluded paths from coverage are: $excludedPathsFromCoverage" 	
fi

if [[ $returnValue != 0 ]] ; then
    exit $returnValue
fi

## SCRIPT

# Create sonar-reports/ for reports output
if [ ! -d "sonar-reports" ]; then
	if [ "$vflag" = "on" ]; then
		echo 'Creating directory sonar-reports/'
	fi
	mkdir sonar-reports
	if [[ $? != 0 ]] ; then
    	exit $?
	fi
fi

# Extracting project information needed later
echo 'Extracting Xcode project information...'
if [ "$vflag" = "on" ]; then
	set -x #echo on
	# Creating compiler commands and flags for use by OCLint
	xctool -project $projectFile -scheme $appScheme -sdk iphonesimulator clean
	xctool -project $projectFile -scheme $appScheme -sdk iphonesimulator -reporter json-compilation-database:compile_commands.json build
	returnValue=$?
	set +x #echo off
else 
	xctool -project $projectFile -scheme $appScheme -sdk iphonesimulator clean > /dev/null
	xctool -project $projectFile -scheme $appScheme -sdk iphonesimulator -reporter json-compilation-database:compile_commands.json build > /dev/null
	returnValue=$?
fi
if [[ $returnValue != 0 ]] ; then
    exit $returnValue
fi


# Unit tests and coverage
echo 'Running tests using xctool...'
if [ "$vflag" = "on" ]; then
	set -x #echo on
	xctool -project $projectFile -scheme $testScheme -sdk iphonesimulator -reporter junit GCC_GENERATE_TEST_COVERAGE_FILES=YES GCC_INSTRUMENT_PROGRAM_FLOW_ARCS=YES test > sonar-reports/TEST-report.xml
	returnValue=$?
	set +x #echo off
else 
	xctool -project $projectFile -scheme $testScheme -sdk iphonesimulator -reporter junit GCC_GENERATE_TEST_COVERAGE_FILES=YES GCC_INSTRUMENT_PROGRAM_FLOW_ARCS=YES test > sonar-reports/TEST-report.xml
	returnValue=$?
fi
if [[ $returnValue != 0 ]] ; then
    exit $returnValue
fi

echo 'Computing coverage report...'
# Extract the path to the .gcno/.gcda coverage files
coverageFilesPath=$(grep 'command' compile_commands.json | sed 's#^.*-o \\/#\/#;s#",##' | awk 'NR<2' | xargs dirname)
if [ "$vflag" = "on" ]; then
	echo "Path for .gcno/.gcda coverage files is: $coverageFilesPath"
fi

# Build the --exclude flags
excludedCommandLineFlags=""
echo $excludedPathsFromCoverage | sed -n 1'p' | tr ',' '\n' > tmpFileRunSonarSh
while read word; do
	excludedCommandLineFlags+=" --exclude $word"
done < tmpFileRunSonarSh
rm -rf tmpFileRunSonarSh
if [ "$vflag" = "on" ]; then
	echo "Command line exclusion flags for gcovr is: $excludedCommandLineFlags"
fi

if [ "$vflag" = "on" ]; then
	set -x #echo on
	gcovr -r . $coverageFilesPath $excludedCommandLineFlags --xml > sonar-reports/coverage.xml
	returnValue=$?
	set +x #echo off
else
	gcovr -r . $coverageFilesPath $excludedCommandLineFlags --xml > sonar-reports/coverage.xml
	returnValue=$?
fi
if [[ $returnValue != 0 ]] ; then
    exit $returnValue
fi

if [ "$oclint" = "on" ]; then

	# OCLint
	echo 'Running OCLint...'
	# Run OCLint with the right set of compiler options
	if [ "$vflag" = "on" ]; then
		set -x #echo on
		oclint-json-compilation-database -- -report-type pmd -o sonar-reports/oclint.xml
		returnValue=$?
		set +x #echo off
	else
		oclint-json-compilation-database -- -report-type pmd -o sonar-reports/oclint.xml
		returnValue=$?
	fi
	# Exit code 5 for max of violations exceeded
	if [[ ( $returnValue != 0 ) && ( $returnValue != 5 ) ]] ; then
	    exit $returnValue
	fi
else
	echo 'Skipping OCLint (test purposes only!)'
fi

# SonarQube
echo 'Running SonarQube using SonarQube Runner...'
if [ "$vflag" = "on" ]; then
	set -x #echo on
	sonar-runner
	returnValue=$?
	set +x #echo off
else
	sonar-runner > /dev/null
fi

if [[ $returnValue != 0 ]] ; then
    exit $returnValue
fi
