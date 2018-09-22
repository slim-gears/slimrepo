// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal.interfaces;

import java.io.Closeable;
import java.util.Iterator;

/**
 * Created by Denis on 10-Apr-15
 *
 */
public interface CloseableIterator<T> extends Iterator<T>, Closeable {
}
