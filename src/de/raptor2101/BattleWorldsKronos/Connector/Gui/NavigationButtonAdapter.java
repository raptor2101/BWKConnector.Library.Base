package de.raptor2101.BattleWorldsKronos.Connector.Gui;

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParserException;

import de.raptor2101.BattleWorldsKronos.Connector.Gui.Controls.NavigationButton;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class NavigationButtonAdapter extends BaseAdapter {

  private static final String XmlTagName_Item = "item";
  private static final String XmlAttributeNamespace = "http://schemas.android.com/apk/res/android";
  private static final String XmlAttributeName_Title = "title";
  private static final String XmlAttributeName_Icon = "icon";
  private static final String XmlAttributeName_Id = "id";

  private ArrayList<NavigationButton> buttons;

  public NavigationButtonAdapter(Context context, int menuResourceId) {
    buttons = new ArrayList<NavigationButton>(10);
    XmlResourceParser parser = context.getResources().getXml(menuResourceId);
    int eventType;

    try {
      do {
        eventType = parser.next();
        if (eventType == XmlResourceParser.START_TAG && XmlTagName_Item.equals(parser.getName())) {
          int buttonId = -1;
          CharSequence title = null;
          Drawable iconDrawable = null;  
          for (int index = 0; index < parser.getAttributeCount(); index++) {
            String attributeName = parser.getAttributeName(index);
            String namsespace = parser.getAttributeNamespace(index);
            if (XmlAttributeNamespace.equals(namsespace)) {
              String value = parser.getAttributeValue(index);
              if (XmlAttributeName_Title.equals(attributeName)) {
                if (value.startsWith("@")) {
                  int resourceId = Integer.parseInt(value.substring(1));
                  title = context.getText(resourceId);
                } else {
                  title = value;
                }
              } else if (XmlAttributeName_Icon.equals(attributeName)) {
                int resourceId;
                if (value.startsWith("@")) {
                  resourceId = Integer.parseInt(value.substring(1));
                } else {
                  resourceId = Integer.parseInt(value);
                }
                iconDrawable = context.getResources().getDrawable(resourceId);
              } else if (XmlAttributeName_Id.equals(attributeName)) {
                if (value.startsWith("@")) {
                  buttonId = Integer.parseInt(value.substring(1));
                } else {
                  buttonId = Integer.parseInt(value);
                }

              }
            }
          }
          
          if(buttonId > -1 && title != null && iconDrawable != null){
            buttons.add(new NavigationButton(context, buttonId, title, iconDrawable));
          } 
        }
      } while (eventType != XmlResourceParser.END_DOCUMENT);
    } catch (XmlPullParserException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Override
  public int getCount() {
    return buttons.size();
  }

  @Override
  public Object getItem(int position) {
    return buttons.get(position);
  }

  @Override
  public long getItemId(int position) {
    return buttons.get(position).getId();
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    return buttons.get(position);
  }

}
