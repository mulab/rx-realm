package mu.lab.common.rx.realm;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * @author guangchen.
 */
public abstract class Query <T extends RealmObject> {
    public abstract RealmResults<T> call(Realm realm);
}
