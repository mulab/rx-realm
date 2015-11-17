# rx-realm
A lightweight wrapper around realm-java which introduces reactive stream semantics to SQL operations.(Inspired by square/sqlbrite)

## Usage

Add the JitPack repository to your build file:
```
repositories {
        // ...
        maven { url "https://jitpack.io" }
}
```
Add the dependency 
```
dependencies {
    compile 'com.github.mulab:rx-realm:1.0.0'
}
```

init in your `Appliction#onCreate` method.
```
RealmConfiguration config = new RealmConfiguration.Builder(context)
    // set configuration for realm, see realm-java's document
    .build();
RealmDatabase.init(config);
```

Query
```
RealmDatabase.createQuery(new Query()<Foo>{
  @Override
  public RealmResults<Foo> call(Realm realm) {
    return realm.where(Foo.class).findAll();
  }
}, Foo.class).subscribe(this);
```
The last parameter of createQuery means watching on `Foo` if it is notified.

Exec
```
RealmDatabase.exec(new Exec() {
    @Override
    public void run(Realm realm) {
      realm.where(Foo.class).findAll().clear();
    }
}, Foo.class).subscribe(this);
```
The last parameter of exec means it will notify `Foo`.
