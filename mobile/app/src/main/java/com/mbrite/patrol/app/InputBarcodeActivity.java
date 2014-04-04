package com.mbrite.patrol.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mbrite.patrol.common.*;
import com.mbrite.patrol.content.providers.*;
import com.mbrite.patrol.model.Asset;

public class InputBarcodeActivity extends Activity {

    private EditText barcodeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_barcode);

        Bundle extras = getIntent().getExtras();
        final String targetBarcode = extras == null ? null : extras.getString(Constants.BARCODE);
        barcodeText = (EditText) findViewById(R.id.barcode);
        final Button submitButton = (Button) findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String barcode = barcodeText.getText().toString();
                try {
                    if (targetBarcode != null) {
                        // verify barcode
                        if (!targetBarcode.equals(barcode)) {
                           throw new IllegalStateException(getString(R.string.error_incorrect_barcode));
                        }
                    }

                    Asset asset = AssetProvider.INSTANCE.getAsset(InputBarcodeActivity.this, barcode, Tracker.INSTANCE.assetIds);
                    if (asset == null) {
                        throw new IllegalStateException(getString(R.string.error_incorrect_barcode));
                    }
                    RecordProvider.INSTANCE.offerAsset(InputBarcodeActivity.this, asset.id);
                    Intent intent = new Intent(InputBarcodeActivity.this, PointsActivity.class);
                    intent.putExtra(Constants.POINTS, asset.points);
                    startActivity(intent);
                } catch (Exception ex) {
                    submitButton.setError(ex.getLocalizedMessage());
                    Toast.makeText(
                            InputBarcodeActivity.this,
                            String.format(getString(R.string.error_of), ex.getLocalizedMessage()),
                            Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
    }
}
