package de.raptor2101.BattleWorldsKronos.Connector.Gui.Adapters;

import java.util.ArrayList;
import java.util.List;

import de.raptor2101.BattleWorldsKronos.Connector.Data.Entities.Message;
import de.raptor2101.BattleWorldsKronos.Connector.Gui.Views.MessageView;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class MessageViewAdapter extends BaseAdapter {
  
  private List<Message> mMassages;
  private Context mContext;
  
  public MessageViewAdapter(Context context){
    mMassages = new ArrayList<Message>(0);
    mContext =context;
  }
  
  public void setMessages(List<Message> messages){
    mMassages = messages;
    this.notifyDataSetChanged();
  }
  
  @Override
  public int getCount() {
    return mMassages.size();
  }

  @Override
  public Object getItem(int position) {
    
    return mMassages.get(position);
  }

  @Override
  public long getItemId(int position) {
    return mMassages.get(position).getMessageId();
  }

  @Override
  public View getView(int position, View currentView, ViewGroup parent) {
    MessageView view;
    if(currentView != null){
      view = (MessageView) currentView;
      
    }
    else {
      view = new MessageView(mContext);
    }
    view.setMessage(mMassages.get(position));
    return (View)view;
  }
}
