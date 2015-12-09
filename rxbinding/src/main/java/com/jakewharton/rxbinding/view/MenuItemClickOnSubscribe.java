package com.jakewharton.rxbinding.view;

import android.view.MenuItem;
import rx.Observable;
import rx.Subscriber;
import rx.android.MainThreadSubscription;
import rx.functions.Func1;

import static com.jakewharton.rxbinding.internal.Preconditions.checkUiThread;

final class MenuItemClickOnSubscribe implements Observable.OnSubscribe<Void> {
  private final MenuItem menuItem;
  private final Func1<? super MenuItem, Boolean> handled;

  MenuItemClickOnSubscribe(MenuItem menuItem, Func1<? super MenuItem, Boolean> handled) {
    this.menuItem = menuItem;
    this.handled = handled;
  }

  @Override public void call(final Subscriber<? super Void> subscriber) {
    checkUiThread();

    MenuItem.OnMenuItemClickListener listener = new MenuItem.OnMenuItemClickListener() {
      @Override public boolean onMenuItemClick(MenuItem item) {
        if (handled.call(menuItem)) {
          if (!subscriber.isUnsubscribed()) {
            subscriber.onNext(null);
          }
          return true;
        }
        return false;
      }
    };

    menuItem.setOnMenuItemClickListener(listener);

    subscriber.add(new MainThreadSubscription() {
      @Override protected void onUnsubscribe() {
        menuItem.setOnMenuItemClickListener(null);
      }
    });
  }
}
