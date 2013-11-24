package de.raptor2101.BattleWorldsKronos.Connector.Gui.Controls;


import de.raptor2101.BattleWorldsKronos.Connector.Gui.R;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NavigationButton extends LinearLayout{

  public NavigationButton(Context context, int buttonId, CharSequence title, Drawable iconDrawable) {
    super(context);
    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    inflater.inflate(R.layout.navigation_button, this);
    
    setId(buttonId);
    TextView textView = (TextView) findViewById(R.id.navigation_button_text);
    textView.setText(title);
    
    ImageView imageView = (ImageView) findViewById(R.id.navigation_button_icon);
    imageView.setImageDrawable(iconDrawable);
  }
}
