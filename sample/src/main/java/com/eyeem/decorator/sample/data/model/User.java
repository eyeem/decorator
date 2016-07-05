package com.eyeem.decorator.sample.data.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by budius on 16.06.16.
 */
public class User extends RealmObject {

   @PrimaryKey public String id;
   public String nickname;
   public String fullname;
   public String webUrl;
   public String thumbUrl;
   public String photoUrl;
   public boolean hidden;
   public boolean blacklisted;
   public long totalPhotos;
   public long totalFollowers;
   public long totalFriends;
   public long totalLikedAlbums;
   public long totalLikedPhotos;
   public String description;
   public String file_id;

}
