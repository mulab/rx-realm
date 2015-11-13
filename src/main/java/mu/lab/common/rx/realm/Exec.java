package mu.lab.common.rx.realm;

import io.realm.Realm;

/**
 * @author guangchen.
 */
public abstract class Exec {
    public abstract void run(Realm realm);
}
