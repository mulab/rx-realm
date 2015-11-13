package mu.lab.common.rx.realm;

import io.realm.RealmObject;
import io.realm.RealmResults;
import rx.Observable;
import rx.Subscriber;

/**
 * Simply copied from
 * https://github.com/square/sqlbrite/blob/master/sqlbrite/src/main/java/com/squareup/sqlbrite/QueryObservable.java
 * The original code has some custom operators where we do not need here, so I omit them.
 * @author guangchen.
 */
public class RealmResultsObservable<T extends RealmObject> extends Observable<RealmResults<T>> {
    protected RealmResultsObservable(final Observable<RealmResults<T>> o) {
        super(new OnSubscribe<RealmResults<T>>() {
            @Override
            public void call(Subscriber<? super RealmResults<T>> subscriber) {
                o.unsafeSubscribe(subscriber);
            }
        });
    }
}
