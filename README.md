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

Die API Docs können entweder 

* mit ```sbt doc``` generiert werden und liegen anschließend im ```doc/```-Verzeichnis oder
* oder sind auf [dieser Seite](https://www.cip.ifi.lmu.de/~hettler/scrawl) zu finden (VPN erforderlich)

## Projektstruktur
* ```src/main/```: enthält das ausführbare Hauptprogramm.
* ```src/cli/```: enthält das Kommandozeilensystem.
* ```src/crawling/```: enthält das Crawlsystem.
* ```src/graph/```: enthält die abstrakte Graphen-Datenstruktur für Sitemaps.
* ```src/webgraph```: enthält die konkrete Graphen-Datenstruktur.
* ```src/test```: enthält eine Tests für den Graphen.
* ```src/analyze```: enthält einen Websiteanalyealgorithmus.
* ```resources/```: enthält die Konfigurationsdatei für Akka.

## Komponenten
### Crawler
Der Crawler ist mit [akka](http://akka.io/) umgesetzt. Er besteht aus zwei Subsystemen: einem **Collectorsystem** und einem **Crawlersystem**.

* Im Crawlersystem arbeiten die Crawlerworker (Aktoren), die jeweils eine Webseite herunterladen und analysieren. Ist der Vorgang beendet, sendet der Crawler eine Antwort mit der gecrawlten Seite an den Collector. Ein Crawlerworker crawlt alle auf einer Webseite vorkommenden internen Links, indem er für jeden Link einen Worker erstellt und den Job an diese delegiert. Dies ist ein rekursiver Vorgang.
* Der Collector ist ein Aktor und dient als zentrale Anlaufstelle für die Crawlerworker. Hat ein Worker eine Webseite gecrawlt, sendet er das Ergebnis an den Collector. Der Collector fügt den gecrawlten Link daraufhin in die Datenstruktur ein.

Während des Crawl-Vorgangs wird sichergestellt, dass jede Seite nur ein mal heruntergeladen und analysiert wird. Stößt der Crawler auf eine besuchte Seite, wird der Link auf die Seite dennoch in die Datenstruktur eingefügt, auch wenn dieser nicht gecrawlt wird. 

### Datenstrktur
TODO Nico

### Command Line Interface
Das Command Line Interface ist auf Erweiterbarkeit ausgelegt. Neue Befehle, sowie deren Verhalten, können in dem ```Argument``` Companionobjekt definiert werden. Soll beispielsweise ein neuer Analysealgorithmus implementiert werden, muss hierfür ein neues Argument Objekt erstellt werden, das, wenn es vom Commandline Interpreter ausgewertet wird, ein Funktionsobjekt in die Crawlereinstellungen einfügt. Dieses Funktionsobjekt definiert den Analysealgorithmus, der automatisch auf jede gecrawlte Seite ausgeführt wird. Ein Analysealgorithmus könnte beispielsweise eine Liste aller Bilder aus dem HTML der gecrawlten Seite extrahieren. Die Liste der extrahierten Bilder würde daraufhin in der Sitemap unter jedem Seiteneintrag auftauchen.

