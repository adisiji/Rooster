package com.blikoon.rooster.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by neobyte on 10/15/2016.
 */

public final class AllUtil {

    public static boolean isValidId(String jid){
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(jid);
        return matcher.matches();
    }

    public static void browseUrl(Context context, String url) {
        if (!url.startsWith("http") && !url.startsWith("https"))
            url = "http://" + url;
        Intent openBrowserIntent = new Intent(Intent.ACTION_VIEW);
        openBrowserIntent.setData(Uri.parse(url));
        context.startActivity(openBrowserIntent);
    }

    /**
     * @param context the application context
     * @param number  the specified phone number
     * @brief methods for doing a phone call with specified phone number
     */
    public static void phoneCall(Context context, String number) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number));
        context.startActivity(intent);
    }
    /**
     * @param context the application context
     * @param address the specified email address
     * @brief methods for sending a mail via mail intent
     */
    public static void mailTo(Context context, String address) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", address, null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Contact with Audacity IT Solutions via company profile app");
        context.startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }

}
