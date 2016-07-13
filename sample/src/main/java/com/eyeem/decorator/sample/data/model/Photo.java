package com.eyeem.decorator.sample.data.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by budius on 15.06.16.
 */
public class Photo extends RealmObject {

   @PrimaryKey public String id;
   public String file_id;
   public String webUrl;
   public int width;
   public int height;
   public String updated;
   public boolean hidden;
   public boolean blacklisted;
   public String thumbUrl;
   public String photoUrl;
   public boolean submittedToMarket;
   public User user;
}
