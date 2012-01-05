/*******************************************************************************
 * Copyright (c) 2008, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.expandbarkit;

import static org.eclipse.rwt.lifecycle.WidgetLCAUtil.preserveProperty;
import static org.eclipse.rwt.lifecycle.WidgetLCAUtil.renderProperty;

import java.io.IOException;

import org.eclipse.rwt.internal.protocol.ClientObjectFactory;
import org.eclipse.rwt.internal.protocol.IClientObject;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.IExpandBarAdapter;
import org.eclipse.swt.widgets.*;


public final class ExpandBarLCA extends AbstractWidgetLCA {

  private static final String TYPE = "rwt.widgets.ExpandBar";
  private static final String[] ALLOWED_STYLES = new String[] { "NO_RADIO_GROUP", "BORDER" };

  private static final String PROP_BOTTOM_SPACING_BOUNDS = "bottomSpacingBounds";
  private static final String PROP_VSCROLLBAR_VISIBLE = "vScrollBarVisible";
  private static final String PROP_VSCROLLBAR_MAX = "vScrollBarMax";

  public void preserveValues( Widget widget ) {
    ExpandBar expandBar = ( ExpandBar )widget;
    ControlLCAUtil.preserveValues( expandBar );
    WidgetLCAUtil.preserveCustomVariant( expandBar );
    preserveProperty( expandBar, PROP_BOTTOM_SPACING_BOUNDS, getBottomSpacingBounds( expandBar ) );
    preserveProperty( expandBar, PROP_VSCROLLBAR_VISIBLE, isVScrollBarVisible( expandBar ) );
    preserveProperty( expandBar, PROP_VSCROLLBAR_MAX, getVScrollBarMax( expandBar ) );
  }

  public void readData( Widget widget ) {
    ControlLCAUtil.processKeyEvents( ( Control )widget );
    ControlLCAUtil.processMenuDetect( ( Control )widget );
    WidgetLCAUtil.processHelp( widget );
  }

  public void renderInitialization( Widget widget ) throws IOException {
    ExpandBar expandBar = ( ExpandBar )widget;
    IClientObject clientObject = ClientObjectFactory.getForWidget( expandBar );
    clientObject.create( TYPE );
    clientObject.setProperty( "parent", WidgetUtil.getId( expandBar.getParent() ) );
    clientObject.setProperty( "style", WidgetLCAUtil.getStyles( expandBar, ALLOWED_STYLES ) );
  }

  public void renderChanges( Widget widget ) throws IOException {
    ExpandBar expandBar = ( ExpandBar )widget;
    ControlLCAUtil.renderChanges( expandBar );
    WidgetLCAUtil.renderCustomVariant( expandBar );
    renderProperty( expandBar,
                    PROP_BOTTOM_SPACING_BOUNDS,
                    getBottomSpacingBounds( expandBar ),
                    null );
    renderProperty( expandBar, PROP_VSCROLLBAR_VISIBLE, isVScrollBarVisible( expandBar ), false );
    renderProperty( expandBar, PROP_VSCROLLBAR_MAX, getVScrollBarMax( expandBar ), 0 );
  }

  public void renderDispose( Widget widget ) throws IOException {
    ClientObjectFactory.getForWidget( widget ).destroy();
  }

  //////////////////
  // Helping methods

  private static Rectangle getBottomSpacingBounds( ExpandBar bar ) {
    return getExpandBarAdapter( bar ).getBottomSpacingBounds();
  }

  private static boolean isVScrollBarVisible( ExpandBar bar ) {
    return getExpandBarAdapter( bar ).isVScrollbarVisible();
  }

  private static int getVScrollBarMax( ExpandBar bar ) {
    int result = 0;
    if( ( bar.getStyle() & SWT.V_SCROLL ) != 0 ) {
      IExpandBarAdapter expandBarAdapter = getExpandBarAdapter( bar );
      ExpandItem[] items = bar.getItems();
      for( int i = 0; i < items.length; i++ ) {
        result += expandBarAdapter.getBounds( items[ i ] ).height;
      }
      result += bar.getSpacing() * ( items.length + 1 );
    }
    return result;
  }

  public static IExpandBarAdapter getExpandBarAdapter( ExpandBar bar ) {
    return bar.getAdapter( IExpandBarAdapter.class );
  }
}
