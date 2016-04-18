** Deprecated! From realm-java 0.87.0 and on, it has builtin observable support **

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-rx--realm-green.svg?style=true)](https://android-arsenal.com/details/1/2803)
[![Build Status](https://travis-ci.org/mulab/rx-realm.svg?branch=master)](https://travis-ci.org/mulab/rx-realm)
# rx-realm
A lightweight wrapper around realm-java which introduces reactive stream semantics to SQL operations.(Inspired by square/sqlbrite)

## Setup

Add the JitPack repository to your build file:
```gradle
repositories {
    // ...
    maven { url "https://jitpack.io" }
}
```

Add the dependency:
```gradle
dependencies {
    compile 'com.github.mulab:rx-realm:1.2.0'
    compile 'io.realm:realm-android:0.86.1'
}
```

Initialize (in `Appliction#onCreate` method for Android App):
```java
RealmConfiguration config = new RealmConfiguration.Builder(context)
    // set configuration for realm, see realm-java's document
    .build();
RealmDatabase.init(config);
```

## Usages

Assume that `Foo` is a realm data model, i.e. `Foo extends RealmObject`.([see more in Realm-java's documentation](https://realm.io/docs/java/latest/#models))

### Query
```java
RealmDatabase.createQuery(new Query()<Foo>{
    @Override
    public RealmResults<Foo> call(Realm realm) {
        return realm.where(Foo.class).findAll();
    }
}, Foo.class).subscribe(this);
```
The last parameter of createQuery means watching on `Foo` if it is notified.

### Exec
```java
RealmDatabase.exec(new Exec() {
    @Override
    public void run(Realm realm) {
        realm.where(Foo.class).findAll().clear();
    }
}, Foo.class).subscribe(this);
```
The last parameter of exec means it will notify `Foo`.
