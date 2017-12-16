package com.trialanderror.viewhandlers;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.HashMap;

public class OptionMenu {
    private final HashMap<String, String> selectedOptions = new HashMap<>();
    private ArrayList<Category> categories;
    private Context context;

    public OptionMenu(Context c, ArrayList<Category> categories) {
        context = c;
        this.categories = categories;
    }

    public ArrayList<Category> getCategories() {
        return categories;
    }

    public boolean isCategorySelected(String categoryID) {
        return selectedOptions.containsKey(categoryID);
    }

    public void setCategories(ArrayList<Category> categories) {
        this.categories = categories;
    }

    public String selectedOption(String categoryID) {
        if (selectedOptions.containsKey(categoryID)) {
            return selectedOptions.get(categoryID);
        } else {
            throw new IllegalArgumentException("Category does not exist");
        }
    }

    public void show() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Setup");
        final View v = generateView();
        builder.setView(v);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                for (int i = 0; i < categories.size(); i++) {
                    Category c = categories.get(i);
                    selectedOptions.put(c.getName(), c.getResult());
                }
            }
        });
        ((Activity) context).runOnUiThread(new Runnable() {
            public void run() {
                builder.show();
            }
        });
    }

    private View generateView() {
        final LinearLayout l = new LinearLayout(context);
        l.setOrientation(LinearLayout.VERTICAL);
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < categories.size(); i++) {
                    Category c = categories.get(i);
                    l.addView(c.getView(context));
                }
            }
        });
        return l;
    }

    public static class Builder {
        ArrayList<Category> categories;
        Context context;

        public Builder(Context c) {
            context = c;
            categories = new ArrayList<Category>();
        }

        public OptionMenu create() {
            return new OptionMenu(context, categories);
        }

        public void addCategory(Category c) {
            categories.add(c);
        }
    }
}
