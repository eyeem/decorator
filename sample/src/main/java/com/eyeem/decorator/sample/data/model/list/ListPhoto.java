package com.eyeem.decorator.sample.data.model.list;

import com.eyeem.decorator.sample.data.model.Photo;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by budius on 16.06.16.
 */
public class ListPhoto extends RealmObject {
   @PrimaryKey public String id;
   public long lastRefresh;
   public RealmList<Photo> list;
}