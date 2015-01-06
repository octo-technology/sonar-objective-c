Sonar Plugin for Objective C
============================

This repository hosts the Objective-C plugin for [SonarQube](http://www.sonarqube.org/). This plugin enables to analyze and track the quality of iOS (iPhone, iPad) and MacOS developments.

The plugin is in active development and has been bootstrapped with the help of the Sonar team. If you wish to contribute, check the [Contributing](https://github.com/octo-technology/sonar-objective-c/wiki/Contributing) wiki page.

Find below an example of an iOS SonarQube dashboard:
<p align="center">
  <img src="sample/screen%20shot%20SonarQube%20dashboard.png" alt="Example iOS SonarQube dashboard" width="80%"/>
</p>

###Features

- [ ] Complexity
- [ ] Design
- [x] Documentation
- [x] Duplications
- [x] Issues
- [x] Size
- [x] Tests

For more details, see the list of [Sonar metrics](https://github.com/octo-technology/sonar-objective-c/wiki/Features) implemented or pending.

###Compatibility

- Use 0.3.x releases for SonarQube 3.x
- Use 0.4.x releases for SonarQube 4.x

###Prerequisites

- a Mac with Xcode...
- [SonarQube](http://docs.codehaus.org/display/SONAR/Setup+and+Upgrade) and [SonarQube Runner](http://docs.codehaus.org/display/SONAR/Installing+and+Configuring+SonarQube+Runner) installed ([HomeBrew](http://brew.sh) installed and ```brew install sonar-runner```)
- [xctool](https://github.com/facebook/xctool) ([HomeBrew](http://brew.sh) installed and ```brew install xctool```)
- [OCLint](http://docs.oclint.org/en/dev/intro/installation.html) installed. Version 0.8.1 recommanded  ([HomeBrew](http://brew.sh) installed and ```brew install https://gist.githubusercontent.com/TonyAnhTran/e1522b93853c5a456b74/raw/157549c7a77261e906fb88bc5606afd8bd727a73/oclint.rb```). 
- [gcovr](http://gcovr.com) installed

###Installation (once for all your Objective-C projects)
- Install [the plugin](http://bit.ly/1fSwd5I) through the Update Center (of SonarQube) or download it into the $SONARQUBE_HOME/extensions/plugins directory
- Restart the SonarQube server.

###Configuration (once per project)
- Copy [sonar-project.properties](https://rawgithub.com/octo-technology/sonar-objective-c/master/sample/sonar-project.properties) in your Xcode project root folder (along your .xcodeproj file)
- Edit the *sonar-project.properties* file to match your Xcode iOS/MacOS project
- Copy [run-sonar.sh](https://rawgithub.com/octo-technology/sonar-objective-c/master/src/main/shell/run-sonar.sh) in your Xcode project root folder and make it executable

**The good news is that you don't have to modify your Xcode project to enable Sonar!**. Ok, there might be one needed modification if you don't have a specific scheme for your test target, but that's all.

###Analysis
- Run the script ```run-sonar.sh``` in your Xcode project root folder
- Enjoy or file an issue!

###Credits
* **Cyril Picat**
* **Denis Bregeon**
* **Romain Felden**
* **François Helg**
* **Mete Balci**
* **Gilles Grousset**

###History
- v0.3.1 (2013/10): fix release
- v0.3 (2013/10): added support for OCUnit tests and test coverage
- v0.2 (2013/10): added OCLint checks as Sonar violations
- v0.0.1 (2012/09): v0 with basic metrics such as nb lines of code, nb lines of comment, nb of files, duplications

###License

Sonar Plugin for Objective C is released under the GNU LGPL 3 license:  
http://www.gnu.org/licenses/lgpl.txt
