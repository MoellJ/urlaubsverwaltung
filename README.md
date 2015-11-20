[![Build Status](https://travis-ci.org/synyx/urlaubsverwaltung.png)](https://travis-ci.org/synyx/urlaubsverwaltung)


## Urlaubsverwaltung

* [Übersicht](#übersicht)
    * [Demo System](#demo-system)
    * [Blog Posts](#blog-posts)
    * [Berechtigungen](#berechtigungen)
* [Installation](#installation)
* [Entwicklung](#entwicklung)
* [Hinweise zu Versionen](#hinweise-zu-versionen)
* [Technologien](#technologien)
* [Lizenz](#lizenz)

---

## Übersicht

Die Urlaubsverwaltung ist eine Web-Anwendung, die es ermöglicht, Urlaubsanträge von Mitarbeitern elektronisch zu verwalten. Mitarbeiter stellen Urlaubsanträge, die von den jeweils Berechtigten genehmigt oder abgelehnt werden.
Die Anwendung bietet eine Übersicht über die bestehenden Urlaubsanträge und ermöglicht außerdem Überblick und Pflege von Urlaubsanspruch und Anzahl verbleibender Urlaubstage der Mitarbeiter. Zusätzlich können Krankmeldungen erfasst und überblickt werden.

![Screenshot Urlaubsverwaltung](http://synyx.de/images/opensource/screen_01.jpg)

#### Demo System

Zum Ausprobieren der Anwendung gibt es ein [Demo System](http://urlaubsverwaltung-demo.synyx.de) mit Testbenutzern für die unterschiedlichen Rollen:

| Rolle             | Benutzername  | Passwort |
| ----------------- | ------------- | -------- |
| Office            | test          | secret   |
| Chef              | testBoss      | secret   |
| Abteilungsleiter  | testHead      | secret   |
| Benutzer          | testUser      | secret   |

#### Blog Posts

Weitere Informationen zur Geschichte und Entwicklung der Urlaubsverwaltung findet man im [synyx Blog](http://blog.synyx.de):

* [Stand November 2011](http://blog.synyx.de/2011/11/elektronische-urlaubsverwaltung-made-by-youngsters/)
* [Stand November 2012](http://blog.synyx.de/2012/11/urlaubsverwaltung-was-hat-sich-getan/)
* [Stand Oktober 2014](http://blog.synyx.de/2014/10/urlaubsverwaltung-goes-mobile/)

#### Berechtigungen

In der Urlaubsverwaltung gibt es aktuell folgende Arten von Berechtigungen:

* **inaktiv**: hat keinen Zugang mehr zur Urlaubsverwaltung (Daten des Benutzers bleiben zur Archivierung bestehen)
* **User**: darf Urlaub für sich selbst beantragen
* **Abteilungsleiter**: darf Urlaubsanträge für die Benutzer seiner Abteilungen einsehen, genehmigen und ablehnen
* **Chef**: darf Urlaubsanträge aller Benutzer einsehen, genehmigen und ablehnen
* **Office**: darf Einstellungen zur Anwendung vornehmen, Mitarbeiter verwalten, Urlaub für Mitarbeiter beantragen/stornieren und Krankmeldungen pflegen

Eine aktive Person kann eine oder mehrere Rollen innehaben.

---

## Installation

Um eine aktuelle Version der Urlaubsverwaltung zu installieren, bitte [diese Anleitung](INSTALLATION_AS_JAR.md) befolgen.

Falls noch eine ältere Version (< 2.12.0) der Urlaubsverwaltung verwendet hier, können Details zur Installation und
Konfiguration [hier](INSTALLATION_AS_WAR.md) nachgelesen werden.

---

## Entwicklung

Im Folgenden werden die durchzuführenden Schritte beschrieben, wenn man an der Urlaubsverwaltung entwickeln möchte.

#### Repository clonen

<pre>git clone git@github.com:synyx/urlaubsverwaltung.git</pre>

#### Anwendung starten

Die Urlaubsverwaltung ist eine Spring Boot Anwendung und kann mit dem Maven Plugin gestartet werden.
Ohne weitere Angabe wird das Development-Environment genutzt, d.h. es wird eine H2-Datenbank verwendet und es werden
Testdaten angelegt.

<pre>mvn clean spring-boot:run</pre>

Im Browser lässt sich die Anwendung dann über `http://localhost:8080/` ansteuern.

Im Development-Environment werden standardmäßig für Entwicklungszwecke Benutzer, Urlaubsanträge und Krankmeldungen
angelegt. Daher kann man sich nun mit verschiedenen Testbenutzern anmelden:

* `testUser/secret`: Benutzer mit der Rolle `User`
* `testBoss/secret`: Benutzer mit der Rolle `Boss`
* `testHead/secret`: Benutzer mit der Rolle `DepartmentHead`
* `test/secret`: Benutzer mit der Rolle `Office`

#### Anlegen von Testdaten deaktivieren

Möchte man, dass beim Starten der Anwendung keine Testdaten generiert werden, muss man die Property `testdata.create`
auf `false` setzen.

#### API

Die Urlaubsverwaltung verfügt über eine API, die unter `http://localhost:8080/api` erreichbar ist.

#### Environments

Siehe [Umgebungen](INSTALLATION_AS_WAR.md#umgebungen)

Standardmäßig ohne jegliche Angabe wird als Environment `dev` genutzt. Möchte man ein anderes Environment nutzen,
muss man beim Starten die Environment System Property `env` setzen, z.B.

<pre>mvn clean spring-boot:run -Denv=prod</pre>

#### Authentifizierung

Siehe [Authentifizierung](INSTALLATION_AS_WAR.md#authentifizierung)

Möchte man LDAP oder Active Directory zur Authentifizierung nutzen, setzt man die Property `auth` entweder als System
oder man konfiguriert diese in den `application.properties`.

Hinweis: Die Verbindung zum LDAP / Active Directory muss dafür selbstverständlich korrekt in den
`application.properties` konfiguriert sein.

##### LDAP

Die Anwendung mit dem Parameter `-Dauth=ldap` starten:

<pre>mvn clean spring-boot:run -Dauth=ldap</pre>

Oder die Property `auth` in den `application.properties` setzen:

<pre>auth=ldap</pre>

##### Active Directory

Die Anwendung mit dem Parameter `-Dauth=activeDirectory` starten:

<pre>mvn clean spring-boot:run -Dauth=activeDirectory</pre>

Oder die Property `auth` in den `application.properties` setzen:

<pre>auth=activeDirectory</pre>

---

## Hinweise zu Versionen

#### Version 2.2.1

Wenn man die Urlaubsverwaltung schon länger nutzt und auf Version 2.2.1 oder höher updaten möchte, muss sichergestellt sein, dass in der Datenbank keine Person mit gleichem Vor- und Nachnamen existiert. Dies führt ansonsten zu einem Problem beim Update des Datenbankschemas und die Anwendung kann nicht starten.

#### Version 2.7.0

Die Anwendung hat nicht mehr mehrere unterschiedliche Properties-Dateien, sondern je eine `application.properties` pro Umgebung.
Außerdem heißt die System Property für die Authentifizierungsmethode nicht mehr `spring.profiles.active`, sondern `auth`.
Die fachlichen Einstellungen werden nicht mehr in einer Properties-Datei gepflegt, sondern innerhalb der Anwendung selbst unter dem Menüpunkt "Einstellungen".

---

## Technologien

Die Anwendung basiert auf dem [Spring](http://www.springsource.org/) MVC Framework.
Zur Ermittlung von Feiertagen wird das Framework [Jollyday](http://jollyday.sourceforge.net/) benutzt.
Das Frontend beinhaltet Elemente von [Bootstrap](http://getbootstrap.com/) gewürzt mit einer Prise
[jQuery](http://jquery.com/) und [Font Awesome](http://fontawesome.io/).
Für die Darstellung der Benutzer Avatare wird [Gravatar](http://de.gravatar.com/) benutzt.
Zur Synchronisation der Urlaubs- und Krankmeldungstermine mit einem Microsoft Exchange Kalender wird die [EWS JAVA API](https://github.com/OfficeDev/ews-java-api) genutzt.

## Lizenz

[synyx/urlaubsverwaltung](http://github.com/synyx/urlaubsverwaltung) is licensed under the [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0)

Alle Logos, Marken- und Warenzeichen unterliegen **nicht** der Apache License 2.0 und dürfen nur mit schriftlicher Genehmigung von [synyx](http://www.synyx.de/) weiterverwendet werden.
