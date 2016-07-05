package com.eyeem.decorator.sample;

import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayDeque;

/**
 * Abstract {@link PagerAdapter} implementation that mirrors the RecyclerView.Adapter API
 * Created by budius on 30.07.15.
 */
public abstract class ScreenPagerAdapter<VH extends ScreenPagerAdapter.ViewHolder> extends PagerAdapter {

   /**
    * Called when ScreenPagerAdapter needs a new {@link ViewHolder} of the given type to represent an item.
    * <p/>
    * This new ViewHolder should be constructed with a new View that can represent the items
    * of the given type. You can either create a new View manually or inflate it from an XML
    * layout file.
    * <p/>
    * The new ViewHolder will be used to display items of the adapter using
    * {@link #onBindViewHolder(ViewHolder, int)}. Since it will be re-used to display different
    * items in the data set, it is a good idea to cache references to sub views of the View to
    * avoid unnecessary {@link View#findViewById(int)} calls.
    *
    * @param parent   The ViewGroup into which the new View will be added after it is bound to
    *                 an adapter position.
    * @param viewType The view type of the new View.
    * @return A new ViewHolder that holds a View of the given view type.
    */
   public abstract VH onCreateViewHolder(ViewGroup parent, int viewType);

   /**
    * Called by ScreenPagerAdapter to display the data at the specified position. This method
    * should update the contents of the {@link ViewHolder#itemView} to reflect the item at
    * the given position.
    * <p/>
    *
    * @param holder   The ViewHolder which should be updated to represent the contents of the
    *                 item at the given position in the data set.
    * @param position The position of the item within the adapter's data set.
    */
   public abstract void onBindViewHolder(VH holder, int position);

   /**
    * Mirror opposite of {@link #onCreateViewHolder(ViewGroup, int)}, use this call to unregister
    * any observers or listeners this ViewHolder might be using
    *
    * @param holder   The ViewHolder which should be unregistered to represent the contents of the
    *                 item at the given position in the data set.
    * @param position The position of the item within the adapter's data set.
    */
   public abstract void onUnbindViewHolder(VH holder, int position);

   /**
    * Called by ScreenPagerAdapter to inform the Adapter that the supplied {@link ViewHolder}
    * just became visible in the ViewPager.
    *
    * @param holder   The ViewHolder which became visible in the ViewPager.
    * @param position The position of the item within the adapter's data set.
    */
   public void onBecomeVisible(VH holder, int position) { /**/ }

   /**
    * Mirror opposite of {@link #onBecomeVisible(ViewHolder, int)}. Indicates that this
    * viewHolders' view is out of the screen, but still attached to the ViewPager.
    *
    * @param holder   The ViewHolder which became invisible in the ViewPager.
    * @param position The position of the item within the adapter's data set.
    */
   public void onBecomeInvisible(VH holder, int position) { /**/ }

   /**
    * Return the view type of the item at <code>position</code> for the purposes
    * of view recycling.
    * <p/>
    * <p>The default implementation of this method returns 0, making the assumption of
    * a single view type for the adapter. Unlike ListView adapters, types need not
    * be contiguous. Consider using id resources to uniquely identify item view types.
    *
    * @param position position to query
    * @return integer value identifying the type of the view needed to represent the item at
    * <code>position</code>. Type codes need not be contiguous.
    */
   public int getItemViewType(int position) {
      return 0;
   }

   private VH primary = null;
   private Recycler recycler = new Recycler();

   @Override
   public void notifyDataSetChanged() {
      recycler.flush();
      super.notifyDataSetChanged();
   }

   @Override
   public Object instantiateItem(ViewGroup container, int position) {

      VH viewHolder = recycler.get(container, getItemViewType(position));
      viewHolder.position = position;
      onBindViewHolder(viewHolder, position);
      container.addView(viewHolder.itemView, 0);

      return viewHolder;
   }

   @Override
   public void destroyItem(ViewGroup container, int position, Object object) {
      VH viewHolder = (VH) object;

      if (viewHolder == primary) {
         onBecomeInvisible(viewHolder, viewHolder.position);
      }

      onUnbindViewHolder(viewHolder, viewHolder.position);
      container.removeView(viewHolder.itemView);

      recycler.put(viewHolder, getItemViewType(position));
   }

   @Override
   public void setPrimaryItem(ViewGroup container, int position, Object object) {
      if (primary != null) {
         if (primary.position == position) return;
         onBecomeInvisible(primary, primary.position);
      }
      primary = (VH) object;
      if (primary != null) {
         onBecomeVisible(primary, primary.position);
      }
   }

   @Override
   public int getItemPosition(Object object) {
      return ((VH) object).position;
   }

   @Override
   public boolean isViewFromObject(View view, Object object) {
      return view.equals(((VH) object).itemView);
   }

   /**
    * Base class for ViewHolders
    */
   public static abstract class ViewHolder {
      final View itemView;
      int position;

      public ViewHolder(View itemView) {
         this.itemView = itemView;
      }
   }

   /**
    * Responsible for keep track of ViewHolders that are not attached to the ViewPager
    */
   private class Recycler {

      private SparseArray<ArrayDeque<VH>> masterCache = new SparseArray<>();

      public void put(VH viewHolder, int viewType) {
         ArrayDeque<VH> cache = getCacheByType(viewType);
         cache.add(viewHolder);
      }

      public VH get(ViewGroup parent, int viewType) {
         ArrayDeque<VH> cache = getCacheByType(viewType);
         VH viewHolder = cache.pollFirst();
         if (viewHolder == null) {
            viewHolder = onCreateViewHolder(parent, viewType);
         } else {
         }
         return viewHolder;
      }

      public void flush() {
         masterCache.clear();
      }

      private ArrayDeque<VH> getCacheByType(int viewType) {
         ArrayDeque<VH> cache = masterCache.get(viewType);
         if (cache == null) {
            cache = new ArrayDeque<>();
            masterCache.put(viewType, cache);
         }
         return cache;
      }
   }
}
