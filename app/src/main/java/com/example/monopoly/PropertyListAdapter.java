package com.example.monopoly;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.ArrayList;
public class PropertyListAdapter extends ArrayAdapter<Cell> {
    private LayoutInflater inflater;

    public PropertyListAdapter(Context context, ArrayList<Cell> properties) {
        super(context, 0, properties);//super call for the arrayadapter without resource because we do it alone
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.property_user_item, parent, false);
        }

        Cell property = getItem(position);
        TextView propertyName = convertView.findViewById(R.id.propertyNameDis);
        TextView propertyPrice = convertView.findViewById(R.id.propertyPriceDis);
        TextView propertyPay = convertView.findViewById(R.id.propertyPayBackDis);
        TextView propertySell = convertView.findViewById(R.id.propertySellDis);
        TextView sellExplain = convertView.findViewById(R.id.sellexplain);

        propertyName.setText(property.getName());
        propertyPrice.setText("Price: " + property.getPrice() + "✧");
        propertyPay.setText("Rent: " + property.getPay() + "✧");
        propertySell.setText("Sell price: " + property.getSell() + "✧");

        if(property.getColor().equals("blue")){
            propertyName.setBackgroundResource(R.color.blue);
        }
        else if(property.getColor().equals("red")){
            propertyName.setBackgroundResource(R.color.red);
        }
        else if(property.getColor().equals("yellow")){
            propertyName.setBackgroundResource(R.color.yellow);
        }
        else if(property.getColor().equals("green")){
            propertyName.setBackgroundResource(R.color.green);
        }
        else if(property.getColor().equals("white")){
            propertyName.setBackgroundResource(R.color.grey);
        }

        return convertView;
    }

    @Override
    public void remove(@Nullable Cell object) {//remove from adapter--build in function
        super.remove(object);
    }
}