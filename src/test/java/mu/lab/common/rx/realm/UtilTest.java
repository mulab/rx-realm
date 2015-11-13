package mu.lab.common.rx.realm;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author guangchen.
 */
public class UtilTest {

    @Test
    public void testCollectRelatedRealmClass() throws Exception {
        Set<String> expected = new HashSet<>(Arrays.asList(new String[]{
            "mu.lab.common.rx.realm.Foo",
            "mu.lab.common.rx.realm.Bar",
            "mu.lab.common.rx.realm.Baz",
        }));
        Set<String> set = new HashSet<>();
        Util.collectRelatedRealmClass(set, Foo.class);
        Assert.assertEquals(expected, set);
    }
}
