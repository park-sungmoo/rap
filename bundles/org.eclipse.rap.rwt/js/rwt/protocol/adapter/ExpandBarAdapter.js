/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

rwt.protocol.AdapterRegistry.add( "rwt.widgets.ExpandBar", {

  factory : function( properties ) {
    var result = new rwt.widgets.ExpandBar();
    rwt.protocol.AdapterUtil.addStatesForStyles( result, properties.style );
    result.setUserData( "isControl", true );
    rwt.protocol.AdapterUtil.setParent( result, properties.parent );
    return result;
  },

  destructor : rwt.protocol.AdapterUtil.getControlDestructor(),

  getDestroyableChildren : function( widget ) {
    var children =  widget._clientArea.getChildren();
    children = rwt.protocol.AdapterUtil.filterUnregisteredObjects( children );
    children = children.concat( rwt.protocol.AdapterUtil.getDragAndDropChildren( widget ) );
    return children;
  },

  properties : rwt.protocol.AdapterUtil.extendControlProperties( [
    "bottomSpacingBounds",
    "vScrollBarMax"
  ] ),

  propertyHandler : rwt.protocol.AdapterUtil.extendControlPropertyHandler( {
    "bottomSpacingBounds" : function( widget, value ) {
      widget.setBottomSpacingBounds.apply( widget, value );
    }
  } ),

  listeners : rwt.protocol.AdapterUtil.extendControlListeners( [
    "Expand",
    "Collapse"
  ] ),

  listenerHandler : rwt.protocol.AdapterUtil.extendControlListenerHandler( {} )

} );
