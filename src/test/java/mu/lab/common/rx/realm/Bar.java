package mu.lab.common.rx.realm;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * @author guangchen.
 */
public class Bar extends RealmObject {
    private RealmList<Bar> childs;

    public RealmList<Bar> getChilds() {
        return childs;
    }

    public void setChilds(RealmList<Bar> childs) {
        this.childs = childs;
    }
}
