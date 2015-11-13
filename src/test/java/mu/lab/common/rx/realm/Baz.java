package mu.lab.common.rx.realm;

import io.realm.RealmObject;

/**
 * @author guangchen.
 */
public class Baz extends RealmObject {
    private Foo foo;
    private Bar bar;

    public Foo getFoo() {
        return foo;
    }

    public void setFoo(Foo foo) {
        this.foo = foo;
    }

    public Bar getBar() {
        return bar;
    }

    public void setBar(Bar bar) {
        this.bar = bar;
    }
}
