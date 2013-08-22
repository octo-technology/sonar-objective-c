Sonar Plugin for Objective C
============================
This repository hosts the Objective-C plugin for [Sonar](http://www.sonarqube.org/). The plugin is in an early development stage and has been bootstrapped with the help of the Sonar team. If you wish to contribute, check the [Contributing wiki page](https://github.com/octo-technology/sonar-objective-c/wiki/Contributing).

###Features

- [ ] Complexity
- [ ] Design
- [x] Documentation
- [x] Duplications
- [ ] Issues
- [x] Size
- [ ] Tests

For more details, see the list of [Sonar metrics](https://github.com/octo-technology/sonar-objective-c/wiki/Features) implemented or pending.

###Getting Started
Have a look at the [sample project](https://github.com/octo-technology/sample-sonar-objective-c), it explains how to setup and analyze an Objective-C project.

###Download

The latest release is the 0.0.1, and it's available [here](http://repository-rfelden.forge.cloudbees.com/release/org/codehaus/sonar-plugin/objectivec/sonar-objective-c-plugin/0.0.1/sonar-objective-c-plugin-0.0.1.jar).

You can also download the latest build of the plugin from [Cloudbees](https://rfelden.ci.cloudbees.com/job/sonar-objective-c/lastSuccessfulBuild/artifact/target/).

In the worst case, the Maven repository is available here: http://repository-rfelden.forge.cloudbees.com/

###Installation
- Install the plugin through the Update Center (of SonarQube) or download it into the SONARQUBE_HOME/extensions/plugins directory
- Restart the SonarQube server.

###Build Status
[![Build Status](https://rfelden.ci.cloudbees.com/job/sonar-objective-c/badge/icon)](https://rfelden.ci.cloudbees.com/job/sonar-objective-c/)

###Credits
* **François Helg**, *OCTO Technology*
* **Cyril Picat**, *OCTO Technology*
* **Mete Balci**

###License

Sonar Plugin for Objective C is released under the GNU LGPL 3 license:  
http://www.gnu.org/licenses/lgpl.txt
