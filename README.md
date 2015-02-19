<p align="left">
  <img src="http://www.backelite.com/bkimages/extension/backelite/design/backelite/templates/img/header_logo.png/3840/2160/PNG" width="100"/>
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

- [ ] Complexity
- [ ] Design
- [x] Documentation
- [x] Duplications
- [x] Issues (OCLint: 63 rules, Faux Pas: 102 rules)
- [x] Size
- [x] Tests

###Compatibility

Releases available from this repository are compliant with SonarQube 4.3.x and above.

###Faux Pas support

[Faux Pas](http://fauxpasapp.com/) is a wonderful tool to analyse iOS or Mac applications source code, however it is not free. A 30 trial version is available [here](http://fauxpasapp.com/try/).

The plugin runs fine even if Faux Pas is not installed (Faux Pas analysis will be skipped).

###Download

Binary packages will be available soon...

###Prerequisites

- a Mac with Xcode
- [SonarQube](http://docs.codehaus.org/display/SONAR/Setup+and+Upgrade) and [SonarQube Runner](http://docs.codehaus.org/display/SONAR/Installing+and+Configuring+SonarQube+Runner) installed ([HomeBrew](http://brew.sh) installed and ```brew install sonar-runner```)
- [xctool](https://github.com/facebook/xctool) ([HomeBrew](http://brew.sh) installed and ```brew install xctool```). If you are using Xcode 6, make sure to update xctool (```brew upgrade xctool```) to a version > 0.2.2.
- [OCLint](http://docs.oclint.org/en/dev/intro/installation.html) installed. Version 0.8.1 recommended  ([HomeBrew](http://brew.sh) installed and ```brew install https://gist.githubusercontent.com/TonyAnhTran/e1522b93853c5a456b74/raw/157549c7a77261e906fb88bc5606afd8bd727a73/oclint.rb```). 
- [gcovr](http://gcovr.com) installed
- [Faux Pas](http://fauxpasapp.com/) command line tools installed (optional)

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
