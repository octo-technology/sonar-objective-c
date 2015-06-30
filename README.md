SonarQube Plugin for Objective C
================================

This repository hosts the Objective-C plugin for [SonarQube](http://www.sonarqube.org/). This plugin enables to analyze and track the quality of iOS (iPhone, iPad) and MacOS developments.

This plugin is not supported by SonarSource. SonarSource offers a [commercial SonarSource Objective-C plugin](http://www.sonarsource.com/products/plugins/languages/objective-c/) as well. Both plugins do not offer the same functionalities/support.

The development of this plugin has always been done thanks to the community. If you wish to contribute, check the [Contributing](https://github.com/octo-technology/sonar-objective-c/wiki/Contributing) wiki page.

Find below an example of an iOS SonarQube dashboard:
<p align="center">
  <img src="sample/screen%20shot%20SonarQube%20dashboard.png" alt="Example iOS SonarQube dashboard" width="80%"/>
</p>

###Features

- [x] Complexity
- [ ] Design
- [x] Documentation
- [x] Duplications
- [x] Issues
- [x] Size
- [x] Tests

For more details, see the list of [SonarQube metrics](https://github.com/octo-technology/sonar-objective-c/wiki/Features) implemented or pending.

###Compatibility

- Use 0.3.x releases for SonarQube < 4.3
- Use 0.4.x releases for SonarQube >= 4.3 (4.x and 5.x)

###Download

The latest version is the 0.4.0 and it's available [here](http://bit.ly/18A7OkE).
The latest SonarQube 3.x release is the 0.3.1, and it's available [here](http://bit.ly/1fSwd5I).
  
You can also download the latest build of the plugin from [Cloudbees](https://rfelden.ci.cloudbees.com/job/sonar-objective-c/lastSuccessfulBuild/artifact/target/).
 
In the worst case, the Maven repository with all snapshots and releases is available here: http://repository-rfelden.forge.cloudbees.com/

###Prerequisites

- a Mac with Xcode...
- [SonarQube](http://docs.codehaus.org/display/SONAR/Setup+and+Upgrade) and [SonarQube Runner](http://docs.codehaus.org/display/SONAR/Installing+and+Configuring+SonarQube+Runner) installed ([HomeBrew](http://brew.sh) installed and ```brew install sonar-runner```)
- [xctool](https://github.com/facebook/xctool) ([HomeBrew](http://brew.sh) installed and ```brew install xctool```). If you are using Xcode 6, make sure to update xctool (```brew upgrade xctool```) to a version > 0.2.2.
- [OCLint](http://docs.oclint.org/en/dev/intro/installation.html) installed. Version 0.8.1 recommended  ([HomeBrew](http://brew.sh) installed and ```brew install https://gist.githubusercontent.com/TonyAnhTran/e1522b93853c5a456b74/raw/157549c7a77261e906fb88bc5606afd8bd727a73/oclint.rb```). 
- [gcovr](http://gcovr.com) installed
- [lizard](https://github.com/terryyin/lizard) installed

###Installation (once for all your Objective-C projects)
- Install [the plugin](http://bit.ly/18A7OkE) through the Update Center (of SonarQube) or download it into the $SONARQUBE_HOME/extensions/plugins directory
- Copy [run-sonar.sh](https://rawgithub.com/octo-technology/sonar-objective-c/master/src/main/shell/run-sonar.sh) somewhere in your PATH
- Restart the SonarQube server.

###Configuration (once per project)
- Copy [sonar-project.properties](https://rawgithub.com/octo-technology/sonar-objective-c/master/sample/sonar-project.properties) in your Xcode project root folder (along your .xcodeproj file)
- Edit the *sonar-project.properties* file to match your Xcode iOS/MacOS project

**The good news is that you don't have to modify your Xcode project to enable SonarQube!**. Ok, there might be one needed modification if you don't have a specific scheme for your test target, but that's all.

###Analysis
- Run the script ```run-sonar.sh``` in your Xcode project root folder
- Enjoy or file an issue!

###Update (once per plugin update)
- Install the [latest plugin](http://bit.ly/18A7OkE) version
- Copy [run-sonar.sh](https://rawgithub.com/octo-technology/sonar-objective-c/master/src/main/shell/run-sonar.sh) somewhere in your PATH

If you still have *run-sonar.sh* file in each of your project (not recommended), you will need to update all those files.

###Credits
* **Cyril Picat**
* **Gilles Grousset**
* **Denis Bregeon**
* **François Helg**
* **Romain Felden**
* **Mete Balci**

###History
- v0.4.1 (2015/05): added support for Lizard to implement complexity metrics.
- v0.4.0 (2015/01): support for SonarQube >= 4.3 (4.x & 5.x)
- v0.3.1 (2013/10): fix release
- v0.3 (2013/10): added support for OCUnit tests and test coverage
- v0.2 (2013/10): added OCLint checks as SonarQube violations
- v0.0.1 (2012/09): v0 with basic metrics such as nb lines of code, nb lines of comment, nb of files, duplications

###License

SonarQube Plugin for Objective C is released under the GNU LGPL 3 license:  
http://www.gnu.org/licenses/lgpl.txt
