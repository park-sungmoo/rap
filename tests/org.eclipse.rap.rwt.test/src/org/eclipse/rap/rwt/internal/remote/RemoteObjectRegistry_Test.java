/*******************************************************************************
* Copyright (c) 2012 EclipseSource and others.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    EclipseSource - initial API and implementation
*******************************************************************************/
package org.eclipse.rap.rwt.internal.remote;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;


public class RemoteObjectRegistry_Test extends TestCase {

  private RemoteObjectRegistry registry;

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    registry = new RemoteObjectRegistry();
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testReturnsSingletonInstance() {
    Object registry1 = RemoteObjectRegistry.getInstance();
    Object registry2 = RemoteObjectRegistry.getInstance();

    assertNotNull( registry1 );
    assertSame( registry1, registry2 );
  }

  public void testCanRegisterRemoteObject() {
    RemoteObjectImpl remoteObject = new RemoteObjectImpl( "id", "type" );
    registry.register( remoteObject );

    RemoteObject result = registry.get( "id" );

    assertSame( remoteObject, result );
  }

  public void testCanRemoveRegisteredObject() {
    RemoteObjectImpl remoteObject = new RemoteObjectImpl( "id", "type" );
    registry.register( remoteObject );

    registry.remove( remoteObject );

    assertNull( registry.get( "id" ) );
  }

  public void testPreventsRegisterDuplicateIds() {
    registry.register( new RemoteObjectImpl( "id", "type" ) );

    try {
      registry.register( new RemoteObjectImpl( "id", "type" ) );
      fail();
    } catch( IllegalArgumentException exception ) {
      assertEquals( "Remote object already registered, id: id", exception.getMessage() );
    }
  }

  public void testPreventsRemoveNonExisting() {
    try {
      registry.remove( new RemoteObjectImpl( "id", "type" ) );
      fail();
    } catch( IllegalArgumentException exception ) {
      assertEquals( "Remote object not found in registry, id: id", exception.getMessage() );
    }
  }

  public void testReturnsOrderedListOfRegisteredObjects() {
    for( int i = 0; i < 10; i++ ) {
      registry.register( new RemoteObjectImpl( "id" + i, "type" ) );
    }

    List<RemoteObjectImpl> allObjects = registry.getRemoteObjects();

    assertEquals( "id0 id1 id2 id3 id4 id5 id6 id7 id8 id9", join( getIds( allObjects ), " " ) );
  }

  private static List<String> getIds( List<RemoteObjectImpl> allObjects ) {
    List<String> ids = new ArrayList<String>();
    for( RemoteObjectImpl object : allObjects ) {
      ids.add( object.getId() );
    }
    return ids;
  }

  private static String join( List<String> list, String glue ) {
    StringBuilder builder = new StringBuilder();
    for( String string : list ) {
      if( builder.length() > 0 ) {
        builder.append( glue );
      }
      builder.append( string );
    }
    return builder.toString();
  }

}
