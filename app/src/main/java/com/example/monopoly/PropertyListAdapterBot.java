package com.example.monopoly;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
public class PropertyListAdapterBot extends ArrayAdapter<Cell> {
    private LayoutInflater inflater;

    public PropertyListAdapterBot(Context context, ArrayList<Cell> properties) {
        super(context, 0, properties);//super call for the arrayadapter without resource because we do it alone
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.property_other_item, parent, false);
        }

        Cell property = getItem(position);
        TextView propertyName = convertView.findViewById(R.id.propertyNameDis2);
        TextView propertyPrice = convertView.findViewById(R.id.propertyPriceDis2);
        TextView propertyPay = convertView.findViewById(R.id.propertyPayBackDis2);
        TextView propertySell = convertView.findViewById(R.id.propertySellDis2);

        propertyName.setText(property.getName());
        propertyPrice.setText("Price: " + property.getPrice() + "✧");
        propertyPay.setText("Rent: " + property.getPayment() + "✧");
        propertySell.setText("Sell price: " + property.getSellPrice() + "✧");

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
}