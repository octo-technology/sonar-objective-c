# SonarQube Objective-C (Community) Plugin

This repository hosts the Objective-C plugin for
[SonarQube](http://www.sonarqube.org/).  This plugin aims to analyze and track
the quality of iOS (iPhone, iPad) and MacOS projects, but it can be used with
other Objective-C projects.

This plugin is not supported by SonarSource.  SonarSource offers a
[commercial SonarSource Objective-C plugin](http://www.sonarsource.com/products/plugins/languages/objective-c/)
as well.  The plugins do not offer the same features/support.

The development of this plugin has always been done thanks to the community.
If you wish to contribute, check the
[Contributing](https://github.com/octo-technology/sonar-objective-c/wiki/Contributing)
wiki page.

## Features

- [x] Complexity
- [ ] Design
- [x] Documentation
- [x] Duplications
- [x] Issues
- [x] Size
- [x] Tests

For more details, see the list of
[SonarQube metrics](https://github.com/octo-technology/sonar-objective-c/wiki/Features)
implemented or pending.


## Compatibility

- Use 0.3.x releases for SonarQube < 4.3
- Use 0.4.0 or later releases for SonarQube >= 4.3 (4.x and 5.x)


## Download

The latest version is the 0.4.0 and it's available [here](http://bit.ly/18A7OkE).
The latest SonarQube 3.x release is the 0.3.1, and it's available [here](http://bit.ly/1fSwd5I).
  
You can also download the latest build of the plugin from
[Cloudbees](https://rfelden.ci.cloudbees.com/job/sonar-objective-c/lastSuccessfulBuild/artifact/target/).
 
In the worst case, the Maven repository with all snapshots and releases is
available here: http://repository-rfelden.forge.cloudbees.com/


## Prerequisites

- A Mac with Xcode
- Optional: [Homebrew](http://brew.sh) for easier prerequisite installation
- [SonarQube](http://docs.codehaus.org/display/SONAR/Setup+and+Upgrade)
- [SonarQube Runner](http://docs.codehaus.org/display/SONAR/Installing+and+Configuring+SonarQube+Runner) (```brew install sonar-runner```)

### JUnit

Any of the following will produce JUnit XML reports for your project:

- [xctool](https://github.com/facebook/xctool) (```brew install xctool```)
  - This is actually a substitute for xcodebuild, so it will compile your app and run tests as part of the process
  - Make sure the version of xctool you use is compatible with the version of Xcode you will be building with
- [xcpretty](https://github.com/supermarin/xcpretty) (```gem install xcpretty```)
  - This will parse xcodebuild's output and prettify it, with the option of generating a JUnit XML report and/or JSON compilation database
- [ocunit2junit](https://github.com/ciryon/OCUnit2JUnit) (```gem install ocunit2junit```)
  - This will parse xcodebuild's output and generate a JUnit XML report


### Coverage

Run your tests with code coverage enabled, then run one of the following tools
to produce a Cobertura XML report which can be imported by this plugin.

- With Xcode prior to version 7, use [gcovr](http://gcovr.com) to parse ```*.gcda``` and ```*.gcno``` files
- With Xcode 7 or greater, use [slather](https://github.com/venmo/slather) to parse the ```Coverage.profdata``` file
  - Note: at time of writing, support for the ```*.profdata``` format has not been released, but can be installed by running ```gem install specific_install && gem specific_install -l https://github.com/viteinfinite/slather.git -b 61f00988e6ad65f817ba81b08533cf78615fff16```
  - Note: at time of writing, xctool is not capable of producing coverage data when using Xcode 7+


### Clang

[Clang Static Analyzer](http://clang-analyzer.llvm.org/) can produce Plist
reports which can be imported by this plugin.

There are different ways to produce the reports, such as using Clang's
[scan-build](http://clang-analyzer.llvm.org/scan-build.html), however it's
probably easiest to use xcodebuild's ```analyze``` action.  Xcodebuild will
invoke the analyzer with all the proper arguments and use any additional
analyzer configuration from your settings under ```*.xcodeproj```.  By default,
it will produce the Plist reports this plugin needs.


### OCLint

[OCLint](http://docs.oclint.org/en/dev/intro/installation.html) version 0.8.1 recommended
(```brew install https://gist.githubusercontent.com/TonyAnhTran/e1522b93853c5a456b74/raw/157549c7a77261e906fb88bc5606afd8bd727a73/oclint.rb```).
Use it to produce a PMD-formatted XML report that can be imported by this
plugin.


### Complexity

Use [Lizard](https://github.com/terryyin/lizard) (```pip install lizard```)
to produce an XML report that can be imported by this plugin.


## Installation (once for all your Objective-C projects)

1. Install [the plugin](http://bit.ly/18A7OkE) through the Update Center (of SonarQube) or download it into the $SONARQUBE_HOME/extensions/plugins directory
2. Restart the SonarQube server.


## Configuration (once per project)

Create a ```sonar-project.properties``` file defining some basic project
configuration and the location of the reports you want to import.

The good news is that you don't have to modify your Xcode project to enable
SonarQube!  Ok, there might be needed modification if you don't have a
specific scheme for your test target or if coverage is not enabled, but that's all.


## Analysis

- Run your build script to produce the various reports
- Run ```sonar-runner```
- Enjoy or file an issue!


## Troubleshooting

If the results from a report don't show up, make sure any relative file or
directory paths in the report match the paths of the files as configured
and indexed by SonarQube.  Typically files are indexed relative to the base
directory of the project/module being analyzed.

For JUnit reports, most tools only record the classname, so make sure your
```*.m``` file is named the same as the classname it contains.  Otherwise, the
plugin won't be able to find the test class.


## Update

- Install the [latest plugin](http://bit.ly/18A7OkE) version
- Check for documented migration steps

### Migration to v0.5.x

- Analysis property names have changed.  Check the sample.
- Cobertura, JUnit, OCLint, and Lizard report properties no longer have defaults
- Coverage report property no longer supports pattern matching.  Only one coverage XML file is supported.
- ```sonar.language``` key changed from ```objc``` to ```objectivec```
  - As a side effect you may have to reconfigure your Quality Profiles


## Contributors

- Cyril Picat
- Gilles Grousset
- Denis Bregeon
- François Helg
- Romain Felden
- Mete Balci
- Andrés Gil Herrera
- Matthew DeTullio


## History

- v0.5.0 (2015/11):
  - added support for Clang
  - made properties configurable in SonarQube UI
  - major refactoring
  - decouple report imports from the language so they can also be used with the commercial plugin
- v0.4.1 (2015/05): added support for Lizard to implement complexity metrics.
- v0.4.0 (2015/01): support for SonarQube >= 4.3 (4.x & 5.x)
- v0.3.1 (2013/10): fix release
- v0.3 (2013/10): added support for OCUnit tests and test coverage
- v0.2 (2013/10): added OCLint checks as SonarQube violations
- v0.0.1 (2012/09): v0 with basic metrics such as nb lines of code, nb lines of comment, nb of files, duplications


## License

SonarQube Plugin for Objective-C is released under the GNU LGPL 3 license:
http://www.gnu.org/licenses/lgpl.txt
