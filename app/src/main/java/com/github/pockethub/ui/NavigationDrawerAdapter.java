package com.github.pockethub.ui;

import static com.github.pockethub.ui.NavigationDrawerObject.TYPE_ITEM_MENU;
import static com.github.pockethub.ui.NavigationDrawerObject.TYPE_ITEM_ORG;
import static com.github.pockethub.ui.NavigationDrawerObject.TYPE_SEPERATOR;
import static com.github.pockethub.ui.NavigationDrawerObject.TYPE_SUBHEADER;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alorma.github.sdk.bean.dto.response.Organization;
import com.alorma.github.sdk.bean.dto.response.User;
import com.github.pockethub.R;
import com.github.pockethub.util.AvatarLoader;

import java.util.ArrayList;
import java.util.List;

public class NavigationDrawerAdapter extends BaseAdapter {

    private final Context context;
    private final AvatarLoader avatars;
    private final LayoutInflater inflater;
    private List<Organization> orgs = new ArrayList<>();
    private List<NavigationDrawerObject> data;

    public NavigationDrawerAdapter(Context context, List<Organization> orgs, final AvatarLoader avatars) {
        this.orgs.addAll(orgs);
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.avatars = avatars;
        createData();
    }

    private void createData() {
        orgs.remove(0);
        String[] names = new String[] { context.getString(R.string.home), context.getString(R.string.gist),
            context.getString(R.string.issue_dashboard), context.getString(R.string.bookmarks) };
        String[] icons = context.getResources().getStringArray(R.array.navigation_drawer_icon_list);
        data = new ArrayList<>();
        int amount = names.length + orgs.size() + 2;
        for (int i = 0; i < amount; i++) {
            if (i < names.length)
                data.add(new NavigationDrawerObject(names[i], icons[i], TYPE_ITEM_MENU));
            else if (i == names.length)
                data.add(new NavigationDrawerObject(TYPE_SEPERATOR));
            else if (i == names.length + 1)
                data.add(new NavigationDrawerObject("Organizations", TYPE_SUBHEADER));
            else
                data.add(new NavigationDrawerObject(orgs.get(i - names.length - 2).login, TYPE_ITEM_ORG,
                    orgs.get(i - names.length - 2)));
        }
    }

    public void setOrgs(List<Organization> orgs) {
        this.orgs.addAll(orgs);
        this.orgs.remove(0);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public NavigationDrawerObject getItem(int position) {
        return data.get(position-1);
    }

    @Override
    public long getItemId(int position) {
        return data.get(position).getType();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        final NavigationDrawerObject obj = data.get(position);
        if (convertView == null || obj.getType() != ((ViewHolder) convertView.getTag()).type) {
            viewHolder = new ViewHolder();
            switch (obj.getType()) {
                case TYPE_ITEM_MENU:
                    convertView = inflater.inflate(R.layout.navigation_drawer_list_item_text, parent, false);
                    viewHolder.name = (TextView) convertView.findViewById(R.id.navigation_drawer_item_name);
                    viewHolder.iconString = (TextView) convertView.findViewById(R.id.navigation_drawer_item_text_icon);
                    break;
                case TYPE_ITEM_ORG:
                    convertView = inflater.inflate(R.layout.navigation_drawer_list_item_image, parent, false);
                    viewHolder.name = (TextView) convertView.findViewById(R.id.navigation_drawer_item_name);
                    viewHolder.iconDrawable = (ImageView) convertView.findViewById(R.id
                        .navigation_drawer_item_drawable_icon);
                    break;
                case TYPE_SUBHEADER:
                    convertView = inflater.inflate(R.layout.navigation_drawer_list_subheader, parent, false);
                    viewHolder.name = (TextView) convertView.findViewById(R.id.navigation_drawer_item_name);
                    convertView.setEnabled(false);
                    convertView.setOnClickListener(null);
                    break;
                default:
                    convertView = inflater.inflate(R.layout.navigation_drawer_list_seperator, parent, false);
                    convertView.setEnabled(false);
                    convertView.setOnClickListener(null);
                    break;
            }
            viewHolder.type = obj.getType();
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        switch (obj.getType()) {
            case TYPE_ITEM_MENU:
                Typeface font = Typeface.createFromAsset(context.getAssets(), "octicons.ttf");
                viewHolder.iconString.setTypeface(font);
                viewHolder.iconString.setText(obj.getIconString());
                viewHolder.name.setText(obj.getTitle());
                break;
            case TYPE_ITEM_ORG:
                avatars.bind(viewHolder.iconDrawable, obj.getUser());
                viewHolder.name.setText(obj.getTitle());
                break;
            case TYPE_SUBHEADER:
                viewHolder.name.setText(obj.getTitle());
                break;
        }

        return convertView;
    }

    private class ViewHolder {
        int type;
        TextView name;
        TextView iconString;
        ImageView iconDrawable;
    }
}
