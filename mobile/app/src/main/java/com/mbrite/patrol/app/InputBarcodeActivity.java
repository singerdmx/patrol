package com.mbrite.patrol.app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class InputBarcodeActivity extends BarcodeParentActivity {

    private EditText barcodeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_barcode);

        barcodeText = (EditText) findViewById(R.id.barcode);
        final Button submitButton = (Button) findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String barcode = barcodeText.getText().toString();
                try {
                    checkBarcode(InputBarcodeActivity.this, barcode);
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
