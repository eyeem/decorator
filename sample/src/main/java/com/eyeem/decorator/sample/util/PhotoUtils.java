package com.eyeem.decorator.sample.util;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import com.eyeem.decorator.sample.data.model.Photo;

/**
 * Created by budius on 06.07.16.
 */
public class PhotoUtils {

   private static final String PHOTO_PATH = "http://cdn.eyeem.com/thumb/";

   public static String getPhotoUrlByHeight(Photo photo, int height) {
      return PHOTO_PATH + photo.file_id + "/h/" + height;
   }

   public static String getPhotoUrlbyWidth(Photo photo, int width) {
      return PHOTO_PATH + photo.file_id + "/w/" + width;
   }

   public static String getPhotoUrl(Photo photo, int width, int height) {
      return PHOTO_PATH + photo.file_id + "/" + width + "/" + height;
   }

   public static String getProfilePhotoUrl(Photo photo, int size) {
      return PHOTO_PATH + photo.user.file_id + "/sq/" + size;
   }

   public static int getWidth(Photo photo, int height) {
      return photo.width * height / photo.height;
   }

   public static int getHeight(Photo photo, int width) {
      return photo.height * width / photo.width;
   }

   public static Drawable getPlaceholder(Photo photo) {
      Drawable d = new ColorDrawable(0xff000000);
      d.setAlpha(112 + Math.abs(photo.file_id.hashCode()) % 129);
      return d;
   }
}
