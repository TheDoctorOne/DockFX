# DockFX
<table>
<tr>
<th><img src="http://i.imgur.com/M69ZWgU.png" ></th>
<th><img src="http://i.imgur.com/yPLbHwy.png" ></th>
</tr>
</table>

## About
This library was created to fill the void for docking frameworks available in the JavaFX RIA platform. Its intention is to provide you with a fully featured docking library. This project and its source code is licensed under the [GNU Lesser General Public License version 3](http://www.gnu.org/licenses/lgpl-3.0.en.html) and you should feel free to make adaptations of this work. Please see the included LICENSE file for further details.

<img src="http://i.imgur.com/b2Oouif.png">

DockFX has a number of features:
* Full documentation
* Gratis and open source
* CSS and styling support
* FXML support 

This version does suppport FXML to some extend. See [BasicFXMLDockPaneAdapter](https://github.com/TheDoctorOne/DockFX/blob/master/src/main/java/org/dockfx/BasicFXMLDockPaneAdapter.java) and [Demo/DemoMainFXML](https://github.com/TheDoctorOne/DockFX/blob/master/src/main/java/org/dockfx/demo/controllers/DemoMainFXML.java) for further information.

Features to be added in a to be determined future version:
* Scene builder integration
* DockBar support for floating toolbars
* Tab pane stacking of dock nodes with draggable headers
* A light docking library using no detachable windows

## Using the Library
You can obtain a binary of the latest jar from the [releases](https://github.com/TheDoctorOne/DockFX/releases) page. The library itself includes a demo as the main class for testing purposes. This demo is always included for the time being as it is very small and not expected to get much bigger. An HTML readme file is included next to the jar but does not need to be distributed with the library. The library and the demo will also work regardless of whether the readme HTML file exists. You should be able to add the library to your class path and use it like any normal library.

## Compiling from Source
The project was originally written in the Eclipse IDE but is also configured for Apache Maven. The project will continue to facilitate development with both command line tools and the Eclipse IDE. Default icons are included from the [Calico icon set](https://github.com/enigma-dev/Calico-Icon) for the dock indicators and title bar.

## Contributing
Adaptations of the project are welcome but you are encouraged to send fixes upstream to the master repository. I use the [Google Java style conventions](https://github.com/google/styleguide) which you can download an Eclipse plugin for. After importing the Eclipse formatter you can use CTRL+SHIFT+F to run the formatter on your code. It is requested that commits sent to this repository follow these conventions. Please see the following [link](https://github.com/HPI-Information-Systems/Metanome/wiki/Installing-the-google-styleguide-settings-in-intellij-and-eclipse) for instructions on configuring the Google style conventions with the Eclipse or IntelliJ IDE.

