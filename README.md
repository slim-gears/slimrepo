# Slim Repo
[![Build Status](https://travis-ci.org/slim-gears/slimrepo.svg?branch=master)](https://travis-ci.org/slim-gears/slimrepo)
[![Maven Repository](https://img.shields.io/github/release/slim-gears/slimrepo.svg?label=Maven)](https://bintray.com/slim-gears/slimrepo/slimrepo-android/_latestVersion)
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Slim%20Repo-brightgreen.svg?style=flat)](http://android-arsenal.com/details/1/1778)
[![Join the chat at https://gitter.im/slim-gears/slimrepo](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/slim-gears/slimrepo?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
### Light-weight modular ORM for Java and Android

##### The library is still under development. Stay tuned for updates.

Background
---

The library was inspired by [GreenDAO](http://greendao-orm.com/ "GreenDAO") and [Microsoft Entity Framework Code First](https://msdn.microsoft.com/en-us/data/ee712907) 

#### Terminology

`Entity` - Data object, POJO 
 
`Repository` - represents abstract working session, *unit-of-work* against ORM

`RepositoryService` - factory, allowing to create `Repository` instances 

#### Features

* **Intuitive syntax** - intuitive, type-safe and highly readable syntax
* **Annotation processing based** - no reflection usage in run-time, *proguard-friendly*
* **Bulk operations support** - *Bulk update* and *bulk delete* are supported
* **Light-weight** - simple and has a low package footprint

## Installation for Android project

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
    compile 'com.slimgears.slimrepo:slimrepo-android:0.7.0'
    apt 'com.slimgears.slimrepo:slimrepo-apt:0.7.0'
}

```
### Usage
* [Quick getting started](https://github.com/slim-gears/slimrepo/wiki/Getting-started)
* [Slim Repo Wikipedia (not complete yet)](https://github.com/slim-gears/slimrepo/wiki)
