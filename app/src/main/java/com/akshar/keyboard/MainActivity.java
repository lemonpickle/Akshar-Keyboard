package com.akshar.keyboard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public final class MainActivity extends Activity {
    private static final int GREEN = 0xFF315C48;
    private static final int INK = 0xFF20251F;
    private static final int MUTED = 0xFF777B70;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setGravity(Gravity.CENTER_VERTICAL);
        page.setPadding(dp(28), dp(32), dp(28), dp(32));
        page.setBackgroundColor(0xFFF4F1E9);
        page.setOnApplyWindowInsetsListener((view, insets) -> {
            view.setPadding(
                    dp(28) + insets.getSystemWindowInsetLeft(),
                    dp(32) + insets.getSystemWindowInsetTop(),
                    dp(28) + insets.getSystemWindowInsetRight(),
                    dp(32) + insets.getSystemWindowInsetBottom());
            return insets;
        });

        TextView title = new TextView(this);
        title.setText(R.string.setup_title);
        title.setTextColor(INK);
        title.setTextSize(32);
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        page.addView(title, matchWrap());

        TextView description = new TextView(this);
        description.setText(R.string.setup_description);
        description.setTextColor(MUTED);
        description.setTextSize(16);
        description.setLineSpacing(dp(4), 1f);
        LinearLayout.LayoutParams descriptionParams = matchWrap();
        descriptionParams.topMargin = dp(14);
        page.addView(description, descriptionParams);

        Button enable = actionButton(R.string.enable_keyboard);
        LinearLayout.LayoutParams buttonParams = matchWrap();
        buttonParams.topMargin = dp(28);
        page.addView(enable, buttonParams);
        enable.setOnClickListener(view ->
                startActivity(new Intent("android.settings.INPUT_METHOD_SETTINGS")));

        Button choose = actionButton(R.string.choose_keyboard);
        LinearLayout.LayoutParams chooseParams = matchWrap();
        chooseParams.topMargin = dp(10);
        page.addView(choose, chooseParams);
        choose.setOnClickListener(view -> {
            InputMethodManager manager =
                    (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (manager != null) {
                manager.showInputMethodPicker();
            }
        });

        TextView privacy = new TextView(this);
        privacy.setText(R.string.privacy_note);
        privacy.setTextColor(MUTED);
        privacy.setTextSize(13);
        privacy.setLineSpacing(dp(3), 1f);
        LinearLayout.LayoutParams privacyParams = matchWrap();
        privacyParams.topMargin = dp(28);
        page.addView(privacy, privacyParams);
        setContentView(page);
    }

    private Button actionButton(int label) {
        Button button = new Button(this);
        button.setText(label);
        button.setTextColor(0xFFFFFFFF);
        button.setTextSize(14);
        button.setAllCaps(false);
        button.setBackgroundTintList(android.content.res.ColorStateList.valueOf(GREEN));
        return button;
    }

    private LinearLayout.LayoutParams matchWrap() {
        return new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
