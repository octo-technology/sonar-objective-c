Sonar Plugin for Objective C
============================

This repository hosts the Objective-C plugin for [SonarQube](http://www.sonarqube.org/). The plugin is in active development and has been bootstrapped with the help of the Sonar team. If you wish to contribute, check the [Contributing](https://github.com/octo-technology/sonar-objective-c/wiki/Contributing) wiki page.

###Features

- [ ] Complexity
- [ ] Design
- [x] Documentation
- [x] Duplications
- [x] Issues
- [x] Size
- [ ] Tests

For more details, see the list of [Sonar metrics](https://github.com/octo-technology/sonar-objective-c/wiki/Features) implemented or pending.

###Download

The latest release is the 0.3.0, and it's available [here](http://repository-rfelden.forge.cloudbees.com/release/org/codehaus/sonar-plugin/objectivec/sonar-objective-c-plugin/0.3.0/sonar-objective-c-plugin-0.3.0.jar).

You can also download the latest build of the plugin from [Cloudbees](https://rfelden.ci.cloudbees.com/job/sonar-objective-c/lastSuccessfulBuild/artifact/target/).

In the worst case, the Maven repository is available here: http://repository-rfelden.forge.cloudbees.com/

###Pre-requisites

- a Mac with Xcode...
- [SonarQube](http://docs.codehaus.org/display/SONAR/Setup+and+Upgrade) and [SonarQube Runner](http://docs.codehaus.org/display/SONAR/Installing+and+Configuring+SonarQube+Runner) installed
- [xctool](https://github.com/facebook/xctool) ([HomeBrew](http://brew.sh) installed and ```brew install xctool```)
- [OCLint](http://docs.oclint.org/en/dev/intro/installation.html) installed. In my case the 0.7 version is failing on my project, so I recommend installing the 0.8.dev version (at least it works with oclint-0.8.dev.2888e0f). 
- [gcovr](http://gcovr.com) installed

###Installation (once for all your Objective-C projects)
- Install [the plugin](http://repository-rfelden.forge.cloudbees.com/release/org/codehaus/sonar-plugin/objectivec/sonar-objective-c-plugin/0.3.0/sonar-objective-c-plugin-0.3.0.jar) through the Update Center (of SonarQube) or download it into the $SONARQUBE_HOME/extensions/plugins directory
- Restart the SonarQube server.

###Configuration (once per project)
- Copy [sonar-project.properties](https://rawgithub.com/octo-technology/sonar-objective-c/master/sample/sonar-project.properties) in your Xcode project root folder (along your .xcodeproj file)
- Edit the *sonar-project.properties* file to match your Xcode iOS/MacOS project
- Copy [run-sonar.sh](https://rawgithub.com/octo-technology/sonar-objective-c/master/script/run-sonar.sh) in your Xcode project root folder and make it executable

**The good news is that you don't have to modify your Xcode project to enable Sonar!**. Ok, there might be one needed modification if you don't have a specific scheme for your test target, but that's all.

###Analysis
- Run the script ```run-sonar.sh``` in your Xcode project root folder
- Enjoy or file an issue!

###Build Status
[![Build Status](https://rfelden.ci.cloudbees.com/job/sonar-objective-c/badge/icon)](https://rfelden.ci.cloudbees.com/job/sonar-objective-c/)

###Credits
* **Cyril Picat**
* **Denis Bregeon**
* **Romain Felden**
* **François Helg**
* **Mete Balci**

###History
- v0.3 (2013/10): added support for OCUnit tests and test coverage
- v0.2 (2013/10): added OCLint checks as Sonar violations
- v0.0.1 (2012/09): v0 with basic metrics such as nb lines of code, nb lines of comment, nb of files, duplications

###License

Sonar Plugin for Objective C is released under the GNU LGPL 3 license:  
http://www.gnu.org/licenses/lgpl.txt
