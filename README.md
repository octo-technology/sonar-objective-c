<p align="left">
  <img src="https://www.backelite.com/wp-content/uploads/2016/09/logo-backelite-square.png" width="100"/>
</p>

| Branch   |      Status                                                                                                                                |
|----------|:------------------------------------------------------------------------------------------------------------------------------------------:|
| master | [![Build Status](https://travis-ci.org/Backelite/sonar-objective-c.svg?branch=master)](https://travis-ci.org/Backelite/sonar-objective-c)  |
| develop| [![Build Status](https://travis-ci.org/Backelite/sonar-objective-c.svg?branch=develop)](https://travis-ci.org/Backelite/sonar-objective-c) |

SonarQube Plugin for Objective-C
================================

This repository is a fork of the open source [SonarQube Plugin for Objective-C](https://github.com/octo-technology/sonar-objective-c). It provides modifications and extra features needed for our internal use.

A SonarQube 5.0 dashboard of the iOS open source project [GreatReader](https://github.com/semweb/GreatReader):
<p align="center">
  <img src="sample/screen%20shot%20SonarQube%20dashboard.png" alt="Example iOS SonarQube dashboard" width="100%"/>
</p>

###Features

| Feature 		| Supported	| Details	|
|---------------|----------|:-----------:|
| Complexity	|YES			| Uses [Lizard](https://github.com/terryyin/lizard)			|
| Design		|NO			|			|
| Documentation	|YES		|			|
| Duplications	|YES		|			|
| Issues		|YES		| Uses [OCLint](http://docs.oclint.org/en/dev/intro/installation.html): 71 rules, and [Faux Pas](http://fauxpasapp.com/): 102 rules|
| Size			|YES		|			|
| Tests			|YES		| Uses [xctool](https://github.com/facebook/xctool), will probably switch to xcodebuild + [xcpretty](https://github.com/supermarin/xcpretty) soon			|
| Code coverage	|YES		| With [gcovr](http://gcovr.com) for project before Xcode 7, otherwise [slather](https://github.com/venmo/slather)|


###Compatibility

Releases available from this repository are compliant with SonarQube 4.3.x and above.

In order not to break compatibility with the original plugin, plugin releases are numbered with 4 digits.

###Faux Pas support

[Faux Pas](http://fauxpasapp.com/) is a wonderful tool to analyse iOS or Mac applications source code, however it is not free. A 30 trial version is available [here](http://fauxpasapp.com/try/).

The plugin runs fine even if Faux Pas is not installed (Faux Pas analysis will be skipped).

###Download

Binary packages are available in the release section.


###Release history

###0.6.0
- SonarQube 6 support. Important : will work with SonarQube 5.x and above only. Will not work anymore with SonarQube 4.5.x anymore.

###0.5.2
- OCLint 0.11.0 support (see https://github.com/Backelite/sonar-objective-c/pull/13)
- Removed required gcovr check in run-sonar.sh (see https://github.com/Backelite/sonar-objective-c/pull/10)
- Fixed issued in run-sonar.sh (see https://github.com/Backelite/sonar-objective-c/pull/2)
- Better test file detection pattern (see https://github.com/Backelite/sonar-objective-c/pull/3/files)

####0.5.1
- Complexity with Lizard !

####0.5.0 (detached from octo project)
- Detached from octo project (to hard to maintain compatibility)
- Removed deprecated API usages for Sonarube 5.3 support

####0.4.0.3 (based on 0.4.0)
- Xcode 7 coverage support (profdata)

####0.4.0.2 (based on 0.4.0)
- Faux Pas support for release 1.5 and 1.6
- Support for multiple projects in a same workspace

####0.4.0.1 (based on 0.4.0)
- Faux Pas support
- Moved OCLint long line threshold to 250
- Add the `plain` reporter at build step to get more information in case of build failure
- Capitalized OCLint rule names


###Prerequisites

- a Mac with Xcode
- [SonarQube](http://docs.codehaus.org/display/SONAR/Setup+and+Upgrade) and [SonarQube Runner](http://docs.codehaus.org/display/SONAR/Installing+and+Configuring+SonarQube+Runner) installed ([HomeBrew](http://brew.sh) installed and ```brew install sonar-runner```)
- [xcpretty](https://github.com/supermarin/xcpretty) (see instructions below)
- [xctool](https://github.com/facebook/xctool) ([HomeBrew](http://brew.sh) installed and ```brew install xctool```). If you are using Xcode 6, make sure to update xctool (```brew upgrade xctool```) to a version > 0.2.2.
- [OCLint](http://oclint-docs.readthedocs.io/en/stable/) installed. Version 0.11.0 recommended. 
- [gcovr](http://gcovr.com) installed for legacy (pre Xcode 7 coverage)
- [slather](https://github.com/SlatherOrg/slather) (```gem install slather```). Version 2.1.0 or above.
- [lizard](https://github.com/terryyin/lizard) ([PIP](https://pip.pypa.io/en/stable/installing/) installed and ```sudo pip install lizard```)
- [Faux Pas](http://fauxpasapp.com/) command line tools installed (optional)

###Installation of xcpretty with JUnit reports fix

At the time, xcpretty needs to be fixed to work with SonarQube. 

To install the fixed version, follow those steps :

	git clone https://github.com/Backelite/xcpretty.git
	cd xcpretty
	git checkout fix/duration_of_failed_tests_workaround
	gem build xcpretty.gemspec
	sudo gem install --both xcpretty-0.2.2.gem

###Code coverage data format

Since Xcode 7, Apple changed its coverage data format to a new format called 'profdata'.
By default this format will be used by the plugin, except if you explicitly force it to legacy mode (for Xcode 6 and below) in your *sonar-project.properties* with this line:

    sonar.objectivec.coverageType=legacy
  

###Installation (once for all your Objective-C projects)
- Download the plugin binary into the $SONARQUBE_HOME/extensions/plugins directory
- Copy [run-sonar.sh](https://rawgithub.com/Backelite/sonar-objective-c/master/src/main/shell/run-sonar.sh) somewhere in your PATH
- Restart the SonarQube server.

###Configuration (once per project)
- Copy [sonar-project.properties](https://rawgithub.com/Backelite/sonar-objective-c/master/sample/sonar-project.properties) in your Xcode project root folder (along your .xcodeproj file)
- Edit the ```sonar-project.properties``` file to match your Xcode iOS/MacOS project

**The good news is that you don't have to modify your Xcode project to enable SonarQube!**. Ok, there might be one needed modification if you don't have a specific scheme for your test target, but that's all.

###Analysis
- Run the script ```run-sonar.sh``` in your Xcode project root folder
- Enjoy or file an issue!

###Update (once per plugin update)
- Install the lastest plugin version
- Copy ```run-sonar.sh``` somewhere in your PATH

If you still have *run-sonar.sh* file in each of your project (not recommended), you will need to update all those files.

###Contributing

Feel free to contribute to this plugin by issuing pull requests to this repository or to the [original one](https://github.com/octo-technology/sonar-objective-c).

###License

SonarQube Plugin for Objective-C is released under the [GNU LGPL 3 license](http://www.gnu.org/licenses/lgpl.txt).
