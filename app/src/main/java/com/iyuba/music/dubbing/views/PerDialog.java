package com.iyuba.music.dubbing.views;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * A scoping annotation to permit objects whose lifetime should
 * conform to the life of the dialog to be memorised in the
 * correct component.
 */
//@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface PerDialog {
}
