# JDAUtils

**WARNING - There will likely be bugs as this library is in alpha development, if you find any please report them on
GitHub**

This is a utility library for JDA providing a command manager and many other utilities

This library aims to always be on the latest version of JDA, if it isn't currently on the latest version, feel free to
submit a pull request.

# Setup

```groovy
repositories {
    mavenCentral()
    maven {
        url = "https://repo.anutley.me/snapshots"
    }
}

dependencies {
    implementation "net.dv8tion:JDA:VERSION"
    implementation "me.anutley:jdautils:VERSION"
}
```

You can also specify certain modules to include, for example

```groovy
repositories {
    mavenCentral()
    maven {
        url = "https://repo.anutley.me/snapshots"
    }
}

dependencies {
    implementation "net.dv8tion:JDA:VERSION"
    implementation "me.anutley:jdautils-commands:VERSION"
}
```

---

# Donating

If this library helps you, and you want to contribute monetarily to the project, you can find me
on [ko-fi](https://ko-fi.com/anutley).

A massive thanks to anyone who donates.

<sup><sub><em>ps if you donate, you can get an exclusive donator role in
my [Discord Server](https://discord.gg/NtbNhGt3XN) </em></sub></sup>
