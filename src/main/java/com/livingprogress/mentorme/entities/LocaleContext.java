package com.livingprogress.mentorme.entities;

import java.util.*;

/**
 * The current locale.
 */
public class LocaleContext {

  private static final ThreadLocal<List<java.util.Locale>> currentLocale = new ThreadLocal<>();

  public static List<java.util.Locale> getCurrentLocales() {
    return currentLocale.get();
  }

  public static void setCurrentLocales(List<java.util.Locale> locales) {
    currentLocale.set(locales);
  }
}
