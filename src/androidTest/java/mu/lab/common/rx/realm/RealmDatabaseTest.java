package mu.lab.common.rx.realm;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import rx.observers.TestSubscriber;

import static org.junit.Assert.*;

/**
 * @author guangchen.
 */
@RunWith(AndroidJUnit4.class)
public class RealmDatabaseTest {

    @Before
    public void setUp() throws Exception {
        RealmConfiguration configuration = new RealmConfiguration
            .Builder(InstrumentationRegistry.getContext())
            .setModules(new TestModule())
            .schemaVersion(0)
            .build();
        RealmDatabase.init(configuration);
        RealmDatabase.deleteDatabase();
    }

    @Test
    public void testCreateQueryAndInsertAndExec() throws Exception {
        // query
        TestSubscriber<RealmResults<Foo>> testSubscriber = new TestSubscriber<>();
        RealmDatabase.createQuery(new Query<Foo>() {
            @Override
            public RealmResults<Foo> call(Realm realm) {
                return realm.where(Foo.class).findAll();
            }
        }, Foo.class).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertValueCount(1);
        List<RealmResults<Foo>> list = testSubscriber.getOnNextEvents();
        assertEquals(1, list.size());
        RealmResults<Foo> results = list.get(0);
        assertEquals(0, results.size());

        //insert
        TestSubscriber<Void> insertSubscriber = new TestSubscriber<>();
        Foo foo = new Foo();
        foo.setId(1);
        RealmDatabase.insertOrUpdate(foo).subscribe(insertSubscriber);
        insertSubscriber.assertNoErrors();
        insertSubscriber.assertValueCount(1);
        insertSubscriber.assertCompleted();

        testSubscriber.assertValueCount(2);
        list = testSubscriber.getOnNextEvents();
        assertEquals(2, list.size());
        results = list.get(1);
        assertEquals(1, results.size());
        assertEquals(1, results.get(0).getId());

        //remove
        TestSubscriber<Void> deleteSubscriber = new TestSubscriber<>();
        RealmDatabase.exec(new Exec() {
            @Override
            public void run(Realm realm) {
                realm.where(Foo.class).findAll().clear();
            }
        }, Foo.class).subscribe(deleteSubscriber);
        deleteSubscriber.assertNoErrors();
        deleteSubscriber.assertValueCount(1);
        deleteSubscriber.assertCompleted();

        testSubscriber.assertValueCount(3);
        list = testSubscriber.getOnNextEvents();
        assertEquals(3, list.size());
        results = list.get(2);
        assertEquals(0, results.size());
    }
}
