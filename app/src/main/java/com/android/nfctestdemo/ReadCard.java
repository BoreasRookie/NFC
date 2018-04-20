package com.android.nfctestdemo;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.nfctestdemo.card.CardManager;

/**
 * Created on 2017/5/16.
 *
 * @author wenchao
 * @since 1.0
 */
public class ReadCard extends Activity implements View.OnClickListener{
    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private Resources res;
    private TextView board;


    private enum ContentType {
        HINT, DATA, MSG
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nfcard);

        final Resources res = getResources();
        this.res = res;
        this.board = (TextView) findViewById(R.id.board);
        findViewById(R.id.btnCopy).setOnClickListener(this);
        findViewById(R.id.btnNfc).setOnClickListener(this);
        findViewById(R.id.btnCopy).setOnClickListener(this);

        NfcAdapter adapter = NfcAdapter.getDefaultAdapter(this);
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCopy: {
                copyData();
                break;
            }
            case R.id.btnNfc: {
                startActivityForResult(new Intent(
                        android.provider.Settings.ACTION_WIRELESS_SETTINGS), 0);
                break;
            }
            case R.id.btnExit: {
                finish();
                break;
            }
            default:
                break;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        final Parcelable p = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        Log.d("NFCTAG", intent.getAction());
        if (p!=null)
            showData(CardManager.load(p,res));
        else
            showData(null);
    }

    private void showData(String data) {
        if (data == null || data.length() == 0) {
            showHint();
            return;
        }

        final TextView board = this.board;
        final Resources res = this.res;

        final int padding = res.getDimensionPixelSize(R.dimen.pnl_margin);

        board.setPadding(padding, padding, padding, padding);
        board.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        board.setTextSize(res.getDimension(R.dimen.text_small));
        board.setTextColor(res.getColor(R.color.text_default));
        board.setGravity(Gravity.NO_GRAVITY);
        board.setTag(ContentType.DATA);
        board.setText(Html.fromHtml(data));
    }

    private void showHint() {
        final TextView board = this.board;
        final Resources res = this.res;
        final String hint;

        if (nfcAdapter == null)
            hint = res.getString(R.string.msg_nonfc);
        else if (nfcAdapter.isEnabled())
            hint = res.getString(R.string.msg_nocard);
        else
            hint = res.getString(R.string.msg_nfcdisabled);

        final int padding = res.getDimensionPixelSize(R.dimen.text_middle);

        board.setPadding(padding, padding, padding, padding);
        board.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
        board.setTextSize(res.getDimension(R.dimen.text_small));
        board.setTextColor(res.getColor(R.color.text_tip));
        board.setGravity(Gravity.CENTER_VERTICAL);
        board.setTag(ContentType.HINT);
        board.setText(Html.fromHtml(hint));
    }

    private void copyData() {
        final CharSequence text = board.getText();
        if (text == null || board.getTag() != ContentType.DATA)
            return;

        ((ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE))
                .setText(text);

        final String msg = res.getString(R.string.msg_copied);
        final Toast toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
