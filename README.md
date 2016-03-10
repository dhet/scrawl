# Scrawl
Scrawl ist ein Websiteanalystetool zur Generierung von konfigurierbarer Sitemaps. Die Funktionalität wird über ein Commandline Interface zur Verfügung gestellt.

von [David Hettler](mailto:david.hettler@campus.lmu.de) und [Nico Hein](mailto: n.hein@campus.lmu.de)

## Benutzung
Die Syntax des Commandline Interfaces lautet:
```
scrawl <url> [<url>, ...] [-<option>, ...]
```

* ```<url>```: Eine Webseite, die gecrawlt werden soll. Es können mehrere URLs angegeben werden, die nacheinander mit den getroffenen Einstellungen gecrawlt werden.
* ```-<option>```: Optionen für den Crawlprozess. Für eine Auflistung der verfügbaren Optionen kann der Befehl ```scrawl -help``` aufgerufen werden.

## Aufsetzen des Projekts
Das Buildskript ```build.sbt``` ist derzeit nur in der Lage die Projektabhängigkeiten herunterzuladen und das Programm auszuführen. Es ist jedoch nicht in der Lage eine ausführbare .jar Datei auszugeben, wie zunächst angedacht war. Das Programm muss daher aus IntelliJ heraus gestartet werden und die Commandline Parameter über dessen Oberfläche eingestellt werden. Ist das [Scala plugin](https://confluence.jetbrains.com/display/SCA/Scala+Plugin+for+IntelliJ+IDEA) installiert, erstellt IntelliJ beim Öffnen des Projektordners ein vollständig generiertes SBT-Projekt, das ohne weiteres Zutun lauffähig sein sollte. 

## Projektsturktur
* ```src/main/```: enthält das ausführbare Hauptprogramm.
* ```src/cli/```: enthält das Kommandozeilensystem.
* ```src/crawling/```: enthält das Crawlsystem.
* ```src/graph/```: enthält die abstrakte Graphen-Datenstruktur für Sitemaps.
* ```src/webgraph```: enthält die konkrete Graphen-Datenstruktur.
* ```src/test```: enthält eine Tests für den Graphen.
* ```src/analyze```: enthält einen Websiteanalyealgorithmus.
* ```resources/```: enthält die Konfigurationsdatei für Akka.
