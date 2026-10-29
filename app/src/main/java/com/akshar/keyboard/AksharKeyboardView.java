package com.akshar.keyboard;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.Locale;

final class AksharKeyboardView extends LinearLayout {
    private enum KeyboardPage {
        LETTERS,
        NUMBERS,
        MATHS
    }

    interface KeyAction {
        void run(String value);
    }

    private static final int GREEN = Color.rgb(49, 92, 72);
    private static final int INK = Color.rgb(32, 37, 31);
    private static final int MUTED = Color.rgb(119, 123, 112);
    private static final int KEY_BACKGROUND = Color.rgb(255, 254, 250);

    private final Context context;
    private final KeyAction typeKey;
    private final Runnable backspace;
    private final Runnable enter;
    private boolean shifted;
    private boolean superscript;
    private boolean subscript;
    private KeyboardPage page = KeyboardPage.LETTERS;

    AksharKeyboardView(
            Context context, KeyAction typeKey, Runnable backspace, Runnable enter) {
        super(context);
        this.context = context;
        this.typeKey = typeKey;
        this.backspace = backspace;
        this.enter = enter;
        setOrientation(VERTICAL);
        setPadding(dp(7), 0, dp(7), 0);
        setBackgroundColor(Color.rgb(244, 241, 233));
        buildKeyboard();
    }

    private void buildKeyboard() {
        if (page == KeyboardPage.NUMBERS) {
            LinearLayout symbols = row();
            String[] symbolsList = AksharImeService.numberSymbols();
            addCharacterKeys(symbols, symbolsList, 0, symbolsList.length);
            for (String symbol : new String[] {"\u20B9", "%"}) {
                addKey(symbols, symbol, null, 1f, () -> typeCharacter(symbol));
            }
            addView(symbols, rowParams(42));

            LinearLayout numbers = row();
            for (String number : new String[] {"1", "2", "3", "4", "5", "6", "7", "8", "9", "0"}) {
                addKey(numbers, number, null, 1f, () -> typeCharacter(number));
            }
            addView(numbers, rowParams(42));
            addEmptyRow();
        } else if (page == KeyboardPage.MATHS) {
            String[] mathSymbols = AksharImeService.mathSymbols();
            LinearLayout firstMathRow = row();
            addCharacterKeys(firstMathRow, mathSymbols, 0, 12);
            addView(firstMathRow, rowParams(42));
            LinearLayout secondMathRow = row();
            addCharacterKeys(secondMathRow, mathSymbols, 12, mathSymbols.length);
            addModeKeys(secondMathRow);
            addView(secondMathRow, rowParams(42));
            addEmptyRow();
        } else {
            String[] keys = AksharImeService.letterKeyboardKeys();
            int[][] rowRanges = {
                    {keys.length - 10, keys.length},
                    {0, 12},
                    {12, keys.length - 10}
            };
            for (int[] range : rowRanges) {
                LinearLayout row = row();
                addCharacterKeys(row, keys, range[0], range[1]);
                addView(row, rowParams(42));
            }
        }

        LinearLayout controls = row();
        Button shift = addKey(controls, "⇧", null, 1f, () -> {
            shifted = !shifted;
            rebuildKeyboard();
        });
        shift.setContentDescription(shifted ? "Shift, on" : "Shift, off");
        if (shifted) {
            shift.setBackground(activeBackground());
            shift.setTextColor(Color.WHITE);
        }
        KeyboardPage shortcutPage = pageShortcutDestination();
        Button pageButton = addKey(controls, pageShortcutLabel(), null, 1f, () -> {
            page = shortcutPage;
            rebuildKeyboard();
        });
        pageButton.setContentDescription("Show " + pageDescription(shortcutPage) + " keyboard");
        if (page == KeyboardPage.NUMBERS) {
            Button mathsPageButton = addKey(controls, "∑", null, 1f, () -> {
                page = KeyboardPage.MATHS;
                rebuildKeyboard();
            });
            mathsPageButton.setContentDescription("Show maths keyboard");
        }
        if (page == KeyboardPage.MATHS) {
            Button numberPageButton = addKey(controls, "123", null, 1f, () -> {
                page = KeyboardPage.NUMBERS;
                rebuildKeyboard();
            });
            numberPageButton.setContentDescription("Show number and symbol keyboard");
        }
        addKey(controls, "space", null, page == KeyboardPage.LETTERS ? 4f : 3f,
                () -> typeKey.run(" "));
        addKey(controls, "⌫", null, 1f, backspace);
        addKey(controls, "↵", null, 1f, enter);
        addView(controls, rowParams(42));
    }

    private void addEmptyRow() {
        addView(row(), rowParams(42));
    }

    private void addCharacterKeys(LinearLayout row, String[] keys, int start, int end) {
        for (int index = start; index < end; index++) {
            String roman = keys[index];
            String key = shifted ? roman.toUpperCase(Locale.ROOT) : roman;
            addKey(row, key, null, 1f, () -> typeCharacter(key));
        }
    }

    private void addModeKeys(LinearLayout row) {
        addModeKey(row, "xⁿ", "Superscript", superscript, () -> {
            superscript = !superscript;
            subscript = false;
            rebuildKeyboard();
        });
        addModeKey(row, "xₙ", "Subscript", subscript, () -> {
            subscript = !subscript;
            superscript = false;
            rebuildKeyboard();
        });
    }

    private String pageShortcutLabel() {
        switch (page) {
            case NUMBERS:
            case MATHS: return "ABC";
            default: return "123";
        }
    }

    private KeyboardPage pageShortcutDestination() {
        return page == KeyboardPage.LETTERS ? KeyboardPage.NUMBERS : KeyboardPage.LETTERS;
    }

    private String pageDescription(KeyboardPage destination) {
        switch (destination) {
            case LETTERS: return "letter";
            case NUMBERS: return "number and symbol";
            default: return "maths";
        }
    }

    private void typeCharacter(String key) {
        typeKey.run(AksharImeService.convertCharacter(key, superscript, subscript));
    }

    private void addModeKey(
            LinearLayout row, String label, String description, boolean active, Runnable action) {
        Button button = addKey(row, label, null, 1f, action);
        button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        button.setContentDescription(description + (active ? ", on" : ", off"));
        if (active) {
            button.setBackground(activeBackground());
            button.setTextColor(Color.WHITE);
        }
    }

    private void rebuildKeyboard() {
        removeAllViews();
        buildKeyboard();
    }

    private LinearLayout row() {
        LinearLayout row = new LinearLayout(context);
        row.setOrientation(HORIZONTAL);
        row.setGravity(Gravity.CENTER);
        return row;
    }

    private Button addKey(
            LinearLayout row, String label, String secondary, float weight, Runnable action) {
        Button button = new Button(context);
        button.setAllCaps(false);
        button.setGravity(Gravity.CENTER);
        button.setPadding(dp(1), 0, dp(1), 0);
        button.setMinWidth(0);
        button.setMinHeight(0);
        button.setTextColor(INK);
        button.setTextSize(TypedValue.COMPLEX_UNIT_SP, secondary == null ? 15 : 13);
        button.setBackground(background());
        button.setStateListAnimator(null);
        button.setContentDescription(secondary == null ? label : label + ", " + secondary);
        if (secondary != null) {
            SpannableString text = new SpannableString(label + "\n" + secondary);
            text.setSpan(new RelativeSizeSpan(0.78f), label.length() + 1, text.length(), 0);
            text.setSpan(new android.text.style.ForegroundColorSpan(MUTED),
                    label.length() + 1, text.length(), 0);
            button.setText(text);
        } else {
            button.setText(label);
        }
        button.setOnClickListener(view -> action.run());
        LayoutParams params = new LayoutParams(0, dp(40), weight);
        params.setMargins(dp(2), dp(1), dp(2), dp(1));
        row.addView(button, params);
        return button;
    }

    private Button addCenteredKey(LinearLayout row, String label, Runnable action, int widthDp) {
        Button button = addKey(row, label, null, 1f, action);
        LayoutParams params = (LayoutParams) button.getLayoutParams();
        params.width = dp(widthDp);
        params.weight = 0f;
        button.setLayoutParams(params);
        return button;
    }

    private GradientDrawable background() {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(KEY_BACKGROUND);
        drawable.setCornerRadius(dp(7));
        drawable.setStroke(dp(1), Color.rgb(230, 226, 216));
        return drawable;
    }

    private GradientDrawable activeBackground() {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(GREEN);
        drawable.setCornerRadius(dp(7));
        drawable.setStroke(dp(1), GREEN);
        return drawable;
    }

    private LayoutParams rowParams(int height) {
        return new LayoutParams(LayoutParams.MATCH_PARENT, dp(height));
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
