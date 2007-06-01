/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.internal.widgets.textkit;

import java.io.IOException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.lifecycle.*;
import org.eclipse.swt.widgets.Text;
import com.w4t.W4TContext;
import com.w4t.util.browser.Mozilla;


final class TextLCAUtil {
  
  static final String PROP_TEXT = "text";
  static final String PROP_TEXT_LIMIT = "textLimit";
  static final String PROP_SELECTION = "selection";
  static final String PROP_MODIFY_LISTENER = "modifyListener";
  static final String PROP_READONLY = "readonly";

  private static final Integer DEFAULT_TEXT_LIMIT 
    = new Integer( Text.MAX_TEXT_LIMIT );
  private static final Point DEFAULT_SELECTION
    = new Point( 0, 0 );

  private static final JSListenerInfo JS_MODIFY_LISTENER_INFO 
    = new JSListenerInfo( JSConst.QX_EVENT_KEY_UP, 
                          "org.eclipse.swt.TextUtil.modifyText", 
                          JSListenerType.STATE_AND_ACTION );
  private static final JSListenerInfo JS_BLUR_LISTENER_INFO 
    = new JSListenerInfo( JSConst.QX_EVENT_BLUR, 
                          "org.eclipse.swt.TextUtil.modifyTextOnBlur", 
                          JSListenerType.ACTION );

  private TextLCAUtil() {
    // prevent instantiation
  }

  static void preserveValues( final Text text ) {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( text );
    adapter.preserve( PROP_TEXT, text.getText() );
    adapter.preserve( PROP_TEXT_LIMIT, new Integer( text.getTextLimit() ) );
    adapter.preserve( PROP_SELECTION, text.getSelection() );
    boolean hasListener = ModifyEvent.hasListener( text );
    adapter.preserve( PROP_MODIFY_LISTENER, Boolean.valueOf( hasListener ) );
    adapter.preserve( PROP_READONLY, Boolean.valueOf( ! text.getEditable() ) );
  }
  
  static void readText( final Text text ) {
    String newText = WidgetLCAUtil.readPropertyValue( text, "text" );
    if( newText != null ) {
      text.setText( newText );
    }
  }
  
  static void readSelection( final Text text ) {
    Point selection = text.getSelection();
    String value = WidgetLCAUtil.readPropertyValue( text, "selectionStart" );
    if( value != null ) {
      selection.x = Integer.parseInt( value );
    }
    value = WidgetLCAUtil.readPropertyValue( text, "selectionCount" );
    if( value != null ) {
      selection.y = selection.x + Integer.parseInt( value );
    }
    text.setSelection( selection );
  }
  
  static void writeReadOnly( final Text text ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( text );
    Boolean newValue = Boolean.valueOf( !text.getEditable() );
    writer.set( PROP_READONLY, "readOnly", newValue, Boolean.FALSE );
  }

  static void writeNoSpellCheck( final Text text ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( text );
    // TODO [rh] this should be solved in qooxdoo
    //      see http://bugzilla.qooxdoo.org/show_bug.cgi?id=291
    if( W4TContext.getBrowser() instanceof Mozilla ) {
      Object[] args = new Object[] { "spellcheck", Boolean.FALSE };
      writer.call( "setHtmlAttribute", args );
    }
  }

  static void writeTextLimit( final Text text ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( text );
    Integer newValue = new Integer( text.getTextLimit() );
    Integer defValue = DEFAULT_TEXT_LIMIT;
    if( WidgetLCAUtil.hasChanged( text, PROP_TEXT_LIMIT, newValue, defValue ) ) 
    {
      // Negative values are treated as 'no limit' which is achieved by passing
      // null to the client-side maxLength property
      if( newValue.intValue() < 0 ) {
        newValue = null;
      }
      writer.set( "maxLength", newValue );
    }
  }

  static void writeSelection( final Text text ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( text );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( text );
    if( !adapter.isInitialized() ) {
      writer.addListener( "mouseup", "org.eclipse.swt.TextUtil.onMouseUp" );
    }
    Point newValue = text.getSelection();
    Point defValue = DEFAULT_SELECTION;
    // TODO [rh] could be optimized: when text was changed and selection is 0,0
    //      there is no need to write JavaScript since the client resets the
    //      selection as well when the new text is set.
    if( WidgetLCAUtil.hasChanged( text, PROP_SELECTION, newValue, defValue ) ) { 
      Integer start = new Integer( newValue.x );
      Integer count = new Integer( text.getSelectionCount() );
      writer.callStatic( "org.eclipse.swt.TextUtil.setSelection", 
                         new Object[] { text, start, count } );
    }
  }
  
  static void writeModifyListener( final Text text ) throws IOException {
    if( ( text.getStyle() & SWT.READ_ONLY ) == 0 ) {
      JSWriter writer = JSWriter.getWriterFor( text );
      boolean hasListener = ModifyEvent.hasListener( text );
      writer.updateListener( JS_MODIFY_LISTENER_INFO, 
                             PROP_MODIFY_LISTENER, 
                             hasListener );
      writer.updateListener( JS_BLUR_LISTENER_INFO, 
                             PROP_MODIFY_LISTENER, 
                             hasListener );
    }
  }

  /**
   * Returns the given string with all newlines replaced with spaces.
   */
  static String stripNewlines( final String text ) {
    return text.replaceAll( "\n", " " );
  }
}
