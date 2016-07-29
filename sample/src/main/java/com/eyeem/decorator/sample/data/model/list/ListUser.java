package com.eyeem.decorator.sample.data.model.list;

import com.eyeem.decorator.sample.data.model.User;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by budius on 16.06.16.
 */
public class ListUser extends RealmObject {
   @PrimaryKey public String id;
   public long lastRefresh;
   public RealmList<User> list;
}