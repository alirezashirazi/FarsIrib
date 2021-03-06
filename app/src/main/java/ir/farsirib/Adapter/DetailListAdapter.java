package ir.farsirib.Adapter;

import ir.farsirib.Model.ListItem;
import ir.farsirib.utils.UICircularImage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import ir.farsirib.R;

public class DetailListAdapter{

        @SuppressLint("InflateParams")
		public static View getView(ListItem item, Context context) {

            // 1. Create inflater
            LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            // 2. Get rowView from inflater
            View rowView = inflater.inflate(R.layout.comment, null, false);

            // 3. Get the two text view from the rowView
            TextView labelView = rowView.findViewById(R.id.name);
            TextView valueView = rowView.findViewById(R.id.comment);
            UICircularImage imageview = rowView.findViewById(R.id.profile);

            // 4. Set the text for textView
            labelView.setText(item.getTitle());
            valueView.setText(Html.fromHtml(item.getDesc()));
            imageview.setImageResource(item.getImageId());

            // 5. retrn rowView
            return rowView;
        }

}