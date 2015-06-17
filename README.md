# Slim Repo <img src="doc/slimrepo128.png" width="48px"/>
[![Build Status](https://travis-ci.org/slim-gears/slimrepo.svg?branch=master)](https://travis-ci.org/slim-gears/slimrepo) [![Maven Repository](https://img.shields.io/github/release/slim-gears/slimrepo.svg?label=Maven)](https://bintray.com/slim-gears/slimrepo/slimrepo-android/_latestVersion) [![License](https://img.shields.io/badge/License-Apache_2.0-green.svg)](http://opensource.org/licenses/apache2.0.php) [![Android Arsenal](https://img.shields.io/badge/Android_Arsenal-Slim_Repo-brightgreen.svg)](http://android-arsenal.com/details/1/1778) [![Join the chat at https://gitter.im/slim-gears/slimrepo](https://img.shields.io/badge/Gitter-Join_Chat-orange.svg)](https://gitter.im/slim-gears/slimrepo?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
---
### Light-weight modular ORM for Java and Android

The library was inspired by [GreenDAO](http://greendao-orm.com/ "GreenDAO") and [Microsoft Entity Framework Code First](https://msdn.microsoft.com/en-us/data/ee712907) 

**Slim Repo** is intended to completely decouple data persistence logic in your application from the underlying storage. CRUD operations performed using simple, readable, intuitive and type safe syntax. Because of Slim Repo's modular design, it's possible to add any other SQL, NoSQL or In-memory storage support, without changing the user code.

#### Features (design goals)

* **Ease of use** - intuitive, type-safe and highly readable syntax
* **Performance** - annotation processing based, fast, no reflection usage in run-time, *proguard*-friendly
* **Bulk operations support** - *Bulk update* and *bulk delete* are supported
* **Light-weight** - simple and has a low package footprint
* **Modularity** - Underlying storage providers (e.g. *SQLite*) are extensions. Other *SQL* or *NoSQL* storage providers can be used without changing the user code

## Gradle configuration for Android project

**Step 1.** Enable annotation processing for your project (if not enabled yet)
```gradle
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'com.neenbedankt.gradle.plugins:android-apt:1.4'
    }
}

apply plugin: 'com.neenbedankt.android-apt'

configurations {
    apt
}
```
**Step 2.** Add jCenter repository (if not added yet)
```gradle
repositories {
	jcenter()
}
```
**Step 3.** Add dependencies
```gradle
dependencies {
    compile 'com.slimgears.slimrepo:slimrepo-android:0.9.0'
    apt 'com.slimgears.slimrepo:slimrepo-apt:0.9.0'
}

```

## Usage

* [Quick getting started](https://github.com/slim-gears/slimrepo/wiki/Getting-started)
* [Slim Repo Wikipedia (not complete yet)](https://github.com/slim-gears/slimrepo/wiki)
* [Sample android project](https://github.com/slim-gears/slimrepo/tree/master/slimrepo-example)

## Architecture
#### Layer diagram
![](https://raw.githubusercontent.com/slim-gears/slimrepo/master/doc/slimrepo-layers.png)

## License
This project is distributed under [Apache License, Version 2.0](http://opensource.org/licenses/apache2.0.php)
