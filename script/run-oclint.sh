#!/bin/sh
projectFile='Stop-tabac.xcodeproj'
scheme='Stop-tabac'
actualFolder='Stop-tabac'

xctool -project $projectFile -scheme $scheme -reporter json-compilation-database:compile_commands.json build

if [ ! -d "$actualFolder/oclint" ]; then
  mkdir $actualFolder/oclint
fi

oclint-json-compilation-database -- -report-type pmd -o $actualFolder/oclint/oclint.xml
