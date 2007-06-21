/*******************************************************************************
 * Copyright (c) 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.internal.theme;


public class ThemeWriter {
  
  private final String id;
  private final String name;
  private StringBuffer code;
  private boolean headWritten;
  private boolean tailWritten;
  private boolean valueWritten;
  private int type;
  
  public static final int META = 1;
  public static final int FONT = 2;
  public static final int COLOR = 3;
  public static final int BORDER = 4;
  public static final int ICON = 5;
  public static final int WIDGET = 6;
  public static final int APPEARANCE = 7;
  
  // TODO [rst] Clarify what the use of the theme title is, remove the name
  //      parameter if unnecessary
  public ThemeWriter( final String id, final String name, final int type ) {
    this.id = id;
    this.name = name;
    this.type = checkType( type );
    this.code = new StringBuffer();
    headWritten = false;
    tailWritten = false;
    valueWritten = false;
  }
  
  public void writeValues( final String values ) {
    beforeWriteValue();
    code.append( values );
    afterWriteValue();
  }

  public void writeFont( final String key, final QxFont font ) {
    if( type != FONT ) {
      throw new IllegalStateException( "Font can only be set in font themes" );
    }
    beforeWriteValue();
    code.append( "    \"" + key + "\" : { " );
    code.append( " family: [" );
    for( int i = 0; i < font.family.length; i++ ) {
      if( i > 0 ) {
        code.append( " ," );
      }
      code.append( "\"" );
      code.append( font.family[ i ] );
      code.append( "\"" );
    }
    code.append( "]" );
    code.append( ", size: " );
    code.append( font.size );
    if( font.bold ) {
      code.append( ", bold: true" );
    }
    if( font.italic ) {
      code.append( ", italic: true" );
    }
    code.append( " }" );
    afterWriteValue();
  }

  public void writeColor( final String key, final QxColor color ) {
    if( type != COLOR ) {
      throw new IllegalStateException( "Color can only be set in color themes" );
    }
    beforeWriteValue();
    code.append( "    \"" + key + "\" : " );
    code.append( "[ " );
    code.append( color.red );
    code.append( ", " );
    code.append( color.green );
    code.append( ", " );
    code.append( color.blue );
    code.append( " ]" );
    afterWriteValue();
  }
  
  public void writeBorder( final String key, final QxBorder border ) {
    if( type != BORDER ) {
      throw new IllegalStateException( "Border can only be set in border themes" );
    }
    beforeWriteValue();
    code.append( "    \"" + key + "\" : " );
    // none
    code.append( "{ width : " );
    code.append( border.width );
    String style = border.getQxStyle();
    if( !"solid".equals( style ) ) {
      code.append( ", style : \"" );
      code.append( style );
      code.append( "\"" );
    }
    String colors = border.getQxColors();
    if( colors != null ) {
      code.append( ", color : " );
      code.append( colors );
    }
    String innerColor = border.getQxInnerColors();
    if( innerColor != null ) {
      code.append( ", innerColor : " );
      code.append( innerColor );
    }
    code.append( " }" );
    afterWriteValue();
  }
  
  public void writeUri( final String pathPrefix ) {
    if( type != WIDGET && type != ICON ) {
      throw new IllegalStateException( "Url can only be set in widget and icon themes" );
    }
    beforeWriteValue();
    code.append( "    \"uri\" : \"" );
    code.append( pathPrefix );
    code.append( "\"" );
    afterWriteValue();
  }
  
  public void writeTheme( final String key, final String value ) {
    if( type != META ) {
      throw new IllegalStateException( "Theme can only be set in meta themes" );
    }
    beforeWriteValue();
    code.append( "    \"" + key + "\" : " );
    code.append( value );
    afterWriteValue();
  }

  public String getGeneratedCode() {
    if( !tailWritten ) {
      writeTail();
    }
    return code.toString();
  }
  
  private void beforeWriteValue() {
    if( !headWritten ) {
      writeHead();
    }
    if( tailWritten ) {
      throw new IllegalStateException( "Tail already written" );
    }
    if( valueWritten ) {
      code.append( ",\n" );
    }
  }

  private void afterWriteValue() {
    valueWritten = true;
  }

  private void writeHead() {
    code.append( "/*\n" );
    code.append( " * Theme file generated by ThemeWriter. Do not edit.\n" );
    code.append( " */\n\n" );
    code.append( "qx.Theme.define( \"" + id + getNameSuffix() + "\",\n" );
    code.append( "{\n" );
    String title = name;
    code.append( "  title : \"" + title + "\",\n" );
    if( type == META ) {
      code.append( "  meta : {\n" );
    } else if( type == FONT ) {
      code.append( "  fonts : {\n" );
    } else if( type == COLOR ) {
      code.append( "  colors : {\n" );
    } else if( type == BORDER ) {
      code.append( "  borders : {\n" );
    } else if( type == APPEARANCE ) {
      code.append( "  appearances : {\n" );
    } else if( type == ICON ) {
      code.append( "  icons : {\n" );
    } else if( type == WIDGET ) {
      code.append( "  widgets : {\n" );
    }
    headWritten = true;
  }
  
  private void writeTail() {
    code.append( "\n" );
    code.append( "  }\n" );
    code.append( "} );\n" );
    tailWritten = true;
  }
  
  private int checkType( final int type ) {
    if( type != META
        && type != FONT
        && type != COLOR
        && type != BORDER
        && type != ICON
        && type != WIDGET
        && type != APPEARANCE )
    {
      throw new IllegalArgumentException( "illegal type" );
    }
    return type;
  }
  
  private String getNameSuffix() {
    String result = "";
    if( type == FONT ) {
      result = "Fonts";
    } else if( type == COLOR ) {
      result = "Colors";
    } else if( type == BORDER ) {
      result = "Borders";
    } else if( type == ICON ) {
      result = "Icons";
    } else if( type == WIDGET ) {
      result = "Widgets";
    } else if( type == APPEARANCE ) {
      result = "Appearances";
    }
    return result;
  }
}
