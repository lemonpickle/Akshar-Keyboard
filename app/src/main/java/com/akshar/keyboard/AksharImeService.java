package com.akshar.keyboard;

import android.inputmethodservice.InputMethodService;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

public final class AksharImeService extends InputMethodService {
    private static final String[] LETTERS = {
            "a", "i", "u", "e", "o", "k", "g", "c",
            "j", "t", "d", "n", "p", "f", "b", "m",
            "y", "r", "l", "v", "s", "h", "z"
    };

    private static final String[] LETTER_PUNCTUATION = {
            "?", "!", "@", "'",
            ";", "_", "#"
    };

    private static final String[] LAST_LETTER_ROW_SYMBOLS = {"\"", ",", "."};
    private static final String[] NUMBER_SYMBOLS = {
            "+", "-", "*", "/", "=", ",", ".", ":"
    };
    private static final String[] MATH_SYMBOLS = {
            "(", ")", "~", "\u00B0", "\u2264",
            "\u2265", "\u2260", "{", "}", "<", ">",
            "\u2286", "\u2282", "\u2208", "\u03C6", "\u03C0",
            "\u222B", "|", "\u2261", "\u2211", "\u222A", "\u2229", "\u03C3"
    };

    @Override
    public View onCreateInputView() {
        return new AksharKeyboardView(this, this::typeKey, this::deleteBackward, this::sendEnter);
    }

    private void typeKey(String key) {
        InputConnection connection = getCurrentInputConnection();
        if (connection == null) {
            return;
        }
        connection.commitText(key, 1);
    }

    static String convertCharacter(String value, boolean superscript, boolean subscript) {
        StringBuilder converted = new StringBuilder();
        for (int offset = 0; offset < value.length();) {
            int codePoint = value.codePointAt(offset);
            converted.appendCodePoint(superscript
                    ? superscript(codePoint)
                    : subscript ? subscript(codePoint) : codePoint);
            offset += Character.charCount(codePoint);
        }
        return converted.toString();
    }

    private static int superscript(int codePoint) {
        switch (codePoint) {
            case '0': return 0x2070;
            case '1': return 0x00B9;
            case '2': return 0x00B2;
            case '3': return 0x00B3;
            case '4': return 0x2074;
            case '5': return 0x2075;
            case '6': return 0x2076;
            case '7': return 0x2077;
            case '8': return 0x2078;
            case '9': return 0x2079;
            case '+': return 0x207A;
            case '-': return 0x207B;
            case '=': return 0x207C;
            case '(': return 0x207D;
            case ')': return 0x207E;
            case 'a': return 0x1D43;
            case 'b': return 0x1D47;
            case 'c': return 0x1D9C;
            case 'd': return 0x1D48;
            case 'e': return 0x1D49;
            case 'f': return 0x1DA0;
            case 'g': return 0x1D4D;
            case 'h': return 0x02B0;
            case 'i': return 0x2071;
            case 'j': return 0x02B2;
            case 'k': return 0x1D4F;
            case 'l': return 0x02E1;
            case 'm': return 0x1D50;
            case 'n': return 0x207F;
            case 'o': return 0x1D52;
            case 'p': return 0x1D56;
            case 'r': return 0x02B3;
            case 's': return 0x02E2;
            case 't': return 0x1D57;
            case 'u': return 0x1D58;
            case 'v': return 0x1D5B;
            case 'w': return 0x02B7;
            case 'x': return 0x02E3;
            case 'y': return 0x02B8;
            case 'z': return 0x1DBB;
            case 'A': return 0x1D2C;
            case 'B': return 0x1D2E;
            case 'D': return 0x1D30;
            case 'E': return 0x1D31;
            case 'G': return 0x1D33;
            case 'H': return 0x1D34;
            case 'I': return 0x1D35;
            case 'J': return 0x1D36;
            case 'K': return 0x1D37;
            case 'L': return 0x1D38;
            case 'M': return 0x1D39;
            case 'N': return 0x1D3A;
            case 'O': return 0x1D3C;
            case 'P': return 0x1D3E;
            case 'R': return 0x1D3F;
            case 'T': return 0x1D40;
            case 'U': return 0x1D41;
            case 'W': return 0x1D42;
            default: return codePoint;
        }
    }

    private static int subscript(int codePoint) {
        switch (codePoint) {
            case '0': return 0x2080;
            case '1': return 0x2081;
            case '2': return 0x2082;
            case '3': return 0x2083;
            case '4': return 0x2084;
            case '5': return 0x2085;
            case '6': return 0x2086;
            case '7': return 0x2087;
            case '8': return 0x2088;
            case '9': return 0x2089;
            case '+': return 0x208A;
            case '-': return 0x208B;
            case '=': return 0x208C;
            case '(': return 0x208D;
            case ')': return 0x208E;
            case 'a': return 0x2090;
            case 'e': return 0x2091;
            case 'h': return 0x2095;
            case 'i': return 0x1D62;
            case 'j': return 0x2C7C;
            case 'k': return 0x2096;
            case 'l': return 0x2097;
            case 'm': return 0x2098;
            case 'n': return 0x2099;
            case 'o': return 0x2092;
            case 'p': return 0x209A;
            case 'r': return 0x1D63;
            case 's': return 0x209B;
            case 't': return 0x209C;
            case 'u': return 0x1D64;
            case 'v': return 0x1D65;
            case 'x': return 0x2093;
            default: return codePoint;
        }
    }

    private void deleteBackward() {
        InputConnection connection = getCurrentInputConnection();
        if (connection == null) {
            return;
        }

        CharSequence selected = connection.getSelectedText(0);
        if (selected != null && selected.length() > 0) {
            connection.commitText("", 1);
            return;
        }

        CharSequence beforeCursor = connection.getTextBeforeCursor(64, 0);
        if (beforeCursor == null || beforeCursor.length() == 0) {
            return;
        }
        String text = beforeCursor.toString();
        java.text.BreakIterator iterator =
                java.text.BreakIterator.getCharacterInstance(java.util.Locale.getDefault());
        iterator.setText(text);
        int previousBoundary = iterator.preceding(text.length());
        if (previousBoundary != java.text.BreakIterator.DONE) {
            connection.deleteSurroundingText(text.length() - previousBoundary, 0);
        }
    }

    private void sendEnter() {
        InputConnection connection = getCurrentInputConnection();
        if (connection == null) {
            return;
        }
        EditorInfo editor = getCurrentInputEditorInfo();
        if (editor != null && editor.actionId != 0) {
            connection.performEditorAction(editor.actionId);
            return;
        }
        if (editor != null) {
            int action = editor.imeOptions & EditorInfo.IME_MASK_ACTION;
            if (action != EditorInfo.IME_ACTION_NONE
                    && action != EditorInfo.IME_ACTION_UNSPECIFIED) {
                connection.performEditorAction(action);
                return;
            }
        }
        if (editor != null
                && (editor.inputType & InputType.TYPE_TEXT_FLAG_MULTI_LINE) == 0) {
            connection.performEditorAction(EditorInfo.IME_ACTION_DONE);
        } else {
            connection.commitText("\n", 1);
        }
    }

    static String[] letterKeyboardKeys() {
        java.util.ArrayList<String> keys = new java.util.ArrayList<>();
        java.util.Collections.addAll(keys, LETTERS);
        java.util.Collections.addAll(keys, LAST_LETTER_ROW_SYMBOLS);
        java.util.Collections.addAll(keys, LETTER_PUNCTUATION);
        return keys.toArray(new String[0]);
    }

    static String[] numberSymbols() {
        return NUMBER_SYMBOLS.clone();
    }

    static String[] mathSymbols() {
        return MATH_SYMBOLS.clone();
    }
}
