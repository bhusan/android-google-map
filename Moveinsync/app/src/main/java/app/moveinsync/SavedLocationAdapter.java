package app.moveinsync;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by bharath on 10/5/15.
 */
public class SavedLocationAdapter extends ArrayAdapter<SavedLocationObject> {
    public SavedLocationAdapter(Context context, int resource, List<SavedLocationObject> list) {
        super(context, resource, list);
    }

    public static class ViewHolder {
        TextView name;
        TextView latitude;
        TextView longitude;
        ImageView share;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SavedLocationObject item = getItem(position);
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.saved_location_object, null);
            holder.name = (TextView) convertView.findViewById(R.id.tvName);
            holder.latitude = (TextView) convertView.findViewById(R.id.tvLatitude);
            holder.longitude = (TextView) convertView.findViewById(R.id.tvLongitude);
            holder.share = (ImageView) convertView.findViewById(R.id.ivShare);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.name.setText(item.getName());
        holder.latitude.setText(Html.fromHtml("<b>Latitude : </b>" + item.getLatitude()));
        holder.longitude.setText(Html.fromHtml("<b>Longitude : </b>" + item.getLongitude()));
        holder.share.setTag(item);
        holder.share.setOnClickListener(shareLocation);
        return convertView;
    }

    View.OnClickListener shareLocation = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SavedLocationObject item = (SavedLocationObject) v.getTag();
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Name : " + item.getName() + "\n Latitude :" + item.getLatitude() + "\n Longitude : " + item.getLongitude());
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Shared through moveinsync application");
            v.getContext().startActivity(Intent.createChooser(sendIntent, "Share using"));

        }
    };
}
