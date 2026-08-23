package com.elad.homeshopping;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends Activity {
    private static final String PREFS = "home_shopping_prefs";
    private static final String KEY_ITEMS = "items_json";

    private static final int STATUS_PENDING = 0;
    private static final int STATUS_TAKEN = 1;
    private static final int STATUS_MISSING = 2;

    private final List<Item> items = new ArrayList<>();
    private LinearLayout listContainer;
    private TextView summaryText;
    private EditText addInput;

    private final List<String> defaultItems = Arrays.asList(
            "נייר טואלט",
            "שקיות אוכל",
            "סבון כלים",
            "צלחות חד פעמי",
            "ביסקוויטים מצות יהודה ואסם",
            "ג׳ל כביסה ירוק",
            "דברי חלב",
            "טחינה",
            "טונה",
            "ירקות",
            "טיטולים",
            "מגבונים",
            "מברשות שיניים",
            "משחת שיניים",
            "מלפפון חמוץ",
            "זיתים",
            "משטחים"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window w = getWindow();
        w.setStatusBarColor(Color.rgb(20, 92, 58));
        w.setNavigationBarColor(Color.rgb(245, 247, 245));
        if (android.os.Build.VERSION.SDK_INT >= 26) {
            w.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }

        loadItems();
        setContentView(buildUi());
        renderItems();
    }

    private View buildUi() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        root.setPadding(dp(14), dp(14), dp(14), dp(10));
        root.setBackgroundColor(Color.rgb(245, 247, 245));

        TextView title = new TextView(this);
        title.setText("קניות לבית");
        title.setTextSize(30);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        title.setTextColor(Color.rgb(20, 70, 45));
        title.setGravity(Gravity.RIGHT);
        root.addView(title, lpMatchWrap());

        TextView subtitle = new TextView(this);
        subtitle.setText("סמן מה לקחת, מה עדיין חסר ומה לא היה בסופר");
        subtitle.setTextSize(15);
        subtitle.setTextColor(Color.rgb(90, 100, 94));
        subtitle.setGravity(Gravity.RIGHT);
        LinearLayout.LayoutParams subLp = lpMatchWrap();
        subLp.bottomMargin = dp(10);
        root.addView(subtitle, subLp);

        summaryText = new TextView(this);
        summaryText.setTextSize(15);
        summaryText.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        summaryText.setTextColor(Color.rgb(35, 70, 50));
        summaryText.setGravity(Gravity.RIGHT);
        summaryText.setPadding(dp(12), dp(10), dp(12), dp(10));
        summaryText.setBackground(makeRounded(Color.WHITE, dp(16), Color.rgb(220, 228, 222)));
        LinearLayout.LayoutParams sumLp = lpMatchWrap();
        sumLp.bottomMargin = dp(10);
        root.addView(summaryText, sumLp);

        LinearLayout addRow = new LinearLayout(this);
        addRow.setOrientation(LinearLayout.HORIZONTAL);
        addRow.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        addRow.setGravity(Gravity.CENTER_VERTICAL);

        addInput = new EditText(this);
        addInput.setHint("הוסף מוצר חדש...");
        addInput.setTextSize(16);
        addInput.setSingleLine(true);
        addInput.setInputType(InputType.TYPE_CLASS_TEXT);
        addInput.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        addInput.setPadding(dp(12), 0, dp(12), 0);
        addInput.setBackground(makeRounded(Color.WHITE, dp(14), Color.rgb(205, 215, 208)));
        LinearLayout.LayoutParams inputLp = new LinearLayout.LayoutParams(0, dp(48), 1f);
        inputLp.leftMargin = dp(8);
        addRow.addView(addInput, inputLp);

        Button addBtn = new Button(this);
        addBtn.setText("+ הוסף");
        addBtn.setTextSize(15);
        addBtn.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        addBtn.setTextColor(Color.WHITE);
        addBtn.setAllCaps(false);
        addBtn.setBackground(makeRounded(Color.rgb(27, 130, 78), dp(14), Color.TRANSPARENT));
        addBtn.setOnClickListener(v -> addNewItem());
        addRow.addView(addBtn, new LinearLayout.LayoutParams(dp(92), dp(48)));

        LinearLayout.LayoutParams addRowLp = lpMatchWrap();
        addRowLp.bottomMargin = dp(10);
        root.addView(addRow, addRowLp);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setClipToPadding(false);
        scroll.setPadding(0, 0, 0, dp(4));

        listContainer = new LinearLayout(this);
        listContainer.setOrientation(LinearLayout.VERTICAL);
        listContainer.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        scroll.addView(listContainer, new ScrollView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        root.addView(scroll, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));

        LinearLayout bottom = new LinearLayout(this);
        bottom.setOrientation(LinearLayout.HORIZONTAL);
        bottom.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        bottom.setGravity(Gravity.CENTER);
        bottom.setPadding(0, dp(8), 0, 0);

        Button resetStatuses = smallActionButton("אפס סימונים");
        resetStatuses.setOnClickListener(v -> confirmResetStatuses());
        bottom.addView(resetStatuses, weightedButtonLp());

        Button restoreDefaults = smallActionButton("שחזר רשימה מובנית");
        restoreDefaults.setOnClickListener(v -> confirmRestoreDefaults());
        LinearLayout.LayoutParams restoreLp = weightedButtonLp();
        restoreLp.rightMargin = dp(8);
        bottom.addView(restoreDefaults, restoreLp);

        root.addView(bottom, lpMatchWrap());
        return root;
    }

    private void renderItems() {
        listContainer.removeAllViews();
        for (int i = 0; i < items.size(); i++) {
            final int index = i;
            Item item = items.get(i);

            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            card.setPadding(dp(12), dp(10), dp(12), dp(10));
            card.setBackground(makeRounded(statusBackground(item.status), dp(16), statusBorder(item.status)));

            TextView name = new TextView(this);
            name.setText(item.name);
            name.setTextSize(19);
            name.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            name.setTextColor(Color.rgb(38, 45, 40));
            name.setGravity(Gravity.RIGHT);
            card.addView(name, lpMatchWrap());

            TextView current = new TextView(this);
            current.setText(statusLabel(item.status));
            current.setTextSize(13);
            current.setTextColor(statusTextColor(item.status));
            current.setGravity(Gravity.RIGHT);
            LinearLayout.LayoutParams curLp = lpMatchWrap();
            curLp.bottomMargin = dp(7);
            card.addView(current, curLp);

            LinearLayout actions = new LinearLayout(this);
            actions.setOrientation(LinearLayout.HORIZONTAL);
            actions.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            actions.setGravity(Gravity.CENTER_VERTICAL);

            Button taken = statusButton("✓ לקחתי", item.status == STATUS_TAKEN);
            taken.setOnClickListener(v -> setStatus(index, STATUS_TAKEN));
            actions.addView(taken, weightedButtonLp());

            Button pending = statusButton("○ לא לקחתי", item.status == STATUS_PENDING);
            pending.setOnClickListener(v -> setStatus(index, STATUS_PENDING));
            LinearLayout.LayoutParams pendingLp = weightedButtonLp();
            pendingLp.rightMargin = dp(6);
            actions.addView(pending, pendingLp);

            Button missing = statusButton("✕ לא היה", item.status == STATUS_MISSING);
            missing.setOnClickListener(v -> setStatus(index, STATUS_MISSING));
            LinearLayout.LayoutParams missingLp = weightedButtonLp();
            missingLp.rightMargin = dp(6);
            actions.addView(missing, missingLp);

            Button delete = new Button(this);
            delete.setText("מחק");
            delete.setTextSize(12);
            delete.setTextColor(Color.rgb(145, 44, 44));
            delete.setAllCaps(false);
            delete.setPadding(dp(4), 0, dp(4), 0);
            delete.setBackground(makeRounded(Color.rgb(255, 242, 242), dp(11), Color.rgb(235, 190, 190)));
            delete.setOnClickListener(v -> confirmDelete(index));
            LinearLayout.LayoutParams deleteLp = new LinearLayout.LayoutParams(dp(62), dp(42));
            deleteLp.rightMargin = dp(6);
            actions.addView(delete, deleteLp);

            card.addView(actions, lpMatchWrap());

            LinearLayout.LayoutParams cardLp = lpMatchWrap();
            cardLp.bottomMargin = dp(8);
            listContainer.addView(card, cardLp);
        }
        updateSummary();
    }

    private Button statusButton(String text, boolean selected) {
        Button b = new Button(this);
        b.setText(text);
        b.setTextSize(12);
        b.setAllCaps(false);
        b.setSingleLine(true);
        b.setPadding(dp(3), 0, dp(3), 0);
        b.setTextColor(selected ? Color.WHITE : Color.rgb(55, 68, 60));
        b.setBackground(makeRounded(
                selected ? Color.rgb(29, 126, 75) : Color.rgb(249, 250, 249),
                dp(11),
                selected ? Color.rgb(29, 126, 75) : Color.rgb(205, 215, 208)));
        return b;
    }

    private Button smallActionButton(String text) {
        Button b = new Button(this);
        b.setText(text);
        b.setTextSize(13);
        b.setAllCaps(false);
        b.setTextColor(Color.rgb(50, 70, 57));
        b.setBackground(makeRounded(Color.WHITE, dp(12), Color.rgb(205, 215, 208)));
        return b;
    }

    private void addNewItem() {
        String name = addInput.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(this, "כתוב שם של מוצר", Toast.LENGTH_SHORT).show();
            return;
        }
        for (Item item : items) {
            if (item.name.equalsIgnoreCase(name)) {
                Toast.makeText(this, "המוצר כבר קיים ברשימה", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        items.add(0, new Item(name, STATUS_PENDING));
        addInput.setText("");
        saveItems();
        renderItems();
    }

    private void setStatus(int index, int status) {
        if (index < 0 || index >= items.size()) return;
        items.get(index).status = status;
        saveItems();
        renderItems();
    }

    private void confirmDelete(int index) {
        if (index < 0 || index >= items.size()) return;
        String name = items.get(index).name;
        new AlertDialog.Builder(this)
                .setTitle("למחוק את המוצר?")
                .setMessage(name)
                .setPositiveButton("מחק", (d, w) -> {
                    items.remove(index);
                    saveItems();
                    renderItems();
                })
                .setNegativeButton("ביטול", null)
                .show();
    }

    private void confirmResetStatuses() {
        new AlertDialog.Builder(this)
                .setTitle("איפוס סימונים")
                .setMessage("כל המוצרים יחזרו למצב 'לא לקחתי'.")
                .setPositiveButton("אפס", (d, w) -> {
                    for (Item item : items) item.status = STATUS_PENDING;
                    saveItems();
                    renderItems();
                })
                .setNegativeButton("ביטול", null)
                .show();
    }

    private void confirmRestoreDefaults() {
        new AlertDialog.Builder(this)
                .setTitle("שחזור הרשימה המובנית")
                .setMessage("פעולה זו תחליף את הרשימה הנוכחית ברשימת ברירת המחדל.")
                .setPositiveButton("שחזר", (d, w) -> {
                    createDefaults();
                    saveItems();
                    renderItems();
                })
                .setNegativeButton("ביטול", null)
                .show();
    }

    private void updateSummary() {
        int taken = 0;
        int missing = 0;
        int pending = 0;
        for (Item item : items) {
            if (item.status == STATUS_TAKEN) taken++;
            else if (item.status == STATUS_MISSING) missing++;
            else pending++;
        }
        summaryText.setText("סה״כ " + items.size() + "  •  ✓ לקחתי " + taken + "  •  ○ נשאר " + pending + "  •  ✕ לא היה " + missing);
    }

    private void loadItems() {
        String json = getSharedPreferences(PREFS, MODE_PRIVATE).getString(KEY_ITEMS, null);
        if (json == null || json.trim().isEmpty()) {
            createDefaults();
            saveItems();
            return;
        }
        try {
            JSONArray arr = new JSONArray(json);
            items.clear();
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                items.add(new Item(o.getString("name"), o.optInt("status", STATUS_PENDING)));
            }
        } catch (Exception e) {
            createDefaults();
            saveItems();
        }
    }

    private void saveItems() {
        JSONArray arr = new JSONArray();
        try {
            for (Item item : items) {
                JSONObject o = new JSONObject();
                o.put("name", item.name);
                o.put("status", item.status);
                arr.put(o);
            }
            getSharedPreferences(PREFS, MODE_PRIVATE)
                    .edit()
                    .putString(KEY_ITEMS, arr.toString())
                    .apply();
        } catch (Exception ignored) {
        }
    }

    private void createDefaults() {
        items.clear();
        for (String s : defaultItems) items.add(new Item(s, STATUS_PENDING));
    }

    private String statusLabel(int status) {
        if (status == STATUS_TAKEN) return "✓ נלקח";
        if (status == STATUS_MISSING) return "✕ לא היה בסופר";
        return "○ עדיין לא לקחתי";
    }

    private int statusBackground(int status) {
        if (status == STATUS_TAKEN) return Color.rgb(230, 248, 237);
        if (status == STATUS_MISSING) return Color.rgb(255, 239, 239);
        return Color.WHITE;
    }

    private int statusBorder(int status) {
        if (status == STATUS_TAKEN) return Color.rgb(151, 214, 173);
        if (status == STATUS_MISSING) return Color.rgb(236, 173, 173);
        return Color.rgb(220, 228, 222);
    }

    private int statusTextColor(int status) {
        if (status == STATUS_TAKEN) return Color.rgb(28, 120, 70);
        if (status == STATUS_MISSING) return Color.rgb(170, 58, 58);
        return Color.rgb(110, 117, 112);
    }

    private android.graphics.drawable.Drawable makeRounded(int fill, int radius, int stroke) {
        android.graphics.drawable.GradientDrawable d = new android.graphics.drawable.GradientDrawable();
        d.setColor(fill);
        d.setCornerRadius(radius);
        if (stroke != Color.TRANSPARENT) d.setStroke(dp(1), stroke);
        return d;
    }

    private LinearLayout.LayoutParams lpMatchWrap() {
        return new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private LinearLayout.LayoutParams weightedButtonLp() {
        return new LinearLayout.LayoutParams(0, dp(42), 1f);
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private static class Item {
        String name;
        int status;

        Item(String name, int status) {
            this.name = name;
            this.status = status;
        }
    }
}
