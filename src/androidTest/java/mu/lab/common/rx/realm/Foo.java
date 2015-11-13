package mu.lab.common.rx.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author guangchen.
 */
public class Foo extends RealmObject {
    @PrimaryKey
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
