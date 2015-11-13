package mu.lab.common.rx.realm;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * @author guangchen.
 */
public class Foo extends RealmObject {
    private Bar bar;
    private RealmList<Baz> bazs;

    public Bar getBar() {
        return bar;
    }

    public void setBar(Bar bar) {
        this.bar = bar;
    }

    public RealmList<Baz> getBazs() {
        return bazs;
    }

    public void setBazs(RealmList<Baz> bazs) {
        this.bazs = bazs;
    }
}
