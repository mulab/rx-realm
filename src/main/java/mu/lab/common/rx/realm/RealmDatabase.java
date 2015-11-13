package mu.lab.common.rx.realm;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.exceptions.RealmException;
import mu.lab.util.Log;
import rx.Observable;
import rx.functions.Func1;
import rx.subjects.PublishSubject;

/**
 * @author guangchen.
 */
public class RealmDatabase {
    private static final String LOG_TAG = RealmDatabase.class.toString();
    private static final ThreadLocal<Realm> realmCache = new ThreadLocal<>();
    private static final Set<String> INIT_VALUE = Collections.singleton("<init>");
    private static RealmConfiguration configuration;
    private static final PublishSubject<Set<String>> triggers = PublishSubject.create();
    public static void init(RealmConfiguration configuration) {
        RealmDatabase.configuration = configuration;
    }

    /**
     * Create a query which watches given tables and emit new query results every time watches get
     * updated.
     * Note: the realm used in the query are not closed.
     * But if you create query from the same thread, the rc of realm will not increase.
     * So it's wise that you never call {@link RealmDatabase#close()}.
     * @param query see {@link Query}
     * @param watches when watches table got insert/update/delete, the query will update
     * @param <T> a realm object type
     * @return {@link RealmResults} as observable
     */
    @SafeVarargs
    public static <T extends RealmObject> RealmResultsObservable<T> createQuery(final Query<T> query, final Class<? extends RealmObject>... watches) {
        Observable<RealmResults<T>> resultsObservable = triggers
            .filter(new Func1<Set<String>, Boolean > () {
                @Override
                public Boolean call(Set<String> classes) {
                    for (Class<? extends RealmObject> clazz : watches) {
                        if (classes.contains(clazz.getCanonicalName())) {
                            return true;
                        }
                    }
                    return false;
                }
            })
            .startWith(INIT_VALUE)
            .map(new Func1<Set<String>, RealmResults<T>>() {
                @Override
                public RealmResults<T> call(Set<String> classes) {
                    Realm realm = realmCache.get();
                    if (realm == null) {
                        realm = Realm.getInstance(configuration);
                        realmCache.set(realm);
                    }
                    return query.call(realm);
                }
            }); /* sqlbrite add a custom back pressure operator by lift here
            aims at reducing pressures when it is rapidly triggered
            */
        return new RealmResultsObservable<>(resultsObservable);
    }

    /**
     * Close realm for current thread.
     */
    public static void close() throws IllegalStateException {
        Realm realm = realmCache.get();
        if (realm == null) {
            throw new IllegalStateException("realm already closed");
        }
        realm.close();
        realmCache.set(null);
    }

    /**
     * Insert or update a {@link RealmObject}.
     * The object must have {@link io.realm.annotations.PrimaryKey}.
     * @param object object to insert or udpate
     * @return Observable for the convenience of control worker thread.
     */
    public static Observable<Void> insertOrUpdate(final RealmObject object) {
        return Observable.just(object).map(new Func1<RealmObject, Void>() {
            @Override
            public Void call(final RealmObject t) {
                Realm realm = Realm.getInstance(configuration);
                executeTransaction(realm, new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.copyToRealmOrUpdate(t);
                    }
                });
                realm.close();
                return null;
            }
        }).map(new Func1<Void, Void>() {
            @SuppressWarnings("unchecked")
            @Override
            public Void call(Void aVoid) {
                final Set<String> toNotify = Util.collectRelatedRealmClass(object.getClass());
                sendTableTrigger(toNotify);
                return null;
            }
        });
    }

    /**
     * Exec an arbitrary realm execution, typically useful for deletion.
     * @param exec see {@link Exec}
     * @param notifies notify queries that watched {@code notifies} to update
     * @return Observable for the convenience of control worker thread.
     */
    @SafeVarargs
    public static Observable<Void> exec(final Exec exec, final Class<? extends RealmObject>... notifies) {
        return Observable.just(exec).map(new Func1<Exec, Void>() {
            @Override
            public Void call(final Exec tExec) {
                Realm realm = Realm.getInstance(configuration);
                executeTransaction(realm, new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        tExec.run(realm);
                    }
                });
                realm.close();
                final Set<String> toNotify = new HashSet<>();
                for (Class clazz: notifies) {
                    toNotify.add(clazz.getCanonicalName());
                }
                sendTableTrigger(toNotify);
                return null;
            }
        });
    }

    private static void sendTableTrigger(Set<String> tables) {
        synchronized (triggers) {
            triggers.onNext(tables);
        }
    }

    public static void deleteDatabase() {
        Realm.deleteRealm(configuration);
    }

    private static void executeTransaction(Realm realm, Realm.Transaction transaction) {
        if (transaction == null)
            return;
        realm.beginTransaction();
        try {
            transaction.execute(realm);
            realm.commitTransaction();
        } catch (RuntimeException e) {
            realm.cancelTransaction();
            Log.e(LOG_TAG, e.getMessage(), e);
            throw new RealmException("Error during transaction.", e);
        } catch (Error e) {
            realm.cancelTransaction();
            Log.e(LOG_TAG, e.getMessage(), e);
            throw e;
        }
    }
}
