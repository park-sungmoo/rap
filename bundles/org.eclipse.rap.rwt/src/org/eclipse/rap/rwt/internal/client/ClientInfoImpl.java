/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.client;

import java.io.Serializable;
import java.util.Map;

import org.eclipse.rap.rwt.client.service.ClientInfo;
import org.eclipse.rap.rwt.internal.remote.RemoteObject;
import org.eclipse.rap.rwt.internal.remote.RemoteObjectFactory;
import org.eclipse.rap.rwt.internal.remote.RemoteOperationHandler;


public class ClientInfoImpl implements ClientInfo, Serializable {

  private Integer timezoneOffset;
  private RemoteObject remoteObject;

  private final class InfoOperationHandler extends RemoteOperationHandler {
    public void handleSet( Map<String, Object> properties ) {
      if( properties.containsKey( "timezoneOffset" ) ) {
        Integer offset = ( Integer )properties.get( "timezoneOffset" );
        ClientInfoImpl.this.timezoneOffset = offset;
      }
    }
  }

  public ClientInfoImpl() {
    RemoteObjectFactory factory = RemoteObjectFactory.getInstance();
    remoteObject = factory.createServiceObject( "rwt.client.ClientInfo" );
    remoteObject.setHandler( new InfoOperationHandler() );
  }

  public int getTimezoneOffset() {
    if( timezoneOffset == null ) {
      throw new IllegalStateException( "timezoneOffset is not set" );
    }
    return timezoneOffset.intValue();
  }

}
