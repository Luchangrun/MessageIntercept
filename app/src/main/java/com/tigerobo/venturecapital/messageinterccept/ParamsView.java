package com.tigerobo.venturecapital.messageinterccept;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

public class ParamsView extends RelativeLayout {

    private EditText editName;
    private EditText editValue;
    private Button delete;
    private OnDeleteListener listener;
    private String value;
    private String name;

    public ParamsView(Context context) {
        this(context, null);
    }

    public ParamsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ParamsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.view_params, this);
        editName = findViewById(R.id.edit_name);
        editValue = findViewById(R.id.edit_value);
        delete = findViewById(R.id.delete);
        delete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onDelete(ParamsView.this);
                }
            }
        });
    }

    public String getEditName() {
        return editName.getText().toString();
    }

    public String getEditValue() {
        return editValue.getText().toString();
    }

    public void setValue(String value) {
        this.value = value;
        if (editValue != null) {
            editValue.setText(value);
        }
    }

    public void setName(String name) {
        this.name = name;
        if (editName != null) {
            editName.setText(name);
        }
    }

    public void setListener(OnDeleteListener listener) {
        this.listener = listener;
    }

    public interface OnDeleteListener {
        void onDelete(ParamsView paramsView);
    }
}
